package com.ylz.ehui.utils;

import com.sm.crypto.cryptolib.DataFormater;
import com.sm.crypto.cryptolib.sm4.SM4Utils;

import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;


/**
 * 安全工具类： 主要功能有加解密、签名、验签等
 *
 * @author sun
 * @version 1.0.0
 */

public class SecurityUtils {

    /**
     * 通过公钥完成加密，再通过Base64Utils加密
     *
     * @param data          待加密的数据
     * @param publicKeyFile 公钥路径
     * @return 公钥加密后的Base64Utils字符串
     * @throws Exception
     * @author sun
     * @date 2014年6月20日
     */
    public static String encryptByPublicKey(String data, String publicKeyFile) throws Exception {
        PublicKey publicKey = FileUtils.getPublicKey(publicKeyFile);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] byteContext = cipher.doFinal(data.getBytes("UTF-8"));
        String encrypt = new String(Base64Utils.encode(byteContext));
        return encrypt;
    }

    /**
     * 通过私钥完成密文解密
     *
     * @param data           待解密的Base64Utils密文
     * @param privateKeyFile 私钥文件路径
     * @return
     * @throws Exception
     * @author sun
     * @date 2014年6月20日
     */
    public static String decryptByPrivateKey(String data, String privateKeyFile) throws Exception {
        PrivateKey privateKey = FileUtils.getPrivateKey(privateKeyFile);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] byteData = cipher.doFinal(Base64Utils.decode(data));
        return new String(byteData, "UTF-8");
    }

    /**
     * 通过AES加密方式完成文本加密
     *
     * @param data 待加密文本
     * @return 加密后文本
     * @throws Exception
     * @author sun
     * @date 2014年6月20日
     */
    private static String encryptByAES(String data, String appSecret, String appId) throws Exception {
        String newPassword = AESUtils.encrypt(appSecret, appId);
        return AESUtils.encrypt(data, newPassword);
    }

    /**
     * 通过DES加密方式完成文本加密
     *
     * @param data 待加密文本
     * @return 加密后文本
     * @throws Exception
     */
    public static String encryptByDES(String data, String appSecret, String appId) throws Exception {
        // String appSecret = CommonConstant.propertiesMap.get("appSecret");
        String newPassword = DESUtil.encrypt(appSecret, appId.substring(0, 8));
        return DESUtil.encrypt(data, newPassword.substring(0, 8));
    }

    /**
     * 通过DES解密方式完成密文解密
     *
     * @param data 待解密文本
     * @return
     * @throws Exception
     */
    private static String decryptByDES(String data, String appSecret, String appId) throws Exception {
        // String appSecret = CommonConstant.propertiesMap.get("appSecret");
        String newPassword = DESUtil.encrypt(appSecret, appId.substring(0, 8));
        // 传输过程加号丢失问题
        data = data.replace(" ", "+");
        return DESUtil.decrypt(data, newPassword.substring(0, 8));

    }

    public static String encryptByType(String rawData, String encryptType) throws Exception {
        if ("AES".equals(encryptType)) {
            return encryptByAES(rawData, SignUtils.APP_SECRET, SignUtils.APP_ID);
        } else if ("SM4".equals(encryptType)) {
            SM4Utils sm4Utils = new SM4Utils(SignUtils.APP_ID, SignUtils.IV);
            String sm4Key = StringUtils.rightPad(sm4Utils.encryptData_CBC(SignUtils.APP_SECRET), 16, "0");
            sm4Utils.setSecretKey(sm4Key);
            return sm4Utils.encryptData_CBC(rawData);
        } else {
            return rawData;
        }
    }

    /**
     * 通过AES解密方式完成密文解密
     *
     * @param data      待解密文本
     * @param appSecret 平台号
     * @return
     * @throws Exception
     */
    public static String decryptByAES(String data, String appSecret, String appId) throws Exception {
        // imei = getPassword(imei);
        // String appSecret = CommonConstant.propertiesMap.get("appSecret");
        String newPassword = AESUtils.encrypt(appSecret, appId);
        return AESUtils.decrypt(data, newPassword);
    }

    /**
     * 获取要签名的MAP 转化成获取摘要需要的MAP
     *
     * @author YANGD
     * @param map
     * @return
     */
    // @SuppressWarnings({ "unchecked"})
    // private static Map<String,Object> getDigestMap(Map<String, Object> map){
    // Map<String, Object> digestMap = new HashMap<String, Object>();
    // for (String key : map.keySet()) {//遍历MAP
    // Object value = map.get(key);
    // if (value instanceof Map) {//如果包含MAP 继续遍历
    // Map<String, Object> valueMap = (Map<String,Object>) value;
    // for (String key2 : valueMap.keySet()) {//遍历MAP
    // Object value2 = valueMap.get(key2);
    // if (value2 instanceof String)//判断是否为字符类型
    // digestMap.put(key2, value2);
    // }
    // }else if (value instanceof String){//如果是字符类型 则加入新的MAP
    // digestMap.put(key, value);
    // }
    // }
    // return digestMap;
    // }

    /**
     * 通过map获取签名摘要
     *
     * @return
     * @author sun
     * @date 2014年6月20日
     */
    // @SuppressWarnings({ "unchecked", "rawtypes" })
    // private static String getDigestStr(Map<String, Object> map) {
    // Set<Entry<String, Object>> set = map.entrySet();
    // List<Entry<String, Object>> arrayList = new ArrayList<Entry<String, Object>>(set);
    // Collections.sort(arrayList, new Comparator() {
    // public int compare(Object o1, Object o2) {
    // Map.Entry obj1 = (Map.Entry) o1;
    // Map.Entry obj2 = (Map.Entry) o2;
    // return (obj1.getKey()).toString().compareTo(obj2.getKey().toString());
    // }
    // });
    // StringBuffer sb = new StringBuffer();
    // for (Entry<String, Object> entry : arrayList) {
    // Object value = entry.getValue();
    // if (value instanceof String) {
    // String key = entry.getKey();
    // if (!"sign".equals(key)) {
    // sb.append(key).append("=").append(value).append("&");
    // }
    // }
    // }
    // String result = sb.toString();
    // System.out.println("zhaiyao:"+result);
    // result = result.substring(0, result.lastIndexOf("&"));
    // return result;
    // }


    /*
     * 字符串加密
     *
     * @author sun
     *
     * @date 2016年10月13日
     *
     * @param text 待加密的字符串
     *
     * @param encryptType 加密类型
     *
     * @return
     *
     * @throws Exception
     */
    public static String decrypt(String text, String encryptType, String appSecret, String appId) throws Exception {
        if (encryptType == null || "".equals(encryptType) || EncType.Plain.toString().equals(encryptType))
            return text;
        else if (EncType.AES.toString().equals(encryptType))
            return SecurityUtils.decryptByAES(text, appSecret, appId);
        else if (EncType.DES.toString().equals(encryptType))
            return SecurityUtils.decryptByDES(text, appSecret, appId);
        else
            return text;
    }

    public static String decryptByType(String encryptType, String encryptData) throws Exception {
        if ("AES".equals(encryptType)) {
            return decryptByAES(encryptData, SignUtils.APP_SECRET, SignUtils.APP_ID);
        } else if ("SM4".equals(encryptType)) {
            String sm4Key = DataFormater.byte2hex(SignUtils.APP_SECRET.getBytes()).substring(0, 16);
            return new SM4Utils(sm4Key, SignUtils.IV).decryptData_CBC(encryptData);
        } else {
            return encryptData;
        }
    }
}
