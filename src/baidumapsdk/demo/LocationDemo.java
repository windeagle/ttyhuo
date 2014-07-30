package baidumapsdk.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.ttyhuo.R;
import cn.ttyhuo.common.MyApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.map.MyLocationConfigeration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.baidu.mapapi.search.poi.*;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 此demo用来展示如何结合定位SDK实现定位，并使用MyLocationOverlay绘制定位位置 同时展示如何使用自定义图标绘制并点击时弹出泡泡
 * 
 */
public class LocationDemo extends Activity implements
        OnGetPoiSearchResultListener, OnGetSuggestionResultListener,
        OnGetGeoCoderResultListener, OnGetShareUrlResultListener {

    private PoiSearch mPoiSearch = null;
    private SuggestionSearch mSuggestionSearch = null;
    private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private ShareUrlSearch mShareUrlSearch = null;

    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationMode mCurrentMode;
    private Button requestLocButton;
    private BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);

    private LinearLayout ly_bottomArea;
    private TextView tv_mark_name;
    private TextView tv_mark_addr;
    private Button btn_select_addr;

    private MyApplication myApplication;
    private MyLocationListener myListener = new MyLocationListener();

    private boolean isFirstLoc = true;// 是否首次定位
    private LatLng firstLatLng;
    private String intentAddress;
    private String intentCity = "";

    private int load_Index = 0;
    private TextView popupText;
    private Button saveScreenButton;
    private LatLng currentPt;
    private String currentAddr = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setOnMyLocationClickListener(myLocationClickListener);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        myApplication = ((MyApplication)getApplication());
        myApplication.mLocationClient.registerLocationListener(myListener);
        myApplication.scanSpan = 30000;
        myApplication.InitLocation();
        myApplication.mLocationClient.start();

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(this);
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(this);

        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);

        ly_bottomArea = (LinearLayout) findViewById(R.id.ly_bottomArea);
        tv_mark_name = (TextView) findViewById(R.id.tv_mark_name);
        tv_mark_addr = (TextView) findViewById(R.id.tv_mark_addr);
        btn_select_addr = (Button) findViewById(R.id.btn_select_addr);

        setupLocationModeBtn();

        setupReturnMyLoc();

        setupMarkerBtn();

        initMap();

        setupKeywordSuggest();

        setupSaveScreenBtn();

        View viewCache = getLayoutInflater().inflate(R.layout.custom_text_view,
                null);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                mBaiduMap.hideInfoWindow();
                ly_bottomArea.setVisibility(View.GONE);

            }
            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return LocationDemo.this.onMapPoiClick(mapPoi);
            }
        });
        mBaiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {
                currentPt = point;
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(currentPt));
            }
        });
        mBaiduMap.setOnMapDoubleClickListener(new BaiduMap.OnMapDoubleClickListener() {
            public void onMapDoubleClick(LatLng point) {
                currentPt = point;
                // 反Geo搜索
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(currentPt));
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            public void onMapStatusChangeStart(MapStatus status) {
            }

            public void onMapStatusChangeFinish(MapStatus status) {
            }

            public void onMapStatusChange(MapStatus status) {
            }
        });
	}

    private void setupSaveScreenBtn() {
        saveScreenButton = (Button) findViewById(R.id.savescreen);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.equals(saveScreenButton)) {
                    // 截图，在SnapshotReadyCallback中保存图片到 sd 卡
                    mBaiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
                        public void onSnapshotReady(Bitmap snapshot) {
                            File file = new File("/mnt/sdcard/test.png");
                            FileOutputStream out;
                            try {
                                out = new FileOutputStream(file);
                                if (snapshot.compress(
                                        Bitmap.CompressFormat.PNG, 100, out)) {
                                    out.flush();
                                    out.close();
                                }
                                Toast.makeText(LocationDemo.this,
                                        "屏幕截图成功，图片存在: " + file.toString(),
                                        Toast.LENGTH_SHORT).show();
                                // 分享短串结果
                                shareMsg(LocationDemo.this, "将图片分享到", "将图片分享到", "您的朋友通过天天有货与您分享一个位置", file.toString());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Toast.makeText(LocationDemo.this, "正在截取屏幕图片...",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };
        saveScreenButton.setOnClickListener(onClickListener);
    }

    private void setupKeywordSuggest() {
        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city(intentCity));
            }
        });
    }

    private void initMap() {
        Intent intent = getIntent();
        if (intent.hasExtra("x") && intent.hasExtra("y")) {
            // 当用intent参数时，设置中心点为指定点
            Bundle b = intent.getExtras();
            firstLatLng = new LatLng(b.getDouble("y"), b.getDouble("x"));
        }
        else
        {
            BDLocation location = myApplication.nowLocation;
            if(location != null)
            {
                firstLatLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
            }
        }

        if (intent.hasExtra("intentAddress")) {
            Bundle b = intent.getExtras();
            intentAddress = b.getString("intentAddress");
            AutoCompleteTextView editSearchKey = (AutoCompleteTextView) findViewById(R.id.searchkey);
            editSearchKey.setText(intentAddress);
        }

        if (intent.hasExtra("intentCity")) {
            Bundle b = intent.getExtras();
            intentCity = b.getString("intentCity");
            String[] intentCityArr = intentCity.split(" ");
            if(intentCityArr.length > 1)
                intentCity = intentCityArr[1];
        }

        BaiduMap.OnMapLoadedCallback loadedCallback = new BaiduMap.OnMapLoadedCallback() {
            /**
             * 地图加载完成回调函数
             */
            public void onMapLoaded(){
                BDLocation location = myApplication.nowLocation;
                if(location != null)
                {
                    isFirstLoc = false;
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            .direction(location.getDirection()).latitude(location.getLatitude())
                            .longitude(location.getLongitude()).build();
                    mBaiduMap.setMyLocationData(locData);
                }

                if(firstLatLng != null)
                {
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(firstLatLng, 14.0f);
                    mBaiduMap.animateMapStatus(u);
                }

                if(intentAddress != null && !intentAddress.isEmpty())
                {
                    // Geo搜索
                    mSearch.geocode(new GeoCodeOption().city(intentCity).address(
                            intentAddress));
                }
            }
        };
        mBaiduMap.setOnMapLoadedCallback(loadedCallback);
    }

    private void setupMarkerBtn() {
        RadioGroup group = (RadioGroup) this.findViewById(R.id.radioGroup);
        OnCheckedChangeListener radioButtonListener = new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.defaulticon) {
					// 传入null则，恢复默认图标
					mCurrentMarker = null;
				}
				if (checkedId == R.id.customicon) {
					// 修改为自定义marker
					mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
				}
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
                                mCurrentMode, true, mCurrentMarker));
			}
		};
        group.setOnCheckedChangeListener(radioButtonListener);
    }

    private void setupLocationModeBtn() {
        requestLocButton = (Button) findViewById(R.id.button1);
        requestLocButton.setText("跟随");
        mCurrentMode = LocationMode.FOLLOWING;

        OnClickListener btnClickListener = new OnClickListener() {
            public void onClick(View v) {
                switch (mCurrentMode) {
                case NORMAL:
                    requestLocButton.setText("跟随");
                    mCurrentMode = LocationMode.FOLLOWING;
                    break;
                case COMPASS:
                    requestLocButton.setText("普通");
                    mCurrentMode = LocationMode.NORMAL;
                    break;
                case FOLLOWING:
                    requestLocButton.setText("罗盘");
                    mCurrentMode = LocationMode.COMPASS;
                    break;
                }
                mBaiduMap.setMyLocationConfigeration(new MyLocationConfigeration(
                        mCurrentMode, true, mCurrentMarker));
            }
        };
        requestLocButton.setOnClickListener(btnClickListener);
    }

    private void setupReturnMyLoc() {
        OnClickListener returnMyLocClickListener = new OnClickListener() {
            public void onClick(View v) {
                MyApplication myApplication = ((MyApplication)getApplication());
                BDLocation location = myApplication.nowLocation;
                if(location != null)
                {
                    LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14.0f);
                    mBaiduMap.animateMapStatus(u);
                }
                myApplication.mLocationClient.requestLocation();
            }
        };
        ImageView returnMyLoc = (ImageView) findViewById(R.id.returnMyLoc);
        returnMyLoc.setOnClickListener(returnMyLocClickListener);
    }

    /**
     * 分享功能
     * @param context 上下文
     * @param activityTitle Activity的名字
     * @param msgTitle 消息标题
     * @param msgText 消息内容
     * @param imgPath 图片路径，不分享图片则传null
     */
    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    /**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					.direction(location.getDirection()).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 14.0f);
                mBaiduMap.animateMapStatus(u);
			}
		}
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 关闭定位图层
        ((MyApplication)getApplication()).mLocationClient.unRegisterLocationListener(myListener);
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;

        mPoiSearch.destroy();
        mSuggestionSearch.destroy();
        mSearch.destroy();
        mShareUrlSearch.destroy();

		super.onDestroy();
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    public void goToNextPage(View v) {
        load_Index++;
        if(load_Index > 0)
            geoPoiInfo = null;
        mBaiduMap.clear();
        AutoCompleteTextView editSearchKey = (AutoCompleteTextView) findViewById(R.id.searchkey);
        if(editSearchKey.getText().toString().trim().isEmpty())
            return;
//        // Geo搜索
//        mSearch.geocode(new GeoCodeOption().city(intentCity).address(
//                editSearchKey.getText().toString()));
        mPoiSearch.searchInCity((new PoiCitySearchOption())
                .city(intentCity)
                .keyword(editSearchKey.getText().toString())
                .pageNum(load_Index));
    }

    private PoiInfo geoPoiInfo;

    @Override
    public void onGetPoiResult(PoiResult result) {
        PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
        if (result == null
                || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {

            if(geoPoiInfo != null)
            {
                mBaiduMap.addOverlay(new MarkerOptions().position(geoPoiInfo.location)
                        .title(geoPoiInfo.address)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.icon_marka)));
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(geoPoiInfo.location, 14.0f));
                mBaiduMap.setOnMarkerClickListener(markerClickListener);

                return;
            }

            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {

            mBaiduMap.setOnMarkerClickListener(overlay);
            if(geoPoiInfo != null)
                result.getAllPoi().add(geoPoiInfo);
            overlay.setData(result);
            overlay.addToMap();
            overlay.zoomToSpan();

            return;
        }

        if(geoPoiInfo != null)
        {

            mBaiduMap.addOverlay(new MarkerOptions().position(geoPoiInfo.location)
                    .title(geoPoiInfo.address)
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.icon_marka)));
            mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(geoPoiInfo.location, 14.0f));
            mBaiduMap.setOnMarkerClickListener(markerClickListener);

            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
            Toast.makeText(LocationDemo.this, strInfo, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(LocationDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(LocationDemo.this, "成功，查看详情页面", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult res) {
        if (res == null || res.getAllSuggestions() == null) {
            return;
        }
        sugAdapter.clear();
        for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
            if (info.key != null)
                sugAdapter.add(info.key);
        }
        sugAdapter.notifyDataSetChanged();
    }

    private class MyPoiOverlay extends PoiOverlay {

        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int index) {
            super.onPoiClick(index);
            final PoiInfo poi = getPoiResult().getAllPoi().get(index);

//            mShareUrlSearch
//                    .requestPoiDetailShareUrl(new PoiDetailShareURLOption()
//                            .poiUid(poi.uid));

//            if (poi.hasCaterDetails) {
//                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
//                        .poiUid(poi.uid));
//            }

            final LatLng ll = poi.location;

//            popupText.setText(poi.address);
//            popupText.setBackgroundResource(R.drawable.popup);
//            Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//            p.y -= 47;
//            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//                public void onInfoWindowClick() {
//                    mBaiduMap.hideInfoWindow();
//
//                    Intent intent = getIntent();
//                    intent.putExtra("addr", poi.address);
//                    intent.putExtra("lat", ll.latitude);
//                    intent.putExtra("lng", ll.longitude);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            };
//            mBaiduMap.showInfoWindow(new InfoWindow(popupText, llInfo, listener));

            setupBottomArea(poi.name, poi.address , ll);

            return true;
        }
    }

    private boolean onMapPoiClick(final MapPoi poi) {

        final LatLng ll = poi.getPosition();

//        popupText.setText(poi.getName());
//        popupText.setBackgroundResource(R.drawable.popup);
//        Point p = mBaiduMap.getProjection().toScreenLocation(ll);
//        p.y -= 47;
//        LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//        InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//            public void onInfoWindowClick() {
//                mBaiduMap.hideInfoWindow();
//
//                Intent intent = getIntent();
//                intent.putExtra("addr", poi.getName());
//                intent.putExtra("lat", ll.latitude);
//                intent.putExtra("lng", ll.longitude);
//                setResult(RESULT_OK, intent);
//                finish();
//            }
//        };
//        mBaiduMap.showInfoWindow(new InfoWindow(popupText, llInfo, listener));

        setupBottomArea(poi.getName(), poi.getName(), ll);

        return false;
    }

    private void setupBottomArea(String title, final String addr, final LatLng ll) {
        tv_mark_name.setText(title);
        tv_mark_addr.setText(addr);
        btn_select_addr.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v) {
                ly_bottomArea.setVisibility(View.GONE);

                Intent intent = getIntent();
                intent.putExtra("addr", addr);
                intent.putExtra("lat", ll.latitude);
                intent.putExtra("lng", ll.longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ly_bottomArea.setVisibility(View.VISIBLE);
    }

    /**
     * 设置底图显示模式
     *
     * @param view
     */
    public void setMapMode(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.normal:
                if (checked)
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.statellite:
                if (checked)
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
        }
    }

    /**
     * 设置是否显示交通图
     *
     * @param view
     */
    public void setTraffic(View view) {
        mBaiduMap.setTrafficEnabled(((CheckBox) view).isChecked());
    }

    BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(final Marker marker){
//            popupText.setText(marker.getTitle());
//            popupText.setBackgroundResource(R.drawable.popup);
//            Point p = mBaiduMap.getProjection().toScreenLocation(marker.getPosition());
//            p.y -= 47;
//            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//                public void onInfoWindowClick() {
//                    mBaiduMap.hideInfoWindow();
//
//                    Intent intent = getIntent();
//                    intent.putExtra("addr", marker.getTitle());
//                    intent.putExtra("lat", marker.getPosition().latitude);
//                    intent.putExtra("lng", marker.getPosition().longitude);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                }
//            };
//            mBaiduMap.showInfoWindow(new InfoWindow(popupText, llInfo, listener));

            setupBottomArea(marker.getTitle(), marker.getTitle(), marker.getPosition());

//            mShareUrlSearch
//                    .requestLocationShareUrl(new LocationShareURLOption()
//                            .location(marker.getPosition()).snippet(marker.getTitle())
//                            .name(marker.getTitle()));
            return true;
        }
    };

    BaiduMap.OnMyLocationClickListener myLocationClickListener = new BaiduMap.OnMyLocationClickListener() {
        /**
         * 地图定位图标点击事件监听函数
         */
        public boolean onMyLocationClick(){

            final BDLocation location = ((MyApplication)LocationDemo.this.getApplication()).nowLocation;
            if(location != null)
            {
//                popupText.setText(location.getAddrStr());
//                popupText.setBackgroundResource(R.drawable.popup);
                final LatLng tmpPoint = new LatLng(location.getLatitude(),
                        location.getLongitude());
//                Point p = mBaiduMap.getProjection().toScreenLocation(tmpPoint);
//                p.y -= 47;
//                LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
//                InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
//                    public void onInfoWindowClick() {
//                        mBaiduMap.hideInfoWindow();
//
//                        Intent intent = getIntent();
//                        intent.putExtra("addr", location.getAddrStr());
//                        intent.putExtra("lat", tmpPoint.latitude);
//                        intent.putExtra("lng", tmpPoint.longitude);
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//                };
//                mBaiduMap.showInfoWindow(new InfoWindow(popupText, llInfo, listener));

                setupBottomArea(location.getAddrStr(), location.getAddrStr(), tmpPoint);

//                mShareUrlSearch
//                        .requestLocationShareUrl(new LocationShareURLOption()
//                                .location(tmpPoint).snippet(location.getAddrStr())
//                                .name(location.getAddrStr()));
                return true;
            }
            return false;
        }
    };

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR || result.getAddress() == null || result.getLocation() == null)
        {
            load_Index = -1;
            geoPoiInfo = null;
            goToNextPage(null);

            //Toast.makeText(LocationDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
            return;
        }

        currentAddr = result.getAddress();

//        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
//                .title(result.getAddress())
//                .icon(BitmapDescriptorFactory
//                        .fromResource(R.drawable.icon_marka)));
//        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(result
//                .getLocation(), 14.0f));
//
//        mBaiduMap.setOnMarkerClickListener(markerClickListener);

        geoPoiInfo = new PoiInfo();
        geoPoiInfo.address = currentAddr;
        geoPoiInfo.location = result.getLocation();
        geoPoiInfo.name = currentAddr;

        load_Index = -1;
        goToNextPage(null);

        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        //Toast.makeText(LocationDemo.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            //Toast.makeText(LocationDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
            //        .show();
        }

        mBaiduMap.clear();

        currentAddr = result.getAddress();

        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .title(result.getAddress())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));

        mBaiduMap.setOnMarkerClickListener(markerClickListener);
        //Toast.makeText(LocationDemo.this, result.getAddress(),
        //        Toast.LENGTH_LONG).show();

    }

    /**
     * 发起搜索
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
        if (v.getId() == R.id.reversegeocode) {
            EditText lat = (EditText) findViewById(R.id.lat);
            EditText lon = (EditText) findViewById(R.id.lon);
            LatLng ptCenter = new LatLng((Float.valueOf(lat.getText()
                    .toString())), (Float.valueOf(lon.getText().toString())));
            // 反Geo搜索
            mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                    .location(ptCenter));
        } else if (v.getId() == R.id.geocode) {
            mBaiduMap.clear();
            AutoCompleteTextView editSearchKey = (AutoCompleteTextView) findViewById(R.id.searchkey);
            if(editSearchKey.getText().toString().trim().isEmpty())
                return;
            // Geo搜索
            mSearch.geocode(new GeoCodeOption().city(intentCity).address(
                    editSearchKey.getText().toString()));
        }
    }

    @Override
    public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {

        shareMsg(LocationDemo.this, "将短串分享到", "将短串分享到", "您的朋友通过天天有货与您分享一个位置: " + currentAddr +
                " -- " + result.getUrl(), null);
    }

    @Override
    public void onGetLocationShareUrlResult(ShareUrlResult result) {

        shareMsg(LocationDemo.this, "将短串分享到", "将短串分享到", "您的朋友通过天天有货与您分享一个位置: " + currentAddr +
                " -- " + result.getUrl(), null);
    }
}
