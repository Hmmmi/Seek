package com.android.ihbut0.seek.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.User;
import com.android.ihbut0.seek.dao.HeadImgDAO;
import com.android.ihbut0.seek.dao.UserDAO;
import com.android.ihbut0.seek.info.AllInformation;
import com.lljjcoder.citypickerview.widget.CityPicker;

import java.util.Calendar;

public class DataManageActivity extends BaseActivity {

    private User mine;

    private LinearLayout mainLayout;

    //EditText在弹出框中的margin参数
    private static final int ET_MARGIN_LEFT = 48;
    private static final int ET_MARGIN_TOP = 32;
    private static final int ET_MARGIN_RIGHT = 48;
    private static final int ET_MARGIN_BOTTOM = 0;

    //资料更改点击按钮
    private LinearLayout headLayout;
    private LinearLayout nicknameLayout;
    private LinearLayout signLayout;
    private LinearLayout birthLayout;
    private LinearLayout sexLayout;
    private LinearLayout addLayout;
    private LinearLayout eduLayout;
    private LinearLayout accountLayout;
    private LinearLayout passwordLayout;
    //与用户相关控件
    private ImageView headImg;
    private TextView nicknameTv;
    private TextView signTv;
    private TextView birthTv;
    private TextView sexTv;
    private TextView addTv;
    private TextView eduTv;
    private TextView accountTv;
    //返回按钮
    private TextView backTv;
    //完成按钮
    private TextView finishTv;
    //标题
    private TextView titleTv;
    //是否修改标记
    private boolean isChange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏设置为透明
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_data_manage);
        initView();
        initData();
        initEvent();
    }

    /**
     * 物理返回按钮点击监听事件
     */
    @Override
    public void onBackPressed() {
        backTv.callOnClick();
    }

    private void initView(){
        mainLayout = findViewById(R.id.data_manage_main_layout);

        headLayout = (LinearLayout) findViewById(R.id.headimg_layout);
        nicknameLayout = (LinearLayout) findViewById(R.id.nickname_layout);
        signLayout = (LinearLayout) findViewById(R.id.sign_layout);
        birthLayout = (LinearLayout) findViewById(R.id.birth_layout);
        sexLayout = (LinearLayout) findViewById(R.id.sex_layout);
        addLayout = (LinearLayout) findViewById(R.id.add_layout);
        eduLayout = (LinearLayout) findViewById(R.id.edu_layout);
        accountLayout = (LinearLayout) findViewById(R.id.account_layout);
        passwordLayout = (LinearLayout) findViewById(R.id.password_layout);

        headImg = (ImageView) findViewById(R.id.data_head_img);
        nicknameTv = (TextView) findViewById(R.id.data_nickname_tv);
        signTv = (TextView) findViewById(R.id.data_sign_tv);
        birthTv = (TextView) findViewById(R.id.data_birth_tv);
        sexTv = (TextView) findViewById(R.id.data_sex_tv);
        addTv = (TextView) findViewById(R.id.data_add_tv);
        eduTv = (TextView) findViewById(R.id.data_edu_tv);
        accountTv = (TextView) findViewById(R.id.data_account_tv);

        backTv = (TextView) findViewById(R.id.menu_back);
        finishTv = (TextView) findViewById(R.id.menu_right);
        titleTv = (TextView) findViewById(R.id.menu_title_tv);
    }

    private void initData(){
        titleTv.setText("我的资料");

        mine = UserDAO.getLocalUser();

        headImg.setImageDrawable(HeadImgDAO.getHeadImg(mine.getHeadImg()));
        nicknameTv.setText(mine.getNickname());
        signTv.setText(mine.getSign());
        birthTv.setText(mine.getBirthday().toString());
        sexTv.setText(mine.getSex()==0 ? "男":"女");
        addTv.setText(mine.getAddress());
        eduTv.setText(mine.getEducation());

        accountTv.setText(mine.getAccount());
    }

    private void initEvent(){

        backTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isChange ){
                    notifyMine();
                    UserDAO.setLocalUser(mine);
                    Toast.makeText(DataManageActivity.this,"已自动保存您修改内容",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        finishTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (isChange){
                    notifyMine();
                    UserDAO.setLocalUser(mine);
                    Toast.makeText(DataManageActivity.this,"已修改",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(DataManageActivity.this,"未修改内容",Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });

        //昵称修改
        nicknameLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                newStringDialog(nicknameTv, "新昵称", 8);
            }
        });

        //签名修改
        signLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newStringDialog(signTv, "新签名", 30);
            }
        });

        //生日修改
        birthLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String birth = birthTv.getText().toString();
                int year = Integer.valueOf(birth.substring(0,4));
                int month = Integer.valueOf(birth.substring(5,7))-1;
                int day = Integer.valueOf(birth.substring(8));

                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        birthTv.setText(year+"-"+
                                String.format("%02d", Integer.valueOf(month+1))+"-"+
                                String.format("%02d", dayOfMonth) );
                        isChange = true;
                    }
                };
                DatePickerDialog dialog = new DatePickerDialog(DataManageActivity.this,
                        listener, year, month ,day);

                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMaxDate(System.currentTimeMillis());

                dialog.show();
            }
        });

        //性别修改
        sexLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] sex = {"男","女"};
                int checkItem = sexTv.getText().toString().equals("男")?0:1;//设置默认选项
                AlertDialog.Builder builder = new AlertDialog.Builder(DataManageActivity.this);
                builder.setTitle("性别");
                builder.setSingleChoiceItems(sex, checkItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexTv.setText(sex[which]);
                        isChange = true;
                        dialog.dismiss();//点击后自动关闭弹出框
                    }
                });
                builder.show();
            }
        });

        //所在地修改
        addLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldAdd = addTv.getText().toString();
                String[] oldAddArray = oldAdd.split(" ");
                String oldProvince,oldCity,oldDistrict;
                if ( oldAddArray.length == 3 ){
                    oldProvince = oldAddArray[0];
                    oldCity = oldAddArray[1];
                    oldDistrict = oldAddArray[2];
                } else {
                    oldProvince = "北京市";
                    oldCity = "北京市";
                    oldDistrict = "昌平区";
                }

                CityPicker.Builder builder = new CityPicker.Builder(DataManageActivity.this);
                builder.title("所在地");
                builder.province(oldProvince);
                builder.city(oldCity);
                builder.district(oldDistrict);
                builder.provinceCyclic(true);
                builder.cityCyclic(false);
                builder.districtCyclic(false);
                builder.visibleItemsCount(11);
                builder.itemPadding(12);
                builder.onlyShowProvinceAndCity(false);

                CityPicker picker = builder.build();

                picker.setOnCityItemClickListener(new CityPicker.OnCityItemClickListener() {
                    @Override
                    public void onSelected(String... citySelected) {
                        //省份
                        String province = citySelected[0];
                        //城市
                        String city =citySelected[1];
                        //区县（如果设定了两级联动，那么该项返回空）
                        String district = citySelected[2];

                        addTv.setText(province+" "+city+" "+district);
                        isChange = true;
                    }
                });

                picker.show();
            }
        });

        //学历修改
        eduLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] eduType = {"小学","中学","专科","本科","硕士","博士","博士后"};
                String oldEdu = eduTv.getText().toString();
                int i = 0, res = 0;//res是原来所选学历在数组中的下标
                for (String e : eduType ){
                    if ( e.equals(oldEdu) ){
                        res = i;
                    }
                    i++;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(DataManageActivity.this);
                builder.setTitle("学历");
                builder.setSingleChoiceItems(eduType, res, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eduTv.setText(eduType[which]);
                        isChange = true;
                        dialog.dismiss();//点击后自动关闭弹出框
                    }
                });
                builder.show();
            }
        });

        mainLayout.setOnTouchListener(this);

    }


    /**
     * 生成一个带EditText的弹出框，并监听修改确定事件，更新控件信息
     * @param oldTv
     * @param title
     * @param maxLength
     */
    @SuppressLint("RestrictedApi")
    private void newStringDialog(final TextView oldTv , String title, int maxLength){
        final EditText editText = new EditText(DataManageActivity.this);
        editText.setHint(oldTv.getText().toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(DataManageActivity.this);
        builder.setTitle(title);

        builder.setView(editText , ET_MARGIN_LEFT , ET_MARGIN_TOP , ET_MARGIN_RIGHT , ET_MARGIN_BOTTOM);
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNickname = editText.getText().toString();
                if( !newNickname.isEmpty() ){
                    oldTv.setText(newNickname);
                    isChange = true;
                }
            }
        });

        builder.show();
    }

    /**
     * 确认修改之前搜集并更新用户信息
     */
    private void notifyMine(){
        mine.setNickname(nicknameTv.getText().toString());
        mine.setSign(signTv.getText().toString());
        mine.setBirthday(birthTv.getText().toString());
        mine.setSex(sexTv.getText().toString().equals("男")?0:1);
        mine.setAddress(addTv.getText().toString());
        mine.setEducation(eduTv.getText().toString());
    }

}
