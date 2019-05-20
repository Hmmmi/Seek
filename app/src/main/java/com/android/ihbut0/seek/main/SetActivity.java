package com.android.ihbut0.seek.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ihbut0.seek.R;

public class SetActivity extends BaseActivity {

    private LinearLayout mainLayout;

    private ImageView backTv;
    private TextView titleTv;
    private ImageView settingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        initView();
        initData();
        initEvent();
    }

    private void initView(){
        mainLayout = findViewById(R.id.set_main_layout);

        backTv = findViewById(R.id.activity_back_img);
        titleTv = findViewById(R.id.left_title_tv);
        titleTv.setText("更      多");
        settingBtn = findViewById(R.id.setting_button);
        settingBtn.setClickable(false);
    }

    private void initData(){}

    private void initEvent(){
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mainLayout.setOnTouchListener(this);
    }

}
