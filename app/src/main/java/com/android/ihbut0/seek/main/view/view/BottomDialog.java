package com.android.ihbut0.seek.main.view.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.ihbut0.seek.R;

public class BottomDialog extends Dialog {

    private boolean isCancelable;//控制点击dialog外部是否dismiss
    private boolean isBackCancelable;//控制返回键是否dismiss
    private View view;
    private Context context;

    //这里的view其实可以替换直接传layout过来的 因为各种原因没传(lan)

    public BottomDialog(@NonNull Context context,View view, boolean cancelable, boolean isBackCancelable) {
        super(context, R.style.BottomDialog);

        this.context = context;
        this.view = view;
        this.isCancelable = cancelable;
        this.isBackCancelable = isBackCancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(view);
        setCancelable(isCancelable);
        setCanceledOnTouchOutside(isBackCancelable);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
    }
}
