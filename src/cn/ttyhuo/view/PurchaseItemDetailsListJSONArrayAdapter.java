package cn.ttyhuo.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import baidumapsdk.demo.BaseMapDemo;
import cn.ttyhuo.R;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PurchaseItemDetailsListJSONArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射
    private JSONArray list;
    private Context context;
    private FieldLayout fl_checkTime;
    private FieldLayout fl_purchaseTime;
    private FieldLayout fl_confirmTime;
    private FieldLayout fl_providerSeqNo;
    private ListView lv;

    public void setList(JSONArray list) {
        this.list = list;
    }

    public PurchaseItemDetailsListJSONArrayAdapter(JSONArray list, Context context, ListView lv) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.lv = lv;
    }

    @Override
    public int getCount() {
        return list.length();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserView oneView;
        ProductView productView;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.purchaseitemdetails_list_item, null);
            View userOrgView = convertView.findViewById(R.id.ly_user);
            oneView = new UserView(userOrgView);
            productView = null;
            HashMap<UserView, ProductView> map = new HashMap<UserView, ProductView>(1);
            map.put(oneView, productView);
            convertView.setTag(map.entrySet().toArray()[0]);// 把View和某个对象关联起来
        } else {
            oneView = ((HashMap.Entry<UserView, ProductView>) convertView.getTag()).getKey();
            //productView = ((HashMap.Entry<UserView, ProductView>) convertView.getTag()).getValue();
        }
        try {
            final JSONObject jObject = list.getJSONObject(position);

            final JSONObject userJObject = jObject.getJSONObject("currentUser");// 根据position获取集合类中某行数据
            oneView.setupViews(userJObject, context);
            final View userOrgView = convertView.findViewById(R.id.ly_user);
            ImageView iv_hasProduct = (ImageView) userOrgView.findViewById(R.id.iv_hasProduct);
            TextView tv_hasProduct = (TextView) userOrgView.findViewById(R.id.tv_hasProduct);
            iv_hasProduct.setVisibility(View.GONE);
            tv_hasProduct.setVisibility(View.GONE);

            JSONObject purchaseJObject = jObject.getJSONObject("purchaseDetail");// 根据position获取集合类中某行数据
            fl_checkTime = (FieldLayout)convertView.findViewById(R.id.fl_checkTime);
            fl_purchaseTime = (FieldLayout)convertView.findViewById(R.id.fl_purchaseTime);
            fl_confirmTime = (FieldLayout)convertView.findViewById(R.id.fl_confirmTime);
            fl_providerSeqNo = (FieldLayout)convertView.findViewById(R.id.fl_providerSeqNo);
            if(fl_purchaseTime != null && !purchaseJObject.isNull("createTime"))
                fl_purchaseTime.setFieldValueText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(purchaseJObject.getLong("createTime"))));
            if(fl_checkTime != null && !purchaseJObject.isNull("checkTime"))
            {
                fl_checkTime.setVisibility(View.VISIBLE);
                fl_checkTime.setFieldValueText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(purchaseJObject.getLong("checkTime"))));
            }
            else
                fl_checkTime.setVisibility(View.GONE);
            if(fl_confirmTime != null && !purchaseJObject.isNull("confirmTime"))
            {
                fl_confirmTime.setVisibility(View.VISIBLE);
                fl_confirmTime.setFieldValueText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(purchaseJObject.getLong("confirmTime"))));
            }
            else
                fl_confirmTime.setVisibility(View.GONE);
            if(fl_providerSeqNo != null && !purchaseJObject.isNull("providerSeqNo") && purchaseJObject.getInt("providerSeqNo") != 0)
            {
                fl_providerSeqNo.setVisibility(View.VISIBLE);
                JSONUtil.setFieldValueFromJson(fl_providerSeqNo, purchaseJObject, "providerSeqNo", "未知");
            }
            else
                fl_providerSeqNo.setVisibility(View.GONE);

            ImageView iv_receipt = (ImageView) userOrgView.findViewById(R.id.iv_receipt);
            TextView tv_receipt = (TextView) userOrgView.findViewById(R.id.tv_receipt);
            ImageView iv_deny = (ImageView) userOrgView.findViewById(R.id.iv_deny);
            TextView tv_deny = (TextView) userOrgView.findViewById(R.id.tv_deny);
            ImageView iv_confirm = (ImageView) userOrgView.findViewById(R.id.iv_confirm);
            TextView tv_confirm = (TextView) userOrgView.findViewById(R.id.tv_confirm);
            ImageView iv_hisPlace = (ImageView) userOrgView.findViewById(R.id.iv_hisPlace);
            TextView tv_hisPlace = (TextView) userOrgView.findViewById(R.id.tv_hisPlace);

            final int pdID = purchaseJObject.getInt("pdid");
            final int status = purchaseJObject.getInt("status");
            try{
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
                            progressDialog.dismiss();
                            String result = msg.obj.toString();
                            JSONObject retJObject = new JSONObject(result);

                            boolean notLogin = retJObject.has("haslogin");
                            boolean success = retJObject.has("success");
                            //服务器未登录
                            if(notLogin)
                            {
                                Toast.makeText(context, "您尚未登录！", Toast.LENGTH_LONG).show();
                            }
                            else if(success)
                            {
                                Toast.makeText(context, "操作成功", Toast.LENGTH_SHORT).show();

                                //userOrgView.setVisibility(View.GONE);
                                JSONArray mData = JSONUtil.delJSONArrayItem(list, jObject);
                                PurchaseItemDetailsListJSONArrayAdapter.this.setList(mData);
                                PurchaseItemDetailsListJSONArrayAdapter.this.notifyDataSetChanged();
                                setListViewHeightBasedOnChildren(lv);
                            }
                            else
                            {
                                Toast.makeText(context, retJObject.getString("errMsg"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                if(status == 0)
                {
                    iv_receipt.setVisibility(View.VISIBLE);
                    tv_receipt.setVisibility(View.VISIBLE);
                    iv_deny.setVisibility(View.VISIBLE);
                    tv_deny.setVisibility(View.VISIBLE);
                    tv_receipt.setOnClickListener(new View.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            new UrlThread(innerHandler, UrlList.MAIN + "mvc/purchase_check_" + pdID, 2).start();
                        }
                    });
                    tv_deny.setOnClickListener(new View.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            new UrlThread(innerHandler, UrlList.MAIN + "mvc/purchase_check_" + pdID + "?reject=1", 1).start();
                        }
                    });
                }
                else
                {
                    iv_receipt.setVisibility(View.GONE);
                    tv_receipt.setVisibility(View.GONE);
                    iv_deny.setVisibility(View.GONE);
                    tv_deny.setVisibility(View.GONE);
                }
                if(status == 1)
                {
                    iv_confirm.setVisibility(View.VISIBLE);
                    tv_confirm.setVisibility(View.VISIBLE);
                    iv_hisPlace.setVisibility(View.VISIBLE);
                    tv_hisPlace.setVisibility(View.VISIBLE);
                    tv_confirm.setOnClickListener(new View.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            new UrlThread(innerHandler, UrlList.MAIN + "mvc/purchase_send_" + pdID, 1).start();
                        }
                    });

                    tv_hisPlace.setOnClickListener(new View.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(View v)
                        {
                            try{
                                Intent intent = new Intent(context, BaseMapDemo.class);
                                JSONObject jObject;
                                if(userJObject.has("user"))
                                    jObject = userJObject.getJSONObject("user");
                                else
                                    jObject = userJObject.getJSONObject("userWithLatLng");

                                double lat = 0.0;
                                double lng = 0.0;
                                String tmpStr = jObject.getString("lat");
                                if(!tmpStr.isEmpty() && !tmpStr.equals("null"))
                                    lat = jObject.getDouble("lat");

                                tmpStr = jObject.getString("lng");
                                if(!tmpStr.isEmpty() && !tmpStr.equals("null"))
                                    lng = jObject.getDouble("lng");

                                intent.putExtra("y", lat);
                                intent.putExtra("x", lng);
                                context.startActivity(intent);
                            }
                            catch(Exception eee){}
                        }
                    });
                }
                else
                {
                    iv_confirm.setVisibility(View.GONE);
                    tv_confirm.setVisibility(View.GONE);
                    iv_hisPlace.setVisibility(View.GONE);
                    tv_hisPlace.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
}