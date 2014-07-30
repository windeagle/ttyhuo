package cn.ttyhuo.common;

/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttyhuo.activity.login.LoginNeedBaseFragment;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.loopj.android.http.PersistentCookieStore;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public class MyApplication extends Application {
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        cookieStore = new PersistentCookieStore(context);

        initImageLoader(getApplicationContext());

        mLocationClient = new LocationClient(getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        InitLocation();
        mLocationClient.start();

        try {
            PackageManager manager = this.getPackageManager();
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        SDKInitializer.initialize(getApplicationContext());
    }

    public Handler versionHandler = null;

    public static void setUpPersistentCookieStore()
    {
        if(context == null)
            return;
        cookieStore = new PersistentCookieStore(context);

        CookieStore httpCookieStore = HttpRequestUtil.cookieManager.getCookieStore();
        for(HttpCookie h : httpCookieStore.getCookies())
        {
            BasicClientCookie newCookie = new BasicClientCookie(h.getName(), h.getValue());
            newCookie.setVersion(h.getVersion());
            newCookie.setDomain(h.getDomain());
            newCookie.setPath(h.getPath());
            newCookie.setSecure(h.getSecure());
            newCookie.setComment(h.getComment());
            cookieStore.addCookie(newCookie);
        }
    }

    public static void getJavaCookieStore(java.net.CookieStore jCookieStore)
    {
        if(cookieStore == null)
            return;

        for(Cookie h : cookieStore.getCookies())
        {
            HttpCookie newCookie = new HttpCookie(h.getName(), h.getValue());
            newCookie.setVersion(h.getVersion());
            newCookie.setDomain(h.getDomain());
            newCookie.setPath(h.getPath());
            newCookie.setSecure(h.isSecure());
            newCookie.setComment(h.getComment());
            jCookieStore.add(URI.create("http://" + h.getDomain()), newCookie);
        }
    }

    public static PersistentCookieStore cookieStore;
    public static Context context;
    public PackageInfo info = null;
    boolean isFirst = true;

    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    private LocationClientOption.LocationMode tempMode = LocationClientOption.LocationMode.Device_Sensors;
    private String tempcoor="bd09ll";

    public int scanSpan = 30000;
    public TextView mLocationResult;
    public BDLocation nowLocation;

    public void InitLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(tempMode);//设置定位模式
        option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
        option.setScanSpan(scanSpan);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setProdName("天天有货");
        //option.setAddrType("all");

        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        if(NetworkUtils.isGpsOpen(context))
            Toast.makeText(context, "GPS is ready", Toast.LENGTH_SHORT);
        else
        {
            NetworkUtils.openGPS(context);
            Toast.makeText(context, "GPS 定位关闭，请打开定位!", Toast.LENGTH_SHORT).show();
//            Intent intent=new Intent(Settings.ACTION_SECURITY_SETTINGS);
//            context.startActivity(intent);
        }

        mLocationClient.setLocOption(option);
    }

    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            nowLocation = location;

            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());

            sb.append("\nCoorType : ");
            sb.append(location.getCoorType());

            sb.append("\nNetworkLocationType : ");
            sb.append(location.getNetworkLocationType());

            sb.append("\nProvince : ");
            sb.append(location.getProvince());

            sb.append("\nCity : ");
            sb.append(location.getCity());

            sb.append("\nCityCode : ");
            sb.append(location.getCityCode());

            sb.append("\nDistrict : ");
            sb.append(location.getDistrict());

            sb.append("\nStreet : ");
            sb.append(location.getStreet());

            sb.append("\nStreetNumber : ");
            sb.append(location.getStreetNumber());

            sb.append("\nAltitude : ");
            sb.append(location.getAltitude());

            sb.append("\nFloor : ");
            sb.append(location.getFloor());

//            if (location.getLocType() == BDLocation.TypeGpsLocation){
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());
            sb.append("\naddr : ");
            sb.append(location.getAddrStr());
            sb.append("\ndirection : ");
            sb.append(location.getDirection());
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
//                sb.append("\naddr : ");
//                sb.append(location.getAddrStr());
            //运营商信息
            sb.append("\noperationers : ");
            sb.append(location.getOperators());
//            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());


            SharedPreferences settings = getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0);
            boolean loginFlag = settings.getBoolean(LoginNeedBaseFragment.LOGIN_FLAG, false);

            if(loginFlag)
            {

                Map<String, String> params = new HashMap<String, String>();

                params.put("lat", String.valueOf(location.getLatitude()));
                params.put("lon", String.valueOf(location.getLongitude()));
                params.put("memo", location.getAddrStr());

                if(!NetworkUtils.isNetworkAvailable(context))
                {
                    Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                }
                if(info != null && isFirst)
                {
                    isFirst = false;
                    new UrlThread(versionHandler, UrlList.MAIN + "version.html", 1).start();
                }
                else
                {
                    new UrlThread(handler, UrlList.MAIN + "mvc/updateCoords", params, 1).start();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
        }
    };

    public String getDistance(double lat, double lng)
    {
        if(nowLocation == null)
            return "0.00";
        LatLng p1 = new LatLng(lat, lng);
        DecimalFormat myFormatter = new DecimalFormat("#.00");
        LatLng p2 = new LatLng(nowLocation.getLatitude(), nowLocation.getLongitude());
        String str = myFormatter.format(DistanceUtil.getDistance(p1, p2)/1000.00);
        if(str.indexOf(".") == 0)
            str = ("0" + str);
        //str = str.substring(0,str.indexOf(".")+2);
        return str;
    }

    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method
        //ImageLoaderConfiguration.createDefault(getBaseCont‌​ext());
        //imageLoader.init(ImageLoaderConfiguration.createDefault(SpecialAlbumActivity.this));
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}