package io.github.hpsocket.soa.framework.web.json;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

/** <b>FastJson 时间日期解析器基类</b><br>
 * 用于设置 {@linkplain com.alibaba.fastjson2.annotation.JSONField#deserializeUsing() JSONField#deserializeUsing()} 注解属性
 */
public abstract class GenericDateTimeReader<T> implements ObjectReader<T>
{
    private static final Pattern DT_23 = Pattern.compile("^\\d{4}-\\d(\\d)?-\\d(\\d)?[T ]\\d(\\d)?:\\d(\\d)?:\\d(\\d)?\\.(\\d){3}$");
    private static final Pattern DT_19 = Pattern.compile("^\\d{4}-\\d(\\d)?-\\d(\\d)?[T ]\\d(\\d)?:\\d(\\d)?:\\d(\\d)?$");
    private static final Pattern DT_16 = Pattern.compile("^\\d{4}-\\d(\\d)?-\\d(\\d)?[T ]\\d(\\d)?:\\d(\\d)?$");
    private static final Pattern D_10  = Pattern.compile("^\\d{4}-\\d(\\d)?-\\d(\\d)?$");
    private static final Pattern D_7   = Pattern.compile("^\\d{4}-\\d(\\d)?$");
    private static final Pattern T_12  = Pattern.compile("^\\d(\\d)?:\\d(\\d)?:\\d(\\d)?\\.(\\d){3}$");
    private static final Pattern T_8   = Pattern.compile("^\\d(\\d)?:\\d(\\d)?:\\d(\\d)?$");
    private static final Pattern T_5   = Pattern.compile("^\\d(\\d)?:\\d(\\d)?$");
    
    protected abstract Function<LocalDateTime, T> getConverter();

    @Override
    public T readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features)
    {
        var str = jsonReader.readString();
        
        if(StringUtils.isEmpty(str))
            return null;
        
        var index1 = str.indexOf('-');
        var index2 = str.indexOf(':');
        var format = (String)null;

        if(index1 > 0 && index2 > 0)
        {
            var sep = (str.indexOf('T') > 0) ? "'T'" : " ";
            
            if(DT_23.matcher(str).matches())
                format = "y-M-d" + sep + "H:m:s.SSS";
            else if(DT_19.matcher(str).matches())
                format = "y-M-d" + sep + "H:m:s";
            else if(DT_16.matcher(str).matches())
                format = "y-M-d" + sep + "H:m";

            if(format != null)
                return getConverter().apply(LocalDateTime.parse(str, DateTimeFormatter.ofPattern(format)));
        }
        else if(index1 > 0)
        {
            if(D_10.matcher(str).matches())
                format = "y-M-d";
            else if(D_7.matcher(str).matches())
            {
                format = "y-M-d";
                str   += "-01";
            }
            
            if(format != null)
                return getConverter().apply(LocalDate.parse(str, DateTimeFormatter.ofPattern(format)).atStartOfDay());
        }
        else if(index2 > 0)
        {
            if(T_12.matcher(str).matches())
                format = "H:m:s.SSS";
            else if(T_8.matcher(str).matches())
                format = "H:m:s";
            else if(T_5.matcher(str).matches())
                format = "H:m";

            if(format != null)
                return getConverter().apply(LocalTime.parse(str, DateTimeFormatter.ofPattern(format)).atDate(LocalDate.EPOCH));
        }
        else
        {
            var timestamp = GeneralHelper.str2Long(str);
            
            if(timestamp != null)
            {
                if(str.length() > 10)
                    return getConverter().apply(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()));
                else
                    return getConverter().apply(LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()));
            }
        }

        return null;
    }

}
