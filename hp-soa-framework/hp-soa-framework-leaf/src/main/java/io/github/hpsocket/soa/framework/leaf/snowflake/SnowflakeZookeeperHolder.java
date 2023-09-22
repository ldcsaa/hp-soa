
package io.github.hpsocket.soa.framework.leaf.snowflake;

import com.alibaba.fastjson2.JSON;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

import com.google.common.collect.Maps;

import io.github.hpsocket.soa.framework.leaf.snowflake.exception.CheckLastTimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.client.ZKClientConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/** Snowflake ID 生成器的 Zookeeper 访问 */
@Slf4j
public class SnowflakeZookeeperHolder
{
	@Getter
	@Setter
	private String zk_AddressNode = null;// 保存自身的key ip:port-000000001
	@Getter
	@Setter
	private String listenAddress = null;// 保存自身的key ip:port
	@Getter
	@Setter
	private int workerID;
	
	private final String PREFIX_ZK_PATH;
	private final String PROP_PATH;
	private final String PATH_FOREVER;// 保存所有数据持久的节点
	private String ip;
	private String port;
	private String connectionString;
	private long lastUpdateTime;

	public SnowflakeZookeeperHolder(String ip, String port, String connectionString, String leafName)
	{
		this.ip = ip;
		this.port = port;
		this.listenAddress = ip + ":" + port;
		this.connectionString = connectionString;

		PREFIX_ZK_PATH = "/snowflake/" + leafName;
		PROP_PATH = System.getProperty("java.io.tmpdir") + File.separator + leafName + "/leafconf/{port}/workerID.properties";
		PATH_FOREVER = PREFIX_ZK_PATH + "/forever";// 保存所有数据持久的节点
	}

	public boolean init()
	{
		try
		{
			CuratorFramework curator = createCuratorWithOptions(connectionString, new RetryUntilElapsed(1000, 50), 5000, 15000);			
			curator.start();
			
			Stat stat = curator.checkExists().forPath(PATH_FOREVER);
			
			if(stat == null)
			{
				// 不存在根节点,机器第一次启动,创建/snowflake/ip:port-000000000,并上传数据
				zk_AddressNode = createNode(curator);
				// worker id 默认是0
				workerID = Integer.parseInt(zk_AddressNode.split("-")[1]);
				updateLocalWorkerID(workerID);
				// 定时上报本机时间给forever节点
				ScheduledUploadData(curator, zk_AddressNode);
				
				return true;
			}
			else
			{
				Map<String, Integer> nodeMap = Maps.newHashMap();// ip:port->00001
				Map<String, String> realNode = Maps.newHashMap();// ip:port->(ipport-000001)
				// 存在根节点,先检查是否有属于自己的根节点
				List<String> keys = curator.getChildren().forPath(PATH_FOREVER);
				
				for(String key : keys)
				{
					String[] nodeKey = key.split("-");
					realNode.put(nodeKey[0], key);
					nodeMap.put(nodeKey[0], Integer.parseInt(nodeKey[1]));
				}
				
				Integer workerid = nodeMap.get(listenAddress);
				
				if(workerid != null)
				{
					// 有自己的节点,zk_AddressNode=ip:port
					zk_AddressNode = PATH_FOREVER + "/" + realNode.get(listenAddress);
					workerID = workerid;// 启动worder时使用会使用
					if(!checkInitTimeStamp(curator, zk_AddressNode))
					{
						throw new CheckLastTimeException("init timestamp check error,forever node timestamp gt this node time");
					}
					
					// 准备创建临时节点
					doService(curator);
					updateLocalWorkerID(workerID);
					log.info("[Old NODE]find forever node have this endpoint ip-{} port-{} workid-{} childnode and start SUCCESS", ip, port, workerID);
				}
				else
				{
					// 表示新启动的节点,创建持久节点 ,不用check时间
					/*
					 * String newNode = createNode(curator);
					 * zk_AddressNode = newNode;
					 * String[] nodeKey = newNode.split("-");
					 * workerID = Integer.parseInt(nodeKey[1]);
					 */
					zk_AddressNode = createNode(curator);
					workerID = Integer.parseInt(zk_AddressNode.split("-")[1]);
					
					doService(curator);
					updateLocalWorkerID(workerID);
					log.info("[New NODE]can not find node on forever node that endpoint ip-{} port-{} workid-{},create own node on forever node and start SUCCESS ", ip, port, workerID);
				}
			}
		}
		catch(Exception e)
		{
			log.error("Start node ERROR {}", e);
			
			try
			{
				Properties properties = new Properties();
				properties.load(new FileInputStream(new File(PROP_PATH.replace("{port}", port + ""))));
				workerID = Integer.valueOf(properties.getProperty("workerID"));
				log.warn("START FAILED ,use local node file properties workerID-{}", workerID);
			}
			catch(Exception e1)
			{
				log.error("Read file error ", e1);
				return false;
			}
		}
		return true;
	}

	private void doService(CuratorFramework curator)
	{
		ScheduledUploadData(curator, zk_AddressNode);// /snowflake_forever/ip:port-000000001
	}

	private void ScheduledUploadData(final CuratorFramework curator, final String zk_AddressNode)
	{
		Executors.newSingleThreadScheduledExecutor(new ThreadFactory()
		{
			@Override
			public Thread newThread(Runnable r)
			{
				Thread thread = new Thread(r, "schedule-upload-time");
				thread.setDaemon(true);
				return thread;
			}
		}).scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				updateNewData(curator, zk_AddressNode);
			}
		}, 1L, 5L, TimeUnit.SECONDS);// 每5s上报数据

	}

	private boolean checkInitTimeStamp(CuratorFramework curator, String zk_AddressNode) throws Exception
	{
		byte[] bytes = curator.getData().forPath(zk_AddressNode);
		Endpoint endPoint = deBuildData(new String(bytes));
		
		// 该节点的时间不能小于最后一次上报的时间
		return (System.currentTimeMillis() >= endPoint.getTimestamp());
	}

	/**
	 * 创建持久顺序节点 ,并把节点数据放入 value
	 *
	 * @param curator
	 * @return
	 * @throws Exception
	 */
	private String createNode(CuratorFramework curator) throws Exception
	{
		try
		{
			return curator.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath(PATH_FOREVER + "/" + listenAddress + "-", buildData().getBytes());
		}
		catch(Exception e)
		{
			log.error("create node error msg {} ", e.getMessage());
			throw e;
		}
	}

	private void updateNewData(CuratorFramework curator, String path)
	{
		try
		{
			if(System.currentTimeMillis() < lastUpdateTime)
			{
				return;
			}
			
			curator.setData().forPath(path, buildData().getBytes());
			lastUpdateTime = System.currentTimeMillis();
		}
		catch(Exception e)
		{
			log.error("update init data error path is {} error is {}", path, e);
		}
	}

	/**
	 * 构建需要上传的数据
	 *
	 * @return
	 */
	private String buildData()
	{
		Endpoint endpoint = new Endpoint(ip, port, System.currentTimeMillis());
		String json = JSON.toJSONString(endpoint);
		
		return json;
	}

	private Endpoint deBuildData(String json) throws IOException
	{
		Endpoint endpoint = JSON.parseObject(json, Endpoint.class);
		
		return endpoint;
	}

	/**
	 * 在节点文件系统上缓存一个workid值,zk失效,机器重启时保证能够正常启动
	 *
	 * @param workerID
	 */
	private void updateLocalWorkerID(int workerID)
	{
		File leafConfFile = new File(PROP_PATH.replace("{port}", port));
		boolean exists = leafConfFile.exists();
		
		log.info("file exists status is {}", exists);
		
		if(exists)
		{
			try
			{
				FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, Charset.defaultCharset(), false);
				log.info("update file cache workerID is {}", workerID);
			}
			catch(IOException e)
			{
				log.error("update file cache error ", e);
			}
		}
		else
		{
			// 不存在文件,父目录页肯定不存在
			try
			{
				boolean mkdirs = leafConfFile.getParentFile().mkdirs();
				log.info("init local file cache create parent dis status is {}, worker id is {}", mkdirs, workerID);
				if(mkdirs)
				{
					if(leafConfFile.createNewFile())
					{
						FileUtils.writeStringToFile(leafConfFile, "workerID=" + workerID, Charset.defaultCharset(), false);
						log.info("local file cache workerID is {}", workerID);
					}
				}
				else
				{
					log.warn("create parent dir error===");
				}
			}
			catch(IOException e)
			{
				log.warn("craete workerID conf file error", e);
			}
		}
	}

	private CuratorFramework createCuratorWithOptions(String connectionString, RetryPolicy retryPolicy, int connectionTimeoutMs, int sessionTimeoutMs)
	{
		ZKClientConfig zkClientConfig = new ZKClientConfig();
		zkClientConfig.setProperty(ZKClientConfig.ENABLE_CLIENT_SASL_KEY, Boolean.FALSE.toString());
		
		return CuratorFrameworkFactory.builder()
				.zkClientConfig(zkClientConfig)
				.connectString(connectionString)
				.retryPolicy(retryPolicy)
				.connectionTimeoutMs(connectionTimeoutMs)
				.sessionTimeoutMs(sessionTimeoutMs)
				.build();
	}

	/**
	 * 上报数据结构
	 */
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	static class Endpoint
	{
		private String ip;
		private String port;
		private long timestamp;
	}

}
