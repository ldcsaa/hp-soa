package io.github.hpsocket.soa.framework.web.support;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson2.JSONObject;

import io.github.hpsocket.soa.framework.core.util.CryptHelper;

public class SignHelper
{
    public static final String NODE_SIGN  = "sign";
    
    public static final JSONObject signJson(JSONObject json, String secret)
    {
        String sign = calcJsonSignString(json, secret);
        json.put(NODE_SIGN, sign);
        
        return json;
    }
    
    public static final String calcJsonSignString(JSONObject json, String secret)
    {
        StringBuilder sb = new StringBuilder(1000);
        calcJsonObjectSignString(json, sb, true);
        
        sb.append(secret);
        
        return CryptHelper.sha(sb.toString());
    }

    public static final void calcJsonObjectSignString(Map<String, ?> json, StringBuilder sb, boolean root)
    {
        Map<String, Object> map = new TreeMap<>(json);
        
        for(Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = entry.getKey();
            Object val = entry.getValue();
            
            if(root && NODE_SIGN.equals(key))
                continue;
            
            sb.append(key);
            appendValue(sb, val);
        }
    }

    @SuppressWarnings("unchecked")
    private static void appendValue(StringBuilder sb, Object val)
    {
        if(val instanceof Map map)
            calcJsonObjectSignString(map, sb, false);
        else if(val instanceof List list)
            list.forEach((o) -> appendValue(sb, o));
        else
            sb.append(String.valueOf(val));
    }

}
