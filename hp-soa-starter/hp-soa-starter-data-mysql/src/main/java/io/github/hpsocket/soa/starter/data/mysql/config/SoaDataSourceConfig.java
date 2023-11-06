
package io.github.hpsocket.soa.starter.data.mysql.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.baomidou.dynamic.datasource.provider.DynamicDataSourceProvider;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceProperties;
import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourcePropertiesCustomizer;

/** <b>HP-SOA 数据源配置</b><br>
 * 当 <i>${hp.soa.data.global-transaction-management.enabled}</i> 为 true 时，启用全局事务配置
 */
@AutoConfiguration
@EnableTransactionManagement
@AutoConfigureBefore(DynamicDataSourceAutoConfiguration.class)
public class SoaDataSourceConfig
{
    public static final String dynamicRoutingDataSourceBeanName = "dynamicRoutingDataSource";
    public static final String dynamicRoutingTransactionManagerBeanName = "dynamicRoutingTransactionManager";
    
    /** 默认动态数据源 */
    @Primary
    @Bean(dynamicRoutingDataSourceBeanName)
    @ConditionalOnMissingBean(name = dynamicRoutingDataSourceBeanName)
    public DataSource dynamicRoutingDataSource(
        DynamicDataSourceProperties properties,
        ObjectProvider<List<DynamicDataSourcePropertiesCustomizer>> dataSourcePropertiesCustomizers,
        List<DynamicDataSourceProvider> providers)
    {
        return new DynamicDataSourceAutoConfiguration(properties, dataSourcePropertiesCustomizers).dataSource(providers);
    }
    
    /** 默认动态数据源事务管理器 */
    @Primary
    @Bean(dynamicRoutingTransactionManagerBeanName)
    @ConditionalOnMissingBean(name = dynamicRoutingTransactionManagerBeanName)
    PlatformTransactionManager dynamicRoutingTransactionManager(@Qualifier(dynamicRoutingDataSourceBeanName) DataSource dataSource)
    {
        return new JdbcTransactionManager(dataSource);
    }

}
