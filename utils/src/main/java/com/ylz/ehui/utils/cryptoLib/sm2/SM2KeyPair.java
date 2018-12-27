package com.ylz.ehui.utils.cryptoLib.sm2;


import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.test.FixedSecureRandom;

import java.math.BigInteger;

/**
 * Author: yms
 * Time: 2018/12/27 13:56
 * Describe:
 */
public class SM2KeyPair {

    /**
     * 公钥
     */
    private ECPoint publicKey;

    /**
     * 私钥
     */
    private BigInteger privateKey;

    SM2KeyPair(ECPoint publicKey, BigInteger privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public ECPoint getPublicKey() {
        return publicKey;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }
}
