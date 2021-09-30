package co.yixiang.modules.xikang.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class JSONMapper {
    public JSONMapper() {
    }

    public static String toJSONString(Object obj) {
        return JSON.toJSONString(obj);
    }

    public static JSONObject toJSONObject(String s) {
        return StringUtils.isBlank(s) ? null : JSON.parseObject(s);
    }

    public static <T> T parseObject(String json, Class<?> responseClazz) {
        return StringUtils.isBlank(json) ? null : JSON.parseObject(json, (Type) responseClazz);
    }

    public static <T> List<T> parseList(String json, Class<?> calzz) {
        return StringUtils.isBlank(json) ? null : JSON.parseArray(json, (Class<T>) calzz);
    }

    public static String toJSONString(String str, String str2) {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put(str, str2);
        return JSON.toJSONString(jsonObj);
    }
}
