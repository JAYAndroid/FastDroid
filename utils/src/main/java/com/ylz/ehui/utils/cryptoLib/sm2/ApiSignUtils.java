package com.ylz.ehui.utils.cryptoLib.sm2;



import com.ylz.ehui.utils.cryptoLib.sm3.Util;

import org.bouncycastle.util.Strings;


public class ApiSignUtils {
    public static final String USER_ID = "1234567812345678";

    public static String sign(String authPrik, String signContent) {
        try {
            return Util.byteToHex(SM2Util.sign(authPrik, Strings.toByteArray(USER_ID), signContent.getBytes("utf-8")));
        } catch (Exception e) {
            throw new RuntimeException("授权签名失败," + e.getMessage());
        }
    }

    public static boolean verify(String authPub, String signContent, String sign) {
        try {
            return SM2Util.verify(authPub, Strings.toByteArray(USER_ID), signContent.getBytes("utf-8"), Util.hexToByte(sign));
        } catch (Exception e) {
            throw new RuntimeException("授权验签失败," + e.getMessage());
        }
    }
}
