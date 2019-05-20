package com.android.ihbut0.seek.main;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.utils.ElGamal;
import com.android.ihbut0.seek.utils.Element;
import com.android.ihbut0.seek.utils.KeyPairUtil;
import com.android.ihbut0.seek.utils.KeyUtil;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.android.ihbut0.seek.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class InterestManageActivity extends BaseActivity {

    private static final String TAG = "MIJING-Interest";

    private static final int GET_INTERESTS_SUCCESS = 1;
    private static final int GET_LOCAL_INTERESTS_SUCCESS = 11;
    private static final int GET_INTERESTS_FAILED = 2;
    private static final int SAVE_INTERESTS_SUCCESS = 3;
    private static final int SAVE_INTERESTS_FAILED = 4;

    private TextView backTv;
    private TextView addTv;
    private TextView titleTv;

    private ImageView loadingImg;

    private String intentJson = "";

    private List<Interest> interests;
    private RecyclerView interestRv;

    private String[] allInterests;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_LOCAL_INTERESTS_SUCCESS:
                    interests = InterestDAO.getInterests();
                    InterestAdapter adapter1 = new InterestAdapter(interests);
                    titleTv.setText("我的兴趣("+interests.size()+")");
                    interestRv.setAdapter(adapter1);
                    //兴趣获取后关闭加载动画，并将其隐藏
                    loadingImg.clearAnimation();
                    loadingImg.setVisibility(View.GONE);
                    break;
                case GET_INTERESTS_SUCCESS:
                    Set<Integer> id = (Set<Integer>) msg.obj;
                    interests = new ArrayList<>();
                    for ( int i : id ) {
                        Interest interest = new Interest(""+ i, allInterests[i]);
                        interests.add(interest);
                    }
                    InterestDAO.setInterests(interests);
                    InterestAdapter adapter = new InterestAdapter(interests);
                    titleTv.setText("我的兴趣("+interests.size()+")");
                    interestRv.setAdapter(adapter);
                    //兴趣获取后关闭加载动画，并将其隐藏
                    loadingImg.clearAnimation();
                    loadingImg.setVisibility(View.GONE);
                    break;
                case GET_INTERESTS_FAILED:
                    Toast.makeText(InterestManageActivity.this,
                            "兴趣获取失败-_-",
                            Toast.LENGTH_SHORT).show();
                    break;
                case SAVE_INTERESTS_SUCCESS:
                    Toast.makeText(InterestManageActivity.this,
                            "兴趣添加成功 : )",
                            Toast.LENGTH_SHORT).show();
                    initData();
                    break;
                case SAVE_INTERESTS_FAILED:
                    Toast.makeText(InterestManageActivity.this,
                            "兴趣添加失败 -_-",
                            Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_interests_manage);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initView(){
        backTv = findViewById(R.id.menu_back);
        titleTv = findViewById(R.id.menu_title_tv);
        addTv = findViewById(R.id.menu_right);
        addTv.setText("修改");

        loadingImg = findViewById(R.id.loading_img);
        loadingImg.setVisibility(View.VISIBLE);
        Animation rotateAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.rotate_anim
        );
        loadingImg.startAnimation(rotateAnimation);

        interestRv = findViewById(R.id.interest_recycler_view);
        interestRv.setLayoutManager(new LinearLayoutManager(InterestManageActivity.this));

        allInterests = getString(R.string.interests_line).split(",");
        Log.d(TAG, ">initView: "+allInterests.length);
    }

    private void initData(){
        titleTv.setText("我的兴趣");

        if ( InterestDAO.getInterests() == null ){
            new Thread(getInterestsRun).start();
            interests = InterestDAO.getInterests();
        } else {
            interests = InterestDAO.getInterests();
            Message message = new Message();
            message.what = GET_LOCAL_INTERESTS_SUCCESS;
            handler.sendMessage(message);
        }

    }

    private void initEvent(){
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addTv.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(InterestManageActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(InterestManageActivity.this);
                builder.setTitle("请输入新兴趣名称");
                builder.setView(editText , 48 , 32 , 48 , 0);
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String interestName = editText.getText().toString();
                        long time = System.currentTimeMillis();
                        KeyUtil keyUtil = new KeyUtil(getApplicationContext());
                        time = System.currentTimeMillis() - time;
                        Log.d(TAG, "SSL公私钥对初始化时间: "+time);

                        byte[] intentCipher = keyUtil.encrypt(interestName);
                        intentJson = Util.bytesToHex(intentCipher);

                        new Thread(saveInterestsRun).start();
                    }
                });

                builder.show();
            }
        });

    }

    Runnable saveInterestsRun = new Runnable() {
        @Override
        public void run() {
            //参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phone", UserDAO.getLocalUser().getAccount());
            params.put("intent", intentJson);
            Message message = new Message();
            String res = SSLUtil.doSSLPost(getApplicationContext(),
                    "SaveInterestServlet",
                    params);
            if ( res.equals("SAVE_SUCCESS") ){
                message.what = SAVE_INTERESTS_SUCCESS;
                handler.sendMessage(message);
            } else {
                message.what = SAVE_INTERESTS_FAILED;
                handler.sendMessage(message);
            }
        }
    };

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
            if ( res != null ){
                Set<Integer> id = new HashSet<>();
                String[] elements = res.split("\n");
                interests = new ArrayList<>(elements.length);
                for (int i = 0 ; i < elements.length ; i++){
                    String s = elements[i];
                    Element e = new Gson().fromJson(s, new TypeToken<Element>() {}.getType());
                    BigInteger decryptElement = ElGamal.decrypt(KeyPairUtil.getKeyPair(), e);

                    if (decryptElement.equals(BigInteger.ONE)){
                        id.add(i);
                    }
                }
                message.what = GET_INTERESTS_SUCCESS;
                message.obj = id;
                handler.sendMessage(message);
            } else {
                message.what = GET_INTERESTS_FAILED;
                handler.sendMessage(message);
            }
        }
    };


    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class InterestHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        private Interest interest;

        private TextView interestNameTv;

        public InterestHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_interest, parent, false));
            itemView.setOnLongClickListener(this);

            interestNameTv = itemView.findViewById(R.id.item_interest_text_view);
        }

        public void bind(Interest i){
            this.interest = i;
            interestNameTv.setText(interest.getIntentCtx());
        }

        @Override
        public boolean onLongClick(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(InterestManageActivity.this);
            builder.setTitle("确认删除兴趣【"+interest.getIntentCtx()+"】吗？");
            builder.setNegativeButton("取消",null);
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    AllInformation.removeInterest(interest.getIntentID());
                    initData();
                }
            });
            AlertDialog dialog = builder.show();
            //通过反射来改变dialog中btn的字体颜色
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            return false;
        }
    }


    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class InterestAdapter extends RecyclerView.Adapter<InterestHolder>{

        private List<Interest> interests;

        public InterestAdapter(List<Interest> interests){
            this.interests = interests;
        }

        @NonNull
        @Override
        public InterestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(InterestManageActivity.this);
            return new InterestManageActivity.InterestHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull InterestHolder holder, int position) {
            Interest interest = interests.get(position);
            holder.bind(interest);
        }

        @Override
        public int getItemCount() {
            return interests.size();
        }
    }

}
