package com.jxtc.bookapp.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.T;

import java.util.*;

/**
 * @author wyl
 * 本类是用于json转化的工具类
 */
public class JSONUtil {

    /**
     * java实体转json字符串
     *
     * @param object
     * @return
     */
    public static String beanToJsonStr(Object object) {
        JSONObject obj = JSONObject.fromObject(object);
        return obj.toString();
    }

    /**
     * json字符串转java对象
     *
     * @param json
     * @return
     */
    public static Object JsonStrToBean(String json, Object obj) {
        JSONObject object = JSONObject.fromObject(json);
        return JSONObject.toBean(object, obj.getClass());
    }

    /**
     * 集合转化为json字符串
     *
     * @param collection
     * @return
     */
    public static String listToJsonStr(Object collection) {
        JSONArray array = JSONArray.fromObject(collection);
        return array.toString();
    }

    /**
     * json字符串转集合
     *
     * @param jsonStr
     * @param obj
     * @return
     */
    public static Collection<T> jsonStrToList(String jsonStr, Object obj) {
        JSONArray array = JSONArray.fromObject(jsonStr);
        return JSONArray.toCollection(array, obj.getClass());
    }

    public static Map<String, Object> parseJSON2Map(String jsonStr) {
        // 最外层解析
        if (jsonStr != null && jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
            Map<String, Object> map = new HashMap<String, Object>();
            JSONObject json = JSONObject.fromObject(jsonStr);
            for (Object k : json.keySet()) {
                Object v = json.get(k);
                // 字段值为null直接转为空
                if (null == v) {
                    map.put(k.toString(), "");
                } // 如果内层还是数组的话，继续解析
                else if (v instanceof JSONArray) {
                    List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                    Iterator<Object> it = ((JSONArray) v).iterator();
                    while (it.hasNext()) {
                        JSONObject json2 = (JSONObject) it.next();
                        list.add(parseJSON2Map(json2.toString()));
                    }
                    map.put(k.toString(), list);
                } else {
                    Map<String, Object> m = parseJSON2Map(v.toString());
                    if (m == null) {
                        map.put(k.toString(), v);
                    } else {
                        map.put(k.toString(), m);
                    }
                }
            }
            return map;
        } else {
            return null;
        }
    }
}
