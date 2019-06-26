package com.jxtc.bookapp.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;

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
     * @param jsonStr
     * @param obj
     * @return
     */
    public static Collection<T> jsonStrToList(String jsonStr, Object obj) {
        JSONArray array = JSONArray.fromObject(jsonStr);
        return JSONArray.toCollection(array, obj.getClass());
    }

}
