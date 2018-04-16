package com.ylz.ehui.utils;

import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 类功能说明
 *
 * @author sun
 * @version 1.0.0
 */

public class AESUtils {
    public static final String VIPARA = "0102030405060708";
    public static final String BM = "UTF-8";

    public static String encrypt(String content, String password) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes("UTF-8"));
        SecretKeySpec key = getKey(password);
        //new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(content.getBytes(BM));
        String encryptResultStr = parseByte2HexStr(encryptedData);
//		String encryptResultStr = Base64.encode(encryptedData);
        return encryptResultStr; // 加密
    }

    public static String decrypt(String content, String password) throws Exception {
        content = new String(content.getBytes("UTF-8"));
        byte[] decryptFrom = parseHexStr2Byte(content);
//		byte[] decryptFrom = Base64.decode(content);
        IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes("UTF-8"));
        SecretKeySpec key = getKey(password);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte[] decryptedData = cipher.doFinal(decryptFrom);

        return new String(decryptedData,BM);
    }


    private static SecretKeySpec getKey(String strKey) throws Exception {
        byte[] arrBTmp = strKey.getBytes("UTF-8");
        byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）

        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        SecretKeySpec skeySpec = new SecretKeySpec(arrB, "AES");

        return skeySpec;
    }

    /**
     * 将二进制转换成16进制
     *
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    public static String genAesKey(int length) { //length表示生成字符串的长度
        String base = "ABCDEFGHIJKLMpqrstuvwNOPRSTUVWXklmnopqrstuvQRSTUVWXYZabcdefghijLMNOPQwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String password = "q!w@e3r$";

//		 String str = encrypt("q!w@e3r$", password);
//		 System.out.println("--" + str);

        String str1 = encrypt("1234567890123456", password);
        System.out.println("--" + str1);
//		String oldStr = decrypt("CFA6E071093E16A948D171A71678B233", password);
//		System.out.println("====" + oldStr);
    }

}
