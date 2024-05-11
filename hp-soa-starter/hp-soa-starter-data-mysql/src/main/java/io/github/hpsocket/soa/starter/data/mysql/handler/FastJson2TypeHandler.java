
package io.github.hpsocket.soa.starter.data.mysql.handler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;

/** <b>mybatis-plus FastJson2 类型转换器</b> */
@MappedTypes({Object.class})
@MappedJdbcTypes(JdbcType.VARCHAR)
public class FastJson2TypeHandler extends AbstractJsonTypeHandler<Object>
{
    boolean jsonObjecct;

    public FastJson2TypeHandler(Class<?> type)
    {
        super(type);
        
        this.jsonObjecct = (JSONObject.class.isAssignableFrom(type));
    }

    @Override
    public Object parse(String json)
    {
        if(jsonObjecct)
            return JSONObject.parse(json);
        
        return JSON.parseObject(json, type);
    }

    @Override
    public String toJson(Object obj)
    {
        if(jsonObjecct)
            return ((JSONObject)(obj)).toString(JSONWriter.Feature.WriteNonStringKeyAsString);
        
        return JSON.toJSONString(obj, JSONWriter.Feature.WriteNonStringKeyAsString);
    }
}