package com.android.ihbut0.seek.main;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMMessageReceiver;
import com.mob.imsdk.model.IMMessage;

import java.util.List;

public class BaseActivity extends AppCompatActivity implements View.OnTouchListener {

    /** 触摸时按下的点 **/
    PointF downP = new PointF();
    /** 触摸时当前的点 **/
    PointF curP = new PointF();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
//        curP.x = event.getX();
//        Log.d("MIJING", "---curP.x: "+curP.x);
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                downP.x = event.getX();
//                Log.d("MIJING DOWN", "---downP.x: "+downP.x);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (curP.x- downP.x > 500) {
//                    Log.i("MIJING", "move-=-=-=--=-");
//                    finish();
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//            default:
//                break;
//        }
        return true;
    }

}
