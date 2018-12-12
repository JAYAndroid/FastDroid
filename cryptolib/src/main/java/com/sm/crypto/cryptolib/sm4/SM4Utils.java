package com.sm.crypto.cryptolib.sm4;


import com.sm.crypto.cryptolib.DataFormater;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SM4Utils {
	public static final String BM = "UTF-8";
	private String secretKey = "";
	private String iv = "";

	public SM4Utils() {
	}

	public SM4Utils(String secretKey) {
		super();
		this.secretKey = secretKey;
	}

	public void setSecretKey(String secretKey){
		this.secretKey = secretKey;
	}

	public SM4Utils(String secretKey, String iv) {
		super();
		this.secretKey = secretKey;
		this.iv = iv;
	}

	public String encryptData_ECB(String plainText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			byte[] keyBytes = getKey(secretKey);
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_ecb(ctx, plainText.getBytes(BM));
			String cipherText = DataFormater.byte2hex(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText.toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decryptData_ECB(String cipherText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			byte[] keyBytes = getKey(secretKey);
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_ecb(ctx, DataFormater.parseHexStr2Byte(cipherText));
			return new String(decrypted, BM);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] getKey(String strKey) throws Exception {
		byte[] arrBTmp = strKey.getBytes(BM);
		byte[] arrB = new byte[16]; // 创建一个空的16位字节数组（默认值为0）
		for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
			arrB[i] = arrBTmp[i];
		}
		return arrB;
	}

	public String encryptData_CBC(String plainText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_ENCRYPT;
			byte[] ivBytes = iv.getBytes(BM);
			byte[] keyBytes = getKey(secretKey);
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_enc(ctx, keyBytes);
			byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, plainText.getBytes(BM));
			String cipherText = DataFormater.byte2hex(encrypted);
			if (cipherText != null && cipherText.trim().length() > 0) {
				Pattern p = Pattern.compile("\\s*|\t|\r|\n");
				Matcher m = p.matcher(cipherText);
				cipherText = m.replaceAll("");
			}
			return cipherText.toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String decryptData_CBC(String cipherText) {
		try {
			SM4_Context ctx = new SM4_Context();
			ctx.isPadding = true;
			ctx.mode = SM4.SM4_DECRYPT;
			byte[] ivBytes = iv.getBytes(BM);
			byte[] keyBytes = getKey(secretKey);
			SM4 sm4 = new SM4();
			sm4.sm4_setkey_dec(ctx, keyBytes);
			byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, DataFormater.parseHexStr2Byte(cipherText));
			return new String(decrypted, BM);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws IOException {
		String password = "q!w@e3r$";
		SM4Utils sm4 = new SM4Utils(password, "0102030405060708");
		String str1 = sm4.encryptData_CBC("1234567890123456");
		System.out.println("encryptdata:" + str1);
		String oldStr = sm4.decryptData_CBC(str1);
		System.out.println("====" + oldStr);
	}
}