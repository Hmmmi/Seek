package com.android.ihbut0.seek.main;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.mob.MobSDK;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String APP_KEY = "2826a3379f736";
    private static final String APP_SECRET = "5d0343fdfc58fd4fd723089322c37b3e";

    private int countDown = 60;//倒计时

    private EditText accountEt;// 手机号输入框
    private EditText verifyCodeEt;//验证码
    private Button getCodeBtn;//获取验证码按钮
    private Button registerBtn;// 注册按钮


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏设置为透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }

    private void initView(){
        accountEt = (EditText) findViewById(R.id.register_account_edit_text);
        verifyCodeEt = (EditText) findViewById(R.id.register_verify_code_edit_text);
        getCodeBtn = (Button) findViewById(R.id.register_get_code_btn);
        registerBtn = (Button) findViewById(R.id.register_btn);

        //启动短信验证SDK
        MobSDK.init(this, APP_KEY, APP_SECRET);
        final EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message message = new Message();
                message.arg1 = event;
                message.arg2 = result;
                message.obj = data;
                handler.sendMessage(message);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }

    private void initEvent(){
        getCodeBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String phoneNum = accountEt.getText().toString();
        switch (v.getId()){
            case R.id.register_get_code_btn:
                //1.验证手机号的正确性
                if ( !isAccountTrue(phoneNum) ){
                    return;
                }
                //2.通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", phoneNum);
                //3.将获取验证码按钮设置为不可点击，并显示倒计时
                getCodeBtn.setClickable(false);
                getCodeBtn.setText("重新发送("+ countDown +")");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (; countDown > 0 ; countDown-- ){
                            handler.sendEmptyMessage(-9);
                            if ( countDown <= 0 ) {
                                break;
                            }
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(-8);
                    }
                }).start();
                break;
            case R.id.register_btn:
                SMSSDK.submitVerificationCode("86", phoneNum, verifyCodeEt.getText().toString());
                break;
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                getCodeBtn.setText("重新发送(" + countDown + ")");
            } else if (msg.what == -8) {
                getCodeBtn.setText("获取验证码");
                getCodeBtn.setClickable(true);
                countDown = 60;
            } else {
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        // 提交验证码成功
                        Toast.makeText(getApplicationContext(), "验证成功 :)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("userName", accountEt.getText().toString().trim());
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码 -_-", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "验证码不正确 :(", Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };

    private boolean isAccountTrue(String phoneNum) {
        if (phoneNum.length()==11 && isMobileNO(phoneNum)) {
            return true;
        }
        if ( phoneNum.isEmpty() ){
            Toast.makeText(this, "请输入手机号 -_-",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "请输入正确的手机号 -_-",Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNum) {
        /*
         *  移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         *  联通：130、131、132、152、155、156、185、186
         *  电信：133、153、180、189、（1349卫通）
         *  总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";
        // "[1]"代表第1位为数字1
        // "[358]"代表第二位可以为3、5、8中的一个
        // "\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNum)) {
            return false;
        } else {
            return mobileNum.matches(telRegex);
        }
    }

}
