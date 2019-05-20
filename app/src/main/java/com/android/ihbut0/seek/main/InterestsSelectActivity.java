package com.android.ihbut0.seek.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.main.adapter.SimpleTreeRecyclerAdapter;
import com.multilevel.treelist.Node;
import com.multilevel.treelist.TreeRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

public class InterestsSelectActivity extends AppCompatActivity {

    private TextView title;
    private ImageView back;
    private ImageView menu;
    private Button finishBtn;

    private TreeRecyclerAdapter treeRecyclerAdapter;
    private RecyclerView interestsTreeRv;
    private List<Node> allInterests = new ArrayList<Node>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interests_select);

        initData();
        initView();
        initEvent();
    }

    private void initView(){
        title = findViewById(R.id.left_title_tv);
        back = findViewById(R.id.activity_back_img);
        menu = findViewById(R.id.setting_button);
        menu.setVisibility(View.INVISIBLE);
        title.setText("选择兴趣");
        finishBtn = findViewById(R.id.finish_button);

        interestsTreeRv = findViewById(R.id.interests_recycler_view);
        interestsTreeRv.setLayoutManager(new LinearLayoutManager(this));
        treeRecyclerAdapter = new SimpleTreeRecyclerAdapter(interestsTreeRv, InterestsSelectActivity.this,
                allInterests, 1,R.mipmap.tree_ex,R.mipmap.tree_ec);
        interestsTreeRv.setAdapter(treeRecyclerAdapter);
    }

    private void initData(){

        allInterests.add( new Node("category0", "-1", getString(R.string.interst_0) ) );
        allInterests.add( new Node("category1", "-1", getString(R.string.interst_1) ) );
        allInterests.add( new Node("category2", "-1", getString(R.string.interst_2) ) );
        allInterests.add( new Node("category3", "-1", getString(R.string.interst_3) ) );
        allInterests.add( new Node("category4", "-1", getString(R.string.interst_4) ) );
        allInterests.add( new Node("category5", "-1", getString(R.string.interst_5) ) );
        allInterests.add( new Node("category6", "-1", getString(R.string.interst_6) ) );
        allInterests.add( new Node("category7", "-1", getString(R.string.interst_7) ) );
        allInterests.add( new Node("category8", "-1", getString(R.string.interst_8) ) );
        allInterests.add( new Node("category9", "-1", getString(R.string.interst_9) ) );
        allInterests.add( new Node("category10", "-1", getString(R.string.interst_10) ) );
        allInterests.add( new Node("category11", "-1", getString(R.string.interst_11) ) );
        allInterests.add( new Node("category12", "-1", getString(R.string.interst_12) ) );
        allInterests.add( new Node("category13", "-1", getString(R.string.interst_13) ) );
        allInterests.add( new Node("category14", "-1", getString(R.string.interst_14) ) );
        allInterests.add( new Node("category15", "-1", getString(R.string.interst_15) ) );
        allInterests.add( new Node("category16", "-1", getString(R.string.interst_16) ) );
        allInterests.add( new Node("category17", "-1", getString(R.string.interst_17) ) );
        allInterests.add( new Node("category18", "-1", getString(R.string.interst_18) ) );
        allInterests.add( new Node("category19", "-1", getString(R.string.interst_19) ) );
        allInterests.add( new Node("category20", "-1", getString(R.string.interst_20) ) );
        allInterests.add( new Node("category21", "-1", getString(R.string.interst_21) ) );
        allInterests.add( new Node("category22", "-1", getString(R.string.interst_22) ) );
        allInterests.add( new Node("category23", "-1", getString(R.string.interst_23) ) );

        String[] categories = getString(R.string.intersts).split("-------------------- ");
        int i = 0, c =0 ;
        for ( String category : categories ) {

            String[] interests = category.split(" ");
            for ( String interest : interests ) {
                allInterests.add( new Node(""+i, "category"+c, interest ) );
                i++;
            }
            c++;
        }
    }

    private void initEvent(){

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //直接加密保存到服务器
                int count = 0;
                for ( Node n : allInterests) {
                    if ( n.isChecked() && n.getChildren().size()==0 ) {
                        Log.d("MIJING", "onClick: " + n.getId());
                        count++;
                    }
                }
            }
        });
    }

}
