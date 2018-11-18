package com.tendy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonMapper {
    public static final int UNDERLINE_TO_CAMEL_CASE = 0;
    public static Pattern linePattern = Pattern.compile("_(\\w)");
    public static Pattern camelPattern = Pattern.compile("[A-Z]");

    public static String toJson(Object obj) {
        String jsonStr = "";
        try {
            jsonStr = JSON.toJSONString(obj);
        } catch (Exception e) {

        }
        return jsonStr;
    }

    public static <T> T json2Bean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return t;
    }

    public static <T> T json2Bean(String jsonString, Type clzz) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, clzz);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return t;
    }

    public static <T> List<T> json2List(String jsonString, Class<T> cls) {
        List<T> list = null;
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {

        }
        return list;
    }

    public static Map<String,Object> json2Map(String jsonString){
        Map<String, Object> map = null;
        try {
            map = JSON.parseObject(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {

        }
        return map;
    }

    public static String toJson(Object obj, int type) {
        String str = toJson(obj);
        StringBuffer sb = new StringBuffer();
        if(type == UNDERLINE_TO_CAMEL_CASE) {
            Matcher matcher = linePattern.matcher(str);
            while (matcher.find()) {
                matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
        } else {
            Matcher matcher = camelPattern.matcher(str);
            while(matcher.find()){
                matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            }
            matcher.appendTail(sb);
        }
        return sb.toString();
    }
}
