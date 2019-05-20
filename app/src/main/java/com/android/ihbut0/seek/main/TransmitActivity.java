package com.android.ihbut0.seek.main;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.dao.ChatListDAO;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;

import java.util.ArrayList;
import java.util.List;

public class TransmitActivity extends BaseActivity {

    private LinearLayout mainLayout;

    private ImageView backImg;
    private TextView titleTv;
    private ImageView menuTv;

    private EditText transmitEt;
    private Button searchBtn;

    private List<Friend> transmitFriends;
    private RecyclerView transmitRv;

    private String transmitMsgCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmit);

        initView();
        initEvent();
    }

    private void initView(){
        mainLayout = findViewById(R.id.transmit_main_layout);

        transmitMsgCtx = getIntent().getStringExtra("transmitMsgCtx");
        backImg = findViewById(R.id.activity_back_img);
        titleTv = findViewById(R.id.left_title_tv);
        titleTv.setText("选择好友");
        menuTv = findViewById(R.id.setting_button);
        menuTv.setBackground(null);
        transmitRv = findViewById(R.id.transmit_recycler_view);

        transmitFriends = new ArrayList<>();
        for (ChatListItem item : ChatListDAO.getChatList() ){
            Friend friend = new Friend();
            friend.setAccount(item.getChatAccount());
            friend.setLabel(item.getChatLabel());
            friend.setHeadImg(item.getHeadImg());
            transmitFriends.add(friend);
        }

        TransmitAdapter adapter = new TransmitAdapter(transmitFriends);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        transmitRv.setLayoutManager(linearLayoutManager);
        transmitRv.setAdapter(adapter);
    }

    private void initEvent(){
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        mainLayout.setOnTouchListener(this);
    }

    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class TransmitHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Friend friend;

        private ImageView headImg;
        private TextView label;

        public TransmitHolder(LayoutInflater inflater, ViewGroup parent){
            super( inflater.inflate(R.layout.item_trainsmit_list,parent,false) );

            headImg = itemView.findViewById(R.id.transmit_head_img_view);
            label = itemView.findViewById(R.id.transmit_label);
            itemView.setOnClickListener(this);
        }

        public void bind(Friend f){
            this.friend = f;
            headImg.setImageDrawable(HeadImgDAO.getHeadImg(friend.getHeadImg()));
            label.setText(friend.getLabel());
        }

        @Override
        public void onClick(View v) {
            MobIM.getChatManager().sendMessage(
                    MobIM.getChatManager().createTextMessage(friend.getAccount(), transmitMsgCtx, 2),
                    new MobIMCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),
                                    "已转发",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    }
            );
        }
    }

    private class TransmitAdapter extends RecyclerView.Adapter<TransmitHolder>{

        private List<Friend> friends;

        public TransmitAdapter(List<Friend> friends){
            this.friends = friends;
        }

        @NonNull
        @Override
        public TransmitHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new TransmitHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull TransmitHolder holder, int position) {
            Friend friend = friends.get(position);
            holder.bind(friend);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }
    }
}
