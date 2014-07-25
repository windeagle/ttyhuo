package cn.ttyhuo.utils;

import android.view.View;
import android.widget.TextView;
import cn.ttyhuo.view.FieldLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-6-26
 * Time: 下午9:00
 * To change this template use File | Settings | File Templates.
 */
public class JSONUtil {

    public static JSONArray joinJSONArray(JSONArray mData, JSONArray array) {
        try {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                mData.put(obj1);
            }
        } catch (Exception e) {
        }
        return mData;
    }

    public static JSONArray delJSONArrayItem(JSONArray array, JSONObject jsonItem) {
        JSONArray mData = new JSONArray();
        try {
            int len = array.length();
            for (int i = 0; i < len; i++) {
                JSONObject obj1 = (JSONObject) array.get(i);
                if(!obj1.equals(jsonItem))
                    mData.put(obj1);
            }
        } catch (Exception e) {
        }
        return mData;
    }

    /**
     * 将JSON字符串转换为Map
     */
    public static Map<String, Object> getMap(String jsonString) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(jsonString);
            @SuppressWarnings("unchecked")
            Iterator<String> keyInter = jsonObject.keys();
            String key;
            Object value;
            Map<String, Object> valueMap = new HashMap<String, Object>();
            while (keyInter.hasNext()) {
                key = keyInter.next();
                value = jsonObject.get(key);
                valueMap.put(key, value);
            }
            return valueMap;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setValueFromJson(TextView tv, JSONObject jsonObject, String fieldName, String defaultValue)
    {
        setValueFromJson(tv, jsonObject, fieldName, defaultValue, false);
    }

    public static void setValueFromJson(TextView tv, JSONObject jsonObject, String fieldName, String defaultValue, boolean hide)
    {
        if(tv != null)
        {
            String tmpValue = JSONUtil.getStringFromJson(jsonObject, fieldName, defaultValue);
            if(tmpValue == defaultValue)
            {
                tv.setText(defaultValue);
                if(hide)
                    tv.setVisibility(View.GONE);
                else
                    tv.setVisibility(View.VISIBLE);
            }
            else
            {
                tv.setText(tmpValue);
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setFieldValueFromJson(FieldLayout fl, JSONObject jsonObject, String fieldName, String defaultValue)
    {
        if(fl != null)
            fl.setFieldValueText(JSONUtil.getStringFromJson(jsonObject, fieldName, defaultValue));
    }

    public static void setValueFromJson(TextView tv, JSONObject jsonObject, String fieldName, int conditionValue, String defaultValue, boolean hide)
    {
        if(tv != null)
        {
            int tmpValue = JSONUtil.getIntFromJson(jsonObject, fieldName, conditionValue);
            if(tmpValue == conditionValue)
            {
                tv.setText(defaultValue);
                if(hide)
                    tv.setVisibility(View.GONE);
                else
                    tv.setVisibility(View.VISIBLE);
            }
            else
            {
                tv.setText(String.valueOf(tmpValue));
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setFieldValueFromJson(FieldLayout fl, JSONObject jsonObject, String fieldName, int conditionValue, String defaultValue)
    {
        if(fl != null)
        {
            int tmpValue = JSONUtil.getIntFromJson(jsonObject, fieldName, conditionValue);
            if(tmpValue == conditionValue)
            {
                fl.setFieldValueText(defaultValue);
            }
            else
                fl.setFieldValueText(String.valueOf(tmpValue));
        }
    }

    public static void setValueFromJson(TextView tv, JSONObject jsonObject, String fieldName, double conditionValue, String defaultValue, boolean hide)
    {
        if(tv != null)
        {
            double tmpValue = JSONUtil.getDoubleFromJson(jsonObject, fieldName, conditionValue);
            if(tmpValue == conditionValue)
            {
                tv.setText(defaultValue);
                if(hide)
                    tv.setVisibility(View.GONE);
                else
                    tv.setVisibility(View.VISIBLE);
            }
            else
            {
                tv.setText(String.valueOf(tmpValue));
                tv.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void setFieldValueFromJson(FieldLayout fl, JSONObject jsonObject, String fieldName, double conditionValue, String defaultValue)
    {
        if(fl != null)
        {
            double tmpValue = JSONUtil.getDoubleFromJson(jsonObject, fieldName, conditionValue);
            if(tmpValue == conditionValue)
            {
                fl.setFieldValueText(defaultValue);
            }
            else
                fl.setFieldValueText(String.valueOf(tmpValue));
        }
    }

    public static String getStringFromJson(JSONObject jsonObject, String fieldName, String defaultValue)
    {
        try {
            String v = jsonObject.getString(fieldName);
            if(StringUtils.isEmpty(v))
                return defaultValue;
            else
                return v;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static int getIntFromJson(JSONObject jsonObject, String fieldName, int defaultValue)
    {
        try {
            String v = jsonObject.getString(fieldName);
            if(StringUtils.isEmpty(v))
                return defaultValue;
            else
                return jsonObject.getInt(fieldName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static double getDoubleFromJson(JSONObject jsonObject, String fieldName, double defaultValue)
    {
        try {
            String v = jsonObject.getString(fieldName);
            if(StringUtils.isEmpty(v))
                return defaultValue;
            else
                return jsonObject.getDouble(fieldName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    public static boolean getBoolFromJson(JSONObject jsonObject, String fieldName)
    {
        try {
            String v = jsonObject.getString(fieldName);
            if(StringUtils.isEmpty(v))
                return false;
            return jsonObject.getBoolean(fieldName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }
}
