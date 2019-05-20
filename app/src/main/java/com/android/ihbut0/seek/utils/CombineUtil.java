package com.android.ihbut0.seek.utils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class CombineUtil {

    public static String[] getQx(String[] x){
        List<BigInteger> data = new ArrayList<BigInteger>();
        for( String xString : x ){
            data.add(new BigInteger(xString.getBytes()).abs().negate());
        }
        Combine t = new Combine();
        t.setData(data);
        t.calculateCoefs();
        return t.putResult();
    }

}
