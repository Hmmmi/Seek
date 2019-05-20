package com.android.ihbut0.seek.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.math.BigInteger;

public class Util {

    /**
     * 时间比较 yyyy-MM-dd hh:m:ss
     * 小于：-1
     * 大于：1
     * 等于：0
     * @param time1
     * @param time2
     * @return
     */
    public static int compareTime(String time1, String time2){
        int y1 = Integer.valueOf( time1.substring(0,4) );
        int M1 = Integer.valueOf( time1.substring(5,7) );
        int d1 = Integer.valueOf( time1.substring(8,10) );
        int h1 = Integer.valueOf( time1.substring(11,13) );
        int m1 = Integer.valueOf( time1.substring(14,16) );
        int s1 = Integer.valueOf( time1.substring(17) );

        int y2 = Integer.valueOf( time2.substring(0,4) );
        int M2 = Integer.valueOf( time2.substring(5,7) );
        int d2 = Integer.valueOf( time2.substring(8,10) );
        int h2 = Integer.valueOf( time2.substring(11,13) );
        int m2 = Integer.valueOf( time2.substring(14,16) );
        int s2 = Integer.valueOf( time2.substring(17) );

        if ( y1 < y2 ){
            return -1;
        } else if (y1 > y2){
            return 1;
        }

        if ( M1 < M2 ){
            return -1;
        } else if (M1 > M2){
            return 1;
        }

        if ( d1 < d2 ){
            return -1;
        } else if (d1 > d2){
            return 1;
        }

        if ( h1 < h2 ){
            return -1;
        } else if (h1 > h2){
            return 1;
        }

        if ( m1 < m2 ){
            return -1;
        } else if (m1 > m2){
            return 1;
        }

        if ( s1 < s2 ){
            return -1;
        } else if (s1 > s2){
            return 1;
        }

        return 0;
    }

    /**
     * 显示byte[]数组内容
     * @param name
     * @param bs
     */
    public static String showBytes(String name, byte[] bs) {
        String result = "";
        result += name+":{";
        int i = 0;
        for(byte b:bs){
            result += b+",";
            i++;
        }
        result += "} : "+i+"\n";
        return result;
    }

    /**
     * 方法三：
     * byte[] to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hexToBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    /**
     * 将本地的兴趣信息转换为大数
     *
     * @param binInterests
     * @return
     */
    public static BigInteger[] binToBigInt(String binInterests){
        int len = binInterests.length();
        BigInteger[] res = new BigInteger[len];

        for (int i = 0 ; i < len ; i++ ){
            char c = binInterests.charAt(i);
            BigInteger bigInteger = new BigInteger(""+c);
            res[i] = bigInteger;
        }

        return res;
    }

}
