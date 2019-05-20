package com.android.ihbut0.seek.main;

import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Interest;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.utils.BinIntersectionUtil;
import com.android.ihbut0.seek.utils.ElGamal;
import com.android.ihbut0.seek.utils.Element;
import com.android.ihbut0.seek.utils.KeyPairUtil;
import com.android.ihbut0.seek.utils.LocalDataUtil;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.android.ihbut0.seek.utils.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;
import com.mob.imsdk.MobIMMessageReceiver;
import com.mob.imsdk.model.IMMessage;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CommonInterestsActivity extends BaseActivity {
    private static final String TAG = "MIJING";

    private LinearLayout mainLayout;

    private TextView backTv;
    private TextView titleTv;
    private TextView rightTv;

    private ImageView loadingImg;

    private static String PREFER_NAME = "LoginInfo";//保存登录状态标志

    private static String friendAccount;
    private static String friendKeyJson;
    private static String[] allInterests;

    private static final int GET_FRIEND_INTERESTS_SUCCESS = 0;
    private static final int GET_FRIEND_INTERESTS_FAILED = 1;

    private List<Interest> commonInterests;
    private RecyclerView commonInterestsRv;

    private MobIMMessageReceiver intersReceiver;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_FRIEND_INTERESTS_SUCCESS:
                    Element[] ci = (Element[]) msg.obj;
                    Log.d(TAG, "handleMessage: "+ci.length);
                    //
                    String localString = LocalDataUtil.getData("intent"+ UserDAO.getLocalUser().getAccount(),
                            PREFER_NAME, getApplicationContext());
                    BigInteger[] bi = Util.binToBigInt(localString);

                    Map<String, BigInteger> pk = new Gson().fromJson(friendKeyJson,
                            new TypeToken<Map<String, BigInteger>>() {}.getType() );
                    Element[] zi = getZeroCipher(friendAccount, pk, bi.length);

                    Element[] ei = BinIntersectionUtil.calEi(ci, bi, pk, zi);

                    commonInterests = new ArrayList<>();
                    int count = 0;
                    for (int i = 0 ; i < ei.length ; i++ ) {
                        Element element = ei[i];
                        BigInteger b = ElGamal.decrypt(pk, element);
                        if ( b.equals(BigInteger.ONE) ) {
                            count++;
                            Interest interest = new Interest(""+ i, allInterests[i]);
                            commonInterests.add(interest);
                        }
                    }
                    titleTv.append("("+count+")");
//
                    CommonInterestsAdapter adapter = new CommonInterestsAdapter(commonInterests);
                    commonInterestsRv.setAdapter(adapter);
                    //交集获取后关闭加载动画，并将其隐藏
                    loadingImg.clearAnimation();
                    loadingImg.setVisibility(View.GONE);
                    break;
                case GET_FRIEND_INTERESTS_FAILED:
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
        setContentView(R.layout.activity_common_interests);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MobIM.removeMessageReceiver(intersReceiver);
    }

    private void initView(){
        mainLayout = findViewById(R.id.comm_interest_main_layout);

        backTv = findViewById(R.id.menu_back);
        rightTv = findViewById(R.id.menu_right);
        rightTv.setText("");
        titleTv = findViewById(R.id.menu_title_tv);
        titleTv.setText("共同兴趣");

        loadingImg = findViewById(R.id.loading_img);
        Animation rotateAnimation = AnimationUtils.loadAnimation(
                getApplicationContext(),
                R.anim.rotate_anim
        );
        loadingImg.startAnimation(rotateAnimation);


        commonInterestsRv = findViewById(R.id.common_interest_recycler_view);
        commonInterestsRv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData(){
        friendAccount = getIntent().getExtras().getString("friendAccount");
        friendKeyJson = getIntent().getExtras().getString("friendPk");
        allInterests = getString(R.string.interests_line).split(",");
//        Log.d(TAG, "1 > initData: "+friendAccount);
//        Log.d(TAG, "2 > initData: "+friendKeyJson);
        new Thread(getFrdItrRunnable).start();

        //1.发送获取交集信号
        //sendMsg(friendAccount, "0");
    }

    private void initEvent(){
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    Runnable getFrdItrRunnable = new Runnable() {
        @Override
        public void run() {
            //参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phone", friendAccount);
            Message message = new Message();
            String res = SSLUtil.doSSLPost(getApplicationContext(),
                    "InterestServlet",
                    params);
            if ( res != null && !res.isEmpty() ){
                String[] elementString = res.split("\n");
                Element[] elements = new Element[elementString.length];
                for ( int i = 0 ; i < elements.length ; i++ ){
                    String s = elementString[i];
                    Element e = new Gson().fromJson(s, new TypeToken<Element>() {}.getType());
                    elements[i] = e;
                }
                message.obj = elements;
                message.what = GET_FRIEND_INTERESTS_SUCCESS;
                handler.sendMessage(message);
            } else {
                message.what = GET_FRIEND_INTERESTS_FAILED;
                handler.sendMessage(message);
            }
        }
    };

    private void getMsg(){
        intersReceiver = new MobIMMessageReceiver() {
            @Override
            public void onMessageReceived(List<IMMessage> list) {
                for ( IMMessage message : list ){
                    String index = message.getBody().substring(0,13);
                    //STEP two 收到INTERSECTION0交互请求
                    if ( index.equals("INTERSECTION1") ) {
                        //TODO 解密
                        //TODO 显示
                        Log.d(TAG, "STEP three: ");

                        break;
                    }
                }
            }
        };
        MobIM.addMessageReceiver(intersReceiver);
    }

    private void sendMsg(String friendAccount, String msgCtx){
        IMMessage message = MobIM.getChatManager().createTextMessage(
                friendAccount,
                "INTERSECTION"+msgCtx,
                2);

        MobIM.getChatManager().sendMessage(message, new MobIMCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(CommonInterestsActivity.this,
                        "SUCCESS -_-",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(CommonInterestsActivity.this,
                        "ERROR -_- ("+i+"):"+s,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Element[] getZeroCipher(String friendAccount, Map<String, BigInteger> pk, int len){
        String cipher = LocalDataUtil.getData( "zeroCipher"+friendAccount,
                PREFER_NAME, getApplicationContext() );
        Element[] zeroCipher = null;
        if ( cipher.equals(LocalDataUtil.DEFAULT_VALUE) ) {
            Log.d(TAG, "calculate ZeroCipher: ");
            //encrypt
            zeroCipher = BinIntersectionUtil.getZeroCipher(pk, len);
            //save
            String cipherJson = new Gson().toJson(zeroCipher);
            LocalDataUtil.saveData("zeroCipher"+friendAccount, cipherJson,
                    PREFER_NAME, getApplicationContext() );
        } else {
            Log.d(TAG, "local ZeroCipher: ");
            String cipherJson = LocalDataUtil.getData("zeroCipher"+friendAccount,
                    PREFER_NAME, getApplicationContext() );
            zeroCipher = new Gson().fromJson(cipherJson,
                    new TypeToken<Element[]>() {}.getType() );
        }
        return zeroCipher;
    }

    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class CommonInterestsHolder extends RecyclerView.ViewHolder{

        private Interest interest;

        private TextView interestNameTv;

        public CommonInterestsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_interest, parent, false));
//            itemView.setOnLongClickListener(this);

            interestNameTv = itemView.findViewById(R.id.item_interest_text_view);
        }

        public void bind(Interest i){
            this.interest = i;
            interestNameTv.setText(interest.getIntentCtx());
        }

    }

    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class CommonInterestsAdapter extends RecyclerView.Adapter<CommonInterestsHolder>{

        private List<Interest> commonInterests;

        public CommonInterestsAdapter(List<Interest> commonInterests){
            this.commonInterests = commonInterests;
        }

        @NonNull
        @Override
        public CommonInterestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(CommonInterestsActivity.this);
            return new CommonInterestsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CommonInterestsHolder holder, int position) {
            Interest interest = commonInterests.get(position);
            holder.bind(interest);
        }

        @Override
        public int getItemCount() {
            return commonInterests.size();
        }
    }
}
