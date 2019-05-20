package com.android.ihbut0.seek.main;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.SeekUser;
import com.android.ihbut0.seek.dao.SeekUserDAO;

import java.util.List;

public class SeekActivity extends BaseActivity {

    private LinearLayout mainLayout;

    private ImageView backTv;
    private TextView titleTv;

    private List<SeekUser> seekUsers;
    private RecyclerView seekUserRv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);

        initView();
        initData();
        initEvent();
    }

    private void initView(){
        mainLayout = findViewById(R.id.seek_main_layout);

        backTv = findViewById(R.id.activity_back_img);
        titleTv = findViewById(R.id.left_title_tv);
        titleTv.setText("匹配好友");

        seekUserRv = findViewById(R.id.seek_recycler_view);
        seekUserRv.setLayoutManager(new LinearLayoutManager(SeekActivity.this));
    }

    private void initData(){
        seekUsers = SeekUserDAO.getSeekUsers();
        SeekUserAdapter adapter = new SeekUserAdapter(seekUsers);
        seekUserRv.setAdapter(adapter);
    }

    private void initEvent(){
        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        seekUserRv.setOnTouchListener(this);
    }

    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class SeekUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private SeekUser seekUser;

        private ImageView headImg;
        private TextView nicknameTv;
        private TextView indexTv;

        public SeekUserHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.item_seek_list, parent, false));
            itemView.setOnClickListener(this);

            headImg = itemView.findViewById(R.id.seek_head_img);
            nicknameTv = itemView.findViewById(R.id.seek_nickname_tv);
            indexTv = itemView.findViewById(R.id.seek_index_tv);

        }

        public void bind(SeekUser s){
            this.seekUser = s;
            headImg.setImageDrawable(getResources().getDrawable(seekUser.getHeadImg()));
            nicknameTv.setText(seekUser.getNickname());
            indexTv.setText(seekUser.getIndex()+"%");
        }

        @Override
        public void onClick(View v) {

        }
    }

    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class SeekUserAdapter extends RecyclerView.Adapter<SeekUserHolder>{

        private List<SeekUser> seekUsers;

        public SeekUserAdapter(List<SeekUser> seekUsers){
            this.seekUsers = seekUsers;
        }

        @NonNull
        @Override
        public SeekUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(SeekActivity.this);
            return new SeekUserHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SeekUserHolder holder, int position) {
            SeekUser seekUser = seekUsers.get(position);
            holder.bind(seekUser);
        }

        @Override
        public int getItemCount() {
            return seekUsers.size();
        }
    }


}
