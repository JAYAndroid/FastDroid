package com.ylz.ehui.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.ArrayMap;

import com.alibaba.fastjson.JSON;
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
    public static String DEFAULT_APP_ID = "Android";

    public static String APP_SECRET = DEFAULT_APP_SECRET;
    public static String APP_ID = DEFAULT_APP_ID;

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
     * @param tempParams
     * @param service
     * @return
     */
    private static Map<String, Object> getRequestMap(Map<String, Object> tempParams, String service) {
        Map<String, Object> resultRequestMap = new TreeMap<>();

        resultRequestMap.put("serviceId", service);
        resultRequestMap.put("timestamp", new DateFormat().format("yyyyMMddHHmmss", System.currentTimeMillis()));
        resultRequestMap.put("signType", SIGN_TYPE);
        resultRequestMap.put("encryptType", ENCRYPT_TYPE);
        tempParams.remove("serviceId");

        if (!StringUtils.isEmpty(String.valueOf(tempParams.get("version")))) {
            resultRequestMap.put("version", String.valueOf(tempParams.get("version")));
            tempParams.remove("version");
        } else {
            resultRequestMap.put("version", String.valueOf(AppUtils.getVersionCode()));
        }

        if (!StringUtils.isEmpty(String.valueOf(tempParams.get("ext_token")))) {
            resultRequestMap.put("token", tempParams.get("ext_token"));
            tempParams.remove("ext_token");
        }

        if (!StringUtils.isEmpty(String.valueOf(tempParams.get("ignoreSigns")))) {
            resultRequestMap.put("ignoreSigns", tempParams.get("ignoreSigns"));
            tempParams.remove("ignoreSigns");
        }


        if (tempParams.containsKey("appId")) {
            APP_ID = String.valueOf(tempParams.get("appId"));
            tempParams.remove("appId");
        } else {
            APP_ID = DEFAULT_APP_ID;
        }

        if (tempParams.containsKey("secret")) {
            APP_SECRET = String.valueOf(tempParams.get("secret"));
            tempParams.remove("secret");
        } else {
            APP_SECRET = DEFAULT_APP_SECRET;
        }

        if (tempParams.containsKey("sessionId")) {
            resultRequestMap.put("sessionId", tempParams.get("sessionId"));
            tempParams.remove("sessionId");
        } else {
            resultRequestMap.put("sessionId", CommonUserInfos.getInstance().getSessionId());
        }

        resultRequestMap.put("deviceId", AppUtils.getUUid());
        resultRequestMap.put("appId", APP_ID);

        //分页
        Map<String, Object> pageMap = new TreeMap<>();
        if (!TextUtils.isEmpty((String) tempParams.get("pageNo")) || !TextUtils.isEmpty((String) tempParams.get("pageSize"))
                || !TextUtils.isEmpty((String) tempParams.get("rows"))) {
            pageMap.put("pageNo", tempParams.get("pageNo"));
            pageMap.put("pageSize", tempParams.get("pageSize"));
            pageMap.put("rows", tempParams.get("rows"));
            tempParams.remove("pageNo");
            tempParams.remove("pageSize");
            tempParams.remove("rows");
            if (!TextUtils.isEmpty((String) tempParams.get("pageDate_c"))) {
                pageMap.put("pageDate_c", tempParams.get("pageDate_c"));
            }
            if (!TextUtils.isEmpty((String) tempParams.get("pageTime_c"))) {
                pageMap.put("pageTime_c", tempParams.get("pageTime_c"));
            }
            if (!TextUtils.isEmpty((String) tempParams.get("pageDate_f"))) {
                pageMap.put("pageDate_f", tempParams.get("pageDate_f"));
            }
            if (!TextUtils.isEmpty((String) tempParams.get("pageTime_f"))) {
                pageMap.put("pageTime_f", tempParams.get("pageTime_f"));
            }
            resultRequestMap.put("pageParam", pageMap);
        }
        if (!TextUtils.isEmpty((String) tempParams.get("start")) && !TextUtils.isEmpty((String) tempParams.get("count"))) {
            pageMap.put("start", tempParams.get("start"));
            pageMap.put("count", tempParams.get("count"));
            tempParams.remove("start");
            tempParams.remove("count");
            resultRequestMap.put("pageParam", pageMap);
        }

        if (ENTRY) {
            tempParams = filterNullParams(tempParams);

            resultRequestMap.put("isEncrypt", 1);
            //明文
//            resultRequestMap.put("param", new Gson().toJson(tempParams));//加密，签名
            resultRequestMap.put("param", JSON.toJSONString(tempParams));//加密，签名

            resultRequestMap.put("sign", getSign(resultRequestMap, APP_SECRET));
            try {
                resultRequestMap.put("encryptData", SecurityUtils.encryptByType(String.valueOf(resultRequestMap.get("param")), ENCRYPT_TYPE));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //删除明文
            resultRequestMap.remove("param");
        } else {
            resultRequestMap.put("isEncrypt", 0);

            if (resultRequestMap.containsKey("ignoreSigns")) {
                resultRequestMap.remove("ignoreSigns");
            }

            resultRequestMap.put("param", tempParams);
        }


        return resultRequestMap;
    }

    public static Map<String, Object> filterNullParams(Map<String, Object> params) {
        List<String> removeSummary = null;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Object value = entry.getValue();
            if (value == null || "".equals(value)) {
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
        if (map == null) {
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

        if (!StringUtils.isEmpty(tempIgnoreParam)) {
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
