package com.android.ihbut0.seek.utils;

import java.math.BigInteger;
import java.util.Map;

public class BinIntersectionUtil {

    public static Element[] calEi(Element[] c, BigInteger[] b, Map<String, BigInteger> pkB, Element[] zeros) {
        int len = c.length;
        Element[] res = new Element[len];
        for (int i = 0; i < len; i++) {
            Element tmp = c[i].pow(b[i], pkB.get("pk_p"));
            res[i] = tmp.multiply(zeros[i], pkB.get("pk_p"));
        }
        return res;
    }

    public static Element[] getZeroCipher(Map<String, BigInteger> pk, int len){
        Element[] res = new Element[len];
        for (int i = 0 ; i < len ; i++ ) {
            Element element = ElGamal.encrypt(pk, BigInteger.ZERO);
            res[i] = element;
        }
        return res;
    }

}
