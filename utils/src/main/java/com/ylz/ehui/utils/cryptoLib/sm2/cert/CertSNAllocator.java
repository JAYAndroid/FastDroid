package com.ylz.ehui.utils.cryptoLib.sm2.cert;

import java.math.BigInteger;

public interface CertSNAllocator {
    BigInteger incrementAndGet() throws Exception;
}
