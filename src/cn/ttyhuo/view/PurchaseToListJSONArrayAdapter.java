package cn.ttyhuo.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.product.MyProductInfoActivity;
import cn.ttyhuo.activity.user.UserInfoActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PurchaseToListJSONArrayAdapter extends BaseAdapter {
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

    public PurchaseToListJSONArrayAdapter(JSONArray list, Context context) {
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
            convertView = mInflater.inflate(R.layout.purchaseto_list_item, null);
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
            JSONObject jObject = list.getJSONObject(position);
            View productOrgView = convertView.findViewById(R.id.ly_product);
            ImageView iv_purchase = (ImageView) productOrgView.findViewById(R.id.iv_purchase);
            TextView tv_purchase = (TextView) productOrgView.findViewById(R.id.tv_purchase);

            final JSONObject productJObject = jObject.getJSONObject("currentProduct");// 根据position获取集合类中某行数据
            productView.setupViews(productJObject, context);

            try {
                final ListView lv = (ListView)convertView.findViewById(R.id.lv_purchaseDetailList);

                final ImageView iv_toggleIcon = (ImageView)convertView.findViewById(R.id.iv_toggleIcon);

                iv_toggleIcon.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v)
                    {
                        if(lv.getVisibility() == View.VISIBLE)
                        {
                            lv.setVisibility(View.GONE);
                            iv_toggleIcon.setImageResource(R.drawable.icon_sp);
                        }
                        else
                        {
                            lv.setVisibility(View.VISIBLE);
                            iv_toggleIcon.setImageResource(R.drawable.icon_sp_up);
                        }
                    }
                });

                final JSONArray purchaseItemDetailsArr = jObject.getJSONArray("purchaseItemDetailList");
                PurchaseItemDetailsListJSONArrayAdapter mJson = new PurchaseItemDetailsListJSONArrayAdapter(purchaseItemDetailsArr, context, lv);
                lv.setAdapter(mJson);//为ListView绑定适配器
                setListViewHeightBasedOnChildren(lv);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Intent intent = new Intent(context, UserInfoActivity.class);
                        try {
                            intent.putExtra("userID", ((JSONObject)purchaseItemDetailsArr.get(position)).getJSONObject("currentUser").getJSONObject("userWithLatLng").getInt("userID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        context.startActivity(intent);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ((LinearLayout)productOrgView).setOnClickListener(new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, MyProductInfoActivity.class);
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



