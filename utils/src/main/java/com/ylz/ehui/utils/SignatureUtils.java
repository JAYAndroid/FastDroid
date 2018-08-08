package com.ylz.ehui.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SignatureUtils {
    private static List<String> ignoreSign = new ArrayList<String>();

    static {
        ignoreSign.add("sign");
        ignoreSign.add("encryptData");
        ignoreSign.add("extenalMap");
        ignoreSign.add("pageParam");
    }


    /**
     * 取值
     *
     * @param value
     * @return
     */
    public static String getValue(Object value) {
        if (value instanceof String)
            return getObjString(value);
        else
            return treeJsonParam(value);
    }

    /**
     * 对象转换为字符串
     *
     * @param object
     * @return
     */
    public static String getObjString(Object object) {
        return (object == null ? "" : (String) object);
    }

    /**
     * 签名集合算法 -- 平铺
     *
     * @param params
     * @return
     */
    @Deprecated
    public static Map<String, String> flattenParams(Map<String, Object> params) {
        if (params == null) {
            return new HashMap<String, String>();
        }

        Map<String, String> flatParams = new HashMap<String, String>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?>) {
                Map<String, Object> flatNestedMap = new HashMap<String, Object>();
                Map<?, ?> nestedMap = (Map<?, ?>) value;
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    flatNestedMap.put(String.format("%s[%s]", key, nestedEntry.getKey()), nestedEntry.getValue());
                }
                flatParams.putAll(flattenParams(flatNestedMap));
            } else if (value instanceof JSONObject) {
                Map<String, Object> flatNestedMap = new HashMap<String, Object>();
                JSONObject nestedMap = (JSONObject) value;
                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    flatNestedMap.put(String.format("%s[%s]", key, nestedEntry.getKey()), nestedEntry.getValue());
                }
                flatParams.putAll(flattenParams(flatNestedMap));
            } else if (value instanceof ArrayList<?>) {
                ArrayList<?> ar = (ArrayList<?>) value;
                Map<String, Object> flatNestedMap = new HashMap<String, Object>();
                int size = ar.size();
                for (int i = 0; i < size; i++) {
                    flatNestedMap.put(String.format("%s[%d]", key, i), ar.get(i));
                }
                flatParams.putAll(flattenParams(flatNestedMap));
            } else if (value instanceof JSONArray) {
                JSONArray ar = (JSONArray) value;
                Map<String, Object> flatNestedMap = new HashMap<String, Object>();
                int size = ar.size();
                for (int i = 0; i < size; i++) {
                    flatNestedMap.put(String.format("%s[%d]", key, i), ar.get(i));
                }
                flatParams.putAll(flattenParams(flatNestedMap));
            } else if ("".equals(value)) {
                flatParams.put(key, "");
                // throw new BussinessException("You cannot set '" + key
                // + "' to an empty string. "
                // + "We interpret empty strings as null in requests. "
                // + "You may set '" + key
                // + "' to null to delete the property.");
            } else if (value == null) {
                flatParams.put(key, "");
            } else {
                // 实体类
                if (value.getClass().getPackage().getName().startsWith("com.ylzinfo.onepay.sdk.domain")) {
                    String prefix = key;
                    //被我注释了，现在失效
                    flatParams.putAll(flattenParams(null));
                } else {
                    flatParams.put(key, value.toString());
                }
            }
        }
        return flatParams;
    }


    /**
     * 转换PARAM
     *
     * @param value
     * @return
     */
    private static String treeJsonParam(Object value) {
        String jsoNParam = null;

        if (value instanceof Map<?, ?>) {
            Map<String, Object> treeNestedMap = new TreeMap<String, Object>();

            Map<?, ?> nestedMap = (Map<?, ?>) value;

            for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                treeNestedMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
            }

            jsoNParam = JSONObject.toJSONString(treeParams(treeNestedMap));
        } else if (value instanceof JSONObject) {
            Map<String, Object> jsonMap = new TreeMap<String, Object>();
            JSONObject nestedMap = (JSONObject) value;

            for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                jsonMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
            }

            jsoNParam = JSONObject.toJSONString(treeParams(jsonMap));
        } else if (value instanceof ArrayList<?>) {
            ArrayList<?> ar = (ArrayList<?>) value;
            jsoNParam = JSONObject.toJSONString(treeList((ar)));
        } else if (value instanceof JSONArray) {
            JSONArray jarr = (JSONArray) value;
            jsoNParam = JSONObject.toJSONString(treeJsonArray((jarr)));
        } else if (value != null && value.getClass().getPackage().getName().startsWith("com.ylzinfo.onepay.sdk.domain")) {
            jsoNParam = JSONObject.toJSONString(treeParams(null));
        } else if (value == null) {

        } else {
            jsoNParam = value.toString();
        }

        return jsoNParam;
    }

    /**
     * 签名集合算法 -- 排序
     *
     * @param params
     * @return
     */
    private static Map<String, Object> treeParams(Map<String, Object> params) {
        if (params == null) {
            return new TreeMap<String, Object>();
        }

        Map<String, Object> treeParams = new TreeMap<String, Object>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?>) {
                Map<String, Object> treeNestedMap = new TreeMap<String, Object>();

                Map<?, ?> nestedMap = (Map<?, ?>) value;

                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    treeNestedMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
                }

                treeParams.put(key, treeParams(treeNestedMap));
            } else if (value instanceof JSONObject) {
                Map<String, Object> treeNestedMap = new TreeMap<String, Object>();

                JSONObject nestedMap = (JSONObject) value;

                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    treeNestedMap.put(key, nestedEntry.getValue());
                }

                treeParams.put(key, treeParams(treeNestedMap));
            } else if (value instanceof ArrayList<?>) {
                ArrayList<?> ar = (ArrayList<?>) value;
                treeParams.put(key, treeList(ar));
            } else if (value instanceof JSONArray) {
                JSONArray ar = (JSONArray) value;
                treeParams.put(key, treeJsonArray(ar));
            } else if ("".equals(value)) {
                // flatParams.put(key, "");
            } else if (value == null) {
                // flatParams.put(key, "");
            } else if (value.getClass().getPackage().getName().startsWith("com.ylzinfo.onepay.sdk.domain")) { // 实体类
                treeParams.put(key, treeParams(null));
            } else {
                treeParams.put(key, value.toString());
            }
        }

        return treeParams;
    }

    /**
     * JsonArray排序
     *
     * @param jarr
     * @return
     */
    private static JSONArray treeJsonArray(JSONArray jarr) {
        if (jarr == null || jarr.size() == 0)
            return null;

        JSONArray jsonArray = new JSONArray();

        int size = jarr.size();

        for (int i = 0; i < size; i++) {
            Object value = jarr.get(i);

            if (value instanceof Map<?, ?>) {
                Map<String, Object> treeNestedMap = new TreeMap<String, Object>();

                Map<?, ?> nestedMap = (Map<?, ?>) value;

                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    treeNestedMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
                }

                jsonArray.add(i, treeParams(treeNestedMap));
            } else if (value instanceof JSONObject) {
                Map<String, Object> treeNestedMap = new TreeMap<String, Object>();

                JSONObject nestedMap = (JSONObject) value;

                for (Map.Entry<?, ?> nestedEntry : nestedMap.entrySet()) {
                    treeNestedMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
                }

                jsonArray.add(i, treeParams(treeNestedMap));
            } else if (value instanceof ArrayList<?>) {
                ArrayList<?> ar = (ArrayList<?>) value;
                jsonArray.add(i, treeList(ar));
            } else if (value instanceof JSONArray) {
                JSONArray ar = (JSONArray) value;
                jsonArray.add(i, treeJsonArray(ar));
            } else if (!(value instanceof String)) {
                jsonArray.add(i, new Gson().toJson(value));
            } else if (value.getClass().getPackage().getName().startsWith("com.ylzinfo.onepay.sdk.domain")) { // 实体类
                jsonArray.add(i, treeParams(null));
            } else {
                jsonArray.add(i, value.toString());
            }
        }

        return jsonArray;
    }

    /**
     * List排序
     *
     * @param list
     * @return
     */
    private static JSONArray treeList(ArrayList<?> list) {
        if (list == null || list.size() == 0)
            return null;

        JSONArray jsonArray = new JSONArray();
        int size = list.size();

        for (int i = 0; i < size; i++) {
            jsonArray.add(i, list.get(i));
        }

        return treeJsonArray(jsonArray);
    }
}
