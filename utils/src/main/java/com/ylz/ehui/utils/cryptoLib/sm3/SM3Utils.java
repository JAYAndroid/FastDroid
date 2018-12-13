package com.ylz.ehui.utils.cryptoLib.sm3;



import com.ylz.ehui.utils.cryptoLib.DataFormater;

import java.io.UnsupportedEncodingException;

public class SM3Utils {

    public static String encrypt(String result) {
        try {
            byte[] md = new byte[32];
            byte[] msg1 = result.getBytes("UTF-8");
            SM3Digest sm3 = new SM3Digest();
            sm3.update(msg1, 0, msg1.length);
            sm3.doFinal(md, 0);
            String s = new String(DataFormater.byte2hex(md));
            return s.toUpperCase();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("加密失败, " + e.getMessage());
        }
    }
}
