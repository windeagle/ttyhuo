package cn.ttyhuo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.common.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-21
 * Time: 上午9:41
 * To change this template use File | Settings | File Templates.
 */
public class CommonUtils {
    public static void call(final Context context, View btn, final String mobile) {
        if(btn != null)
            btn.setOnClickListener(new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v){
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.CALL");
                    intent.setData(Uri.parse("tel:" + mobile));//mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                    context.startActivity(intent);
                }
            });
    }

    public static void setupFaceImg(Context context, ImageView imageView, String imgUrl) {
        if (imgUrl.startsWith("http")) {
            ImageLoader.getInstance().displayImage(imgUrl, imageView, new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true).cacheInMemory(true)
                    .cacheOnDisc(true).build());
        } else {
            Bitmap mBitmap = PhotoUtils.decodeBitmap(imgUrl, PxUtils.dip2px(context, 200),
                    PxUtils.dip2px(context, 200));
            imageView.setImageBitmap(mBitmap);
        }
    }

    public static void setupDistance(Activity context, TextView distanceTv, JSONObject jObject) throws JSONException {
        if(distanceTv == null)
            return;

        try {
            double lat = 0.0;
            double lng = 0.0;
            String v = jObject.getString("fromLat");
            if(!StringUtils.isEmpty(v))
                lat = jObject.getDouble("fromLat");

            v = jObject.getString("fromLng");
            if(!StringUtils.isEmpty(v))
                lng = jObject.getDouble("fromLng");

            if(lat != 0.0 && lng != 0.0)
            {
                String distance = ((MyApplication)(context).getApplication()).getDistance(lat, lng);
                distanceTv.setText(distance + "km");
            }
            else
                distanceTv.setText("未知");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String getFromCityStr(JSONObject jObject) {
        String fromCityStr = "未知";
        try {
            String v = jObject.getString("fromCity");
            if(!StringUtils.isEmpty(v))
                fromCityStr = v;
            String[] cityArr = fromCityStr.toString().split(" |-");
            if(cityArr.length > 2)
                fromCityStr = cityArr[1] + "-" + cityArr[2];
            else if(cityArr.length > 1)
                fromCityStr = cityArr[1];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return fromCityStr;
    }

    public static String getToCityStr(JSONObject jObject) {
        String toCityStr = "未知";
        try {
            String v = jObject.getString("toCity");
            if(!StringUtils.isEmpty(v))
                toCityStr = v;
            String[] cityArr = toCityStr.toString().split(" |-");
            if(cityArr.length > 2)
                toCityStr = cityArr[1] + "-" + cityArr[2];
            else if(cityArr.length > 1)
                toCityStr = cityArr[1];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toCityStr;
    }

    public static void setupUserVerifyImg(JSONObject jsonObject, ImageView userVerifyImg) {
        if(userVerifyImg == null)
            return;

        try
        {
            userVerifyImg.setVisibility(View.VISIBLE);
            int verifyFlag = jsonObject.getInt("provideUserVerifyFlag");
            if(verifyFlag == 1)
                userVerifyImg.setImageResource(R.drawable.icon_verify_1);
            else if(verifyFlag == 3)
                userVerifyImg.setImageResource(R.drawable.icon_verify_3);
            else if(verifyFlag == 5)
                userVerifyImg.setImageResource(R.drawable.icon_verify_5);
            else if(verifyFlag == 7)
                userVerifyImg.setImageResource(R.drawable.icon_verify_7);
            else if(verifyFlag == 13)
                userVerifyImg.setImageResource(R.drawable.icon_verify_5_1);
            else if(verifyFlag == 15)
                userVerifyImg.setImageResource(R.drawable.icon_verify_7_1);
            else
                userVerifyImg.setVisibility(View.GONE);
        } catch (JSONException ex) {
            ex.printStackTrace();
            userVerifyImg.setVisibility(View.GONE);
        }
    }
}
