package com.agmbat.baidumap.position;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.agmbat.baidumap.R;
import com.agmbat.map.LocationObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;

/**
 * 显示百度地图页面
 */
public class LocationActivity extends Activity implements OnGetGeoCoderResultListener,
        BaiduMap.OnMapStatusChangeListener, AdapterView.OnItemClickListener {

    /**
     * 请求码
     */
    private static final int REQUEST_CODE = 0x123;

    /**
     * 显示的地图
     */
    private MapView mMapView;

    /**
     * 附近地点列表
     */
    private ListView mListView;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    /**
     * 按钮：回到原地
     */
    private ImageView mLocationButton;

    /**
     * 列表适配器
     */
    private LocationAdapter mAdapter;

    /**
     * 百度地图对象
     */
    private BaiduMap mBaiduMap;

    /**
     * 地理编码
     */
    private GeoCoder mSearch;

    /**
     * 定位
     */
    private LocationClient mLocClient;
    private MyLocationListener myLocationListener = new MyLocationListener();

    /**
     * MapView 中央对于的屏幕坐标
     */
    private Point mCenterPoint = null;

    /**
     * 当前经纬度
     */
    private LatLng mLocationLatLng;

    /**
     * 获取的位置
     */
    private LocationObject mLocationObject = new LocationObject();

    /**
     * 是否第一次定位
     */
    private boolean isFirstLoc = true;

    private boolean isTouch = true;


    // 地图触摸事件监听器
    private BaiduMap.OnMapTouchListener mOnTouchListener = new BaiduMap.OnMapTouchListener() {
        @Override
        public void onTouch(MotionEvent event) {
            isTouch = true;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // 显示列表，查找附近的地点
                searchPoi();
                mLocationButton.setImageResource(R.drawable.back_origin_normal);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_location);
        setupViews();
    }

    /**
     * 点击查找
     */
    void onClickSearch() {
        Intent intent = new Intent(this, SearchPositionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(intent, REQUEST_CODE);
    }

    /**
     * 点击发送
     */
    void onClickSend() {
        Intent intent = new Intent();
        LocationObject.putToIntent(intent, mLocationObject);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 点击回到原点
     */
    void onClickLocation() {
        if (mLocationLatLng != null) {
            // 实现动画跳转
            mLocationButton.setImageResource(R.drawable.back_origin_select);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLocationLatLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(mLocationLatLng));
        }
    }

    /**
     * 初始化Ui
     */
    private void setupViews() {
        mMapView = (MapView) findViewById(R.id.map_view);
        mListView = (ListView) findViewById(android.R.id.list);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_location_load_bar);
        mLocationButton = (ImageView) findViewById(R.id.img_location_back_origin);

        findViewById(R.id.title_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.title_btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSearch();
            }
        });

        findViewById(R.id.title_btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSend();
            }
        });

        mLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLocation();
            }
        });

        // 地图初始化
        mBaiduMap = mMapView.getMap();
        // 设置为普通矢量图地图
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMapView.setPadding(10, 0, 0, 10);
        mMapView.showZoomControls(false);
        // 设置缩放比例(500米)
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
        mBaiduMap.setMapStatus(msu);
        mBaiduMap.setOnMapTouchListener(mOnTouchListener);

        // 初始化当前 MapView 中心屏幕坐标
        mCenterPoint = mBaiduMap.getMapStatus().targetScreen;
        mLocationLatLng = mBaiduMap.getMapStatus().target;

        // 地理编码
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);

        // 地图状态监听
        mBaiduMap.setOnMapStatusChangeListener(this);
        // 定位初始化
        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(5000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        // 可定位
        mBaiduMap.setMyLocationEnabled(true);

        // 列表初始化
        mAdapter = new LocationAdapter(this, new ArrayList<PoiInfo>());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLocationButton.setImageResource(R.drawable.back_origin_normal);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            mAdapter.setSelectItemIndex(0);
            // 获取经纬度
            LatLng latLng = data.getParcelableExtra(SearchPositionActivity.KEY_POSITION);
            // 实现动画跳转
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
            mBaiduMap.animateMapStatus(u);
            mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(latLng));
        }
    }

    /**
     * 显示列表，查找附近的地点
     */
    public void searchPoi() {
        if (mCenterPoint == null) {
            return;
        }
        // 获取当前 MapView 中心屏幕坐标对应的地理坐标
        LatLng currentLatLng = mBaiduMap.getProjection().fromScreenLocation(mCenterPoint);
        // 发起反地理编码检索
        mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    /**
     * list item click
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        isTouch = false;
        // 设置选中项下标，并刷新
        mAdapter.setSelectItemIndex(position);
        mAdapter.notifyDataSetChanged();

        mBaiduMap.clear();
        PoiInfo info = mAdapter.getItem(position);
        // 获取位置
        mLocationObject.mAddress = info.name;
        mLocationObject.mLatitude = info.location.latitude;
        mLocationObject.mLongitude = info.location.longitude;

        // 动画跳转
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(info.location);
        mBaiduMap.animateMapStatus(u);

        mLocationButton.setImageResource(R.drawable.back_origin_normal);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        // 正向地理编码指的是由地址信息转换为坐标点的过程
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }

        LatLng location = result.getLocation();
        String address = result.getAddress();
        // 获取反向地理编码结果
        PoiInfo poiInfo = new PoiInfo();
        poiInfo.address = address;
        poiInfo.location = location;
        poiInfo.name = address;

        mLocationObject.mLatitude = location.latitude;
        mLocationObject.mLongitude = location.longitude;
        mLocationObject.mAddress = result.getAddress();

        mAdapter.clear();
        if (!TextUtils.isEmpty(mLocationObject.mAddress)) {
            mAdapter.add(poiInfo);
        }
        if (result.getPoiList() != null && result.getPoiList().size() > 0) {
            mAdapter.addAll(result.getPoiList());
        }
        mAdapter.notifyDataSetChanged();
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
     *
     * @param status 地图状态改变开始时的地图状态
     */
    public void onMapStatusChangeStart(MapStatus status) {
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
    }

    /**
     * 地图状态变化中
     *
     * @param status 当前地图状态
     */
    @Override
    public void onMapStatusChange(MapStatus status) {
        if (isTouch) {
            mAdapter.clear();
            mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(status.target));
            mListView.setSelection(0);
            mAdapter.setSelectItemIndex(0);
        }
    }

    /**
     * 地图状态改变结束
     *
     * @param status 地图状态改变结束后的地图状态
     */
    @Override
    public void onMapStatusChangeFinish(MapStatus status) {
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LatLng currentLatLng = new LatLng(latitude, longitude);
            mLocationLatLng = new LatLng(latitude, longitude);

            // 是否第一次定位
            if (isFirstLoc) {
                isFirstLoc = false;
                // 实现动画跳转
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(currentLatLng);
                mBaiduMap.animateMapStatus(u);

                mSearch.reverseGeoCode((new ReverseGeoCodeOption()).location(currentLatLng));
            }
        }
    }
}