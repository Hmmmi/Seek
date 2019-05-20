package com.android.ihbut0.seek.main;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.Friend;
import com.android.ihbut0.seek.bean.Message;
import com.android.ihbut0.seek.dao.FriendDAO;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.mob.imsdk.MobIM;
import com.mob.imsdk.MobIMCallback;
import com.mob.imsdk.MobIMMessageReceiver;
import com.mob.imsdk.model.IMConversation;
import com.mob.imsdk.model.IMMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseActivity {

    private LinearLayout mainLayout;

    private Friend friend;
    private List<Message> messages;

    private ImageView backBtnImg;
    private TextView friendLabelTv;
    private ImageView friendInfoImg;

    private RecyclerView messageRv;
    private MessageAdapter messageAdapter;

    private EditText chatEditText;
    private Button sendBtn;
    private boolean isSendClicked;

    private MobIMMessageReceiver receiver;

    private static final int DATA_CHANGED = 1;//数据集更新
    private static final int DATA_INSERTED = 2;//数据集插入


    Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DATA_CHANGED:
                    new Thread(removeIntersectionIndex).start();
                    messageAdapter.notifyDataSetChanged();
                    break;
                case DATA_INSERTED:
                    messageAdapter.notifyItemInserted(msg.arg1);
                    messageRv.scrollToPosition(messages.size() - 1);
                    break;
            }
            //将该会话所有消息标记为已读
            MobIM.getChatManager().markConversationAllMessageAsRead(friend.getAccount(),//会话账号
                    IMConversation.TYPE_USER);//会话类型
        }
    };

    Runnable removeIntersectionIndex = new Runnable() {
        @Override
        public void run() {
            Iterator<Message> iterator = messages.iterator();
            while (iterator.hasNext()){
                Message message = iterator.next();
                if ( message.getMsgCtx().length() > 12
                        && message.getMsgCtx().substring(0,12)
                        .equals("INTERSECTION") ){
                    iterator.remove();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initView();
        initData();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //将该会话所有消息标记为已读
        MobIM.getChatManager().markConversationAllMessageAsRead(friend.getAccount(),//会话账号
                IMConversation.TYPE_USER);//会话类型
        //activity在销毁时需要注销MessageReceiver
        //否则回到activity后不能监听
        MobIM.removeMessageReceiver(receiver);
    }

    private void initView() {
        //初始化控件
        mainLayout = findViewById(R.id.chat_activity_main_layout);

        backBtnImg = (ImageView) findViewById(R.id.chat_back_img);
        friendLabelTv = (TextView) findViewById(R.id.chat_friend_label_tv);
        friendInfoImg = (ImageView) findViewById(R.id.chat_friend_info_img);
        messageRv = (RecyclerView) findViewById(R.id.chat_msg_recycler_view);

        chatEditText = (EditText) findViewById(R.id.chat_edit_text);
        sendBtn = (Button) findViewById(R.id.chat_send_btn);
    }

    private void initData() {
        friend = FriendDAO.getFriend(getIntent().getStringExtra("chatAccount"));

        //设值
        friendLabelTv.setText(friend.getLabel());

        //适配RecyclerView
        messages = getMessages();
        messageAdapter = new MessageAdapter(messages);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //使RecyclerView自动回滚到底部
        linearLayoutManager.setStackFromEnd(true);
        messageRv.setLayoutManager(linearLayoutManager);
        messageRv.setAdapter(messageAdapter);

        getReceiveMsg();
    }

    private void initEvent() {
        backBtnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friendLabelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friendInfoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, FriendInfoActivity.class);
                intent.putExtra("friendAccount", friend.getAccount());
                startActivity(intent);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBtn.setClickable(false);
                String msgCtx = chatEditText.getText().toString();
                chatEditText.setText("");
                String sendAccount = UserDAO.getLocalUser().getAccount();
                String receiveAccount = friend.getAccount();
                sendMsg(msgCtx, sendAccount, receiveAccount);
                sendBtn.setClickable(true);
            }
        });

        //触碰事件监听
//        messageRv.setOnTouchListener(this);
    }

    //发送消息
    private void sendMsg(String msgCtx, String from, String to) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        if (msgCtx.isEmpty()) {
            Toast.makeText(getApplicationContext(), "请输入消息内容", Toast.LENGTH_SHORT).show();
        } else {
            MobIM.getChatManager().sendMessage(MobIM.getChatManager().createTextMessage(to, msgCtx, 2),
                    new MobIMCallback<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ChatActivity.this,
                                    "【消息发送成功】to(" + to + ")：" + msgCtx,
                                    Toast.LENGTH_SHORT).show();
                            Message message = new Message(UUID.randomUUID(), from, to, date, 0, msgCtx);
                            messages.add(message);
                            messageAdapter.notifyItemInserted(messages.size() - 1);
                            messageRv.scrollToPosition(messages.size() - 1);
                        }

                        @Override
                        public void onError(int i, String s) {
                            Toast.makeText(ChatActivity.this,
                                    "错误码"+"【"+i+"】：" + s,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    /*** 分页获取会话历史消息记录 -- 逆序 ***/
    private List<Message> getMessages() {
        List<Message> messagesTmp = new ArrayList<>();

        MobIM.getChatManager().getMessageList(friend.getAccount(),
                IMConversation.TYPE_USER,
                100, 1, new MobIMCallback<List<IMMessage>>() {

                    SimpleDateFormat simpleDateFormat =
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    @Override
                    public void onSuccess(List<IMMessage> imMessages) {
                        for (IMMessage message : imMessages) {
                            Message m = new Message(UUID.randomUUID(), //MessageID
                                    message.getFrom(), //sendAccount
                                    message.getTo(), //receiveAccount
                                    simpleDateFormat.format(new Date(message.getCreateTime())), //createTime
                                    message.getType(), message.getBody()); //msgCtx
                            messagesTmp.add(m);
                        }
                        Collections.reverse(messagesTmp);
                        messages = messagesTmp;

                        android.os.Message message = new android.os.Message();
                        message.what = DATA_CHANGED;
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d("MIJING", "onError: 哈哈消息失败：" + i + "  " + s);
                    }

                });
        return messagesTmp;
    }

    //收到消息时回调
    private void getReceiveMsg() {

        receiver = new MobIMMessageReceiver() {
            @Override
            public void onMessageReceived(List<IMMessage> list) {
                Log.d("MIJING", "收到消息数量 onMessageReceived: "+list.size());
                android.os.Message message = new android.os.Message();
                message.what = DATA_INSERTED;
                message.arg1 = messages.size();

                //收到消息时监听事件
                for (IMMessage imMessage : list) {
                    Message m = new Message(UUID.randomUUID(), //MessageID
                            imMessage.getFrom(), //sendAccount
                            imMessage.getTo(), //receiveAccount
                            new SimpleDateFormat("").format
                                    (new Date(imMessage.getCreateTime())), //createTime
                            imMessage.getType(), imMessage.getBody()); //msgCtx
                    messages.add(m);
                }
                new Thread(removeIntersectionIndex).start();
                handler.sendMessage(message);
            }
        };
        MobIM.addMessageReceiver(receiver);
    }


    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class MessageHolder extends RecyclerView.ViewHolder {

        private Message message;

        private ImageView leftImg;
        private ImageView rightImg;
        private TextView msgTv;

        private LinearLayout msgLayout;

        public MessageHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_message, parent, false));
            leftImg = (ImageView) itemView.findViewById(R.id.message_left_img);
            rightImg = (ImageView) itemView.findViewById(R.id.message_right_img);
            msgTv = (TextView) itemView.findViewById(R.id.message_text_view);

            msgLayout = (LinearLayout) itemView.findViewById(R.id.message_text_layout);

            msgTv.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    //弹出框
                    String items[] = {"复制", "转发"};
                    generateDialog(items, msgTv.getText().toString());
                    return false;
                }
            });
        }

        //长按弹出框
        private void generateDialog(final String[] items, final String msgCtx) {

            AlertDialog builder = new AlertDialog.Builder(ChatActivity.this)
                    .setItems(items, new DialogInterface.OnClickListener() {//添加列表
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0://复制
                                    //剪切板
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    //需要复制的数据
                                    ClipData clipData = ClipData.newPlainText("message", msgCtx);
                                    clipboard.setPrimaryClip(clipData);
                                    Toast.makeText(ChatActivity.this,
                                            "复制成功",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    break;
                                case 1://转发
                                    Intent intent = new Intent(ChatActivity.this, TransmitActivity.class);
                                    intent.putExtra("transmitMsgCtx", msgCtx);
                                    startActivity(intent);
                                    break;
                            }
                        }
                    }).create();
            builder.show();

            Window dialogWindow = builder.getWindow();
            WindowManager.LayoutParams params = dialogWindow.getAttributes();

            Point point = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            // 将window的宽高信息保存在point中
            display.getSize(point);
            // 将设置后的大小赋值给window的宽高
            params.width = (int) (point.x * 0.35f);
//            params.height = (int) (point.y * 0.5f);
            // 方式一：设置属性
            dialogWindow.setAttributes(params);
        }

        public void bind(Message msg) {
            this.message = msg;
            //STEP 1.消息显示布局
            // 如果是本机用户发送，显示right_img
            if (message.getSendAccount().equals(UserDAO.getLocalUser().getAccount())) {
                leftImg.setImageDrawable(null);
                leftImg.setBackground(null);
                rightImg.setImageDrawable(HeadImgDAO.getHeadImg(UserDAO.getLocalUser().getHeadImg()));
                msgLayout.setGravity(Gravity.RIGHT | Gravity.CENTER);
                msgTv.setBackground(getResources().getDrawable(R.drawable.bg_message_right));
                msgTv.setTextColor(getResources().getColor(R.color.white_text));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        msgTv.getLayoutParams());
                params.setMargins(24,0,0,0);
                msgTv.setLayoutParams(params);
            } else {
                Friend friend = FriendDAO.getFriend(message.getSendAccount());
                leftImg.setImageDrawable(HeadImgDAO.getHeadImg(friend.getHeadImg()));
                rightImg.setImageDrawable(null);
                rightImg.setBackground(null);
                msgLayout.setGravity(Gravity.LEFT | Gravity.CENTER);
                msgTv.setBackground(getResources().getDrawable(R.drawable.bg_message));
                msgTv.setTextColor(getResources().getColor(R.color.black_text));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        msgTv.getLayoutParams());
                params.setMargins(0,0,24,0);
                msgTv.setLayoutParams(params);
            }
            //STEP 2.消息分类别显示
            //暂时只支持文本信息
            msgTv.setText(message.getMsgCtx());
        }
    }


    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {

        private List<Message> messages;

        public MessageAdapter(List<Message> messages) {
            this.messages = messages;
        }

        @NonNull
        @Override
        public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new MessageHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
            Message message = messages.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }

}
