package io.github.hpsocket.soa.framework.web.converter;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/** String &lt;-&gt; JSON */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StringToJsonMapper {

    default JSONObject stringToJsonObject(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return JSONObject.parseObject(str);
    }

    default String jsonObjectToString(JSONObject jsonObject) {
        if(jsonObject == null) {
            return null;
        }

        return jsonObject.toJSONString();
    }

    default JSONArray stringToJsonArray(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return JSONArray.parseArray(str);
    }

    default String jsonArrayToString(JSONArray jsonArray) {
        if(jsonArray == null) {
            return null;
        }

        return jsonArray.toJSONString();
    }

    default <T> List<T> stringToList(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return (List<T>)JSONArray.parse(str);
    }

    default String listToString(List<?> list) {
        if(list == null) {
            return null;
        }

        return JSONArray.toJSONString(list);
    }

    default Byte stringToByte(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Byte.parseByte(str);
    }

    default Short stringToShort(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Short.parseShort(str);
    }

    default Integer stringToInteger(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Integer.parseInt(str);
    }

    default Long stringToLong(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Long.parseLong(str);
    }

    default Float stringToFloat(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Float.parseFloat(str);
    }

    default Double stringToDouble(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return Double.parseDouble(str);
    }

    default Boolean stringToBoolean(String str) {
        if(StringUtils.isBlank(str)) {
            return null;
        }

        return str.equalsIgnoreCase("true")
                || str.equalsIgnoreCase("yes")
                || str.equalsIgnoreCase("on")
                || str.equalsIgnoreCase("1");
    }

    default List<Integer> stringListToIntegerList(List<String> list) {
        if(list == null) {
            return null;
        }

        return list.stream().map(this::stringToInteger).toList();
    }

    default List<Long> stringListToLongList(List<String> list) {
        if(list == null) {
            return null;
        }

        return list.stream().map(this::stringToLong).toList();
    }

    default List<String> integerListToStringList(List<Integer> list) {
        if(list == null) {
            return null;
        }

        return list.stream().map(String::valueOf).toList();
    }

    default List<String> longListToStringList(List<Long> list) {
        if(list == null) {
            return null;
        }

        return list.stream().map(String::valueOf).toList();
    }

}
