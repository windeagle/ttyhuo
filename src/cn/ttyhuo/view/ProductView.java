package cn.ttyhuo.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.product.ProductEditActivity;
import cn.ttyhuo.activity.user.UserInfoActivity;
import cn.ttyhuo.common.ConstHolder;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 把每行布局文件的各个控件包装成一个对象
 */
public class ProductView {
    TextView tv_fromCity;
    TextView tv_toCity;
    ImageView iv_userFace;
    ImageView iv_userVerify;
    TextView tv_userName;
    TextView tv_productTitle;
    TextView tv_productDescription;
    private LinearLayout ly_provider;

    TextView tv_departurePlace;
    TextView tv_openTime;

    TextView tv_truckType;
    TextView tv_loadLimit;
    TextView tv_truckLength;
    TextView tv_genhuo;
    TextView tv_price;
    TextView tv_countInfo;
    TextView tv_departureTime;

    TextView tv_totalInfo;
    ImageView iv_purchase;
    TextView tv_purchase;
    TextView tv_favoriteUserCount;
    TextView tv_thumbUpCount;

    ImageView iv_phoneIcon;

    FieldLayout tv_fromTime;
    FieldLayout tv_fromAddr;
    FieldLayout tv_toTime;
    FieldLayout tv_toAddr;

    FieldLayout tv_jobPosition;
    FieldLayout tv_company;
    FieldLayout tv_fixPhoneNo;
    FieldLayout tv_description;
    TextView tv_footer_call_btn;

    ImageView productEdit;
    TextView tv_new_window_btn;
    TextView tv_pause_btn;
    TextView tv_delete_btn;
    LinearLayout ly_myproduct_footer;

    private RelativeLayout progressBar;

    boolean isMy = false;

    public ProductView(View convertView, boolean isMy)
    {
        this(convertView);
        this.isMy = isMy;
    }

    public ProductView(View convertView) {
        tv_fromCity = (TextView) convertView.findViewById(R.id.fromCity);
        tv_toCity = (TextView) convertView.findViewById(R.id.toCity);

        iv_userFace = (ImageView) convertView.findViewById(R.id.iv_userFace);
        iv_userVerify = (ImageView) convertView.findViewById(R.id.iv_userVerify);
        tv_userName = (TextView) convertView.findViewById(R.id.tv_userName);
        tv_productTitle = (TextView) convertView.findViewById(R.id.tv_productTitle);
        tv_productDescription = (TextView) convertView.findViewById(R.id.tv_productDescription);
        ly_provider = (LinearLayout) convertView.findViewById(R.id.ly_provider);

        tv_departurePlace = (TextView) convertView.findViewById(R.id.tv_lastPlace);
        tv_openTime = (TextView) convertView.findViewById(R.id.tv_lastTime);

        tv_truckType = (TextView) convertView.findViewById(R.id.tv_truckType);
        tv_loadLimit = (TextView) convertView.findViewById(R.id.tv_loadLimit);
        tv_truckLength = (TextView) convertView.findViewById(R.id.tv_truckLength);

        tv_genhuo = (TextView) convertView.findViewById(R.id.tv_genhuo);
        tv_price = (TextView) convertView.findViewById(R.id.tv_price);
        tv_countInfo = (TextView) convertView.findViewById(R.id.tv_countInfo);
        tv_departureTime = (TextView) convertView.findViewById(R.id.tv_departureTime);

        tv_totalInfo = (TextView) convertView.findViewById(R.id.tv_totalInfo);
        iv_purchase = (ImageView) convertView.findViewById(R.id.iv_purchase);
        tv_purchase = (TextView) convertView.findViewById(R.id.tv_purchase);

        tv_thumbUpCount = (TextView) convertView.findViewById(R.id.tv_thumbUpCount);
        tv_favoriteUserCount = (TextView) convertView.findViewById(R.id.tv_favoriteUserCount);
        iv_phoneIcon = (ImageView) convertView.findViewById(R.id.iv_phoneIcon);

        tv_description = (FieldLayout) convertView.findViewById(R.id.description);
        tv_jobPosition = (FieldLayout) convertView.findViewById(R.id.fl_jobPosition);
        tv_company = (FieldLayout) convertView.findViewById(R.id.fl_company);
        tv_fixPhoneNo = (FieldLayout) convertView.findViewById(R.id.fl_fixPhoneNo);

        tv_fromTime = (FieldLayout) convertView.findViewById(R.id.fromTime);
        tv_fromAddr = (FieldLayout) convertView.findViewById(R.id.fromAddr);
        tv_toTime = (FieldLayout) convertView.findViewById(R.id.toTime);
        tv_toAddr = (FieldLayout) convertView.findViewById(R.id.toAddr);
        tv_footer_call_btn = (TextView) convertView.findViewById(R.id.tv_footer_call_btn);

        tv_new_window_btn = (TextView) convertView.findViewById(R.id.tv_new_window_btn);
        tv_pause_btn = (TextView) convertView.findViewById(R.id.tv_pause_btn);
        tv_delete_btn = (TextView) convertView.findViewById(R.id.tv_delete_btn);
        productEdit = (ImageView) convertView.findViewById(R.id.productEdit);
        ly_myproduct_footer = (LinearLayout) convertView.findViewById(R.id.ly_myproduct_footer);

        progressBar = (RelativeLayout) convertView.findViewById(R.id.progressBar1);
    }

    public void setupViews(JSONObject jsonObject, final Context context) throws JSONException {
        //支持ProductWithLatLng
        JSONObject jObject = jsonObject.has("product") ? jsonObject.getJSONObject("product") : jsonObject;

        JSONUtil.setValueFromJson(tv_userName, jsonObject, "providerUserName", "佚名");
        String title = JSONUtil.getStringFromJson(jObject, "title", "未命名货源");
        String imgUrl = JSONUtil.getStringFromJson(jsonObject,"provideUserImgUrl", "");
        String status =  JSONUtil.getStringFromJson(jsonObject, "status", "召车中");
        if(!status.equals("召车中"))
            title += context.getResources().getString(R.string.product_status, status);
        tv_productTitle.setText(title);
        if(title.equals("未命名货源"))
            tv_productTitle.setVisibility(View.GONE);
        else
            tv_productTitle.setVisibility(View.VISIBLE);
        if(tv_productDescription != null)
        {
            String tmpValue = JSONUtil.getStringFromJson(jObject, "description", "");
            if(tmpValue.isEmpty())
                tv_productDescription.setVisibility(View.GONE);
            else
            {
                tv_productDescription.setVisibility(View.VISIBLE);
                if(tmpValue.length() > 38)
                    tmpValue = tmpValue.substring(0, 35) + "...";
                tv_productDescription.setText(tmpValue);
            }
        }

        try{
            final int provideUserID = jObject.getInt("provideUserID");
            if(ly_provider != null)
                ly_provider.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v){
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        intent.putExtra("userID", provideUserID);
                        context.startActivity(intent);
                    }
                });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tv_fromCity.setText(CommonUtils.getFromCityStr(jObject));
        tv_toCity.setText(CommonUtils.getToCityStr(jObject));

        CommonUtils.setupDistance((Activity) context, tv_departurePlace, jObject);
        JSONUtil.setValueFromJson(tv_openTime, jsonObject, "windowOpenTime", "未知");

        Integer truckType = jObject.getInt("truckType");
        if(truckType != null && truckType > 0)
            tv_truckType.setText(ConstHolder.TruckTypeItems[truckType - 1]);
        else
            tv_truckType.setText("未知");

        double loadLimitD = JSONUtil.getDoubleFromJson(jObject, "loadLimit", 0.0);
        if(loadLimitD == 0.0)
            tv_loadLimit.setVisibility(View.GONE);
        else
            tv_loadLimit.setVisibility(View.VISIBLE);
        tv_loadLimit.setText(context.getResources().getString(R.string.user_loadLimitStr, loadLimitD));

        double truckLengthD = JSONUtil.getDoubleFromJson(jObject, "truckLength", 0.0);
        if(truckLengthD == 0.0)
            tv_truckLength.setVisibility(View.GONE);
        else
            tv_truckLength.setVisibility(View.VISIBLE);
        tv_truckLength.setText(context.getResources().getString(R.string.user_truckLengthStr, truckLengthD));

        JSONObject productWindow = jObject;
        if(jsonObject.has("currentProductWindow"))
        {
            //支持ProductForList
            productWindow = jsonObject.getJSONObject("currentProductWindow");
        }

        double priceD = JSONUtil.getDoubleFromJson(productWindow, "price", 0.0);
        if(priceD == 0.0)
            tv_price.setVisibility(View.GONE);
        else
            tv_price.setVisibility(View.VISIBLE);
        tv_price.setText(context.getResources().getString(R.string.product_price, priceD));

        String occupiedCountString = context.getResources().getString(R.string.product_occupiedCount, JSONUtil.getStringFromJson(productWindow, "occupiedCount", "0"));
        String limitCountString = context.getResources().getString(R.string.product_limitCount, JSONUtil.getStringFromJson(productWindow, "limitCount", "0"));
        if(occupiedCountString.equals(limitCountString))
            tv_countInfo.setText(occupiedCountString + "/" + limitCountString + "(已召满)");
        else
            tv_countInfo.setText(occupiedCountString + "/" + limitCountString);
        try
        {
            tv_departureTime.setText(StringUtils.getTimeStr(new Date(productWindow.getLong("departureTime"))) + "发车");
            if(tv_fromTime != null)
                tv_fromTime.setFieldValueText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(productWindow.getLong("departureTime"))));
            if(tv_toTime != null)
                tv_toTime.setFieldValueText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(productWindow.getLong("arrivalTime"))));
        } catch (JSONException ex) {
            tv_departureTime.setText("提货时间未知");
            if(tv_fromTime != null)
                tv_fromTime.setFieldValueText("未知");
            if(tv_toTime != null)
                tv_toTime.setFieldValueText("未知");
            ex.printStackTrace();
        }

        int thumbUpCount = jsonObject.getInt("thumbUpCount");
        int favoriteUserCount = jsonObject.getInt("favoriteUserCount");
        boolean alreadyFavorite = jsonObject.getBoolean("alreadyFavorite");
        final int productID = jObject.getInt("productID");
        if(jsonObject.has("product") && ly_myproduct_footer != null)
        {
            final int statusInt = jObject.getInt("status");
            setOperation(productID, (byte)statusInt, context);
        }

        setFavoriteAndThumbUp(productID, thumbUpCount, favoriteUserCount, alreadyFavorite, context, jsonObject);

        if(tv_genhuo != null)
        {
            double genhuoI = JSONUtil.getIntFromJson(productWindow, "merchandiserNum", 0);
            if(genhuoI == 0)
                tv_genhuo.setVisibility(View.GONE);
            else
                tv_genhuo.setVisibility(View.VISIBLE);
            tv_genhuo.setText(genhuoI + "跟货员/车");
        }

        if(tv_totalInfo != null)
            tv_totalInfo.setText("自"
                    + new SimpleDateFormat("yyyy-MM-dd").format(new Date(jObject.getLong("createDate")))
                    + "日" + jsonObject.getInt("productWindowCount")
                    + "次召车总成交" + jsonObject.getInt("allPurchaseCount") + "车");

        CommonUtils.setupUserVerifyImg(jsonObject, iv_userVerify);

        JSONUtil.setFieldValueFromJson(tv_fromAddr, jObject, "fromAddr", "未知");
        JSONUtil.setFieldValueFromJson(tv_toAddr, jObject, "toAddr", "未知");
        JSONUtil.setFieldValueFromJson(tv_description, jObject, "description", "无");

        JSONUtil.setFieldValueFromJson(tv_jobPosition, jsonObject, "providerJobPosition", "未知");
        JSONUtil.setFieldValueFromJson(tv_fixPhoneNo, jsonObject, "providerFixPhoneNo", "未知");
        JSONUtil.setFieldValueFromJson(tv_company, jsonObject, "providerCompany", "未知");

        if(!isMy)
        {
            if(JSONUtil.getBoolFromJson(jsonObject, "canBuy"))
            {
                tv_purchase.setVisibility(View.VISIBLE);
                //iv_purchase.setVisibility(View.VISIBLE);
                View.OnClickListener purchaseOnClickListener = new View.OnClickListener() {
                    public void onClick(View v) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.VISIBLE);

                        Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                        final ProgressDialog dialog = ProgressDialog.show(context,
                                context.getString(R.string.app_name), "操作进行中", true,
                                true, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                dialog.dismiss();
                            }
                        });
                        final Handler innerHandler = new Handler() {
                            public void handleMessage(Message msg) {
                                try {
                                    String result = msg.obj.toString();
                                    JSONObject jObject = new JSONObject(result);

                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    dialog.dismiss();

                                    boolean notLogin = jObject.has("haslogin");
                                    boolean success = jObject.has("success");
                                    //服务器未登录
                                    if (notLogin) {
                                        DialogUtils.createNormalDialog(context, 0, "尚未登录", "您尚未登录！", null, null, null, null).show();
                                    } else if (success) {
                                        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();
                                        DialogUtils.createNormalDialog(context, 0, "操作成功", jObject.getString("msg"), null, null, null, null).show();
                                        tv_purchase.setText("已接货");
                                        tv_purchase.setOnClickListener(null);
                                        iv_purchase.setOnClickListener(null);
                                    } else {
                                        DialogUtils.createNormalDialog(context, 0, "提示", jObject.getString("errMsg"), null, null, null, null).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        if(!NetworkUtils.isNetworkAvailable(context))
                        {
                            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/purchase_" + productID, 1).start();
                    }
                };
                tv_purchase.setOnClickListener(purchaseOnClickListener);
                iv_purchase.setOnClickListener(purchaseOnClickListener);
            }
            else
            {
                tv_purchase.setVisibility(View.GONE);
                iv_purchase.setVisibility(View.GONE);
            }
        }
        else
        {
            tv_purchase.setVisibility(View.GONE);
            iv_purchase.setVisibility(View.GONE);
        }

        if(!StringUtils.isEmpty(imgUrl))
            CommonUtils.setupFaceImg(context, iv_userFace, imgUrl);
        else
            iv_userFace.setImageResource(R.drawable.item_icon_fans);

        final String mobile = JSONUtil.getStringFromJson(jObject, "provideUserMobileNo", "");
        if(!StringUtils.isEmpty(mobile))
        {
            CommonUtils.call(context, tv_footer_call_btn, mobile);
            if(isMy && iv_phoneIcon != null)
                iv_phoneIcon.setVisibility(View.GONE);
            CommonUtils.call(context, iv_phoneIcon, mobile);
        }
    }

    private void setOperation(final int productID, final byte status, final Context context) {
        if(status < 0)
        {
            productEdit.setVisibility(View.GONE);
            ly_myproduct_footer.setVisibility(View.GONE);
            return;
        }
        productEdit.setVisibility(View.VISIBLE);
        ly_myproduct_footer.setVisibility(View.VISIBLE);
        final String pauseAction = status == 3 ? "product_open_" :"product_pause_";
        if(status == 3)
        {
            tv_new_window_btn.setVisibility(View.GONE);
            tv_pause_btn.setText("恢复");
        }
        else
        {
            tv_new_window_btn.setVisibility(View.VISIBLE);
            tv_pause_btn.setText("暂停");
        }

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.app_name));
        progressDialog.setMessage("操作进行中");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        final Handler innerHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                try {
                    String result = msg.obj.toString();
                    JSONObject jObject = new JSONObject(result);

                    if(progressBar != null)
                    {
                        progressBar.setVisibility(View.GONE);
                    }
                    progressDialog.dismiss();

                    boolean notLogin = jObject.has("haslogin");
                    boolean success = jObject.has("success");
                    if(notLogin)
                    {
                        Toast.makeText(context, "您尚未登录！", Toast.LENGTH_LONG).show();
                    }
                    else if(success)
                    {
                        Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();

                        if(msg.what == 1)
                        {
                            if(status == 3)
                                setOperation(productID, (byte)2, context);
                            if(status == 2)
                                setOperation(productID, (byte)3, context);
                        }
                        if(msg.what == 2)
                            setOperation(productID, (byte)-3, context);
                    }
                    else
                    {
                        Toast.makeText(context, jObject.getString("系统异常"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        if(tv_new_window_btn != null)
        {
            View.OnClickListener lickListener = new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ProductEditActivity.class);
                    intent.putExtra("productID", productID);
                    context.startActivity(intent);
                }
            };
            tv_new_window_btn.setOnClickListener(lickListener);
            productEdit.setOnClickListener(lickListener);
        }
        if(tv_pause_btn != null)
        {
            tv_pause_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if(progressBar != null)
                        progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    if(!NetworkUtils.isNetworkAvailable(context))
                    {
                        Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                    }
                    new UrlThread(innerHandler, UrlList.MAIN + "mvc/" + pauseAction + productID, 1).start();
                }
            });
        }
        if(tv_delete_btn != null)
        {
            tv_delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    DialogUtils.createNormalDialog(context, 0, "请确认", "危险动作请确认！", "确认", new DialogInterface.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(progressBar != null)
                                progressBar.setVisibility(View.VISIBLE);
                            Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            if(!NetworkUtils.isNetworkAvailable(context))
                            {
                                Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                            }
                            new UrlThread(innerHandler, UrlList.MAIN + "mvc/product_disable_" + productID, 2).start();
                        }
                    }, null, null).show();
                }
            });
        }
    }

    private void setFavoriteAndThumbUp(final int productID, final int thumbUpCount, final int favoriteUserCount,
                                       final boolean alreadyFavorite, final Context context, final JSONObject ojObject) {
        try {
            ojObject.put("thumbUpCount", thumbUpCount).put("favoriteUserCount", favoriteUserCount).put("alreadyFavorite", alreadyFavorite);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(alreadyFavorite)
        {
            tv_favoriteUserCount.setText(context.getResources().getString(R.string.product_cancelFavoriteUserCount, favoriteUserCount));
            tv_favoriteUserCount.setBackgroundResource(R.drawable.op_bg_cancel_love);
            tv_favoriteUserCount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.icon_op_u_c_love);
        }
        else
        {
            tv_favoriteUserCount.setText(context.getResources().getString(R.string.product_favoriteUserCount, favoriteUserCount));
            tv_favoriteUserCount.setBackgroundResource(R.drawable.op_bg_love);
            tv_favoriteUserCount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.icon_op_u_love);
        }
        tv_thumbUpCount.setText(context.getResources().getString(R.string.product_thumbUpCount, thumbUpCount));

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
                                    setFavoriteAndThumbUp(productID, thumbUpCount, favoriteUserCount - 1, false, context, ojObject);
                                else
                                    setFavoriteAndThumbUp(productID, thumbUpCount, favoriteUserCount + 1, true, context, ojObject);
                            }
                            if(msg.what == 2)
                                setFavoriteAndThumbUp(productID, thumbUpCount + 1, favoriteUserCount, alreadyFavorite, context, ojObject);
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
                    if(progressBar != null)
                        progressBar.setVisibility(View.VISIBLE);
                    Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                    if(!NetworkUtils.isNetworkAvailable(context))
                    {
                        Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                    }
                    new UrlThread(innerHandler, UrlList.MAIN + "mvc/thumbUp_1_" + productID, 2).start();
                }
            });
            if(alreadyFavorite)
            {
                tv_favoriteUserCount.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v)
                    {
                        if(progressBar != null)
                            progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                        if(!NetworkUtils.isNetworkAvailable(context))
                        {
                            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/unFavorite_" + productID, 1).start();
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
                        if(progressBar != null)
                            progressBar.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                        if(!NetworkUtils.isNetworkAvailable(context))
                        {
                            Toast.makeText(context, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/favorite_" + productID, 1).start();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}