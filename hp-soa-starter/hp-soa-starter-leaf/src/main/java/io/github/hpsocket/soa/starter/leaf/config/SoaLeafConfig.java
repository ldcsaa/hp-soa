
package io.github.hpsocket.soa.starter.leaf.config;
        
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.leaf.segment.SegmentIdGenImpl;
import io.github.hpsocket.soa.framework.leaf.segment.dao.impl.IdAllocDaoImpl;
import io.github.hpsocket.soa.framework.leaf.service.GlobalIdService;
import io.github.hpsocket.soa.framework.leaf.service.IdGen;
import io.github.hpsocket.soa.framework.leaf.snowflake.SnowflakeIdGenImpl;
import io.github.hpsocket.soa.starter.leaf.service.impl.GlobalIdServiceImpl;

/** <b>HP-SOA Leaf 分布式全局 ID 配置</b> */
@AutoConfiguration
@Import(GlobalIdServiceImpl.class)
@ConditionalOnExpression("${hp.soa.gid.leaf.segment.enable:false} || ${hp.soa.gid.leaf.snowflake.enable:true}")
public class SoaLeafConfig
{
    private static final String LEAF_SEGMENT_ID_DATA_SOURCE_BEAN    = "leafSegmentIdDataSource";
    
    @Value("${hp.soa.gid.leaf.snowflake.name:default}")
    private String leafName;
    @Value("${hp.soa.gid.leaf.snowflake.zk-address:}")
    private String zkAddress;
    @Value("${hp.soa.gid.leaf.snowflake.server-port:${server.port}}")
    private int port;

    @AutoConfiguration
    @SuppressWarnings("serial")
    @ConditionalOnClass({DruidDataSource.class, SqlSessionFactory.class})
    @ConfigurationProperties(prefix = "hp.soa.gid.leaf.segment")
    @ConditionalOnProperty(name = "hp.soa.gid.leaf.segment.enable", havingValue = "true", matchIfMissing = false)
    public static class LeafSegmentIdProperties extends DruidDataSource
    {
        @Bean(name = LEAF_SEGMENT_ID_DATA_SOURCE_BEAN, initMethod = "init", destroyMethod = "close")
        public DataSource leafSegmentIdDataSource(LeafSegmentIdProperties leafSegmentIdProperties) throws SQLException
        {
            DruidDataSource ds = leafSegmentIdProperties.cloneDruidDataSource();

            ds.init();

            ValidConnectionChecker vcc = ds.getValidConnectionChecker();
            
            if(vcc instanceof MySqlValidConnectionChecker mvcc)
                mvcc.setUsePingMethod(false);
            
            return ds;
        }
        
        @Bean(GlobalIdService.LEAF_SEGMENT_ID_GENERATOR_BEAN)
        @ConditionalOnBean(name = LEAF_SEGMENT_ID_DATA_SOURCE_BEAN, value = DataSource.class)
        public IdGen segmentIdGen(@Qualifier(LEAF_SEGMENT_ID_DATA_SOURCE_BEAN) DataSource dataSource)
        {
            IdAllocDaoImpl dao = new IdAllocDaoImpl(dataSource);
            SegmentIdGenImpl idGen = new SegmentIdGenImpl();
            idGen.setDao(dao);
            
            if(!idGen.init())
                throw new BeanCreationException(GlobalIdService.LEAF_SEGMENT_ID_GENERATOR_BEAN, "Segment Service Init Fail");
            
            return idGen;
        }
    }
    
    @Bean(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN)
    @ConditionalOnProperty(name = "hp.soa.gid.leaf.snowflake.enable", havingValue = "true", matchIfMissing = true)
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
            throw new BeanCreationException(GlobalIdService.LEAF_SNOWFLAKE_ID_GENERATOR_BEAN, "Snowflake Service Init Fail");
        
        return idGen;
    }
    
}
