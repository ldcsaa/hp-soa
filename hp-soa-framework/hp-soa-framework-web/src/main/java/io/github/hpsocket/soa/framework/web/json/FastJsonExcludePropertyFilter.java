package io.github.hpsocket.soa.framework.web.json;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.alibaba.fastjson2.filter.PropertyFilter;
import io.github.hpsocket.soa.framework.web.json.JSONFieldExclude.Exclude;

/** <b>FastJson 属性排除过滤器</b><br>
 * 处理 {@linkplain JSONFieldExclude} 注解，序列化时排除特定属性
 */
public class FastJsonExcludePropertyFilter implements PropertyFilter
{
    @Override
    public boolean apply(Object object, String name, Object value)
    {
        if(object == null)
            return true;
        
        Class<?> clazz = object.getClass();
        
        if( clazz.isPrimitive()                     ||
            clazz.isArray()                         ||
            Map.class.isAssignableFrom(clazz)       ||
            Collection.class.isAssignableFrom(clazz))
            return true;
        
        try
        {
            Field field = clazz.getDeclaredField(name);
            
            JSONFieldExclude annotation = field.getAnnotation(JSONFieldExclude.class);
            
            if(annotation == null)
                return true;

            JSONFieldExclude.Exclude exclude = annotation.value();
            
            if(exclude == Exclude.ALWAYS)
                return false;
            else if(exclude == Exclude.NULL)
                return Objects.nonNull(value);
            else if(exclude == Exclude.ABSENT)
            {
                if(Objects.isNull(value))
                    return false;
                
                if(value instanceof Optional<?> opt)
                    return opt.isPresent();
            }
            else if(exclude == Exclude.EMPTY)
            {
                if(Objects.isNull(value))
                    return false;
                
                if(value instanceof Optional<?> opt)
                    return opt.isPresent();
                else if(value instanceof String str)
                    return !str.isEmpty();
                else if(value instanceof Collection<?> col)
                    return !col.isEmpty();
                else if(value instanceof Map<?, ?> map)
                    return !map.isEmpty();
                else if(clazz.isArray())
                    return Array.getLength(value) > 0;
            }
            else
            {
                throw new IllegalArgumentException("Unexpected value: " + exclude);
            }
        }
        catch(NoSuchFieldException | SecurityException e)
        {
            
        }
        
        return true;
    }

}
