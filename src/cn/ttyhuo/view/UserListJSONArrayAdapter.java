package cn.ttyhuo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.ttyhuo.R;
import org.json.JSONArray;
import org.json.JSONObject;

public class UserListJSONArrayAdapter extends BaseAdapter {
    private LayoutInflater mInflater;// 动态布局映射
    private JSONArray list;
    private Context context;

    public void setList(JSONArray list) {
        this.list = list;
    }

    public UserListJSONArrayAdapter(JSONArray list, Context context) {
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
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.user_list_item, null);// 根据布局文件实例化view
            oneView = new UserView(convertView);
            convertView.setTag(oneView);// 把View和某个对象关联起来
        } else {
            oneView = (UserView) convertView.getTag();
        }
        try {
            JSONObject jObject = list.getJSONObject(position);// 根据position获取集合类中某行数据
            oneView.setupViews(jObject, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }
}



