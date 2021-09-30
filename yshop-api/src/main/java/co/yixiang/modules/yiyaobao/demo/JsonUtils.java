package co.yixiang.modules.yiyaobao.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * author: tcg
 * Date: 2009-6-29
 * Time: 18:01:42
 */
@SuppressWarnings("unchecked")
public class JsonUtils {

    public static JsonConfig jsonConfig;

    static {
        jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(java.util.Date.class, new JsonDateValueProcessor("yyyy-MM-dd"));
        jsonConfig.registerJsonValueProcessor(java.sql.Date.class, new JsonDateValueProcessor("yyyy-MM-dd"));
        jsonConfig.registerJsonValueProcessor(java.sql.Time.class, new JsonDateValueProcessor("HH:mm:ss"));
        jsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonDateValueProcessor("yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * 将jsonString转化为list对象
     *
     * @param jsonString
     * @return
     */
    public static List json2List(String jsonString) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        List<Map> list = new ArrayList<Map>();
        for (Object obj : jsonObject.keySet()) {
            Map map = json2Map(jsonObject.getString(obj.toString()));
            list.add(map);
        }
        return list;
    }

    /**
     * 将jsonString转化为map对象
     *
     * @param jsonString
     * @return
     */
    public static Map json2Map(String jsonString) {
        Map<Object, String> map = new HashMap<Object, String>();
        JSONObject jsonObj = JSONObject.fromObject(jsonString);
        for (Object object : jsonObj.keySet()) {
            String value = jsonObj.get(object.toString()).toString();
            map.put(object, value);
        }
        return map;
    }

    /**
     * json转对象
     * @param jsonString
     * @param objectClass
     * @return
     */
    public static <T> T json2Object(String jsonString,Class<T> objectClass) {
        JSONObject jsonObj = JSONObject.fromObject(jsonString);
        return (T) JSONObject.toBean(jsonObj, objectClass);

    }
    /**
     * json转对象
     * @param jsonString
     * @param objectClass
     * @param specialCollections 集合类型的属性类型映射
     * @return
     */
    public static <T> T json2Object(String jsonString,Class<T> objectClass,Map<String,Class> specialCollections) {
        JSONObject jsonObj = JSONObject.fromObject(jsonString);
        return (T) JSONObject.toBean(jsonObj, objectClass,specialCollections);

    }
    /**
     * 把对象转化为json字符串
     *
     * @param o
     * @return
     */
    public static String object2Json(Object o) {
        if (o == null) {
            return "";
        }
        return JSONObject.fromObject(o, jsonConfig).toString();
    }

    /**
     * 把对象转化为json字符串
     *
     * @param o
     * @return
     */
    public static String object2Json(Object o, JsonConfig jsonConfig) {
        if (o == null) {
            return "";
        }
        return JSONObject.fromObject(o, jsonConfig).toString();
    }

    /**
     * 将List转成Json
     *
     * @param list
     * @param prettyPrint
     * @return
     */
    public static String list2Json(List list, boolean prettyPrint) {
        return (new flexjson.JSONSerializer()).exclude("*.class")
                .prettyPrint(prettyPrint).serialize(list);
    }

    /**
     * 将List转成Json,prettyPrint默认为false
     *
     * @param list
     * @return
     */
    public static String list2Json(List list) {
        return list2Json(list, false);
    }

    /**
     * 将list序列化为JSON
     *
     * @param list
     * @param excludes
     * @param prettyPrint
     * @return
     */
    public static String list2Json(List list, boolean prettyPrint, String... excludes) {
        flexjson.JSONSerializer serializer = new flexjson.JSONSerializer();
        String[] exclude = new String[excludes.length + 1];

        for(int i=0; i<excludes.length; i++) {
            exclude[i] = excludes[i];
        }

        exclude[excludes.length] = "*.class";

        serializer.exclude(exclude);

        return serializer.prettyPrint(prettyPrint).serialize(list);
    }

    /**
     * 将list序列化为JSON
     *
     * @param list
     * @param excludes
     * @return
     */
    public static String list2Json(List list, String... excludes) {
        return list2Json(list, false, excludes);
    }
    /**
     * 将jsonString转化为list对象
     *
     * @param jsonString
     * @return
     */
    public static JSONArray json2Array(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray;
    }

    /**
     * 将对象转化为jsonArray
     *
     * @param object
     * @return
     */
    public static JSONArray object2JsonArray(Object object) {
        JSONArray jsonArray = JSONArray.fromObject(object,jsonConfig);
        return jsonArray;
    }

    /**
     * 将JSONObjec对象转换成Map-List集合
     * @param json
     * @return
     */
    public static HashMap<String, Object> jsonArray2Map(JSONObject json){
        HashMap<String, Object> map = new HashMap<String, Object>();
        Set<?> keys = json.keySet();
        for(Object key : keys){
            Object o = json.get(key);
            if(o instanceof JSONArray)
                map.put((String) key, jsonArray2List((JSONArray) o));
            else if(o instanceof JSONObject)
                map.put((String) key, jsonArray2Map((JSONObject) o));
            else
                map.put((String) key, o);
        }
        return map;
    }

    /**
     * 将JSONArray对象转换成Map-List集合
     * @param json
     * @return
     */
    public static List jsonArray2List(JSONArray json){
        List<Object> list = new ArrayList<Object>();
        for(Object o : json){
            if(o instanceof JSONArray)
                list.add(jsonArray2List((JSONArray) o));
            else if(o instanceof JSONObject)
                list.add(jsonArray2Map((JSONObject) o));
            else
                list.add(o);
        }
        return list;
    }

    /**
     * 将jsonString数组转化为list对象
     *
     * @param jsonString
     * @return
     */
    public static List json2ArrayList(String jsonString){
        JSONArray json = json2Array(jsonString);
        List list = jsonArray2List(json);
        return list;
    }

}