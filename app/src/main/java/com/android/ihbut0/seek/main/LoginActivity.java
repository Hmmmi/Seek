package com.android.ihbut0.seek.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.utils.ElGamal;
import com.android.ihbut0.seek.utils.Element;
import com.android.ihbut0.seek.utils.KeyPairUtil;
import com.android.ihbut0.seek.utils.LocalDataUtil;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "MIJING";

    private EditText phoneEt;
    private EditText passwordEt;
    private Button loginBtn;

    private TextView registerTv;

    private SharedPreferences preferences;//用来保存登录状态
    private static String PREFER_NAME = "LoginInfo";//保存登录状态标志

    private String phone;
    private String password;

    private static final int LOGIN_SUCCESS = 0;
    private static final int LOGIN_FAILED = 1;
    private static final int GET_INTERESTS_SUCCESS = 2;
    private static final int GET_INTERESTS_FAILED = 3;

    /**
     * 用于处理消息的Handler
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String result = "";
            switch ( msg.what ) {
                case LOGIN_SUCCESS :
                    User user = new Gson().fromJson(msg.obj.toString(), User.class);
                    UserDAO.setLocalUser(user);
                    result = "欢迎使用";

                    saveLoginInfo();
                    checkIntentData();
                    break;
                case LOGIN_FAILED :
                    result = "登录失败";
                    passwordEt.setText("");
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    break;
                case GET_INTERESTS_SUCCESS :
                    result = "欢迎使用";
                    Intent i1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(i1);
                    break;
                case GET_INTERESTS_FAILED :
                    result = "你好像还没有选择兴趣呢 -_-";
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                    Intent i2 = new Intent(LoginActivity.this, InterestsSelectActivity.class);
                    startActivity(i2);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏设置为透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_login);

        initView();
        initData();
        initEvent();
    }

    private void initView() {
        phoneEt = findViewById(R.id.phone_edit_text);
        passwordEt = findViewById(R.id.password_edit_text);
        loginBtn = findViewById(R.id.login_button);

        registerTv = findViewById(R.id.login_register_tv);

        preferences = getApplicationContext().getSharedPreferences(PREFER_NAME,
                MODE_PRIVATE);

        saveHeadImgs();
    }

    private void initData() {

        String account = preferences.getString("account", "");
        String password = preferences.getString("password", "");
        phoneEt.setText(account);
        passwordEt.setText(password);
    }

    private void initEvent() {

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = phoneEt.getText().toString();
                password = passwordEt.getText().toString();
                new Thread(runnable).start();
            }
        });

        registerTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(User.PHONE, phone);
            params.put(User.PASSWORD, password);
            String res = SSLUtil.doSSLPost(getApplicationContext(),
                    "LoginServlet",
                    params);
            Log.d("TTTT", "RES run: "+res);
            if ( res.equals("LoginFail") ) {
                Message message = new Message();
                message.what = LOGIN_FAILED;
                mHandler.sendMessage(message);
            } else {
                Message message = new Message();
                message.obj = res;
                message.what = LOGIN_SUCCESS;
                mHandler.sendMessage(message);
            }
        }
    };

    /**
     * 保存登录信息
     */
    private void saveLoginInfo(){
        if ( preferences.getString("account", "def").equals("def") ){
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString("account", UserDAO.getLocalUser().getAccount());
            editor.putString("password", UserDAO.getLocalUser().getPassword());
            editor.commit();    //提交
        }
    }

    Runnable getInterestsRun = new Runnable() {
        @Override
        public void run() {
            //参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phone", UserDAO.getLocalUser().getAccount());
            Message message = new Message();
            String res = SSLUtil.doSSLPost(getApplicationContext(),
                    "InterestServlet",
                    params);
            if ( res != null && !res.isEmpty() ){
                String[] elements = res.split("\n");
                List<Interest> interests = new ArrayList<>(elements.length);
                for (String s : elements ) {
                    Element e = new Gson().fromJson(s, new TypeToken<Element>() {}.getType());
                    BigInteger decryptElement = ElGamal.decrypt(KeyPairUtil.getKeyPair(), e);
                    Interest i = new Interest(""+ UUID.randomUUID(), ""+decryptElement);
                    interests.add(i);
                }
                InterestDAO.setInterests(interests);
                //把兴趣数据保存到本地
                String interestsDada = "";
                for ( Interest i : interests ) {
                    interestsDada += i.getIntentCtx();
                }
                LocalDataUtil.saveData("intent"+UserDAO.getLocalUser().getAccount(),
                        interestsDada, PREFER_NAME, getApplicationContext());
                message.what = GET_INTERESTS_SUCCESS;
                mHandler.sendMessage(message);
            } else {
                message.what = GET_INTERESTS_FAILED;
                mHandler.sendMessage(message);
            }
        }
    };

    /**
     * 检查兴趣数据
     */
    private void checkIntentData(){
        Intent intent = null;

        String flag = preferences.getString("intent"+UserDAO.getLocalUser().getAccount(), "def");
        //Log.d(TAG, "checkIntentData: 】"+flag+"【");
        if ( flag.equals("def") || flag.isEmpty() ){
            new Thread(getInterestsRun).start();
        } else {
            String localString = LocalDataUtil.getData("intent"+UserDAO.getLocalUser().getAccount(), PREFER_NAME, getApplicationContext());
            //Log.d( TAG, "IntentData: 本地数据 >  "+localString );
            //TODO 将01串变成List<Interest>对象，直接调用InterestDAO的接口
            intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

    private void saveHeadImgs(){
        Drawable[] headImgs = new Drawable[8];
        headImgs[0] = getResources().getDrawable(R.drawable.u1);
        headImgs[1] = getResources().getDrawable(R.drawable.u2);
        headImgs[2] = getResources().getDrawable(R.drawable.u3);
        headImgs[3] = getResources().getDrawable(R.drawable.u4);
        headImgs[4] = getResources().getDrawable(R.drawable.u5);
        headImgs[5] = getResources().getDrawable(R.drawable.u6);
        headImgs[6] = getResources().getDrawable(R.drawable.u7);
        headImgs[7] = getResources().getDrawable(R.drawable.u8);
        HeadImgDAO.setHeadImgs(headImgs);
    }
}
