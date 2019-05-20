package com.android.ihbut0.seek.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.android.ihbut0.seek.main.FriendInfoActivity;
import com.android.ihbut0.seek.utils.HttpCallbackListener;
import com.android.ihbut0.seek.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendFragment extends Fragment {

    private TextView titleTv;
    private TextView rightTv;

    private List<Friend> friends;
    private RecyclerView friendRv;
    private FriendAdapter friendAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        initView(view);
        initData();

        return view;
    }

    private void initView(View view){
        titleTv = view.findViewById(R.id.left_title_tv);
        rightTv = view.findViewById(R.id.right_tv);
        titleTv.setText("好友");
//        rightTv.setText("添加");

        friends = FriendDAO.getFriends();
        friendAdapter = new FriendAdapter(friends);
        friendRv = (RecyclerView) view.findViewById(R.id.friend_recycle_view);

        friendRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendRv.setAdapter(friendAdapter);
    }

    //初始化数据
    private void initData(){
//        getFriends();
    }


    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Friend friend;

        private ImageView friendImg;
        private TextView friendLabelTv;
//        private TextView friendIndexTv;

        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_friend_list, parent, false));

            //监听点击事件
            itemView.setOnClickListener(this);

            friendImg = itemView.findViewById(R.id.chat_list_head_img);
            friendLabelTv = itemView.findViewById(R.id.friend_label_tv);
//            friendIndexTv = (TextView)itemView.findViewById(R.id.friend_index_tv);
        }

        public void bind(Friend friend){
            this.friend = friend;
            friendImg.setImageDrawable(HeadImgDAO.getHeadImg(friend.getHeadImg()));
            friendLabelTv.setText(friend.getLabel());
//            friendIndexTv.setText(String.valueOf(friend.getIndex())+"%");
        }

        //点击事件响应
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getContext(),FriendInfoActivity.class);
            intent.putExtra("friendAccount",friend.getAccount());
            getActivity().startActivity(intent);
        }

    }

    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class FriendAdapter extends RecyclerView.Adapter<FriendHolder>{

        private List<Friend> friends;

        public FriendAdapter(List<Friend> friends){
            this.friends = friends;
        }

        @NonNull
        @Override
        public FriendHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());

            return new FriendHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull FriendHolder holder, int position) {
            Friend friend = friends.get(position);
            holder.bind(friend);
        }

        @Override
        public int getItemCount() {
            return friends.size();
        }
    }

}
