package cn.ttyhuo.utils;

import android.text.TextUtils;
import org.json.JSONObject;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String getTimeStr(Date d) {
        Date n = new Date();
        long timeSpan = n.getTime() - d.getTime();
        long mimiSecondsOfDay = 1000 * 3600 * 24;
        long mimiSecondsOfHour = 1000 * 3600;
        if (timeSpan > mimiSecondsOfHour * 100)
            return (timeSpan / mimiSecondsOfDay) + "天前";
        if (timeSpan > 1000 * 6000)
            return (timeSpan / mimiSecondsOfHour) + "小时前";
        if (timeSpan > 1000 * 100)
            return (timeSpan / 60000) + "分钟前";
        if (timeSpan > 0)
            return (timeSpan / 1000) + "秒前";
        if (timeSpan > -1000 * 100)
            return (-timeSpan / 1000) + "秒后";
        if (timeSpan > -1000 * 6000)
            return (-timeSpan / 60000) + "分钟后";
        if (timeSpan > -mimiSecondsOfHour * 100)
            return (-timeSpan / mimiSecondsOfHour) + "小时后";
        return (-timeSpan / mimiSecondsOfDay) + "天后";
    }

	public static boolean isEmail(String strEmail) {
		if (strEmail == null) {
			return false;
		}

		String strPattern = "^[a-zA-Z][\\w\\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";
		Pattern p = Pattern.compile(strPattern);
		Matcher m = p.matcher(strEmail);
		return m.matches();
	}

	public static boolean isMobileNO(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNumber);
		return m.matches();
	}

	public static String toZero(String name) {
		if (name == null) {
			return "0";
		}
		return name;
	}

	public static String checkNull(String str) {
		if (str == null || str.equals("null") || str.equals("NULL")
				|| str.equals(JSONObject.NULL)) {
			return "";
		}
		return str;
	}

	public static boolean isEmpty(String s) {
		return TextUtils.isEmpty(s) || s.trim().isEmpty() || s.equals("null")
				|| s.equals("NULL") || s.equals(JSONObject.NULL);
	}

	public static String getDateByTime(String time) {
		if (isEmpty(time) || time.length() < 16) {
			return time;
		}
		return time.substring(0, 10);
	}

	public static boolean isNumeric(String str) {
		for (int i = str.length(); --i >= 0;) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
