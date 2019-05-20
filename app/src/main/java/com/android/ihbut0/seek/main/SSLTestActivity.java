package com.android.ihbut0.seek.main;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.utils.SSLUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;

import static com.mob.MobSDK.getContext;


public class SSLTestActivity extends AppCompatActivity {

    private static final String TAG = "MIJING-SSL";
    private static final int VERIFY_SUCCESS = 1;
    private static final int VERIFY_FAILED = 2;

    private static final String KEY_STORE_TYPE_JKS = "bks";
    private static final String KEY_STORE_TYPE_P12 = "PKCS12";
    private static final String SCHEME_HTTPS = "https";
    private static final int HTTPS_PORT = 8443;
    private static final String HTTPS_URL = "https://192.168.123.176:8443/SeekServer/servlet/" +
            "FriendServlet?phone=18872783811";
    private static final String KEY_STORE_CLIENT_PATH = "client.p12";
    private static final String KEY_STORE_TRUST_PATH = "client.truststore";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_STORE_TRUST_PASSWORD = "123456";

    private ImageView sslLoadingImg;
    private TextView loadingTv;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //旋转停止
            sslLoadingImg.clearAnimation();
            switch (msg.what){
                case VERIFY_SUCCESS:
                    Toast.makeText(SSLTestActivity.this,
                            "验证成功-_-",
                            Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(SSLTestActivity.this,MainActivity.class);
//                    startActivity(intent);
                    break;
                case VERIFY_FAILED:
                    loadingTv.setText("验证失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssltest);

        initView();
        new Thread(runnable).start();
    }

    private void initView(){
        sslLoadingImg = findViewById(R.id.ssl_loading_img);
        Animation rotateAnimation = AnimationUtils.loadAnimation(
                getContext(),
                R.anim.rotate_anim
        );
        sslLoadingImg.setAnimation(rotateAnimation);
        loadingTv = findViewById(R.id.loading_text);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String url="LoginServlet";

            Map params=new HashMap();
            params.put("phone","18872783822");
            params.put("password","188727838");
            String res = SSLUtil.doSSLPost(SSLTestActivity.this,url,params);
            Log.d(TAG, "run: "+res);
            if (res!=null){
                Message message = new Message();
                message.what = VERIFY_SUCCESS;
                handler.sendMessage(message);
            } else {
                Message message = new Message();
                message.what = VERIFY_FAILED;
                handler.sendMessage(message);
            }
//            doSSLPost(url, params,  charset);
        }
    };

    private void doSSLPost(String url, Map<String, String> map, String charset) {
        HttpClient httpClient = new DefaultHttpClient();
        String result = null;
        Message message = new Message();//与主线程交互的Message
        try {
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE_P12);
            KeyStore trustStore = KeyStore.getInstance(KEY_STORE_TYPE_JKS);
            InputStream ksIn = getAssets().open(KEY_STORE_CLIENT_PATH);//new FileInputStream(KEY_STORE_CLIENT_PATH);
            InputStream tsIn = getAssets().open(KEY_STORE_TRUST_PATH);//new FileInputStream(new File(KEY_STORE_TRUST_PATH));
            try {
                keyStore.load(ksIn, KEY_STORE_PASSWORD.toCharArray());
                trustStore.load(tsIn, KEY_STORE_TRUST_PASSWORD.toCharArray());
            } finally {
                try {
                    ksIn.close();
                    tsIn.close();
                } catch (Exception ignore) {
                    Log.d(TAG, "sslLogin IGNORE: " + ignore);
                }
            }
            SSLSocketFactory socketFactory = new SSLSocketFactory(keyStore, KEY_STORE_PASSWORD, trustStore);
            Scheme sch = new Scheme(SCHEME_HTTPS, socketFactory, HTTPS_PORT);
            httpClient.getConnectionManager().getSchemeRegistry().register(sch);
            HttpPost httpPost = new HttpPost(url);
            HttpResponse response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    result = EntityUtils.toString(resEntity, charset);
                }
            }
            Log.d(TAG, "doSSLPost: "+result);
            if (result.equals("VERIFY_SUCCESS")){
                message.what = VERIFY_SUCCESS;
            } else {
                message.what = VERIFY_FAILED;
            }
            handler.sendMessage(message);
        }catch(Exception e){
            Log.d(TAG, "doSSLPost ERROR: "+e);
            e.printStackTrace();
        }
    }
}
