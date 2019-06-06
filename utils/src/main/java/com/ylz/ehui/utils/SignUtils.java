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

    private static String DEFAULT_APP_SECRET = "SKnYwGwnwh3LI56mMwJgDw==";
    private static String DEFAULT_APP_ID = "Android";

    private static String APP_SECRET = DEFAULT_APP_SECRET;
    private static String APP_ID = DEFAULT_APP_ID;
    private static String SIGN_TYPE = "MD5";
    private static String ENCRYPT_TYPE = "AES";
    private static String IV = "0102030405060708";

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
     * @param innerParams
     * @param service
     * @return
     */
    private static Map<String, Object> getRequestMap(Map<String, Object> innerParams, String service) {
        Map<String, Object> outParams = new TreeMap<>();

        outParams.put("serviceId", service);
        outParams.put("timestamp", new DateFormat().format("yyyyMMddHHmmss", System.currentTimeMillis()));
        outParams.put("signType", SIGN_TYPE);
        outParams.put("encryptType", ENCRYPT_TYPE);

        if (innerParams.containsKey("termType")) {
            outParams.put("termType", innerParams.get("termType"));
            innerParams.remove("termType");
        }

        if (innerParams.containsKey("termTypeInner")) {
            innerParams.put("termType", innerParams.get("termTypeInner"));
            innerParams.remove("termTypeInner");
        }

        innerParams.remove("serviceId");

        if (!StringUtils.isEmpty(String.valueOf(innerParams.get("version")))) {
            outParams.put("version", String.valueOf(innerParams.get("version")));
            innerParams.remove("version");
        } else {
            outParams.put("version", String.valueOf(AppUtils.getVersionCode()));
        }

        if (innerParams.containsKey("versionInner")) {
            innerParams.put("version", innerParams.get("versionInner"));
            innerParams.remove("versionInner");
        }

        if (!StringUtils.isEmpty(String.valueOf(innerParams.get("ext_token")))) {
            outParams.put("token", innerParams.get("ext_token"));
            innerParams.remove("ext_token");
        }

        if (!StringUtils.isEmpty(String.valueOf(innerParams.get("ignoreSigns")))) {
            outParams.put("ignoreSigns", innerParams.get("ignoreSigns"));
            innerParams.remove("ignoreSigns");
        }


        if (innerParams.containsKey("appId")) {
            APP_ID = String.valueOf(innerParams.get("appId"));
            innerParams.remove("appId");
        }

        if (innerParams.containsKey("secret")) {
            APP_SECRET = String.valueOf(innerParams.get("secret"));
            innerParams.remove("secret");
        }

        if (innerParams.containsKey("sessionId")) {
            outParams.put("sessionId", innerParams.get("sessionId"));
            innerParams.remove("sessionId");
        } else {
            outParams.put("sessionId", CommonUserInfos.getInstance().getSessionId());
        }

        outParams.put("deviceId", AppUtils.getUUid());
        outParams.put("appId", APP_ID);

        //分页
        Map<String, Object> pageMap = new TreeMap<>();
        if (!TextUtils.isEmpty((String) innerParams.get("pageNo")) || !TextUtils.isEmpty((String) innerParams.get("pageSize"))
                || !TextUtils.isEmpty((String) innerParams.get("rows"))) {
            pageMap.put("pageNo", innerParams.get("pageNo"));
            pageMap.put("pageSize", innerParams.get("pageSize"));
            pageMap.put("rows", innerParams.get("rows"));
            innerParams.remove("pageNo");
            innerParams.remove("pageSize");
            innerParams.remove("rows");
            if (!TextUtils.isEmpty((String) innerParams.get("pageDate_c"))) {
                pageMap.put("pageDate_c", innerParams.get("pageDate_c"));
            }
            if (!TextUtils.isEmpty((String) innerParams.get("pageTime_c"))) {
                pageMap.put("pageTime_c", innerParams.get("pageTime_c"));
            }
            if (!TextUtils.isEmpty((String) innerParams.get("pageDate_f"))) {
                pageMap.put("pageDate_f", innerParams.get("pageDate_f"));
            }
            if (!TextUtils.isEmpty((String) innerParams.get("pageTime_f"))) {
                pageMap.put("pageTime_f", innerParams.get("pageTime_f"));
            }
            outParams.put("pageParam", pageMap);
        }
        if (!TextUtils.isEmpty((String) innerParams.get("start")) && !TextUtils.isEmpty((String) innerParams.get("count"))) {
            pageMap.put("start", innerParams.get("start"));
            pageMap.put("count", innerParams.get("count"));
            innerParams.remove("start");
            innerParams.remove("count");
            outParams.put("pageParam", pageMap);
        }

        if (innerParams.containsKey("pageSizeInner")) {
            innerParams.put("pageSize", innerParams.get("pageSizeInner"));
            innerParams.remove("pageSizeInner");
        }

        if (innerParams.containsKey("pageNoInner")) {
            innerParams.put("pageNo", innerParams.get("pageNoInner"));
            innerParams.remove("pageNoInner");
        }

        if (ENTRY) {
            innerParams = filterNullParams(innerParams);

            outParams.put("isEncrypt", 1);
            //明文
//            outParams.put("param", new Gson().toJson(innerParams));//加密，签名
            outParams.put("param", JSON.toJSONString(innerParams));//加密，签名

            outParams.put("sign", getSign(outParams, APP_SECRET));
            try {
                outParams.put("encryptData", SecurityUtils.encryptByType(String.valueOf(outParams.get("param")), ENCRYPT_TYPE));
            } catch (Exception e) {
                e.printStackTrace();
            }

            //删除明文
            outParams.remove("param");
        } else {
            outParams.put("isEncrypt", 0);

            if (outParams.containsKey("ignoreSigns")) {
                outParams.remove("ignoreSigns");
            }

            outParams.put("param", innerParams);
        }

        if (Utils.isDebug()) {
            LogUtils.json(JSON.toJSONString(outParams));
        }
        return outParams;
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


    public static void setAppId(String appId) {
        if (StringUtils.isEmpty(appId)) {
            return;
        }

        DEFAULT_APP_ID = appId;
        APP_ID = appId;
    }

    public static void resetAppId(){
        APP_ID = DEFAULT_APP_ID;
    }


    public static void setAppSecret(String appSecret) {
        if (StringUtils.isEmpty(appSecret)) {
            return;
        }
        DEFAULT_APP_SECRET = appSecret;
        APP_SECRET = appSecret;
    }

    public static void setSignType(String signType) {
        if (StringUtils.isEmpty(signType)) {
            return;
        }
        SIGN_TYPE = signType;
    }

    public static void setEncryptType(String encryptType) {
        if (StringUtils.isEmpty(encryptType)) {
            return;
        }
        ENCRYPT_TYPE = encryptType;
    }

    public static String getAppSecret() {
        return APP_SECRET;
    }

    public static String getAppId() {
        return APP_ID;
    }

    public static String getIV() {
        return IV;
    }

    public static void setIV(String iv) {
        IV = iv;
    }

    public static void resetSecret() {
        APP_SECRET = DEFAULT_APP_SECRET;
    }
}
