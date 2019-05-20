package com.android.ihbut0.seek.main;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.main.fragment.SingleFragment;
import com.android.ihbut0.seek.main.view.adapter.ViewPagerAdapter;
import com.android.ihbut0.seek.main.view.view.BottomIndicator;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;
import com.mob.imsdk.MobIM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MIJING";

    private static final int GET_SUCCESS = 1;   //获取好友成功
    private static final int GET_FAIL = 2;      //获取好友失败
    private List<Friend> friends;

    private User mine;

    private ViewPager mViewPager;//装载fragment的容器
    private FragmentPagerAdapter mPagerAdapter;//fragment适配器

    private List<String> mTitles = Arrays.asList("Chat","Friend","Me");
    private List<Fragment> mFragments = new ArrayList<>();

    private BottomIndicator mIndicator;//底部tab栏


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_FAIL:
                    Toast.makeText(MainActivity.this,
                            "aou",
                            Toast.LENGTH_SHORT).show();
                    break;
                case GET_SUCCESS:
                    FriendDAO.setFriends(friends);
                    break;
            }
            initData();
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

        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mViewPager = (ViewPager)findViewById(R.id.viewPager);
        mIndicator = (BottomIndicator)findViewById(R.id.bottom_indicator);

        MobSDK.init(this, AllInformation.APP_KEY, AllInformation.APP_SECRET);

        mine = UserDAO.getLocalUser();
        MobSDK.setUser(mine.getAccount(), mine.getNickname(), null, null);

        //消息接收监听
        new Thread(getFriendRun).start();

    }

    private void initData() {
        for(String title : mTitles)
        {
            mFragments.add(SingleFragment.newInstance(title));
        }
        mPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),this,mFragments);
        mViewPager.setAdapter(mPagerAdapter);
        mIndicator.setViewPager(mViewPager);
    }

    Runnable getFriendRun = new Runnable() {
        @Override
        public void run() {
            getFriends();
        }
    };

    private void getFriends(){
        //与服务器交互
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("phone", UserDAO.getLocalUser().getAccount());
        Message message = new Message();

        String res = SSLUtil.doSSLPost(getApplicationContext(),
                "FriendServlet",
                params);

        if ( res != null ){
            Gson gson = new Gson();
            friends = gson.fromJson(res, new TypeToken<List<Friend>>() {}.getType());
            message.what = GET_SUCCESS;
            handler.sendMessage(message);
        } else {
            message.what = GET_FAIL;
            handler.sendMessage(message);
        }
    }


    /**
     * 物理返回按钮监听事件
     * 登录后，在主页面返回直接返回到桌面，而不是直接finish()返回到LoginActivity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void refreshMsgUnread(){
        int allUnreadMsgCounts = MobIM.getChatManager().getAllUnreadMessageCount(true);
        Log.d(TAG, "refreshMsgUnread: "+allUnreadMsgCounts);
    }
}
