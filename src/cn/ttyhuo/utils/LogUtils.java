package cn.ttyhuo.utils;

import android.util.Log;

public class LogUtils {

	private static final boolean IF_LOG = true;
	// private static final boolean IF_LOG = false;
	private static final String LOG_TAG = "cn.ttyhuo";

	public static void i(String msg) {
		if (IF_LOG)
			Log.i(LOG_TAG, msg);
	}

	public static void d(String msg) {
		if (IF_LOG)
			Log.d(LOG_TAG, msg);
	}

	public static void e(String msg) {
		if (IF_LOG)
			Log.e(LOG_TAG, msg);
	}

	public static void e(String msg, Throwable tr) {
		if (IF_LOG)
			Log.e(LOG_TAG, msg, tr);
	}

	public static void i(String TAG, String msg) {
		if (IF_LOG)
			Log.i(TAG, msg);
	}

	public static void d(String TAG, String msg) {
		if (IF_LOG)
			Log.d(TAG, msg);
	}

	public static void e(String TAG, String msg) {
		if (IF_LOG)
			Log.e(TAG, msg);
	}

}
