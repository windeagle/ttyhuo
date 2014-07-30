package cn.ttyhuo.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

public class NetworkUtils {

	public static boolean isNetworkAvailable(final Context context) {
		if (context == null) {
			return false;
		}

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) {
			return false;
		}

		return cm.getActiveNetworkInfo() != null;
	}

	public static boolean isWifiAvailable(final Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null) {
			return false;
		}

		NetworkInfo info = cm.getActiveNetworkInfo();

		if (info == null) {
			return false;
		}

		return info.getType() == ConnectivityManager.TYPE_WIFI;
	}

    public static final boolean isGpsOpen(final Context context){
        LocationManager locationManager=(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }

    public static void gpsOpen(final Context context){
        if(isGpsOpen(context))
            Toast.makeText(context, "GPS is ready", Toast.LENGTH_SHORT);
        else
        {
            Toast.makeText(context, "GPS 定位关闭，请打开定位!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Settings.ACTION_SECURITY_SETTINGS);
            ((Activity)context).startActivityForResult(intent, 0);
        }
    }

    /**
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
     * @param context
     * @return true 表示开启
     */
    public static final boolean isLocationOpen(final Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 强制帮用户打开GPS
     * @param context
     */
    public static final void openGPS(Context context) {
        Intent GPSIntent = new Intent();
        GPSIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
        GPSIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
