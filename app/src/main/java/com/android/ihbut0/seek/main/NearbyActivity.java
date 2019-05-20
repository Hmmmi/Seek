package com.android.ihbut0.seek.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import com.android.ihbut0.seek.R;
import com.android.ihbut0.seek.bean.NearbyUser;
import com.android.ihbut0.seek.dao.NearbyUserDAO;
import com.android.ihbut0.seek.utils.LocationUtil;

import java.util.List;

public class NearbyActivity extends BaseActivity {

    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    private static final int GPS_REQUEST_CODE = 10;

    private LinearLayout mainLayout;

    private ImageView backImg;
    private TextView titleTv;

    private ImageView settingBtn;
    private TextView locationTv;
    private RecyclerView nearbyRv;

    //位置监听器
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            getLocationInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            getLocationInfo(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
            getLocationInfo(null);
        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_nearby);

        initView();
        initData();
        initEvent();
    }


    /**
     * 初始化事件
     */
    private void initView(){
        mainLayout = findViewById(R.id.nearby_main_layout);

        backImg = (ImageView) findViewById(R.id.activity_back_img);
        titleTv = findViewById(R.id.left_title_tv);
        titleTv.setText("附近的人");

        settingBtn = (ImageView) findViewById(R.id.setting_button);
        locationTv = (TextView) findViewById(R.id.location_tv);

        nearbyRv = (RecyclerView) findViewById(R.id.nearby_recycler_view);
        nearbyRv.setLayoutManager(new LinearLayoutManager(NearbyActivity.this));

        showContacts();
    }

    /**
     * 初始化数据
     */
    private void initData(){
        List<NearbyUser> nearbyUsers = NearbyUserDAO.getNearbyUsers();
//        Log.d("MIJING", "initData: "+nearbyUsers.size());
        NearbyActivity.NearbyAdapter nearbyAdapter = new NearbyActivity.NearbyAdapter(nearbyUsers);
        nearbyRv.setAdapter(nearbyAdapter);

        locationTv.append( LocationUtil.getLngAndLat(getApplicationContext()) );
    }

    @SuppressLint("MissingPermission")
    /**
     *
     */
    private void initLocation(){
        //获取定位信息
        String serviceString = Context.LOCATION_SERVICE;
        LocationManager locationManager = (LocationManager) getSystemService(serviceString);
        List<String> list = locationManager.getProviders(true);

        String provider;

        boolean bl = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        locationTv.append("是否支持netWork定位："+bl+"\n");

        //检查是否打开了网络定位或GPS
        if (list.contains(LocationManager.NETWORK_PROVIDER)) {
            //是否为网络位置控制器
            provider = LocationManager.NETWORK_PROVIDER;
            locationTv.append("网络位置控制器" + "\n");
        } else if (list.contains(LocationManager.GPS_PROVIDER)) {
            //是否为GPS位置控制器
            provider = LocationManager.GPS_PROVIDER;
            locationTv.append("GPS位置控制器" + "\n");
        } else {
            Toast.makeText(this, "请检查网络或GPS是否打开", Toast.LENGTH_LONG).show();
            return;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        getLocationInfo(location);
        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
    }


    /**
     * 初始化监听事件
     */
    private void initEvent(){
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAuth();
            }
        });

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

//        nearbyRv.setOnTouchListener(this);
    }

    /**
     * 权限申请回调方法
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //用户允许改权限，0表示允许，-1表示拒绝 PERMISSION_GRANTED = 0， PERMISSION_DENIED = -1
                //permission was granted, yay! Do the contacts-related task you need to do.
                //这里进行授权被允许的处理
            } else {
                //permission denied, boo! Disable the functionality that depends on this permission.
                //这里进行权限被拒绝的处理
                Toast.makeText(getApplicationContext(), "获取位置权限失败，请手动开启", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 获取权限
     */
    public void showContacts(){
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION")
                != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(NearbyActivity.this,new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 100);
        }else{
            System.out.println("定位开始！！！！！！！！！！！！！！！！");

        }
    }

    /**
     * 解析定位详细信息
     * @param location
     */
    private void getLocationInfo(Location location) {
        String latLongInfo;
        if (location != null) {
            double latitude = location.getLatitude();//纬度
            double longitude = location.getLongitude();//经度
            double altitude = location.getAltitude();//海拔
            latLongInfo = "纬度: " + latitude + "\n经度: " + longitude + "\n海拔: " +altitude + "m";
        }else{
            latLongInfo = "请耐心等待";
        }
        locationTv.append(latLongInfo);
    }

    /**
     * 打开定位
     */
    private void setAuth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, GPS_REQUEST_CODE);
        } else {
            Toast.makeText(NearbyActivity.this, "手机的系统不支持此功能", Toast.LENGTH_SHORT).show();
        }
    }

    /*******************************************
     *******************************************
     ***************** 控制器********************
     *******************************************
     *******************************************/
    private class NearbyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private NearbyUser nearbyUser;

        private ImageView nearbyHeadImg;
        private TextView nearbyNicknameTv;
        private TextView nearbyDistanceTv;
        private TextView nearbyIndexTv;

        public NearbyHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_nearby_list, parent, false));
            itemView.setOnClickListener(this);//点击事件

            nearbyHeadImg = (ImageView) itemView.findViewById(R.id.seek_head_img);
            nearbyNicknameTv = (TextView) itemView.findViewById(R.id.nearby_nickname_tv);
            nearbyDistanceTv = (TextView) itemView.findViewById(R.id.nearby_distance_tv);
            nearbyIndexTv = (TextView) itemView.findViewById(R.id.nearby_index_tv);
        }

        public void bind(NearbyUser nearbyUser){
            this.nearbyUser = nearbyUser;
            nearbyHeadImg.setImageDrawable(getResources().getDrawable(nearbyUser.getHeadImg()));
            nearbyNicknameTv.setText(nearbyUser.getNickname());
            nearbyDistanceTv.setText(""+nearbyUser.getDistance()+"km");
            nearbyIndexTv.setText(""+nearbyUser.getIndex()+"%");
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(NearbyActivity.this,"账号："+nearbyUser.getNearbyAccount(),Toast.LENGTH_SHORT).show();
        }
    }

    /*******************************************
     *******************************************
     ***************** 适配器********************
     *******************************************
     *******************************************/
    private class NearbyAdapter extends RecyclerView.Adapter<NearbyActivity.NearbyHolder>{

        List<NearbyUser> nearbyUsers;

        public NearbyAdapter(List<NearbyUser> nearbyUsers){
            this.nearbyUsers = nearbyUsers;
        }

        @NonNull
        @Override
        public NearbyActivity.NearbyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(NearbyActivity.this);
            return new NearbyActivity.NearbyHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull NearbyActivity.NearbyHolder holder, int position) {
            NearbyUser nearbyUser = nearbyUsers.get(position);
            holder.bind(nearbyUser);
        }

        @Override
        public int getItemCount() {
            return nearbyUsers.size();
        }
    }

}
