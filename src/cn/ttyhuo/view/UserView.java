package cn.ttyhuo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.MainPage;
import cn.ttyhuo.common.ConstHolder;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * 把每行布局文件的各个控件包装成一个对象
 */
public class UserView {
    ImageView iv_userFace;
    ImageView iv_userVerify;
    TextView tv_userName;

    ImageView iv_gender;
    TextView tv_userAge;
    TextView tv_lastPlace;
    TextView tv_lastTime;

    TextView tv_truckType;
    TextView tv_licensePlate;
    TextView tv_loadLimit;
    TextView tv_truckLength;
    TextView tv_driverAge;
    TextView tv_driverShuoShuo;

    TextView tv_thumbUpCount;
    TextView tv_favoriteUserCount;
    ImageView iv_hasProduct;
    TextView tv_hasProduct;

    TextView tv_company;
    ImageView iv_phoneIcon;

    LinearLayout ll_gender;
    LinearLayout ly_truck;
    LinearLayout line_truck;
    LinearLayout ll_driverAge;
    LinearLayout ly_company;
    LinearLayout line_company;
    LinearLayout ly_route;
    LinearLayout line_route;

    TextView tv_userTypeStr;
    TextView tv_mobileNo;
    ImageView iv_userEdit;
    TextView tv_userStatus;

    TextView tv_dongTaiNum;
    ImageView iv_dongTaiImg;

    ImageView iv_truckTypeImg;
    TextView tv_truckWidth;
    TextView tv_truckHeight;
    TextView tv_modelNumber;
    TextView tv_seatingCapacity;

    FieldLayout fl_title;
    FieldLayout fl_description;
    FieldLayout fl_hobby;
    FieldLayout fl_homeTown;
    FieldLayout fl_createDate;

    FieldLayout fl_company;
    FieldLayout fl_jobPosition;
    FieldLayout fl_address;
    FieldLayout fl_fixPhoneNo;
    FieldLayout fl_industryName;

    TextView tv_footer_call_btn;
    private RelativeLayout progressBar;

    public UserView(View convertView) {
        iv_userFace = (ImageView) convertView.findViewById(R.id.iv_userFace);
        iv_userVerify = (ImageView) convertView.findViewById(R.id.iv_userVerify);
        tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);

        tv_lastPlace = (TextView) convertView.findViewById(R.id.tv_lastPlace);
        tv_lastTime = (TextView) convertView.findViewById(R.id.tv_lastTime);
        iv_gender = (ImageView) convertView.findViewById(R.id.iv_gender);
        tv_userAge = (TextView) convertView.findViewById(R.id.tv_userAge);

        tv_truckType = (TextView) convertView.findViewById(R.id.tv_truckType);
        tv_licensePlate = (TextView) convertView.findViewById(R.id.tv_licensePlate);
        tv_loadLimit = (TextView) convertView.findViewById(R.id.tv_loadLimit);
        tv_truckLength = (TextView) convertView.findViewById(R.id.tv_truckLength);
        tv_driverAge = (TextView) convertView.findViewById(R.id.tv_driverAge);
        tv_driverShuoShuo = (TextView) convertView.findViewById(R.id.tv_driverShuoShuo);

        tv_thumbUpCount = (TextView) convertView.findViewById(R.id.tv_thumbUpCount);
        tv_favoriteUserCount = (TextView) convertView.findViewById(R.id.tv_favoriteUserCount);
        tv_hasProduct = (TextView) convertView.findViewById(R.id.tv_hasProduct);
        iv_hasProduct = (ImageView) convertView.findViewById(R.id.iv_hasProduct);

        tv_company = (TextView) convertView.findViewById(R.id.tv_company);
        iv_phoneIcon = (ImageView) convertView.findViewById(R.id.iv_phoneIcon);

        ll_gender = (LinearLayout) convertView.findViewById(R.id.ll_gender);
        ly_truck = (LinearLayout) convertView.findViewById(R.id.ly_truck);
        ll_driverAge = (LinearLayout) convertView.findViewById(R.id.ll_driverAge);
        ly_company = (LinearLayout)convertView.findViewById(R.id.ly_company);
        ly_route = (LinearLayout)convertView.findViewById(R.id.ly_route);
        line_route = (LinearLayout)convertView.findViewById(R.id.line_route);
        line_truck = (LinearLayout)convertView.findViewById(R.id.line_truck);
        line_company = (LinearLayout)convertView.findViewById(R.id.line_company);

        iv_userEdit = (ImageView) convertView.findViewById(R.id.iv_userEdit);
        tv_mobileNo = (TextView) convertView.findViewById(R.id.tv_mobileNo);
        tv_userTypeStr = (TextView) convertView.findViewById(R.id.tv_userTypeStr);
        tv_userStatus = (TextView)convertView.findViewById(R.id.tv_userStatus);

        tv_dongTaiNum = (TextView) convertView.findViewById(R.id.tv_dongTaiNum);
        iv_dongTaiImg = (ImageView) convertView.findViewById(R.id.iv_dongTaiImg);

        iv_truckTypeImg = (ImageView) convertView.findViewById(R.id.iv_truckTypeImg);
        tv_truckWidth = (TextView) convertView.findViewById(R.id.tv_truckWidth);
        tv_truckHeight = (TextView) convertView.findViewById(R.id.tv_truckHeight);
        tv_modelNumber = (TextView) convertView.findViewById(R.id.tv_modelNumber);
        tv_seatingCapacity = (TextView) convertView.findViewById(R.id.tv_seatingCapacity);

        fl_title = (FieldLayout) convertView.findViewById(R.id.title);
        fl_description = (FieldLayout) convertView.findViewById(R.id.description);
        fl_hobby = (FieldLayout) convertView.findViewById(R.id.hobby);
        fl_homeTown = (FieldLayout) convertView.findViewById(R.id.homeTown);
        fl_createDate = (FieldLayout) convertView.findViewById(R.id.createDate);

        fl_jobPosition = (FieldLayout) convertView.findViewById(R.id.fl_jobPosition);
        fl_company = (FieldLayout) convertView.findViewById(R.id.fl_company);
        fl_address = (FieldLayout) convertView.findViewById(R.id.fl_address);
        fl_fixPhoneNo = (FieldLayout) convertView.findViewById(R.id.fl_fixPhoneNo);
        fl_industryName = (FieldLayout) convertView.findViewById(R.id.fl_industryName);

        tv_footer_call_btn = (TextView) convertView.findViewById(R.id.tv_footer_call_btn);
        progressBar = (RelativeLayout) convertView.findViewById(R.id.progressBar1);
    }

    public void setupViews(JSONObject jsonObject, final Context context) throws JSONException {

        JSONObject jObject;
        if(jsonObject.has("user"))
            jObject = jsonObject.getJSONObject("user");
       else
            jObject = jsonObject.getJSONObject("userWithLatLng");

        String userStatus = JSONUtil.getStringFromJson(jObject, "status", "正常");
        if(!userStatus.equals("正常"))
        {
            if(tv_userStatus != null)
                tv_userStatus.setText("(" + userStatus + ")");
        }
        else
        {
            if(tv_userStatus != null)
                tv_userStatus.setText("");
        }

        if(tv_userTypeStr != null)
            tv_userTypeStr.setText(JSONUtil.getStringFromJson(jsonObject, "userTypeStr", ""));

        String userName = JSONUtil.getStringFromJson(jObject, "userName", "佚名");
        String imgUrl = JSONUtil.getStringFromJson(jObject, "imgUrl", "");
        int verifyFlag = 0;
        if(JSONUtil.getBoolFromJson(jObject, "sfzVerify"))
        {
            verifyFlag = 1;
            iv_userVerify.setVisibility(View.VISIBLE);
            imgUrl = JSONUtil.getStringFromJson(jObject, "faceImgUrl", imgUrl);
            userName = JSONUtil.getStringFromJson(jObject, "identityName", userName);
        }
        else
        {
            iv_userVerify.setVisibility(View.GONE);
        }
        tv_userName.setText(userName);

        int gender = JSONUtil.getIntFromJson(jsonObject, "gender", 0);
        if(gender == 2){
            iv_gender.setImageResource(R.drawable.icon_nv_big);
            ll_gender.setBackgroundResource(R.drawable.bg_nv);
        }
        else if(gender == 1){
            iv_gender.setImageResource(R.drawable.icon_nan_big);
            ll_gender.setBackgroundResource(R.drawable.bg_nan);
        }
        else
        {
            //TODO:保密的性别处理
        }

        Integer age = JSONUtil.getIntFromJson(jsonObject, "age", 0);
        tv_userAge.setText(age.toString());

        double lat = JSONUtil.getDoubleFromJson(jObject, "lat", 0.0);
        double lng = JSONUtil.getDoubleFromJson(jObject, "lng", 0.0);

        String distance = ((MyApplication)((Activity)context).getApplication()).getDistance(lat, lng);

        //TODO:
        tv_lastPlace.setText(distance + "km");
        JSONUtil.setValueFromJson(tv_lastTime, jObject, "latlngDate", "未知");
        if(tv_mobileNo != null)
        {
            String mobileNo = JSONUtil.getStringFromJson(jObject, "mobileNo", "");
            if(mobileNo.length() > 7)
            {
                mobileNo = mobileNo.substring(0, 3) + "****" +  mobileNo.substring(7);
            }
            tv_mobileNo.setText(mobileNo);
        }

        int thumbUpCount = jObject.getInt("thumbUpCount");
        int favoriteUserCount = jObject.getInt("favoriteUserCount");
        boolean alreadyFavorite = jObject.getBoolean("alreadyFavorite");
        final int userID = jObject.getInt("userID");

        setFavoriteAndThumbUp(userID, thumbUpCount, favoriteUserCount, alreadyFavorite, context, jObject);

        if(JSONUtil.getBoolFromJson(jsonObject, "hasProduct"))
        {
            iv_hasProduct.setVisibility(View.GONE);
            tv_hasProduct.setVisibility(View.VISIBLE);

            View.OnClickListener theClick = new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v){
                    switch (v.getId()) {
                        case R.id.iv_hasProduct:
                        case R.id.tv_hasProduct:
                            Intent intent = new Intent(context, MainPage.class);
                            intent.putExtra("contentFragment", "UserProductFragment");
                            intent.putExtra("windowTitle", "他的货源");
                            intent.putExtra("hasWindowTitle", true);
                            intent.putExtra("extraID", userID);
                            context.startActivity(intent);
                            break;

                        default:
                            break;
                    }
                }
            };
            iv_hasProduct.setOnClickListener(theClick);
            tv_hasProduct.setOnClickListener(theClick);
        }
        else
        {
            iv_hasProduct.setVisibility(View.GONE);
            tv_hasProduct.setVisibility(View.GONE);
        }

        verifyFlag = setupTruckInfo(context, jObject, jsonObject, verifyFlag);

        if(fl_title != null)
        {
            JSONUtil.setFieldValueFromJson(fl_title, jsonObject, "title", "无");
            JSONUtil.setFieldValueFromJson(fl_description, jsonObject, "description", "无");
            JSONUtil.setFieldValueFromJson(fl_hobby, jsonObject, "hobby", "无");
            JSONUtil.setFieldValueFromJson(fl_homeTown, jsonObject, "homeTown", "未知");
            JSONUtil.setFieldValueFromJson(fl_createDate, jObject, "createDate", "未知");
        }

        verifyFlag = setupCompanyInfo(jsonObject, jObject, verifyFlag);

        setupUserVerifyImg(jObject, verifyFlag);

        setupFaceImg(context, imgUrl);

        final String mobile = JSONUtil.getStringFromJson(jObject, "mobileNo", "");
        if(!mobile.isEmpty())
        {
            if(tv_footer_call_btn != null)
            {
                tv_footer_call_btn.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.CALL");
                        intent.setData(Uri.parse("tel:" + mobile));//mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                        context.startActivity(intent);
                    }
                });
            }

            if(iv_phoneIcon != null)
            {
                iv_phoneIcon.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.CALL");
                        intent.setData(Uri.parse("tel:" + mobile));//mobile为你要拨打的电话号码，模拟器中为模拟器编号也可
                        context.startActivity(intent);
                    }
                });
            }
        }
        else
        {
            if(tv_footer_call_btn != null)
                tv_footer_call_btn.setOnClickListener(null);
            if(iv_phoneIcon != null)
                iv_phoneIcon.setOnClickListener(null);
        }
    }

    private void setupUserVerifyImg(JSONObject jObject, int verifyFlag) throws JSONException {
        iv_userVerify.setVisibility(View.VISIBLE);
        if(verifyFlag == 1)
            iv_userVerify.setImageResource(R.drawable.icon_verify_1);
        else if(verifyFlag == 3)
            iv_userVerify.setImageResource(R.drawable.icon_verify_3);
        else if(verifyFlag == 5)
        {
            String industryType = jObject.getString("industryType");
            if(industryType.equals("1"))
                iv_userVerify.setImageResource(R.drawable.icon_verify_5_12);
            else
                iv_userVerify.setImageResource(R.drawable.icon_verify_5);
        }
        else if(verifyFlag == 7)
        {
            String industryType = jObject.getString("industryType");
            if(industryType.equals("1"))
                iv_userVerify.setImageResource(R.drawable.icon_verify_7_1);
            else
                iv_userVerify.setImageResource(R.drawable.icon_verify_72);
        }
        else
            iv_userVerify.setVisibility(View.GONE);
    }

    private void setupFaceImg(Context context, String imgUrl) {
        if(!imgUrl.isEmpty() && !imgUrl.equals("null"))
        {
            try {
                if (imgUrl.startsWith("http")) {
                    ImageLoader.getInstance().displayImage(imgUrl,iv_userFace, new DisplayImageOptions.Builder()
                            .resetViewBeforeLoading(true).cacheInMemory(true)
                            .cacheOnDisc(true).build());
                } else {
                    Bitmap mBitmap = PhotoUtils.decodeBitmap(imgUrl, PxUtils.dip2px(context, 200),
                            PxUtils.dip2px(context, 200));
                    iv_userFace.setImageBitmap(mBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            iv_userFace.setImageResource(R.drawable.item_icon_fans);
    }

    private int setupCompanyInfo(JSONObject jsonObject, JSONObject jObject, int verifyFlag) throws JSONException {
        if(JSONUtil.getBoolFromJson(jObject, "companyVerify"))
        {
            verifyFlag += 4;
        }

        if(StringUtils.isEmpty(JSONUtil.getStringFromJson(jObject, "company", "")))
        {
            if(ly_company != null) ly_company.setVisibility(View.GONE);
            if(tv_company != null) tv_company.setVisibility(View.GONE);
            if(line_company != null) line_company.setVisibility(View.GONE);
        }
        else
        {
            if(ly_company != null) ly_company.setVisibility(View.VISIBLE);
            if(tv_company != null)
            {
                tv_company.setVisibility(View.VISIBLE);
                JSONUtil.setValueFromJson(tv_company, jObject, "company", "未知");
            }
            if(line_company != null) line_company.setVisibility(View.VISIBLE);

            if(fl_title != null)
            {
                JSONUtil.setFieldValueFromJson(fl_jobPosition, jsonObject, "jobPosition", "未知");
                JSONUtil.setFieldValueFromJson(fl_address, jsonObject, "address", "未知");
                JSONUtil.setFieldValueFromJson(fl_fixPhoneNo, jsonObject, "fixPhoneNo", "未知");
                JSONUtil.setFieldValueFromJson(fl_industryName, jsonObject, "industryName", "未知");
                JSONUtil.setFieldValueFromJson(fl_company, jObject, "company", "未知");

                String industryType = jObject.getString("industryType");
                if(industryType.equals("1")) {
                    fl_industryName.setFieldValueText("物流货运行业");
                }
            }
        }
        return verifyFlag;
    }

    private int setupTruckInfo(Context context, JSONObject jObject, JSONObject jsonObject, int verifyFlag) throws JSONException {
        if(JSONUtil.getBoolFromJson(jObject, "driverVerify"))
        {
            verifyFlag += 2;
        }

        if(jObject.get("truckInfo") == JSONObject.NULL)
        {
            ly_truck.setVisibility(View.GONE);
            if(line_truck != null)
                line_truck.setVisibility(View.GONE);

            if(line_route != null)
                line_route.setVisibility(View.GONE);
            if(ly_route != null)
                ly_route.setVisibility(View.GONE);

            if(ll_driverAge != null)
                ll_driverAge.setVisibility(View.GONE);

            tv_licensePlate.setVisibility(View.GONE);

            if(tv_driverShuoShuo != null)
                tv_driverShuoShuo.setVisibility(View.GONE);
        }
        else
        {
            ly_truck.setVisibility(View.VISIBLE);
            if(line_truck != null)
                line_truck.setVisibility(View.VISIBLE);

            if(ly_route != null)
                ly_route.setVisibility(View.GONE);
            if(line_route != null)
                line_route.setVisibility(View.GONE);
//            try{
//                if(jsonObject.has("userRoutes") && jsonObject.getJSONArray("userRoutes").length() > 0)
//                    ly_route.setVisibility(View.VISIBLE);
//                else
//                    ly_route.setVisibility(View.GONE);
//            }
//            catch (Exception e){}
            if(ll_driverAge != null)
                ll_driverAge.setVisibility(View.VISIBLE);
            tv_licensePlate.setVisibility(View.VISIBLE);

            try {
                JSONObject truckInfoJsonObj = jObject.getJSONObject("truckInfo");

                Integer truckType =JSONUtil.getIntFromJson(truckInfoJsonObj,"truckType",0);
                if(truckType != null && truckType > 0)
                    tv_truckType.setText(ConstHolder.TruckTypeItems[truckType - 1]);
                else
                    tv_truckType.setText("未知");

                JSONUtil.setValueFromJson(tv_licensePlate, truckInfoJsonObj, "licensePlate", "未知", true);

                //NOTE: 用 tv_truckWidth 判断是列表还是详情
                if(tv_truckWidth == null)
                {
                    tv_loadLimit.setText(context.getResources().getString(R.string.user_loadLimitStr, JSONUtil.getStringFromJson(truckInfoJsonObj, "loadLimit", "未知")));
                    tv_truckLength.setText(context.getResources().getString(R.string.user_truckLengthStr, JSONUtil.getStringFromJson(truckInfoJsonObj, "truckLength", "未知")));
                }
                else
                {
                    JSONUtil.setValueFromJson(tv_loadLimit, truckInfoJsonObj, "loadLimit", 0.0, "未知", true);
                    JSONUtil.setValueFromJson(tv_truckLength, truckInfoJsonObj, "truckLength", 0.0, "未知", true);
                }

                JSONUtil.setValueFromJson(tv_truckWidth, truckInfoJsonObj, "truckWidth", 0, "未知", true);
                JSONUtil.setValueFromJson(tv_truckHeight, truckInfoJsonObj, "truckHeight", 0, "未知", true);
                JSONUtil.setValueFromJson(tv_modelNumber, truckInfoJsonObj, "modelNumber", "未知");
                JSONUtil.setValueFromJson(tv_seatingCapacity, truckInfoJsonObj, "seatingCapacity", 0, "未知", true);

                if(tv_driverShuoShuo != null)
                {
                    String tmpValue = JSONUtil.getStringFromJson(truckInfoJsonObj, "memo", "");
                    if(tmpValue.isEmpty())
                        tv_driverShuoShuo.setVisibility(View.GONE);
                    else
                    {
                        tv_driverShuoShuo.setVisibility(View.VISIBLE);
                         //NOTE: 用 tv_truckWidth 判断是列表还是详情
                        if(tv_truckWidth == null && tmpValue.length() > 38)
                            tmpValue = tmpValue.substring(0, 35) + "...";
                        tv_driverShuoShuo.setText(tmpValue);
                    }
                }

                Integer age = new Date().getYear()  + 1900 - JSONUtil.getIntFromJson(truckInfoJsonObj,"releaseYear", 0);
                tv_driverAge.setText(context.getResources().getString(R.string.user_driverAgeStr, age));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return verifyFlag;
    }

    private void setFavoriteAndThumbUp(final int userID, final int thumbUpCount, final int favoriteUserCount,
                                       final boolean alreadyFavorite, final Context context, final JSONObject ojObject) {
        try {
            ojObject.put("thumbUpCount", thumbUpCount).put("favoriteUserCount", favoriteUserCount).put("alreadyFavorite", alreadyFavorite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(alreadyFavorite)
        {
            tv_favoriteUserCount.setText(context.getResources().getString(R.string.user_cancelFavoriteUserCount, favoriteUserCount));
            tv_favoriteUserCount.setBackgroundResource(R.drawable.op_bg_cancel_view);
            tv_favoriteUserCount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.icon_op_u_c_view);
        }
        else
        {
            tv_favoriteUserCount.setText(context.getResources().getString(R.string.user_favoriteUserCount, favoriteUserCount));
            tv_favoriteUserCount.setBackgroundResource(R.drawable.op_bg_view);
            tv_favoriteUserCount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.icon_op_u_view);
        }
        tv_thumbUpCount.setText(context.getResources().getString(R.string.user_thumbUpCount, thumbUpCount));

        try{
            final Handler innerHandler = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    try {
                        String result = msg.obj.toString();
                        JSONObject jObject = new JSONObject(result);

                        if(progressBar != null)
                        {
                            progressBar.setVisibility(View.GONE);
                        }

                        boolean notLogin = jObject.has("haslogin");
                        boolean success = jObject.has("success");
                        //服务器未登录
                        if(notLogin)
                        {
                            Toast.makeText(context, "您尚未登录！", Toast.LENGTH_LONG).show();
                        }
                        else if(success)
                        {
                            Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();

                            if(msg.what == 1)
                            {
                                if(alreadyFavorite)
                                    setFavoriteAndThumbUp(userID, thumbUpCount, favoriteUserCount - 1, false, context, ojObject);
                                else
                                    setFavoriteAndThumbUp(userID, thumbUpCount, favoriteUserCount + 1, true, context, ojObject);
                            }
                            if(msg.what == 2)
                                setFavoriteAndThumbUp(userID, thumbUpCount + 1, favoriteUserCount, alreadyFavorite, context, ojObject);
                        }
                        else
                        {
                            Toast.makeText(context, jObject.getString("errMsg"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            tv_thumbUpCount.setOnClickListener(new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v)
                {
//                        if(progressBar != null)
//                            progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                    if(!NetworkUtils.isNetworkAvailable(context))
                    {
                        Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                    }
                    new UrlThread(innerHandler, UrlList.MAIN + "mvc/thumbUp_0_" + userID, 2).start();
                }
            });
            if(alreadyFavorite)
            {
                tv_favoriteUserCount.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v)
                    {
//                        if(progressBar != null)
//                            progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                        if(!NetworkUtils.isNetworkAvailable(context))
                        {
                            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/unFollow_" + userID, 1).start();
                    }
                });
            }
            else
            {
                tv_favoriteUserCount.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v)
                    {
//                        if(progressBar != null)
//                            progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                        if(!NetworkUtils.isNetworkAvailable(context))
                        {
                            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/follow_" + userID, 1).start();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
