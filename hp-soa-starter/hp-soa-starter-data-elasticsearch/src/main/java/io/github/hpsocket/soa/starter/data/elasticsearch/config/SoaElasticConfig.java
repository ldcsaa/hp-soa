
package io.github.hpsocket.soa.starter.data.elasticsearch.config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;

import io.github.hpsocket.soa.framework.web.support.OffsetDateTimeProvider;
import io.github.hpsocket.soa.framework.web.support.ZonedDateTimeProvider;

/** <b>HP-SOA Elasticsearch 配置</b> */
@AutoConfiguration
@ConditionalOnExpression("'${spring.elasticsearch.uris:}' != ''")
public class SoaElasticConfig extends ElasticsearchConfigurationSupport
{
    public static final String zonedDateTimeProviderBeanName  = "zonedDateTimeProvider";
    public static final String offsetDateTimeProviderBeanName = "offsetDateTimeProvider";

    /** {@linkplain ZonedDateTime} 时间日期提供者 */
    @Bean(zonedDateTimeProviderBeanName)
    @ConditionalOnMissingBean(name = zonedDateTimeProviderBeanName)
    ZonedDateTimeProvider zonedDateTimeProvider()
    {
        return ZonedDateTimeProvider.INSTANCE;
    }

    /** {@linkplain OffsetDateTime} 时间日期提供者 */
    @Bean(offsetDateTimeProviderBeanName)
    @ConditionalOnMissingBean(name = offsetDateTimeProviderBeanName)
    OffsetDateTimeProvider offsetDateTimeProvider()
    {
        return OffsetDateTimeProvider.INSTANCE;
    }

    /** 自定义日期时间类型转换器<br/>
     * 处理 Java Bean 的日期时间类型字段与 ES Date 类型字段映射。
     */
    @Bean
    @Override
    public ElasticsearchCustomConversions elasticsearchCustomConversions()
    {
        List<Converter<?, ?>> converters = new ArrayList<>();

        // Long/String/Date -> LocalDateTime
        converters.add(LongToLocalDateTimeConverter.INSTANCE);
        converters.add(StringToLocalDateTimeConverter.INSTANCE);
        converters.add(DateToLocalDateTimeConverter.INSTANCE);
        
        // Long/String/Date -> LocalDate        
        converters.add(LongToLocalDateConverter.INSTANCE);
        converters.add(StringToLocalDateConverter.INSTANCE);
        converters.add(DateToLocalDateConverter.INSTANCE);
        
        // Long/String/Date -> ZonedDateTime
        converters.add(LongToZonedDateTimeConverter.INSTANCE);
        converters.add(StringToZonedDateTimeConverter.INSTANCE);
        converters.add(DateToZonedDateTimeConverter.INSTANCE);

        // Long/String/Date -> OffsetDateTime
        converters.add(LongToOffsetDateTimeConverter.INSTANCE);
        converters.add(StringToOffsetDateTimeConverter.INSTANCE);
        converters.add(DateToOffsetDateTimeConverter.INSTANCE);

        // LocalDateTime -> Long/String/Date
        converters.add(LocalDateTimeToLongConverter.INSTANCE);
        converters.add(LocalDateTimeToStringConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);
        
        // LocalDate -> Long/String/Date
        converters.add(LocalDateToLongConverter.INSTANCE);
        converters.add(LocalDateToStringConverter.INSTANCE);
        converters.add(LocalDateToDateConverter.INSTANCE);
        
        // ZonedDateTime -> Long/String/Date
        converters.add(ZonedDateTimeToLongConverter.INSTANCE);
        converters.add(ZonedDateTimeToStringConverter.INSTANCE);
        converters.add(ZonedDateTimeToDateConverter.INSTANCE);

        // OffsetDateTime -> Long/String/Date
        converters.add(OffsetDateTimeToLongConverter.INSTANCE);
        converters.add(OffsetDateTimeToStringConverter.INSTANCE);
        converters.add(OffsetDateTimeToDateConverter.INSTANCE);

        return new ElasticsearchCustomConversions(converters);
    }

    @ReadingConverter
    enum LongToLocalDateTimeConverter implements Converter<Long, LocalDateTime>
    {
        INSTANCE;

        @Override
        public LocalDateTime convert(Long source)
        {
            return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    @WritingConverter
    enum LocalDateTimeToLongConverter implements Converter<LocalDateTime, Long>
    {
        INSTANCE;

        @Override
        public Long convert(LocalDateTime source)
        {
            return source.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }

    @ReadingConverter
    enum StringToLocalDateTimeConverter implements Converter<String, LocalDateTime>
    {
        INSTANCE;

        @Override
        public LocalDateTime convert(String source)
        {
            String t    = source.indexOf('T') > 0 ? "'T'" : " ";
            String mill = source.indexOf('.') > 0 ? ".SSS" : "";

            StringBuilder sb = new StringBuilder(30);
            sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);

            if(source.length() > 19 + mill.length())
                sb.append("z");

            DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());

            return LocalDateTime.parse(source, df);
        }
    }

    @WritingConverter
    enum LocalDateTimeToStringConverter implements Converter<LocalDateTime, String>
    {
        INSTANCE;

        @Override
        public String convert(LocalDateTime source)
        {
            return source.atZone(ZoneId.systemDefault()).toString();
        }
    }

    @WritingConverter
    enum DateToLocalDateTimeConverter implements Converter<Date, LocalDateTime>
    {
        INSTANCE;

        @Override
        public LocalDateTime convert(Date date)
        {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        }
    }

    @WritingConverter
    enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date>
    {
        INSTANCE;

        @Override
        public Date convert(LocalDateTime source)
        {
            return new Date(source.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @WritingConverter
    enum LocalDateToLongConverter implements Converter<LocalDate, Long>
    {
        INSTANCE;

        @Override
        public Long convert(LocalDate source)
        {
            return source.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
    }

    @ReadingConverter
    enum LongToLocalDateConverter implements Converter<Long, LocalDate>
    {
        INSTANCE;

        @Override
        public LocalDate convert(Long source)
        {
            return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    @ReadingConverter
    enum StringToLocalDateConverter implements Converter<String, LocalDate>
    {
        INSTANCE;

        @Override
        public LocalDate convert(String source)
        {
            int length = source.length();

            if(length <= 10)
                return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            String t    = source.indexOf('T') > 0 ? "'T'" : " ";
            String mill = source.indexOf('.') > 0 ? ".SSS" : "";

            StringBuilder sb = new StringBuilder(30);
            sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);

            if(length > 19 + mill.length())
                sb.append("z");

            DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());

            return LocalDate.parse(source, df);
        }
    }

    @WritingConverter
    enum LocalDateToStringConverter implements Converter<LocalDate, String>
    {
        INSTANCE;

        @Override
        public String convert(LocalDate source)
        {
            return source.toString();
        }
    }

    @WritingConverter
    enum DateToLocalDateConverter implements Converter<Date, LocalDate>
    {
        INSTANCE;

        @Override
        public LocalDate convert(Date date)
        {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    @WritingConverter
    enum LocalDateToDateConverter implements Converter<LocalDate, Date>
    {
        INSTANCE;

        @Override
        public Date convert(LocalDate source)
        {
            return new Date(source.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
    }

    @ReadingConverter
    enum LongToZonedDateTimeConverter implements Converter<Long, ZonedDateTime>
    {
        INSTANCE;

        @Override
        public ZonedDateTime convert(Long source)
        {
            return Instant.ofEpochMilli(source).atZone(ZoneId.systemDefault());
        }
    }

    @WritingConverter
    enum ZonedDateTimeToLongConverter implements Converter<ZonedDateTime, Long>
    {
        INSTANCE;

        @Override
        public Long convert(ZonedDateTime source)
        {
            return source.toInstant().toEpochMilli();
        }
    }

    @ReadingConverter
    enum StringToZonedDateTimeConverter implements Converter<String, ZonedDateTime>
    {
        INSTANCE;

        @Override
        public ZonedDateTime convert(String source)
        {
            String t    = source.indexOf('T') > 0 ? "'T'" : " ";
            String mill = source.indexOf('.') > 0 ? ".SSS" : "";

            StringBuilder sb = new StringBuilder(30);
            sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);

            if(source.length() > 19 + mill.length())
                sb.append("z");

            DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());

            return ZonedDateTime.parse(source, df);
        }
    }

    @WritingConverter
    enum ZonedDateTimeToStringConverter implements Converter<ZonedDateTime, String>
    {
        INSTANCE;

        @Override
        public String convert(ZonedDateTime source)
        {
            return source.toString();
        }
    }

    @ReadingConverter
    enum DateToZonedDateTimeConverter implements Converter<Date, ZonedDateTime>
    {
        INSTANCE;

        @Override
        public ZonedDateTime convert(Date date)
        {
            return date.toInstant().atZone(ZoneId.systemDefault());
        }
    }

    @WritingConverter
    enum ZonedDateTimeToDateConverter implements Converter<ZonedDateTime, Date>
    {
        INSTANCE;

        @Override
        public Date convert(ZonedDateTime source)
        {
            return new Date(source.toInstant().toEpochMilli());
        }
    }

    @ReadingConverter
    enum LongToOffsetDateTimeConverter implements Converter<Long, OffsetDateTime>
    {
        INSTANCE;

        @Override
        public OffsetDateTime convert(Long source)
        {
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(source), ZoneId.systemDefault());
        }
    }

    @WritingConverter
    enum OffsetDateTimeToLongConverter implements Converter<OffsetDateTime, Long>
    {
        INSTANCE;

        @Override
        public Long convert(OffsetDateTime source)
        {
            return source.toInstant().toEpochMilli();
        }
    }

    @ReadingConverter
    enum StringToOffsetDateTimeConverter implements Converter<String, OffsetDateTime>
    {
        INSTANCE;

        @Override
        public OffsetDateTime convert(String source)
        {
            String t    = source.indexOf('T') > 0 ? "'T'" : " ";
            String mill = source.indexOf('.') > 0 ? ".SSS" : "";

            StringBuilder sb = new StringBuilder(30);
            sb.append("yyyy-MM-dd").append(t).append("HH:mm:ss").append(mill);

            if(source.length() > 19 + mill.length())
                sb.append("z");

            DateTimeFormatter df = DateTimeFormatter.ofPattern(sb.toString());

            return OffsetDateTime.parse(source, df);
        }
    }

    @WritingConverter
    enum OffsetDateTimeToStringConverter implements Converter<OffsetDateTime, String>
    {
        INSTANCE;

        @Override
        public String convert(OffsetDateTime source)
        {
            return source.toString();
        }
    }

    @ReadingConverter
    enum DateToOffsetDateTimeConverter implements Converter<Date, OffsetDateTime>
    {
        INSTANCE;

        @Override
        public OffsetDateTime convert(Date date)
        {
            return OffsetDateTime.ofInstant(date.toInstant(), ZoneOffset.systemDefault());
        }
    }

    @WritingConverter
    enum OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date>
    {
        INSTANCE;

        @Override
        public Date convert(OffsetDateTime source)
        {
            return new Date(source.toInstant().toEpochMilli());
        }
    }

}
