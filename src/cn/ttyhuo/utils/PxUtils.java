package cn.ttyhuo.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class PxUtils {
	public static int mDeviceWidth = 0;
	public static int mDeviceHeight = 0;

	public static int getDeviceWidth(Activity context) {
		if (mDeviceWidth == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);

			mDeviceWidth = dm.widthPixels;
		}

		LogUtils.i("mDeviceWidth:" + mDeviceWidth);
		return mDeviceWidth;
	}

	public static int getDeviceHeight(Activity context) {
		if (mDeviceHeight == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			context.getWindowManager().getDefaultDisplay().getMetrics(dm);

			mDeviceHeight = dm.heightPixels;
		}

		LogUtils.i("mDeviceHeight:" + mDeviceHeight);
		return mDeviceHeight;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
