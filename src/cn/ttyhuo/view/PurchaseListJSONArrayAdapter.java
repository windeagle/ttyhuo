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
import cn.ttyhuo.R;
import cn.ttyhuo.activity.product.ProductInfoActivity;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PurchaseListJSONArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射
    private JSONArray list;
    private Context context;
    private FieldLayout fl_checkTime;
    private FieldLayout fl_purchaseTime;
    private FieldLayout fl_confirmTime;
    private FieldLayout fl_providerSeqNo;

    public void setList(JSONArray list) {
        this.list = list;
    }

    public PurchaseListJSONArrayAdapter(JSONArray list, Context context) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.purchase_list_item, null);
            View productOrgView = convertView.findViewById(R.id.ly_product);
            oneView = null;
            productView = new ProductView(productOrgView);
            HashMap<UserView, ProductView> map = new HashMap<UserView, ProductView>(1);
            map.put(oneView, productView);
            convertView.setTag(map.entrySet().toArray()[0]);// 把View和某个对象关联起来
        } else {
            //oneView = ((HashMap.Entry<UserView, ProductView>) convertView.getTag()).getKey();
            productView = ((HashMap.Entry<UserView, ProductView>) convertView.getTag()).getValue();
        }
        try {
            final JSONObject jObject = list.getJSONObject(position);
            final View productOrgView = convertView.findViewById(R.id.ly_product);
            ImageView iv_purchase = (ImageView) productOrgView.findViewById(R.id.iv_purchase);
            TextView tv_purchase = (TextView) productOrgView.findViewById(R.id.tv_purchase);

            final JSONObject productJObject = jObject.getJSONObject("currentProduct");// 根据position获取集合类中某行数据
            productView.setupViews(productJObject, context);

            ((LinearLayout)productOrgView).setOnClickListener( new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, ProductInfoActivity.class);
                    try{
                        JSONObject jObject = productJObject.has("product") ? productJObject.getJSONObject("product") : productJObject;
                        intent.putExtra("productID", jObject.getInt("productID"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    context.startActivity(intent);
                }
            });
            iv_purchase.setVisibility(View.GONE);
            tv_purchase.setVisibility(View.GONE);

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

            ImageView iv_cancel = (ImageView) productOrgView.findViewById(R.id.iv_cancel);
            TextView tv_cancel = (TextView) productOrgView.findViewById(R.id.tv_cancel);

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

                                //productOrgView.setVisibility(View.GONE);
                                JSONArray mData = JSONUtil.delJSONArrayItem(list, jObject);
                                PurchaseListJSONArrayAdapter.this.setList(mData);
                                PurchaseListJSONArrayAdapter.this.notifyDataSetChanged();
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
                    iv_cancel.setVisibility(View.VISIBLE);
                    tv_cancel.setVisibility(View.VISIBLE);
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        // 点击按钮 追加数据 并通知适配器
                        @Override
                        public void onClick(View v)
                        {
                            Toast.makeText(context, "正在操作", Toast.LENGTH_SHORT).show();
                            progressDialog.show();
                            new UrlThread(innerHandler, UrlList.MAIN + "mvc/purchase_cancel_" + pdID, 2).start();
                        }
                    });
                }
                else
                {
                    iv_cancel.setVisibility(View.GONE);
                    tv_cancel.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}