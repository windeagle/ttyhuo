package cn.ttyhuo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
}
