package cn.ttyhuo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ttyhuo.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class RoutesJSONArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射
    private JSONArray list;
    private Context context;

    public RoutesJSONArrayAdapter(JSONArray list, Context context) {
        this.list = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if(list == null)
            return 0;
        return list.length();
    }

    @Override
    public Object getItem(int position) {
        if(list == null)
            return null;
        try {
            return list.getJSONObject(position);
        } catch (Exception e) {
            // TODO: handle exception
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_route, null);// 根据布局文件实例化view
        }
        try {
            JSONObject r = list.getJSONObject(position);// 根据position获取集合类中某行数据
            TextView tv_fromCity = (TextView)convertView.findViewById(R.id.fromCity);
            TextView tv_toCity = (TextView)convertView.findViewById(R.id.toCity);

            String fromProvince = r.getString("fromProvince");
            String fromCity = r.getString("fromCity");
            String toProvince = r.getString("toProvince");
            String toCity = r.getString("toCity");

            if(fromProvince.equals(fromCity))
                tv_fromCity.setText(fromCity);
            else
                tv_fromCity.setText(fromProvince + " " + fromCity);

            if(toProvince.equals(toCity))
                tv_toCity.setText(toCity);
            else
                tv_toCity.setText(toProvince + " " + toCity);

        } catch (Exception e) {
            // TODO: handle exception
        }
        return convertView;
    }
}



