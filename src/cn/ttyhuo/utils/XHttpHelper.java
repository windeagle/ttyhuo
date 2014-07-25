package cn.ttyhuo.utils;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;

public class XHttpHelper {
	private static AsyncHttpClient client;

	public static AsyncHttpClient getInstance() {
		if (client == null) {
			client = new AsyncHttpClient();
		}

		return client;
	}

	public static boolean checkHttp(Context context) {
		if (NetworkUtils.isNetworkAvailable(context)) {
			return true;
		} else {
			Toast.makeText(context, "请检查网络连接", Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	public static boolean checkHttpSilent(Context context) {
		if (NetworkUtils.isNetworkAvailable(context)) {
			return true;
		} else {
			return false;
		}
	}

}
