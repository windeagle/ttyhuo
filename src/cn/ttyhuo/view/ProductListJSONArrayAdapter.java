package cn.ttyhuo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.ttyhuo.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProductListJSONArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射
    private JSONArray list;
    private Context context;
    private boolean isMy;

    public void setList(JSONArray list) {
        this.list = list;
    }

    public ProductListJSONArrayAdapter(JSONArray list, Context context, boolean isMy) {
        this(list, context);
        this.isMy = isMy;
    }

    public ProductListJSONArrayAdapter(JSONArray list, Context context) {
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
        ProductView oneView;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.product_list_item, null);// 根据布局文件实例化view
            oneView = new ProductView(convertView, isMy);
            convertView.setTag(oneView);// 把View和某个对象关联起来
        } else {
            oneView = (ProductView) convertView.getTag();
        }
        try {
            JSONObject jObject = list.getJSONObject(position);// 根据position获取集合类中某行数据
            oneView.setupViews(jObject, context);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return convertView;
    }
}