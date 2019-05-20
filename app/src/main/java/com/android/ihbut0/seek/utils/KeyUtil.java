package com.android.ihbut0.seek.utils;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import javax.crypto.Cipher;

public class KeyUtil {

    private static final String KEY_STORE_TYPE_BKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String KEY_STORE_CLIENT_PATH = "client.p12";
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";

    private PrivateKey privateKey;
    private PublicKey publicKey;

    public KeyUtil(Context context){
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            InputStream ksIn = context.getAssets().open(KEY_STORE_CLIENT_PATH);
            InputStream tsIn = context.getAssets().open(KEY_STORE_TRUST_PATH);
            try {
                keyStore.load(ksIn,
                        KEY_STORE_PASSWORD.toCharArray());
                trustStore.load(tsIn,
                        KEY_STORE_TRUST_PASSWORD.toCharArray());
            } finally {
                try {
                    ksIn.close();
                    tsIn.close();
                } catch (Exception ignore) {
                }
            }

            Enumeration enumAliases = keyStore.aliases();
            String keyAlias = null;
            if (enumAliases.hasMoreElements()) {
                keyAlias = (String) enumAliases.nextElement();
            }
            privateKey = (PrivateKey) keyStore.getKey(keyAlias,
                    KEY_STORE_PASSWORD.toCharArray());
            Certificate cert = keyStore.getCertificate(keyAlias);
            publicKey = cert.getPublicKey();

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }

    }

    /**
     * 加密
     * @param plaintext
     * @return
     */
    public byte[] encrypt(String plaintext){
        byte[] res = new byte[256];
        try {
            byte[] msg = plaintext.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            res = cipher.doFinal(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 解密
     * @param cipherText
     * @return
     */
    public String decrypt(byte[] cipherText){
        String m = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] resBytes = cipher.doFinal(cipherText);
            m = new String(resBytes, "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;
    }

}
