package baidumapsdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttyhuo.R;
import com.baidu.mapapi.map.*;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.*;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;

/**
 * 演示MapView的基本用法
 */
public class BaseMapDemo extends Activity implements BaiduMap.OnMapClickListener, OnGetGeoCoderResultListener, OnGetShareUrlResultListener {

	@SuppressWarnings("unused")
	private static final String LTAG = BaseMapDemo.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;

    TextView popupText;
    // 保存搜索结果地址
    private String currentAddr = null;

    GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
    private ShareUrlSearch mShareUrlSearch = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();

        mSearch = GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(this);
        mShareUrlSearch = ShareUrlSearch.newInstance();
        mShareUrlSearch.setOnGetShareUrlResultListener(this);

        View viewCache = getLayoutInflater().inflate(R.layout.custom_text_view,
                null);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);

		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = intent.getExtras();
			final LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
            mBaiduMap = mMapView.getMap();

            BaiduMap.OnMapLoadedCallback loadedCallback = new BaiduMap.OnMapLoadedCallback() {
                /**
                 * 地图加载完成回调函数
                 */
                public void onMapLoaded(){
                    if(p != null)
                    {
                        mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                                .location(p));
                    }
                }
            };

            mBaiduMap.setOnMapLoadedCallback(loadedCallback);
            mBaiduMap.setOnMapClickListener(this);
		} else {
			mMapView = new MapView(this, new BaiduMapOptions());
            mBaiduMap = mMapView.getMap();
		}
		setContentView(mMapView);
	}

    @Override
    public void onMapClick(LatLng point) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(final MapPoi poi) {
        return false;
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaseMapDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
        }
        currentAddr = result.getAddress();

        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .title(result.getAddress())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));

        mBaiduMap.setOnMarkerClickListener(markerClickListener);
        String strInfo = String.format("纬度：%f 经度：%f",
                result.getLocation().latitude, result.getLocation().longitude);
        Toast.makeText(BaseMapDemo.this, strInfo, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(BaseMapDemo.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
                    .show();
        }
        currentAddr = result.getAddress();

        mBaiduMap.clear();
        mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
                .title(result.getAddress())
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.icon_marka)));
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
                .getLocation()));

        mBaiduMap.setOnMarkerClickListener(markerClickListener);
        Toast.makeText(BaseMapDemo.this, result.getAddress(),
                Toast.LENGTH_LONG).show();

    }

    BaiduMap.OnMarkerClickListener markerClickListener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 被点击的 marker
         */
        public boolean onMarkerClick(final Marker marker){
            popupText.setText(marker.getTitle());
            popupText.setBackgroundResource(R.drawable.popup);
            Point p = mBaiduMap.getProjection().toScreenLocation(marker.getPosition());
            p.y -= 47;
            LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
            InfoWindow.OnInfoWindowClickListener listener = new InfoWindow.OnInfoWindowClickListener() {
                public void onInfoWindowClick() {
                    mBaiduMap.hideInfoWindow();
                mShareUrlSearch
                        .requestLocationShareUrl(new LocationShareURLOption()
                                .location(marker.getPosition()).snippet(marker.getTitle())
                                .name(marker.getTitle()));
                }
            };
            mBaiduMap.showInfoWindow(new InfoWindow(popupText, llInfo, listener));

            return true;
        }
    };

    @Override
    public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {

        LocationDemo.shareMsg(BaseMapDemo.this, "将短串分享到", "将短串分享到", "您的朋友通过天天有货与您分享一个位置: " + currentAddr +
                " -- " + result.getUrl(), null);
    }

    @Override
    public void onGetLocationShareUrlResult(ShareUrlResult result) {

        LocationDemo.shareMsg(BaseMapDemo.this, "将短串分享到", "将短串分享到", "您的朋友通过天天有货与您分享一个位置: " + currentAddr +
                " -- " + result.getUrl(), null);
    }

	@Override
	protected void onPause() {
		super.onPause();
		// activity 暂停时同时暂停地图控件
		mMapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// activity 恢复时同时恢复地图控件
		mMapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
	}
}
