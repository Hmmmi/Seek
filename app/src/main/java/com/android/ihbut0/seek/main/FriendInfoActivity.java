package com.android.ihbut0.seek.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.bean.Message;
import com.android.ihbut0.seek.dao.ChatListDAO;
import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.main.view.view.BottomDialog;
import com.android.ihbut0.seek.utils.Blur;
import com.android.ihbut0.seek.utils.KeyUtil;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.android.ihbut0.seek.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.MobSDK;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;
import com.mob.imsdk.model.IMUser;

import java.util.HashMap;
import java.util.List;

public class FriendInfoActivity extends BaseActivity {

    //EditText在弹出框中的margin参数
    private static final int ET_MARGIN_LEFT = 48;
    private static final int ET_MARGIN_TOP = 32;
    private static final int ET_MARGIN_RIGHT = 48;
    private static final int ET_MARGIN_BOTTOM = 0;

    private Friend friend;

    private LinearLayout mainLayout;

    private LinearLayout interestLayout;//共同兴趣Layout

    private ImageView backImg;
    private TextView indexTv;
    private TextView moreTv;

    //用户相关信息
    private LinearLayout friendInfoBg;
    private ImageView friendInfoHeadImg;
    private TextView friendInfoLabelTv;
    private TextView friendInfoSignTv;
    private TextView friendInfoAccountTv;
    private TextView friendInfoBirthTv;
    private TextView friendInfoSexTv;
    private TextView friendInfoAddTv;
    private TextView friendInfoEduTv;

    private Button chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏设置为透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_friend_info);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView(){
        mainLayout = findViewById(R.id.friend_info_main_layout);

        backImg = findViewById(R.id.friend_info_back_img);
        moreTv = findViewById(R.id.friend_info_more_tv);

        interestLayout = findViewById(R.id.friend_info_interest_layout);

        friendInfoBg = findViewById(R.id.friend_info_layout);
        friendInfoHeadImg = findViewById(R.id.friend_info_head_img);
        friendInfoLabelTv = findViewById(R.id.friend_info_label_tv);
        friendInfoSignTv = findViewById(R.id.friend_info_sign_tv);
        friendInfoAccountTv = findViewById(R.id.friend_info_account_tv);
        friendInfoBirthTv = findViewById(R.id.friend_info_birth_tv);
        friendInfoSexTv = findViewById(R.id.friend_info_sex_tv);
        friendInfoAddTv = findViewById(R.id.friend_info_add_tv);
        friendInfoEduTv = findViewById(R.id.friend_info_edu_tv);

        chatBtn = findViewById(R.id.friend_info_chat_btn);
    }

    private void initData(){
        Intent intent = getIntent();
        String friendAccount = intent.getStringExtra("friendAccount");
        friend = FriendDAO.getFriend(friendAccount);

        //将背景图片模糊化
        Bitmap bitmap = Blur.blurBitmap(getApplicationContext(), R.drawable.mypagebackground, (int) 25);
        friendInfoBg.setBackground( new BitmapDrawable(bitmap) );

        friendInfoHeadImg.setImageDrawable(HeadImgDAO.getHeadImg(friend.getHeadImg()));
        friendInfoLabelTv.setText(friend.getLabel()+"("+friend.getNickname()+")");
        friendInfoSignTv.setText(friend.getSign());
        friendInfoAccountTv.setText(friend.getAccount());
        friendInfoBirthTv.setText(friend.getBirthday());
        friendInfoSexTv.setText(friend.getSex()==0?"男":"女");
        friendInfoAddTv.setText(friend.getAddress());
        friendInfoEduTv.setText(friend.getEducation());
    }

    /**
     * 初始化控件监听事件
     */
    private void initEvent(){

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = LayoutInflater.from(FriendInfoActivity.this).inflate(R.layout.bottom_dialog, null);
                BottomDialog bottomDialog  = new BottomDialog(FriendInfoActivity.this, rootView, true, true);
                bottomDialog.show();
                TextView cancelTv = rootView.findViewById(R.id.dialog_cancel_tv);
                TextView deleteTv = rootView.findViewById(R.id.dialog_delete_tv);
                TextView deFriendTv = rootView.findViewById(R.id.dialog_de_friend_tv);
                cancelTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomDialog.dismiss();
                    }
                });
                deleteTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        删除好友
//                        AllInformation.removeFriend(friend.getAccount());
                        bottomDialog.dismiss();
                        finish();
                    }
                });
                deFriendTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = friend.getAccount();
                Intent intent = new Intent(FriendInfoActivity.this, ChatActivity.class);
                intent.putExtra("chatAccount",account);
                startActivity(intent);
            }
        });

        interestLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendInfoActivity.this, CommonInterestsActivity.class);
                intent.putExtra("friendAccount",friend.getAccount());
                intent.putExtra("friendPk",friend.getBackground());
                startActivity(intent);
            }
        });

        friendInfoLabelTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(FriendInfoActivity.this);
                editText.setHint(friend.getLabel());

                AlertDialog.Builder builder = new AlertDialog.Builder(FriendInfoActivity.this);
                builder.setTitle("好友备注");

                builder.setView(editText , ET_MARGIN_LEFT , ET_MARGIN_TOP , ET_MARGIN_RIGHT , ET_MARGIN_BOTTOM);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String label = editText.getText().toString();
//                        if( !label.isEmpty() ){
//                            friendInfoLabelTv.setText(label+"("+friend.getNickname()+")");
////                            AllInformation.saveFriendLabel(friend.getAccount(), label);
//                        }
                    }
                });

                builder.show();
            }
        });

        mainLayout.setOnTouchListener(this);
    }

}
