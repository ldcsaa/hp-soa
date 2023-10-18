
package io.github.hpsocket.soa.starter.leaf.config;
        
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import com.baomidou.dynamic.datasource.exception.CannotFindDataSourceException;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.leaf.segment.SegmentIdGenImpl;
import io.github.hpsocket.soa.framework.leaf.segment.dao.impl.IdAllocDaoImpl;
import io.github.hpsocket.soa.framework.leaf.service.GlobalIdService;
import io.github.hpsocket.soa.framework.leaf.service.IdGen;
import io.github.hpsocket.soa.framework.leaf.snowflake.SnowflakeIdGenImpl;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import io.github.hpsocket.soa.starter.leaf.service.impl.GlobalIdServiceImpl;

/** <b>HP-SOA Leaf 分布式全局 ID 配置</b> */
@AutoConfiguration
@Import(GlobalIdServiceImpl.class)
@ConditionalOnExpression("${hp.soa.gid.leaf.snowflake.enabled:true} || ${hp.soa.gid.leaf.segment.enabled:false}")
public class SoaLeafConfig
{
    private static final String DEFAULT_LEAF_SEGMENT_DATA_SOURCE = "leaf";
    
    @Value("${hp.soa.gid.leaf.snowflake.name:default}")
    private String leafName;
    @Value("${hp.soa.gid.leaf.snowflake.zk-address:}")
    private String zkAddress;
    @Value("${hp.soa.gid.leaf.snowflake.server-port:${server.port}}")
    private int port;
    
    @Value("${hp.soa.gid.leaf.segment.data-source:" + DEFAULT_LEAF_SEGMENT_DATA_SOURCE + "}")
    private String leafSegmentDataSource;
    
    /** 雪花 ID 生成器 */
    @Bean(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN)
    @ConditionalOnProperty(name = "hp.soa.gid.leaf.snowflake.enabled", havingValue = "true", matchIfMissing = true)
    public IdGen snowflakeIdGen()
    {
        if(GeneralHelper.isStrEmpty(leafName))
            throw new BeanCreationException(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN, "property 'hp.soa.gid.leaf.snowflake.name' not config");
        if(GeneralHelper.isStrEmpty(zkAddress))
            throw new BeanCreationException(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN, "property 'hp.soa.gid.leaf.snowflake.zk-address' not config");
        if(port <= 0)
            throw new BeanCreationException(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN, "property 'hp.soa.gid.leaf.snowflake.server-port' is illegal");
        
        SnowflakeIdGenImpl idGen = new SnowflakeIdGenImpl(zkAddress, port, leafName);
        
        if(!idGen.init())
            throw new BeanCreationException(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN, "Bean Init Fail");
        
        return idGen;
    }
    
    /** 段号 ID 生成器 */
    @Bean(GlobalIdService.LEAF_SEGMENT_ID_GENERATOR_BEAN)
    @ConditionalOnProperty(name = "hp.soa.gid.leaf.segment.enabled", havingValue = "true", matchIfMissing = false)
    public IdGen segmentIdGen()
    {
        DataSource dataSource   = findLeafSegmentDataSource();
        IdAllocDaoImpl dao      = new IdAllocDaoImpl(dataSource);
        SegmentIdGenImpl idGen  = new SegmentIdGenImpl();
        
        idGen.setDao(dao);
        
        if(!idGen.init())
            throw new BeanCreationException(GlobalIdService.LEAF_SEGMENT_ID_GENERATOR_BEAN, "Bean Init Fail");
        
        return idGen;
    }
    
    private DataSource findLeafSegmentDataSource()
    {
        Map<String, DataSource> dataSources = SpringContextHolder.getApplicationContext().getBeansOfType(DataSource.class);
        
        for(Map.Entry<String, DataSource> entry : dataSources.entrySet())
        {
            DataSource ds = entry.getValue();
            
            if(entry.getKey().equals(leafSegmentDataSource))
                return ds;
            else if(ds instanceof DynamicRoutingDataSource drDs)
            {
                DataSource dataSource = null;
                
                try
                {
                    dataSource = drDs.getDataSource(leafSegmentDataSource);
                }
                catch(CannotFindDataSourceException e)
                {
                    
                }
                
                if(dataSource != null)
                    return dataSource;
            }
        }
        
        throw new BeanCreationException(GlobalIdService.LEAF_SEGMENT_ID_GENERATOR_BEAN, "data source '" + leafSegmentDataSource + "' not found");
    }
    
}
