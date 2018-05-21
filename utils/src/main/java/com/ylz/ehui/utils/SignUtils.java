package com.ylz.ehui.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.ylz.ehui.common.bean.CommonUserInfos;
import com.ylz.ehui.utils.AppUtils;
import com.ylz.ehui.utils.MD5Utils;
import com.ylz.ehui.utils.SecurityUtils;
import com.ylz.ehui.utils.SignatureUtils;
import com.ylz.ehui.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/********************
 * 作者：malus
 * 日期：16/11/4
 * 时间：上午10:31
 * 注释：访问网络时，签名
 ********************/
public class SignUtils {
    public static final String APP_SECRET = "SKnYwGwnwh3LI56mMwJgDw==";
    public static final String APP_ID = "Android";
    public static final boolean ENTRY = true;

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
        map.put("timestamp", new DateFormat().format("yyyyMMddhhmmssSSS", System.currentTimeMillis()));
        map.put("signType", "MD5");
        map.put("encryptType", "AES");
        map.put("version", AppUtils.getVersionCode());
        map.put("deviceId", AppUtils.getUUid());
        map.put("appId", APP_ID);
        map.put("sessionId", CommonUserInfos.getInstance().getSessionId());

        if (ENTRY) {
            Gson gson = new Gson();
            map.put("isEncrypt", 1);
            //明文
            map.put("param", SignatureUtils.getValue(params));            //加密，签名
            map.put("sign", getSign(map, APP_SECRET));
            try {
                map.put("encryptData", SecurityUtils.encryptByAES(gson.toJson(params), APP_SECRET, APP_ID));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //删除明文
            map.remove("param");
        } else {
            map.put("isEncrypt", 0);
            map.put("param", params);
        }
        //分页
        Map<String, Object> pageMap = new TreeMap<>();
        if (!TextUtils.isEmpty((String) params.get("pageNo")) && !TextUtils.isEmpty((String) params.get("pageSize"))) {
            pageMap.put("pageNo", params.get("pageNo"));
            pageMap.put("pageSize", params.get("pageSize"));
            params.remove("pageNo");
            params.remove("pageSize");
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
        return map;
    }

    /**
     * 对MAP签名 过滤空值 拼接&key=***
     *
     * @param map
     * @param key
     * @return
     */
    public static String getSign(Map<String, Object> map, String key) {
        if (map == null)
            return "";

        ArrayList<String> list = new ArrayList<String>();

        for (Map.Entry<String, Object> entry : map.entrySet()) {
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
        String signType = (String) map.get("signType");
        Log.d("RetrofitLog", "content:" + result);
        if (StringUtils.isEmpty(signType) || "MD5".equals(signType)) {
            result = MD5Utils.getMD5String(result).toUpperCase();
        } else if ("RSA".equals(signType)) {
            // RSA 签名
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
