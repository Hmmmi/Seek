package com.android.ihbut0.seek.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;

public class SSLUtil {
    private static final String KEY_STORE_TYPE_BKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String SCHEME_HTTPS = "https";
    private static final int HTTPS_PORT = 8443;
    private static final String KEY_STORE_CLIENT_PATH = "client.p12";
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";
    private static final String CHAR_SET_FORMAT = "UTF-8";
    private static final String ORIGIN_ADDRESS = "https://118.178.136.10:8443/SeekServer/servlet/";

    public static String doSSLPost(Context context, String url, Map<String, String> map){
        HttpClient httpClient = new DefaultHttpClient();
        String result = null;
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_BKS);
            InputStream ksIn = context.getAssets().open(KEY_STORE_CLIENT_PATH);
            InputStream tsIn = context.getAssets().open(KEY_STORE_TRUST_PATH);
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } finally {
                try {
                    ksIn.close();
                    tsIn.close();
                } catch (Exception ignore) {
                }
            }

            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, trustStore);
            Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
            HttpPost httpPost = new HttpPost(ORIGIN_ADDRESS+url);
            //设置参数
            if (map != null) {
                List<NameValuePair> list = new ArrayList<NameValuePair>();
                Iterator iterator = map.entrySet().iterator();
                while (iterator.hasNext()) {
					Map.Entry<String, String> elem = (Map.Entry<String, String>) iterator.next();
                    list.add(new BasicNameValuePair(elem.getKey(), elem.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(list, CHAR_SET_FORMAT));
            }
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, CHAR_SET_FORMAT);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

}
