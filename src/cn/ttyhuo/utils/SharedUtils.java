package cn.ttyhuo.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtils {

	public static SharedPreferences mPreference;

	public static synchronized SharedPreferences getPreference(Context context) {
		if (mPreference == null) {
			mPreference = context.getSharedPreferences(
					context.getPackageName(), Context.MODE_PRIVATE);
		}
		return mPreference;
	}

	// ////////////
	// public static SharedPreferences getPreference(Context context) {
	// if (mPreference == null)
	// mPreference = PreferenceManager
	// .getDefaultSharedPreferences(context);
	// return mPreference;
	// }

	public static void setInteger(Context context, String name, int value) {
		getPreference(context).edit().putInt(name, value).commit();
	}

	public static int getInteger(Context context, String name, int default_i) {
		return getPreference(context).getInt(name, default_i);
	}

	public static void setString(Context context, String name, String value) {
		getPreference(context).edit().putString(name, value).commit();
	}

	public static String getString(Context context, String name) {
		return getPreference(context).getString(name, "");
	}

}
