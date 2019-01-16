package com.ylz.ehui.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.ylz.ehui.common.bean.CommonUserInfos;
import com.ylz.ehui.utils.cryptoLib.sm3.SM3Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/********************
 * 作者：malus
 * 日期：16/11/4
 * 时间：上午10:31
 * 注释：访问网络时，签名
 ********************/
public class SignUtils {
    private static List<String> fixIgnoreSign = new ArrayList<>();

    static {
        fixIgnoreSign.add("sign");
        fixIgnoreSign.add("encryptData");
        fixIgnoreSign.add("extenalMap");
        fixIgnoreSign.add("pageParam");
    }

    public static String DEFAULT_APP_SECRET = "SKnYwGwnwh3LI56mMwJgDw==";

    public static String APP_SECRET = DEFAULT_APP_SECRET;
    public static String APP_ID = "Android";

    public static String SIGN_TYPE = "MD5";
    public static String ENCRYPT_TYPE = "AES";

    public static String IV = "0102030405060708";

    public static boolean ENTRY = !Utils.isDebug();


    public static Map getRequest(Map<String, Object> params, String service) {
        if (params == null) {
            params = new ArrayMap<>();
        }

        return getRequestMap(params, service);
    }

    /**
     * 获取请求
     *
     * @param params
     * @param service
     * @return
     */
    private static Map<String, Object> getRequestMap(Map<String, Object> params, String service) {
        Map<String, Object> map = new TreeMap<>();

        map.put("serviceId", service);
        map.put("timestamp", new DateFormat().format("yyyyMMddHHmmss", System.currentTimeMillis()));
        map.put("signType", SIGN_TYPE);
        map.put("encryptType", ENCRYPT_TYPE);

        if (!StringUtils.isEmpty(String.valueOf(params.get("version")))) {
            map.put("version", String.valueOf(params.get("version")));
            params.remove("version");
        } else {
            map.put("version", String.valueOf(AppUtils.getVersionCode()));
        }

        if (!StringUtils.isEmpty(String.valueOf(params.get("ext_token")))) {
            map.put("token", params.get("ext_token"));
            params.remove("ext_token");
        }

        if (!StringUtils.isEmpty(String.valueOf(params.get("ignoreSigns")))) {
            map.put("ignoreSigns", params.get("ignoreSigns"));
            params.remove("ignoreSigns");
        }

        map.put("deviceId", AppUtils.getUUid());
        map.put("appId", APP_ID);
        map.put("sessionId", CommonUserInfos.getInstance().getSessionId());
        params.remove("serviceId");

        //分页
        Map<String, Object> pageMap = new TreeMap<>();
        if (!TextUtils.isEmpty((String) params.get("pageNo")) || !TextUtils.isEmpty((String) params.get("pageSize"))
                || !TextUtils.isEmpty((String) params.get("rows"))) {
            pageMap.put("pageNo", params.get("pageNo"));
            pageMap.put("pageSize", params.get("pageSize"));
            pageMap.put("rows", params.get("rows"));
            params.remove("pageNo");
            params.remove("pageSize");
            params.remove("rows");
            if (!TextUtils.isEmpty((String) params.get("pageDate_c"))) {
                pageMap.put("pageDate_c", params.get("pageDate_c"));
            }
            if (!TextUtils.isEmpty((String) params.get("pageTime_c"))) {
                pageMap.put("pageTime_c", params.get("pageTime_c"));
            }
            if (!TextUtils.isEmpty((String) params.get("pageDate_f"))) {
                pageMap.put("pageDate_f", params.get("pageDate_f"));
            }
            if (!TextUtils.isEmpty((String) params.get("pageTime_f"))) {
                pageMap.put("pageTime_f", params.get("pageTime_f"));
            }
            map.put("pageParam", pageMap);
        }
        if (!TextUtils.isEmpty((String) params.get("start")) && !TextUtils.isEmpty((String) params.get("count"))) {
            pageMap.put("start", params.get("start"));
            pageMap.put("count", params.get("count"));
            params.remove("start");
            params.remove("count");
            map.put("pageParam", pageMap);
        }

        if (ENTRY) {
            params = filterNullParams(params);

            map.put("isEncrypt", 1);
            //明文
//            map.put("param", new Gson().toJson(params));//加密，签名
            map.put("param", JSON.toJSONString(params));//加密，签名

            map.put("sign", getSign(map, APP_SECRET));
            try {
                map.put("encryptData", SecurityUtils.encryptByType(String.valueOf(map.get("param")), ENCRYPT_TYPE));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //删除明文
            map.remove("param");
        } else {
            map.put("isEncrypt", 0);

            if (map.containsKey("ignoreSigns")) {
                map.remove("ignoreSigns");
            }

            map.put("param", params);
        }


        return map;
    }

    private static Map<String, Object> filterNullParams(Map<String, Object> params) {
        List<String> removeSummary = null;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null || value == "") {
                if (removeSummary == null) {
                    removeSummary = new ArrayList<>();
                }

                removeSummary.add(entry.getKey());
            }
        }

        if (removeSummary != null && removeSummary.size() > 0) {
            for (String key : removeSummary) {
                params.remove(key);
            }
        }

        if (!(params instanceof TreeMap)) {
            TreeMap<String, Object> transMap = new TreeMap<>(params);
            return transMap;
        }

        return params;
    }

    /**
     * 对MAP签名 过滤空值 拼接&key=***
     *
     * @param map
     * @param key
     * @return
     */
    public static String getSign(Map<String, Object> map, String key) {
        if (map == null){
            return "";
        }

        String tempIgnoreParam = "";

        if (map.containsKey("ignoreSigns")) {
            tempIgnoreParam = String.valueOf(map.get("ignoreSigns"));
            map.remove("ignoreSigns");
        }

        Map<String, Object> signParams = new TreeMap<>(map);
        for (String ignoreParam : fixIgnoreSign) {
            signParams.remove(ignoreParam);
        }

        if(!StringUtils.isEmpty(tempIgnoreParam)){
            signParams.remove(tempIgnoreParam);
        }

        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : signParams.entrySet()) {
            //  getObjString(entry.getValue()))
            if (!StringUtils.isEmpty(SignatureUtils.getValue(entry.getValue()))) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }

        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        String signType = (String) signParams.get("signType");
        if (StringUtils.isEmpty(signType) || "MD5".equals(signType)) {
            result = MD5Utils.getMD5String(result).toUpperCase();
        } else if ("SM3".equals(signType)) {
            // RSA 签名
            result = SM3Utils.encrypt(result);
        }

        return result;
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

}
