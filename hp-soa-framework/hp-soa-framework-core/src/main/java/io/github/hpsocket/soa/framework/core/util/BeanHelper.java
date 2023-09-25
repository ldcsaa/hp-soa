package io.github.hpsocket.soa.framework.core.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;

/** Java Bean 帮助类，执行 Java Bean 反射和内省等相关操作 */
public class BeanHelper
{
    /** 原生数据类型集合 */
    public static final Set<Class<?>> NATIVE_CLASS_SET    = new HashSet<Class<?>>(8);
    /** 简单数据类型集合 */
    public static final Set<Class<?>> SMIPLE_CLASS_SET    = new HashSet<Class<?>>(18);
    /** 基本类型包装类集合 */
    public static final Set<Class<?>> WRAPPER_CLASS_SET    = new HashSet<Class<?>>(8);

    private static final String STRING_DELIMITERS        = " ,;|\t\n\r\f";
    private static final char ATTR_SEP_CHAR                = '.';
    
    static
    {
        NATIVE_CLASS_SET.add(int.class);
        NATIVE_CLASS_SET.add(long.class);
        NATIVE_CLASS_SET.add(float.class);
        NATIVE_CLASS_SET.add(double.class);
        NATIVE_CLASS_SET.add(byte.class);
        NATIVE_CLASS_SET.add(char.class);
        NATIVE_CLASS_SET.add(short.class);
        NATIVE_CLASS_SET.add(boolean.class);

        SMIPLE_CLASS_SET.add(int.class);
        SMIPLE_CLASS_SET.add(long.class);
        SMIPLE_CLASS_SET.add(float.class);
        SMIPLE_CLASS_SET.add(double.class);
        SMIPLE_CLASS_SET.add(byte.class);
        SMIPLE_CLASS_SET.add(char.class);
        SMIPLE_CLASS_SET.add(short.class);
        SMIPLE_CLASS_SET.add(boolean.class);
        SMIPLE_CLASS_SET.add(Integer.class);
        SMIPLE_CLASS_SET.add(Long.class);
        SMIPLE_CLASS_SET.add(Float.class);
        SMIPLE_CLASS_SET.add(Double.class);
        SMIPLE_CLASS_SET.add(Byte.class);
        SMIPLE_CLASS_SET.add(Character.class);
        SMIPLE_CLASS_SET.add(Short.class);
        SMIPLE_CLASS_SET.add(Boolean.class);
        SMIPLE_CLASS_SET.add(String.class);
        SMIPLE_CLASS_SET.add(Date.class);
        
        WRAPPER_CLASS_SET.add(Integer.class);
        WRAPPER_CLASS_SET.add(Long.class);
        WRAPPER_CLASS_SET.add(Float.class);
        WRAPPER_CLASS_SET.add(Double.class);
        WRAPPER_CLASS_SET.add(Byte.class);
        WRAPPER_CLASS_SET.add(Character.class);
        WRAPPER_CLASS_SET.add(Short.class);
        WRAPPER_CLASS_SET.add(Boolean.class);
    }

    /** 检查是否为非抽象公共实例方法 */
    public static final boolean isInstanceField(Field field)
    {
        int flag = field.getModifiers();
        return (!Modifier.isStatic(flag));
    }
    
    /** 检查是否为非抽象公共实例方法 */
    public static final boolean isInstanceNotFinalField(Field field)
    {
        int flag = field.getModifiers();
        return (!Modifier.isStatic(flag) && !Modifier.isFinal(flag));
    }
    
    /** 检查是否为非抽象公共实例方法 */
    public static final boolean isPublicInstanceMethod(Method method)
    {
        int flag = method.getModifiers();
        return (!Modifier.isStatic(flag) && !Modifier.isAbstract(flag) && Modifier.isPublic(flag));
    }
    
    /** 检查是否为公共接口 */
    public static final boolean isPublicInterface(Class<?> clazz)
    {
        int flag = clazz.getModifiers();
        return (Modifier.isInterface(flag) && Modifier.isPublic(flag));
    }

    /** 检查是否为公共类 */
    public static final boolean isPublicClass(Class<?> clazz)
    {
        int flag = clazz.getModifiers();
        return (!Modifier.isInterface(flag) && Modifier.isPublic(flag));
    }

    /** 检查是否为非接口非抽象公共类 */
    public static final boolean isPublicNotAbstractClass(Class<?> clazz)
    {
        int flag = clazz.getModifiers();
        return (!Modifier.isInterface(flag) && !Modifier.isAbstract(flag) && Modifier.isPublic(flag));
    }

    /** 检查 clazz 是否为原生数据类型 */
    public final static boolean isNativeType(Class<?> clazz)
    {
        return NATIVE_CLASS_SET.contains(clazz);
    }

    /** 检查 clazz 是否为简单数据类型 */
    public final static boolean isSimpleType(Class<?> clazz)
    {
        return SMIPLE_CLASS_SET.contains(clazz);
    }

    /** 检查 clazz 是否为基础类型包装类 */
    public final static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_CLASS_SET.contains(clazz);
    }

    /** 检查包装类和基础类型是否匹配 */
    public final static boolean isWrapperAndPrimitiveMatch(Class<?> wrapperClazz, Class<?> primitiveClass)
    {
        if(!primitiveClass.isPrimitive())    return false;
        if(!isWrapperType(wrapperClazz))    return false;
        
        try
        {
            Field f = wrapperClazz.getDeclaredField("TYPE");
            return f.get(null) == primitiveClass;
        }
        catch(Exception e)
        {
            
        }
        
        return false;
    }
    
    /** 检查源类型是否兼容目标类型 */
    public static final boolean isCompatibleType(Class<?> srcClazz,Class<?> destClazz)
    {
        return    (
                    destClazz.isAssignableFrom(srcClazz)            ||
                    isWrapperAndPrimitiveMatch(destClazz, srcClazz)    ||
                    isWrapperAndPrimitiveMatch(srcClazz, destClazz)
                );
    }
    
    /** 检查源数组的元素类型是否兼容目标数组的元素类型 */
    public static final boolean isCompatibleArray(Class<?> srcClazz, Class<?> destClazz)
    {
        if(srcClazz.isArray() && destClazz.isArray())
        {
            Class<?> srcComponentType = srcClazz.getComponentType();
            Class<?> destComponentType = destClazz.getComponentType();
        
            return    isCompatibleType(srcComponentType, destComponentType);
        }
        
        return false;
    }
    
    /** 检查属性是否可以联级装配 */
    public static final boolean isCascadableProperty(PropertyDescriptor pd)
    {
        return (pd != null && getPropertyWriteMethod(pd) != null && isCascadable(pd.getPropertyType()));
    }
    
    /** 检查成员变量是否可以联级装配 */
    public static final boolean isCascadableField(Field f)
    {
        return (f != null && isInstanceNotFinalField(f) && isCascadable(f.getType()));
    }
    
    /** 检查类是否可以联级装配 */
    public static final boolean isCascadable(Class<?> clazz)
    {
        return    isPublicNotAbstractClass(clazz)                &&
                !isSimpleType(clazz)                        &&
                !clazz.isArray()                            &&
                !Collection.class.isAssignableFrom(clazz)    &&
                !Map.class.isAssignableFrom(clazz)            ;
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性或成员变量
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param properties    : 属性或成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性或成员变量名称一致<br>
     *                        属性或成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性或成员变量的实际类型：直接对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性或成员变量赋值<br> 
     *  @return                : 生成的 Bean 实例
     */
    public static final <B, T> B createBean(Class<B> clazz, Map<String, T> properties)
    {
        return createBean(clazz, properties, null);
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性或成员变量
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param valueMap        : 属性或成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性或成员变量名称可能一直也可能不一致<br>
     *                        属性或成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性或成员变量赋值<br>
     *  @param keyMap        : properties.key / Bean 属性或成员变量名映射，当 properties 的 key 与属性或成员变量名不对应时，
     *                        用 keyMap 把它们关联起来
     *  @return                  生成的 Bean 实例  
     */
    public static final <B, T> B createBean(Class<B> clazz, Map<String, T> valueMap, Map<String, String> keyMap)
    {
        B bean = null;
        
        try
        {
            bean = clazz.getDeclaredConstructor().newInstance();
            setPropertiesOrFieldValues(bean, valueMap, keyMap);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return bean;
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param properties    : 属性名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性名称一致<br>
     *                        属性值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br> 
     *  @return                : 生成的 Bean 实例
     */
    public static final <B, T> B createBeanByProperties(Class<B> clazz, Map<String, T> properties)
    {
        return createBeanByProperties(clazz, properties, null);
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param properties    : 属性名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性名称可能一直也可能不一致<br>
     *                        属性值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br>
     *  @param keyMap        : properties.key / Bean 属性名映射，当 properties 的 key 与属性名不对应时，
     *                        用 keyMap 把它们关联起来
     *  @return                  生成的 Bean 实例  
     */
    public static final <B, T> B createBeanByProperties(Class<B> clazz, Map<String, T> properties, Map<String, String> keyMap)
    {
        B bean = null;
        
        try
        {
            bean = clazz.getDeclaredConstructor().newInstance();
            setProperties(bean, properties, keyMap);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return bean;
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param values        : 成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与成员变量名称可能一直也可能不一致<br>
     *                        成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对成员变量值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对成员变量值<br>
     *  @return                  生成的 Bean 实例  
     */
    public static final <B, T> B createBeanByFieldValues(Class<B> clazz, Map<String, T> values)
    {
        return createBeanByFieldValues(clazz, values, null);
    }
    
    /** 创建指定类型的 Java Bean，并设置相关属性
     * 
     *  @param clazz        : Bean 类型<br>
     *  @param values        : 成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与成员变量名称可能一直也可能不一致<br>
     *                        成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对成员变量值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对成员变量值<br>
     *  @param keyMap        : values.key / Bean 成员变量名映射，当 values 的 key 与成员变量名不对应时，
     *                        用 keyMap 把它们关联起来
     *  @return                  生成的 Bean 实例  
     */
    public static final <B, T> B createBeanByFieldValues(Class<B> clazz, Map<String, T> values, Map<String, String> keyMap)
    {
        B bean = null;
        
        try
        {
            bean = clazz.getDeclaredConstructor().newInstance();
            setFieldValues(bean, values, keyMap);
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
        
        return bean;
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param properties    : 属性名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性名称一致<br>
     *                        属性值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br> 
     */
    public static final <T> void setProperties(Object bean, Map<String, T> properties)
    {
        setProperties(bean, properties, null);
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param properties    : 属性名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性名称可能一直也可能不一致<br>
     *                        属性值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br>
     *  @param keyMap        : properties.key / Bean 属性名映射，当 properties 的 key 与属性名不对应时，
     *                        用 keyMap 把它们关联起来  
     */
    public static final <T> void setProperties(Object bean, Map<String, T> properties, Map<String, String> keyMap)
    {
        if(properties == null || properties.isEmpty())
            return;
        
        Map<Object, Map<String, T>> subs    = new HashMap<Object, Map<String, T>>();
        Map<String, PropertyDescriptor> pps = getPropDescMap(bean.getClass());
        Map<String, T> params                = translateKVMap(properties, keyMap);

        parseCascadeProperties(bean, subs, pps, params, null);
        
        if(!subs.isEmpty())
        {
            Set<Map.Entry<Object, Map<String, T>>> sset = subs.entrySet();
            for(Map.Entry<Object, Map<String, T>> e : sset)
            {
                try
                {
                    PropertyDescriptor key    = (PropertyDescriptor)e.getKey();
                    Object o                = key.getPropertyType().getDeclaredConstructor().newInstance();
                    
                    setProperties(o, e.getValue());
                    setProperty(bean, key, o);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param values        : 成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与成员变量名称可能一直也可能不一致<br>
     *                        成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再成员变量赋值<br>
     */
    public static final <T> void setFieldValues(Object bean, Map<String, T> values)
    {
        setFieldValues(bean, values, null);
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param values        : 成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与成员变量名称可能一直也可能不一致<br>
     *                        成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再成员变量赋值<br>
     *  @param keyMap        : properties.key / Bean 成员变量名映射，当 properties 的 key 与成员变量名不对应时，
     *                        用 keyMap 把它们关联起来  
     */
    public static final <T> void setFieldValues(Object bean, Map<String, T> values, Map<String, String> keyMap)
    {
        if(values == null || values.isEmpty())
            return;
        
        Map<Object, Map<String, T>> subs = new HashMap<Object, Map<String, T>>();
        Map<String, Field> fms            = getInstanceFieldMap(bean.getClass());
        Map<String, T> params            = translateKVMap(values, keyMap);
        
        parseCascadeFields(bean, subs, fms, params);
        
        if(!subs.isEmpty())
        {
            Set<Map.Entry<Object, Map<String, T>>> sset = subs.entrySet();
            for(Map.Entry<Object, Map<String, T>> e : sset)
            {
                try
                {
                    Field key    = (Field)e.getKey();
                    Object o    = key.getType().getDeclaredConstructor().newInstance();
                    
                    setFieldValues(o, e.getValue());
                    setFieldValue(bean, key, o);
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param valueMap        : 属性或成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性或成员变量名称可能一直也可能不一致<br>
     *                        属性或成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性或成员变量的实际类型：直接对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属或成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋或成员变量值<br>
     */
    public static final <T> void setPropertiesOrFieldValues(Object bean, Map<String, T> valueMap)
    {
        setPropertiesOrFieldValues(bean, valueMap, null);
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean            : Bean 实例<br>
     *  @param valueMap        : 属性或成员变量名 / 值映射<br>
     *                        其中名称为 {@link String} 类型，与属性或成员变量名称可能一直也可能不一致<br>
     *                        属性或成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性或成员变量的实际类型：直接对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属或成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属赋或成员变量值<br>
     *  @param keyMap        : properties.key / Bean 属性或成员变量名映射，当 properties 的 key 与属性或成员变量名不对应时，
     *                        用 keyMap 把它们关联起来  
     */
    public static final <T> void setPropertiesOrFieldValues(Object bean, Map<String, T> valueMap, Map<String, String> keyMap)
    {
        if(valueMap == null || valueMap.isEmpty())
            return;
        
        Map<Object, Map<String, T>> subs    = new HashMap<Object, Map<String, T>>();
        Map<String, PropertyDescriptor> pps = getPropDescMap(bean.getClass());
        Map<String, T> params                = translateKVMap(valueMap, keyMap);
        Map<String, T> failParams            = new HashMap<String, T>();
        
        parseCascadeProperties(bean, subs, pps, params, failParams);
        
        if(!failParams.isEmpty())
        {
            Map<String, Field> fms = getInstanceFieldMap(bean.getClass());
            parseCascadeFields(bean, subs, fms, failParams);
        }
        
        if(!subs.isEmpty())
        {
            Set<Map.Entry<Object, Map<String, T>>> sset = subs.entrySet();
            for(Map.Entry<Object, Map<String, T>> e : sset)
            {
                Object key             = e.getKey();
                Map<String, T> value = e.getValue();
                
                try
                {
                    if(key instanceof PropertyDescriptor)
                    {
                        PropertyDescriptor pd    = (PropertyDescriptor)key; 
                        Object o                = pd.getPropertyType().getDeclaredConstructor().newInstance();
                        
                        setPropertiesOrFieldValues(o, value);
                        setProperty(bean, pd, o);
                    }
                    else
                    {
                        Field f        = (Field)key;
                        Object o    = f.getType().getDeclaredConstructor().newInstance();
                        
                        setPropertiesOrFieldValues(o, value);
                        setFieldValue(bean, f, o);                        
                    }
                }
                catch(Exception ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private static <T> void parseCascadeProperties(Object bean, Map<Object, Map<String, T>> subs, Map<String, PropertyDescriptor> pps, Map<String, T> params, Map<String, T> failParams)
    {
        Set<Map.Entry<String, T>> set = params.entrySet();
        
        for(Map.Entry<String, T> e : set)
        {
            String key    = e.getKey();
            T value        = e.getValue();
            int index    = key.indexOf(ATTR_SEP_CHAR);
        
            if(index == -1)
            {
                PropertyDescriptor pd = pps.get(key);
                
                if(getPropertyWriteMethod(pd) != null)
                    setProperty(bean, pd, value);
                else if(failParams != null)
                    failParams.put(key, value);
            }
            else
            {
                String skey                = key.substring(0, index);
                PropertyDescriptor pd    = pps.get(skey);
                
                if(getPropertyWriteMethod(pd) == null)
                {
                    if(failParams != null)
                        failParams.put(key, value);
                }
                else if(isCascadableProperty(pd))
                {
                    if(!subs.containsKey(pd))
                        subs.put(pd, new HashMap<String, T>());
                    
                    subs.get(pd).put(key.substring(index + 1), value);
                }
            }
        }
    }

    private static <T> void parseCascadeFields(Object bean, Map<Object, Map<String, T>> subs, Map<String, Field> fms, Map<String, T> params)
    {
        Set<Map.Entry<String, T>> set = params.entrySet();
        
        for(Map.Entry<String, T> e : set)
        {
            String key    = e.getKey();
            T value        = e.getValue();
            int index    = key.indexOf(ATTR_SEP_CHAR);
            
            if(index == -1)
            {
                Field f = fms.get(key);
                setFieldValue(bean, f, value);
            }
            else
            {
                String skey    = key.substring(0, index);
                Field f        = fms.get(skey);
                
                if(isCascadableField(f))
                {
                    if(!subs.containsKey(f))
                        subs.put(f, new HashMap<String, T>());
                    
                    subs.get(f).put(key.substring(index + 1), value);
                }
            }
        }
    }

    private static final <T> Map<String, T> translateKVMap(Map<String, T> valueMap, Map<String, String> keyMap)
    {
        if(keyMap == null || keyMap.isEmpty())
            return valueMap;
        
        Map<String, T> resultMap        = new HashMap<String, T>();
        Set<Map.Entry<String, T>> set    = valueMap.entrySet();
        
        for(Map.Entry<String, T> e : set)
        {
            String key    = e.getKey();
            String name    = key;
        
            if(keyMap.containsKey(key))
                name = keyMap.get(key);

            resultMap.put(name, e.getValue());
        }
        
        return resultMap;
    }

    /** 设置 Java Bean 的属性
     * 
     *  @param bean        : Bean 实例<br>
     *  @param pd        : 属性描述符<br>
     *  @param value    : 属性值，可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br>  
     */
    public static final <T> boolean setProperty(Object bean, PropertyDescriptor pd, T value)
    {
        if(pd != null)
        {
            Method method = getPropertyWriteMethod(pd);
            
            if(method != null)
            {
                Type genericType = null;
                Class<?> clazz     = pd.getPropertyType();
            
                if    (
                        Collection.class.isAssignableFrom(clazz)    &&
                        value != null                                && 
                        !clazz.isAssignableFrom(value.getClass())
                    )
                {
                    Type[] types = method.getGenericParameterTypes();
                    
                    if(types.length > 0)
                        genericType = types[0];
                }
                
                Result<Boolean, Object> result = parseValue(value, clazz, genericType);
    
                if(result.getFlag())
                {
                    invokeMethod(bean, method, result.getValue());
                    return true;
                }
            }
        }

        return false;
    }
    
    /** 设置 Java Bean 的属性
     * 
     *  @param bean        : Bean 实例<br>
     *  @param field    : 成员变量 {@link Field} 对象<br>
     *  @param value    : 成员变量值，可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对成员变量值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再成员变量赋值<br>  
     */
    public static final <T> boolean setFieldValue(Object bean, Field field, T value)
    {
        if(field != null && isInstanceNotFinalField(field))
        {
            Class<?> clazz     = field.getType();
            Type genericType = field.getGenericType();
            
            Result<Boolean, Object> result = parseValue(value, clazz, genericType);
            
            if(result.getFlag())
            {
                invokeSetFieldValue(bean, field, result.getValue());
                return true;
            }
        }
        
        return false;
    }

    /** 把对象转换为目标类型
     * 
     *  @param value        : 待转换的参数，可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 参数的类型与目标类型兼容：不作转换<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：尝试执行自动类型转换<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：尝试执行自动类型转换<br>  
     *  @param clazz        : 目标类型<br>
     *  @param genericType    : 目标类型的元素类型 (只当目标类型是 {@link List} 或 {@link Set} 等集合类型时才需要)<br>
     */
    @SuppressWarnings("unchecked")
    public static final <T> Result<Boolean, Object> parseValue(T value, Class<?> clazz, Type genericType)
    {
        Result<Boolean, Object> result = Result.initialBoolean();
        
        if(value == null)
        {
            if(clazz.isPrimitive())
                result.set(Boolean.TRUE, GeneralHelper.str2Object(clazz, (String)value));
            else
                result.set(Boolean.TRUE, value);
        }
        else if(clazz.isAssignableFrom(value.getClass()))
            result.set(Boolean.TRUE, value);
        else if(isWrapperAndPrimitiveMatch(value.getClass(), clazz))
            result.set(Boolean.TRUE, value);
        else if(isCompatibleArray(value.getClass(), clazz))
            getArrayValue(value, clazz.getComponentType(), result);
        else if(Collection.class.isAssignableFrom(clazz))
            getCollectionValue(value, (Class<? extends Collection<?>>)clazz, genericType, result);
        else
            getSimpleValue(value, clazz, result);
        
        return result;
    }
    
    private static final <T> void getArrayValue( T value, Class<?> clazz, Result<Boolean, Object> result)
    {
        int length = Array.getLength(value);
        Object array = Array.newInstance(clazz, length);
        
        System.arraycopy(value, 0, array, 0, length);
        
        result.set(Boolean.TRUE, array);
    }

    private static final <T> void getCollectionValue(T value, Class<? extends Collection<?>> colClazz, Type genericType, Result<Boolean, Object> result)
    {
        if(genericType instanceof ParameterizedType)
        {
            Class<?> paramClazz = (Class<?>)(((ParameterizedType)genericType).getActualTypeArguments()[0]);
            getCollectionValue(value, colClazz, paramClazz, result);
        }
    }

    private static final <T> void getCollectionValue(T value, Class<? extends Collection<?>> clazz, Class<?> paramClazz, Result<Boolean, Object> result)
    {
        Class<?> valueType        = value.getClass();
        Class<?> valueComType    = valueType.getComponentType();
        
        if    (
                isSimpleType(paramClazz)                    &&
                (    
                    (valueType.equals(String.class))        || 
                    (valueType.isArray() && valueComType.equals(String.class))
                )

            )
        {
            Collection<?> col = parseCollectionParameter(clazz, paramClazz, value);
            result.set(Boolean.TRUE, col);
        }
    }
    
    private static final <T> void getSimpleValue(T value, Class<?> clazz, Result<Boolean, Object> result)
    {
        Class<?> valueType        = value.getClass();
        Class<?> valueComType    = valueType.getComponentType();
        Class<?> clazzComType    = clazz.getComponentType();

        if    (
                (    
                    (valueType.equals(String.class))        || 
                    (valueType.isArray() && valueComType.equals(String.class))
                )
                &&
                (    
                    (isSimpleType(clazz))        || 
                    (clazz.isArray() && isSimpleType(clazzComType))
                )
            )
        {
            Object param = parseParameter(clazz, value);
            result.set(Boolean.TRUE, param);
        }
    }
    
    private static final <T> Collection<?> parseCollectionParameter(Class<? extends Collection<?>> clazz, Class<?> paramClazz, T obj)
    {
        Collection<Object> col = getRealCollectionClass(clazz);
        
        if(col != null)
        {
            Class<?> valueType    = obj.getClass();
            String[] value        = null;
            
            if(valueType.isArray())
                value    = (String[])obj;
            else
            {
                String str    = (String)obj;
                StringTokenizer st = new StringTokenizer(str, STRING_DELIMITERS);
                value    = new String[st.countTokens()];

                for(int i = 0; st.hasMoreTokens(); i++)
                    value[i] = st.nextToken();
            }

            for(int i = 0; i < value.length; i++)
            {
                String v = value[i];
                Object p = GeneralHelper.str2Object(paramClazz, v);
                col.add(p);
            }
        }
        
        return col;
    }

    @SuppressWarnings("unchecked")
    public static final Collection<Object> getRealCollectionClass(Class<? extends Collection<?>> clazz)
    {
        Class<?> realClazz = null;
        
        if(isPublicNotAbstractClass(clazz))
            realClazz = clazz;
        else if(SortedSet.class.isAssignableFrom(clazz))
            realClazz = TreeSet.class;
        else if(Set.class.isAssignableFrom(clazz))
            realClazz = HashSet.class;
        else if(Collection.class.isAssignableFrom(clazz))
            realClazz = ArrayList.class;
        
        if(realClazz == null)
            throw new RuntimeException(clazz.getName() + "is not Collection class");

        try
        {
            return (Collection<Object>)realClazz.getDeclaredConstructor().newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static final Map<Object, Object> getRealMapClass(Class<? extends Map<?, ?>> clazz)
    {
        Class<?> realClazz = null;
        
        if(isPublicNotAbstractClass(clazz))
            realClazz = clazz;
        else if(SortedMap.class.isAssignableFrom(clazz))
            realClazz = TreeMap.class;
        else if(Map.class.isAssignableFrom(clazz))
            realClazz = HashMap.class;
        
        if(realClazz == null)
            throw new RuntimeException(clazz.getName() + "is not Map class");

        try
        {
            return (Map<Object, Object>)realClazz.getDeclaredConstructor().newInstance();
        }
        catch(Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 通过反射机制调用方法，失败则抛出异常 */
    @SuppressWarnings("unchecked")
    public static final <T> T invokeMethod(Object bean, Method method, Object ... param)
    {
        try
        {
            method.setAccessible(true);
            return (T)method.invoke(bean, param);
        }
        catch(Exception e)
        {
            if(e instanceof InvocationTargetException)
            {
                Exception cause = (Exception)e.getCause();
                if(cause != null) e = cause;
            }
            
            throw new RuntimeException(e);
        }
    }
    
    /** 通过反射机制调用方法，执行结果由 {@link InternalTestResult} 标识，不抛出异常 */
    public static final <T> Result<Boolean, T> tryInvokeMethod(Object bean, Method method, Object ... param)
    {
        Result<Boolean, T> result = Result.initialBoolean();
        
        try
        {
            T value = invokeMethod(bean, method, param);
            result.set(Boolean.TRUE, value);
        }
        catch (Exception e)
        {
        }
        
        return result;
    }
    
    /** 通过反射机制获取成员变量值，失败则抛出异常 */
    @SuppressWarnings("unchecked")
    public static final <T> T invokeGetFieldValue(Object bean, Field field)
    {    
        try
        {
            field.setAccessible(true);
            return (T)field.get(bean);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /** 通过反射机制获取成员变量值，执行结果由 {@link InternalTestResult} 标识，不抛出异常 */
    public static final <T> Result<Boolean, T> tryInvokeGetFieldValue(Object bean, Field field)
    {
        Result<Boolean, T> result = Result.initialBoolean();
        
        try
        {
            T value = invokeGetFieldValue(bean, field);
            result.set(Boolean.TRUE, value);
        }
        catch (Exception e)
        {
        }
        
        return result;
    }

    /** 通过反射机制设置成员变量值，失败则抛出异常 */
    public static final void invokeSetFieldValue(Object bean, Field field, Object value)
    {
        try
        {
            field.setAccessible(true);
            field.set(bean, value);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }        
    }

    /** 通过反射机制设置成员变量值，失败则返回 false，不抛出异常 */
    public static final boolean tryInvokeSetFieldValue(Object bean, Field field, Object value)
    {
        boolean isOK = true;
        
        try
        {
            invokeSetFieldValue(bean, field, value);
        }
        catch (Exception e)
        {
            isOK = false;
        }
        
        return isOK;
    }

    private static final <T> Object parseParameter(Class<?> clazz, T obj)
    {
        Object param        = null;
        Class<?> valueType    = obj.getClass();
        
        if(clazz.isArray())
        {
            String[] value = null;
            
            if(valueType.isArray())
                value    = (String[])obj;
            else
            {
                String str    = (String)obj;
                StringTokenizer st = new StringTokenizer(str, STRING_DELIMITERS);
                value    = new String[st.countTokens()];

                for(int i = 0; st.hasMoreTokens(); i++)
                    value[i] = st.nextToken();
            }
            
            int length        = value.length;
            Class<?> type    = clazz.getComponentType();
            param            = Array.newInstance(type, length);

            for(int i = 0; i < length; i++)
            {
                String v = value[i];
                Object p = GeneralHelper.str2Object(type, v);
                Array.set(param, i, p);
            }
        }
        else
        {
            String value = null;
            
            if(valueType.isArray())
            {
                String[] array    = (String[])obj;
                if(array.length > 0)
                    value = array[0];
            }
            else
                value = (String)obj;
            
            param = GeneralHelper.str2Object(clazz, value);
        }
        
        return param;
    }
    
    /** 获取指定类型 Java Bean 的所有属性描述（包括 Object 类除外的所有父类定义的属性）
     * 
     *  @param startClass    : Bean 类型
     *  @return                  属性名 / 描述对象映射  
     */
    public static final Map<String, PropertyDescriptor> getPropDescMap(Class<?> startClass)
    {
        return getPropDescMap(startClass, Object.class);
    }
    
    /** 获取指定类型 Java Bean 的所有属性描述（包括 stopClass 及更高层父类除外的所有父类定义的属性）
     * 
     *  @param startClass    : Bean 类型
     *  @param stopClass    : 截止查找的父类类型
     *  @return                  属性名 / 描述对象映射  
     */
    public static final Map<String, PropertyDescriptor> getPropDescMap(Class<?> startClass, Class<?> stopClass)
    {
        Map<String, PropertyDescriptor> map = new HashMap<String, PropertyDescriptor>();
        
        try
        {
            BeanInfo info = Introspector.getBeanInfo(startClass, stopClass);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            
            for(PropertyDescriptor pd : pds)
                map.put(pd.getName(), pd);
        }
        catch(IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
        
        return map;
    }
    
    /** 获取 Java Bean 的属性
     * 
     *  @param bean    : Bean 实例
     *  @return        : Bean 属性名 / 值映射  
     */
    public static final Map<String, Object> getProperties(Object bean)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, PropertyDescriptor> pps = getPropDescMap(bean.getClass());
        Set<Map.Entry<String, PropertyDescriptor>> set = pps.entrySet();
        
        for(Map.Entry<String, PropertyDescriptor> o : set)
        {
            String key = o.getKey();
            PropertyDescriptor pd = o.getValue();
            Method method = getPropertyReadMethod(pd);
            
            if(method != null)
            {
                Object obj = invokeMethod(bean, method);
                result.put(key, obj);
            }
        }
        
        return result;
    }

    /** 获取指定类型 Java Bean 的名称为 name 的属性描述对象
     * 
     *  @param startClass    : Bean 类型
     *  @param name            : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final PropertyDescriptor getPropDescByName(Class<?> startClass, String name)
    {
        return getPropDescByName(startClass, Object.class, name);
    }
    
    /** 获取指定类型 Java Bean 的名称为 name 的属性描述对象
     * 
     *  @param startClass    : Bean 类型
     *  @param stopClass    : 截止查找的父类类型
     *  @param name            : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final PropertyDescriptor getPropDescByName(Class<?> startClass, Class<?> stopClass, String name)
    {
        try
        {
            BeanInfo info = Introspector.getBeanInfo(startClass, stopClass);
            PropertyDescriptor[] pds = info.getPropertyDescriptors();
            
            for(PropertyDescriptor pd : pds)
            {
                if(pd.getName().equals(name))
                    return pd;
            }
        }
        catch(IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
        
        return null;
    }

    /** 获取属性的 getter 方法的 {@link Method} 对象
     * 
     *  @param startClass    : Bean 类型
     *  @param property        : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final Method getPropertyReadMethod(Class<?> startClass, String property)
    {
        return getPropertyReadMethod(startClass, null, property);
    }
    
    /** 获取属性的 getter 方法的 {@link Method} 对象
     * 
     *  @param startClass    : Bean 类型
     *  @param stopClass    : 截止查找的父类类型
     *  @param property        : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final Method getPropertyReadMethod(Class<?> startClass, Class<?> stopClass, String property)
    {
        PropertyDescriptor pd = getPropDescByName(startClass, stopClass, property);
        return getPropertyReadMethod(pd);
    }

    /** 获取属性的 getter 方法的 {@link Method} 对象
     * 
     *  @param pd            : 属性的 {@link PropertyDescriptor} 描述符
     *  @return                  {@link Method} 对象，找不到则返回 null  
     */
    public static final Method getPropertyReadMethod(PropertyDescriptor pd)
    {
        return getPropertyMethod(pd, true);
    }
    
    /** 获取属性的 setter 方法的 {@link Method} 对象
     * 
     *  @param startClass    : Bean 类型
     *  @param property        : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final Method getPropertyWriteMethod(Class<?> startClass, String property)
    {
        return getPropertyWriteMethod(startClass, null, property);
    }
    
    /** 获取属性的 setter 方法的 {@link Method} 对象
     * 
     *  @param startClass    : Bean 类型
     *  @param stopClass    : 截止查找的父类类型
     *  @param property        : 属性名称
     *  @return                  描述对象映射，找不到属性则返回 null  
     */
    public static final Method getPropertyWriteMethod(Class<?> startClass, Class<?> stopClass, String property)
    {
        PropertyDescriptor pd = getPropDescByName(startClass, stopClass, property);
        return getPropertyWriteMethod(pd);
    }

    /** 获取属性的 setter 方法的 {@link Method} 对象
     * 
     *  @param pd            : 属性的 {@link PropertyDescriptor} 描述符
     *  @return                  {@link Method} 对象，找不到则返回 null  
     */
    public static final Method getPropertyWriteMethod(PropertyDescriptor pd)
    {
        return getPropertyMethod(pd, false);
    }

    /** 获取属性的 getter 或 setter 方法的 {@link Method} 对象
     * 
     *  @param pd            : 属性的 {@link PropertyDescriptor} 描述符
     *  @param readOrWrite    : 标识：true -> getter, false -> setter
     *  @return                  {@link Method} 对象，找不到则返回 null  
     */
    public static final Method getPropertyMethod(PropertyDescriptor pd, boolean readOrWrite)
    {
        if(pd != null)
        {
            Method method = readOrWrite ? pd.getReadMethod() : pd.getWriteMethod();
            if(method != null && isPublicInstanceMethod(method))
                return method;
        }
        
        return null;
    }

    /** 设置 Java Bean 的名称为 name 的属性值
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 属性名称
     *  @param value        : 属性值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性的实际类型：直接对属性赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性赋值<br> 
     */
    public static final <T> boolean setProperty(Object bean, String name, T value)
    {
        PropertyDescriptor pd = getPropDescByName(bean.getClass(), name);
        return setProperty(bean, pd, value);
    }
    
    /** 获取 Java Bean 的名称为 name 的属性值
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 属性名称
     *  @throws                  RuntimeException 失败则抛出相应的运行期异常
     */
    public static final <T> Result<Boolean, T> getProperty(Object bean, String name)
    {
        PropertyDescriptor pd = getPropDescByName(bean.getClass(), name);
        return getProperty(bean, pd);
    }

    /** 获取 Java Bean 的名称为 name 的属性值
     * 
     *  @param bean            : Bean 实例
     *  @param pd            : 属性描述符
     *  @throws                  RuntimeException 失败则抛出相应的运行期异常
     */
    public static final <T> Result<Boolean, T> getProperty(Object bean, PropertyDescriptor pd)
    {
        Result<Boolean, T> result = Result.initialBoolean();
        Method method = getPropertyReadMethod(pd);
        
        if(method != null)
        {
            T value = invokeMethod(bean, method);
            result.set(Boolean.TRUE, value);
        }
        
        return result;
    }

    /** 设置 Java Bean 的名称为 name 的成员变量值
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 成员变量名称
     *  @param value        : 成员变量值可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 成员变量的实际类型：直接对成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对成员变量赋值<br> 
     */
    public static <T> boolean setFieldValue(Object bean, String name, T value)
    {
        Field field = getInstanceFiledByName(bean.getClass(), name);
        return setFieldValue(bean, field, value);
    }
    
    /** 获取 Java Bean 的名称为 name 的成员值
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 成员变量名称
     *  @throws                  RuntimeException 失败则抛出相应的运行期异常
     */
    public static final <T> Result<Boolean, T> getFieldValue(Object bean, String name)
    {
        Field field = getInstanceFiledByName(bean.getClass(), name);
        return getFieldValue(bean, field);
    }

    /** 获取 Java Bean 的名称为 name 的成员值
     * 
     *  @param bean            : Bean 实例
     *  @param field        : 成员变量 {@link Field} 对象
     *  @throws                  RuntimeException 失败则抛出相应的运行期异常
     */
    public static final <T> Result<Boolean, T> getFieldValue(Object bean, Field field)
    {
        Result<Boolean, T> result = Result.initialBoolean();
        
        if(field != null && isInstanceField(field))
        {
            T value = invokeGetFieldValue(bean, field);
            result.set(Boolean.TRUE, value);
        }
            
        return result;
    }

    /** 设置 Java Bean 的名称为 name 的属性或成员变量值，如果 setter 方法不存在，则尝试直接修改成员变量
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 属性名称或成员变量名称
     *  @param value        : 属性值或成员变量可能为以下 3 中类型：<br>
     *                        &nbsp; &nbsp; 1) 属性或成员变量的实际类型：直接对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 2) {@link String} 类型：先执行自动类型转换再对属性或成员变量赋值<br>
     *                        &nbsp; &nbsp; 3) {@link String}[] 类型：先执行自动类型转换再对属性或成员变量赋值<br> 
     */
    public static final <T> boolean setPropertyOrFieldValue(Object bean, String name, T value)
    {
        return setProperty(bean, name, value) || setFieldValue(bean, name, value);
    }

    /** 设置 Java Bean 的名称为 name 的属性值，如果 getter 方法不存在，则尝试直接获取成员变量的值
     * 
     *  @param bean            : Bean 实例
     *  @param name            : 属性名称或成员变量名称
     */
    public static final <T> Result<Boolean, T> getPropertyOrFieldValue(Object bean, String name)
    {
        Result<Boolean, T> result = getProperty(bean, name);
        
        if(!result.getFlag())
            result = getFieldValue(bean, name);
        
        return result;
    }

    /** 获取某个类所有成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @return                : 失败返回空集合
     */    
    public static final Set<Field> getAllFields(Class<?> clazz)
    {
        return getAllFields(clazz, null);
    }
    
    /** 获取某个类所有成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param stopClazz    : 终止查找的类（这个类的成员变量也不被获取）
     *  @return                : 失败返回空集合
     */
    public static final Set<Field> getAllFields(Class<?> clazz, Class<?> stopClazz)
    {
        Set<Field> fields = new HashSet<Field>();
        
        while(clazz != null && clazz != stopClazz)
        {
            Field[] fs = clazz.getDeclaredFields();
            Collections.addAll(fields, fs);
            
            clazz = clazz.getSuperclass();
            
        }
        
        return fields;
    }
    
    /** 获取某个类所有成员成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @return                : 失败返回空集合
     */    
    public static final Set<Field> getInstanceFields(Class<?> clazz)
    {
        return getInstanceFields(clazz, null);
    }
    
    /** 获取某个类所有成员成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param stopClazz    : 终止查找的类（这个类的成员变量也不被获取）
     *  @return                : 失败返回空集合
     */
    public static final Set<Field> getInstanceFields(Class<?> clazz, Class<?> stopClazz)
    {
        Set<Field> fields = new HashSet<Field>();
        
        while(clazz != null && clazz != stopClazz)
        {
            Field[] fs = clazz.getDeclaredFields();
            for(Field f : fs)
            {
                if(isInstanceField(f))
                    fields.add(f);
            }
            
            clazz = clazz.getSuperclass();
        }
        
        return fields;
    }
    
    /** 获取指定类型 Java Bean 的所有成员成员变量的 {@link Field} 对象（包括 stopClass 及更高层父类除外的所有父类定义的成员变量）
     * ，该方法会屏蔽父类的同名成员变量
     *  @return                  成员变量名 / 描述对象映射  
     */
    public static final Map<String, Field> getInstanceFieldMap(Class<?> clazz)
    {
        return getInstanceFieldMap(clazz, null);
    }
    
    /** 获取指定类型 Java Bean 的所有成员变量的 {@link Field} 对象（包括 stopClass 及更高层父类除外的所有父类定义的成员变量）
     * ，该方法会屏蔽父类的同名成员变量
     *  @param clazz        : Bean 类型
     *  @param stopClazz    : 截止查找的父类类型
     *  @return                  成员变量名 / 描述对象映射  
     */
    public static final Map<String, Field> getInstanceFieldMap(Class<?> clazz, Class<?> stopClazz)
    {
        Map<String, Field> map = new HashMap<String, Field>();
        
        while(clazz != null && clazz != stopClazz)
        {
            Field[] fs = clazz.getDeclaredFields();
            for(Field f : fs)
            {
                String name = f.getName();
                if(isInstanceField(f) && !map.containsKey(name))
                    map.put(name, f);
            }
            
            clazz = clazz.getSuperclass();
        }
        
        return map;
    }
    
    /** 获取 Java Bean 的成员变量值
     * 
     *  @param bean    : Bean 实例
     *  @return        : Bean 成员变量名 / 值映射  
     */
    public static final Map<String, Object> getFieldValues(Object bean)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, Field> fms = getInstanceFieldMap(bean.getClass());
        Set<Map.Entry<String, Field>> set = fms.entrySet();
        
        for(Map.Entry<String, Field> o : set)
        {
            String key    = o.getKey();
            Field field    = o.getValue();
            
            if(field != null && isInstanceField(field))
            {
                Object obj = invokeGetFieldValue(bean, field);
                result.put(key, obj);
            }
        }
        
        return result;
    }

    /** 获取某个类中名称为 name 的成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param name            : 方法名称
     *  @return                : 失败返回 null
     */
    public static final Field getFiledByName(Class<?> clazz,String name)
    {
        return getFiledByName(clazz, null, name);
    }

    /** 获取某个类中名称为 name 的成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param stopClazz    : 终止查找的类（这个类的成员变量也不被获取）
     *  @param name            : 方法名称
     *  @return                : 失败返回 null
     */
    public static final Field getFiledByName(Class<?> clazz, Class<?> stopClazz, String name)
    {
        Field f = null;
        
        do
        {
            try
            {
                f = clazz.getDeclaredField(name);
            }
            catch(NoSuchFieldException e)
            {
                clazz = clazz.getSuperclass();
            }
        } while(f == null && clazz != null && clazz != stopClazz);
        
        return f;
    }
    
    /** 获取某个类中名称为 name 的成员成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param name            : 方法名称
     *  @return                : 失败返回 null
     */
    public static final Field getInstanceFiledByName(Class<?> clazz,String name)
    {
        return getInstanceFiledByName(clazz, null, name);
    }

    /** 获取某个类中名称为 name 的成员变量的 {@link Field} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param stopClazz    : 终止查找的类（这个类的成员变量也不被获取）
     *  @param name            : 方法名称
     *  @return                : 失败返回 null
     */
    public static final Field getInstanceFiledByName(Class<?> clazz, Class<?> stopClazz, String name)
    {
        Field f = null;
        
        do
        {
            try
            {
                Field f2 = clazz.getDeclaredField(name);
                
                if(isInstanceField(f2))
                {
                    f = f2;
                    break;
                }
                else
                    clazz = clazz.getSuperclass();
                    
            }
            catch(NoSuchFieldException e)
            {
                clazz = clazz.getSuperclass();
            }
        } while(clazz != null && clazz != stopClazz);
        
        return f;
    }
    
    /** 获取某个类所有方法的 {@link Method} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @return                : 失败返回空集合
     */
    public static final Set<Method> getAllMethods(Class<?> clazz)
    {
        return getAllMethods(clazz, null);
    }
    
    /** 获取某个类所有方法的 {@link Method} 对象
     * 
     *  @param clazz        : 要查找的类
     *  @param stopClazz    : 终止查找的类（这个类的方法也不被获取）
     *  @return                : 失败返回空集合
     */
    public static final Set<Method> getAllMethods(Class<?> clazz, Class<?> stopClazz)
    {
        Set<Method> methods = new HashSet<Method>();
        
        while(clazz != null && clazz != stopClazz)
        {
            Method[] m = clazz.getDeclaredMethods();
            Collections.addAll(methods, m);
            
            clazz = clazz.getSuperclass();
            
        }
        
        return methods;
    }
    
    /** 获取某个类中名称为 name，参数为 parameterTypes 的方法的 {@link Method} 对象
     * 
     *  @param clazz            : 要查找的类
     *  @param name                : 方法名称
     *  @param parameterTypes    : 参数类型
     *  @return                    : 失败返回 null
     */
    public static final Method getMethodByName(Class<?> clazz, String name, Class<?>... parameterTypes)
    {
        return getMethodByName(clazz, null, name, parameterTypes);
    }
    
    /** 获取某个类中名称为 name，参数为 parameterTypes 的方法的 {@link Method} 对象
     * 
     *  @param clazz            : 要查找的类
     *  @param stopClazz        : 终止查找的类（这个类的方法也不被获取）
     *  @param name                : 方法名称
     *  @param parameterTypes    : 参数类型
     *  @return                    : 失败返回 null
     */
    public static final Method getMethodByName(Class<?> clazz, Class<?> stopClazz, String name, Class<?>... parameterTypes)
    {
        Method m = null;
        
        do
        {
            try
            {
                m = clazz.getDeclaredMethod(name, parameterTypes);
            }
            catch(NoSuchMethodException e)
            {
                clazz = clazz.getSuperclass();
            }
        } while(m == null && clazz != null && clazz != stopClazz);
        
        return m;
    }
    
    /** 用 {@linkplain Class#getMethod(String, Class...)} 获取 {@link Method} 对象
     * 
     *  @param clazz            : 要查找的类
     *  @param name                : 方法名称
     *  @param parameterTypes    : 参数类型
     *  @return                    : 失败返回 null
     */
    public static final Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes)
    {
        Method m = null;
        
        try
        {
            m = clazz.getMethod(name, parameterTypes);
        }
        catch(NoSuchMethodException e)
        {
        }
        
        return m;
    }
}
