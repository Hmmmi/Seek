package com.android.ihbut0.seek.main.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.ChatListItem;
import com.android.ihbut0.seek.dao.ChatListDAO;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.InterestDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.main.ChatActivity;
import com.android.ihbut0.seek.main.LoginActivity;
import com.android.ihbut0.seek.main.MainActivity;
import com.android.ihbut0.seek.main.NearbyActivity;

import com.android.ihbut0.seek.utils.ElGamal;
import com.android.ihbut0.seek.utils.Element;
import com.android.ihbut0.seek.utils.IntersectionUtil;
import com.android.ihbut0.seek.utils.KeyPairUtil;
import com.android.ihbut0.seek.utils.LocalDataUtil;
import com.android.ihbut0.seek.utils.SSLUtil;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;
import com.mob.imsdk.MobIMMessageReceiver;
import com.mob.imsdk.MobIMReceiver;
import com.mob.imsdk.model.IMConversation;
import com.mob.imsdk.model.IMMessage;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListFragment extends Fragment {

    private static final String TAG = "MIJING";
    private static String PREFER_NAME = "LoginInfo";//保存登录状态标志

    private TextView titleTv;
    private TextView rightTv;
    private ImageView loadingImg;
    private LinearLayout noMsgLayout;

    private List<ChatListItem> chatListItems;
    private RecyclerView chatListRv;
    private ChatListAdapter chatListAdapter;

    private FloatingActionButton nearbyFab;

    private MobIMCallback<List<IMConversation>> conversationCallback;
    private MobIMReceiver generalReceiver;//连接IM
    private MobIMMessageReceiver messageReceiver;//接收消息

    private static final int DATA_CHANGED = 1;  //数据集更新
    private static final int DATA_INSERTED = -1;//数据集插入
    private static final int SET_DATA = 2;      //数据集插入

    private static final int INTERSECTION_1 = 4;

    private static final int GET_CI_SUCCESS = 5;    //交集协议获取ci成功
    private static final int GET_CI_FAILED = 6;     //交集协议获取ci失败

    private String fromAccount;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_CHANGED:
                    chatListAdapter.notifyDataSetChanged();
                    break;
                case DATA_INSERTED:
                    chatListAdapter.notifyItemInserted(msg.arg1);
                    chatListRv.scrollToPosition(chatListItems.size() - 1);
                    break;
                case SET_DATA:
                    ChatListDAO.setChatList(chatListItems);
                    chatListAdapter = new ChatListAdapter(chatListItems);
                    chatListRv.setAdapter(chatListAdapter);
                    break;
                case INTERSECTION_1:
                    Log.d(TAG, " > handleMessage: "+"INTERSECTION_0");
                    //获取ci[] 开启新线程
                    new Thread(getInterestsRun).start();
                    break;
                //交集协议 获取ci成功
                case GET_CI_SUCCESS:
                    //TODO 计算ei[] & sendMsg
                    String[] elements = (String[]) msg.obj;
                    Element[] ei = new Element[elements.length];
                    String interestsString = LocalDataUtil.getData(
                            "intent"+UserDAO.getLocalUser().getAccount() ,
                            PREFER_NAME, getContext() );
                    Log.d(TAG, "handleMessage: " + interestsString );
                    sendMsg(fromAccount,"1");
                    break;
                //交集协议 获取ci失败
                case GET_CI_FAILED:
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(
                R.layout.fragment_chat_list,
                container,
                false
        );

        initView(view);
        refreshData();
        initEvent();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MobIM.removeGeneralReceiver(generalReceiver);
        MobIM.removeMessageReceiver(messageReceiver);
    }

    private void initView(View view) {
        titleTv = view.findViewById(R.id.left_title_tv);
        rightTv = view.findViewById(R.id.right_tv);
        titleTv.setText("聊一聊");
        rightTv.setBackground(null);

        //rightTv.setText("刷新");

        loadingImg = view.findViewById(R.id.loading_img);
        noMsgLayout = view.findViewById(R.id.no_msg_layout);

        nearbyFab = view.findViewById(R.id.chat_list_nearby_fab);

        chatListItems = new ArrayList<>();
        chatListRv = view.findViewById(R.id.chat_list_recycler_view);
        chatListRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        //先加载会话
        messageReceiver = new MobIMMessageReceiver() {
            @Override
            public void onMessageReceived(List<IMMessage> list) {
                refreshData();
                for ( IMMessage message : list ){
                    String index = message.getBody().substring(0,13);
                    //STEP two 收到INTERSECTION0交互请求
                    if ( index.equals("INTERSECTION0") ){

                        Log.d(TAG, "STEP two: ");
                        Message msg = new Message();
                        msg.what = INTERSECTION_1;
                        fromAccount = message.getFrom();
                        handler.sendMessage(msg);
                        break;
                    }
                }
            }
        };

        generalReceiver = new MobIMReceiver() {
            public void onConnected() {
                //连接im成功后，关闭加载动画，并将其隐藏
                loadingImg.clearAnimation();
                loadingImg.setVisibility(View.GONE);
                //连接im成功后，刷新会话列表
                refreshData();
            }

            public void onConnecting() {
                //TODO 显示正在连接
                loadingImg.setVisibility(View.VISIBLE);
                Animation rotateAnimation = AnimationUtils.loadAnimation(
                        getContext(),
                        R.anim.rotate_anim
                );
                loadingImg.startAnimation(rotateAnimation);
            }

            public void onDisconnected(int error) {
                Toast.makeText(getContext(),"加载失败-_-",Toast.LENGTH_SHORT).show();
                //连接im成功后，关闭加载动画，并将其隐藏
                loadingImg.clearAnimation();
                loadingImg.setVisibility(View.GONE);
                android.os.Message message = new android.os.Message();
                message.what = SET_DATA;
                handler.sendMessage(message);
            }
        };

        MobIM.addMessageReceiver(messageReceiver);
        MobIM.addGeneralReceiver(generalReceiver);
    }

    private void refreshData() {
        MobIM.getChatManager().getAllLocalConversations(initConversationCallback());
    }

    private void initEvent() {

        nearbyFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NearbyActivity.class);
                getActivity().startActivity(intent);
            }
        });

        rightTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

    }

    private void sendMsg(String account, String msgCtx){
        IMMessage message = MobIM.getChatManager().createTextMessage(
                account,
                "INTERSECTION"+msgCtx,
                2);

        MobIM.getChatManager().sendMessage(message, new MobIMCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),
                        "SUCCESS RE -_-",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(getContext(),
                        "ERROR RE -_- ("+i+"):"+s,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    Runnable getInterestsRun = new Runnable() {
        @Override
        public void run() {
            //参数
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("phone", UserDAO.getLocalUser().getAccount());
            Message message = new Message();
            String res = SSLUtil.doSSLPost(getContext(),
                    "InterestServlet",
                    params);
            if ( res != null ){
                String[] elements = res.split("\n");
                message.what = GET_CI_SUCCESS;
                message.obj = elements;
                handler.sendMessage(message);
            } else {
                message.what = GET_CI_FAILED;
                handler.sendMessage(message);
            }
        }
    };

    //获取会话记录，接口回调机制
    private MobIMCallback<List<IMConversation>> initConversationCallback() {
        if (conversationCallback == null) {
            noMsgLayout.setVisibility(View.VISIBLE);
            conversationCallback = new MobIMCallback<List<IMConversation>>() {
                @Override
                public void onSuccess(List<IMConversation> imConversations) {
                    Log.d(TAG, "onSuccess: "+"success success size : " + imConversations.size());
                    if ( imConversations.size() != 0 ) {
                        noMsgLayout.setVisibility(View.GONE);
                    }
                    chatListItems.clear();
                    for (IMConversation conversation : imConversations) {
                        String friendAccount = conversation.getLastMessage().getFrom()//From是本地账号
                                .equals(UserDAO.getLocalUser().getAccount()) ?
                                conversation.getLastMessage().getTo() :
                                conversation.getLastMessage().getFrom();

                        ChatListItem item = new ChatListItem(friendAccount);
                        item.setLastMsg(conversation.getLastMessage().getBody());
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .format(conversation.getLastMessage().getCreateTime());
                        item.setLastTime(time);
//                        Log.d(TAG, "【"+friendAccount+"】:"+conversation.getUnreadMsgCount());
                        item.setMsgUnread(conversation.getUnreadMsgCount());
                        chatListItems.add(item);
                    }
                    android.os.Message message = new android.os.Message();
                    message.what = SET_DATA;
                    handler.sendMessage(message);
                }

                @Override
                public void onError(int i, String s) {
                    Log.d(TAG, "onError: i:[" + i+"]  "+s);
                }
            };
        }
        return conversationCallback;
    }


    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class ChatListHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private ChatListItem chatListItem;
        private boolean isClicked;

        private ImageView chatHeadImg;
        private TextView chatLabelTv;
        private TextView lastMsgTv;
        private TextView lastTimeTv;
        private TextView msgUnreadTv;

        public ChatListHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_chat_list, parent, false));
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            chatHeadImg = itemView.findViewById(R.id.chat_list_head_img);
            chatLabelTv = itemView.findViewById(R.id.chat_list_label_tv);
            lastMsgTv = itemView.findViewById(R.id.chat_list_last_msg_tv);
            lastTimeTv = itemView.findViewById(R.id.chat_list_time_tv);
            msgUnreadTv = itemView.findViewById(R.id.chat_list_msg_unread_tv);
        }

        public void bind(ChatListItem oldChatListItem) {
            //解决在修改昵称后返回聊天界面不刷新问题
            this.chatListItem = oldChatListItem;

            chatHeadImg.setImageDrawable(HeadImgDAO.getHeadImg(chatListItem.getChatHeadImg()));
            chatLabelTv.setText(chatListItem.getChatLabel());
            //获取最后一条消息记录
            String lastMsg = chatListItem.getLastMsg();
            String msgCtx = "";
            if ( lastMsg.length() > 12 &&
                    lastMsg.substring(0,12).equals("INTERSECTION")){
                msgCtx = "交集协议交互";
                msgUnreadTv.setVisibility(View.GONE);
                lastMsgTv.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            } else {
                msgCtx = lastMsg;
                int msgUnread = chatListItem.getMsgUnread();
                if (msgUnread == 0) {
                    msgUnreadTv.setVisibility(View.GONE);
                } else {
                    msgUnreadTv.setText("[" + chatListItem.getMsgUnread() + "]条未读");
                }
            }
            lastMsgTv.setText(msgCtx);

            String time = chatListItem.getLastTime();
            //获取当前时间的年月日
            String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(new Date())
                    .substring(0, 10);
            //如果当前年月日与最后一条消息的年月日 相同：只显示 HH：mm
//                                           不同：只显示 yyyy-MM-dd
            String lastTime = now.equals(time.substring(0, 10)) ?
                    time.substring(time.length() - 9, time.length() - 3) :
                    time.substring(0, time.length() - 9);

            lastTimeTv.setText(lastTime);
        }

        //单击事件监听
        @Override
        public void onClick(View v) {
            itemView.setClickable(false);
            Intent intent = new Intent(getContext(), ChatActivity.class);
            intent.putExtra("chatAccount", chatListItem.getChatAccount());
            getActivity().startActivity(intent);
            itemView.setClickable(true);
        }

        //长按事件监听
        @Override
        public boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("移除该聊天？");
            builder.setNegativeButton("取消", null);
            builder.setPositiveButton("移除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //删除该条聊天记录
                    boolean isDel = MobIM.getChatManager().delConversation(
                            chatListItem.getChatAccount(),
                            IMConversation.TYPE_USER);
                    if (isDel) {
                        refreshData();
                    } else {
                        Toast.makeText(getContext(),
                                "移除失败",
                                Toast.LENGTH_SHORT).show();
                    }

                }
            });
            AlertDialog dialog = builder.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            return true;
            //返回为true时，临界时间不会同时触发单击与长按两种事件
        }
    }

    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class ChatListAdapter extends RecyclerView.Adapter<ChatListHolder> {

        private List<ChatListItem> chatList;

        public ChatListAdapter(List<ChatListItem> chatList) {
            this.chatList = chatList;
        }

        @NonNull
        @Override
        public ChatListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ChatListHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatListHolder holder, int position) {
            ChatListItem item = chatList.get(position);
            holder.bind(item);
        }

        @Override
        public int getItemCount() {
            return chatList.size();
        }

    }

}
