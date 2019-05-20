package com.android.ihbut0.seek.main.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.main.DataManageActivity;
import com.android.ihbut0.seek.main.InterestManageActivity;
import com.android.ihbut0.seek.main.SetActivity;
import com.android.ihbut0.seek.utils.Blur;


public class MineFragment extends Fragment {

    private User mine;

    //用户相关信息
    private ImageView headImg;
    private TextView signTextView;
    private TextView nicknameTextView;

    private LinearLayout dataBackgroundLayout;

    //菜单栏三个按钮
    private LinearLayout dataManageLayout;
    private LinearLayout intentManageLayout;
    private LinearLayout settingLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_mine, container, false);
        initView(view);
        initData();
        initEvent();

        return view;
    }

    //重返前台
    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 初始化控件
     */
    private void initView(View view){
        dataManageLayout = view.findViewById(R.id.layout_data_management);
        intentManageLayout = view.findViewById(R.id.layout_intent_management);
        settingLayout = view.findViewById(R.id.layout_setting);
        dataBackgroundLayout = view.findViewById(R.id.background_layout);

        headImg = view.findViewById(R.id.head_img_view);
        signTextView = view.findViewById(R.id.sign_text_view);
        nicknameTextView = view.findViewById(R.id.nickname_text_view);
    }

    /**
     * 初始化数据
     */
    private void initData(){
        //获取用户信息
        mine = UserDAO.getLocalUser();
        //更新与用户相关控件信息
        headImg.setImageDrawable(HeadImgDAO.getHeadImg(mine.getHeadImg()));
        nicknameTextView.setText(mine.getNickname());
        signTextView.setText(mine.getSign());

        //将背景图片模糊化
        Bitmap bitmap = Blur.blurBitmap(getContext(), R.drawable.img_background1, (int) 25);
        dataBackgroundLayout.setBackground(new BitmapDrawable(bitmap));
//        dataBackgroundLayout.setBackground(getResources().getDrawable(R.drawable.img_background1));
    }

    /**
     * 控件的监听事件
     */
    private void initEvent(){
        //资料管理
        dataManageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), DataManageActivity.class);
                getActivity().startActivity(intent);
            }
        });
        intentManageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), InterestManageActivity.class);
                getActivity().startActivity(intent);
            }
        });
        settingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SetActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }

}
