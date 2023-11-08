
package io.github.hpsocket.soa.starter.data.mongodb.config;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import io.github.hpsocket.soa.framework.web.support.OffsetDateTimeProvider;
import io.github.hpsocket.soa.framework.web.support.ZonedDateTimeProvider;

/** <b>HP-SOA Elasticsearch 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.data.mongodb.uri:}' != '' || '${spring.data.mongodb.host:}' != ''")
public class SoaMongoConfig
{
    public static final String zonedDateTimeProviderBeanName  = "zonedDateTimeProvider";
    public static final String offsetDateTimeProviderBeanName = "offsetDateTimeProvider";
    
    /** 事务管理器（注：单机部署不支持事务） */
    @Bean
    @ConditionalOnMissingBean(MongoTransactionManager.class)
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory)
    {
        return new MongoTransactionManager(factory);
    }

    /** {@linkplain ZonedDateTime} 时间日期提供者 */
    @Bean(zonedDateTimeProviderBeanName)
    @ConditionalOnMissingBean(name = zonedDateTimeProviderBeanName)
    public ZonedDateTimeProvider zonedDateTimeProvider()
    {
        return new ZonedDateTimeProvider();
    }

    /** {@linkplain OffsetDateTime} 时间日期提供者 */
    @Bean(offsetDateTimeProviderBeanName)
    @ConditionalOnMissingBean(name = offsetDateTimeProviderBeanName)
    public OffsetDateTimeProvider offsetDateTimeProvider()
    {
        return new OffsetDateTimeProvider();
    }

    /** 自定义日期时间类型转换器<br/>
     * 处理 Java Bean 的日期时间类型字段与 MongoDB Date 类型字段映射。
     */
    @Bean
    public MongoCustomConversions customConversions()
    {
        List<Converter<?, ?>> converters = new ArrayList<>();
        
        // Date -> ZonedDateTime
        converters.add(DateToZonedDateTimeConverter.INSTANCE);
        // ZonedDateTime -> Date
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);
        
        // Date -> OffsetDateTime
        converters.add(DateToOffsetDateTimeConverter.INSTANCE);
        // OffsetDateTime -> Date
        converters.add(OffsetDateTimeToDateConverter.INSTANCE);
        
        return new MongoCustomConversions(converters);
    }

    @ReadingConverter
    enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime>
    {
        INSTANCE;

        @Override
        public ZonedDateTime convert(Date source)
        {
            return source == null ? null : ZonedDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }

    @WritingConverter
    enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date>
    {
        INSTANCE;

        @Override
        public Date convert(ZonedDateTime source)
        {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

    @ReadingConverter
    enum DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime>
    {
        INSTANCE;

        @Override
        public OffsetDateTime convert(Date source)
        {
            return source == null ? null : OffsetDateTime.ofInstant(source.toInstant(), ZoneId.systemDefault());
        }
    }

    @WritingConverter
    enum OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date>
    {
        INSTANCE;

        @Override
        public Date convert(OffsetDateTime source)
        {
            return source == null ? null : Date.from(source.toInstant());
        }
    }

}
