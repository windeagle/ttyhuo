package cn.ttyhuo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.ttyhuo.R;

import java.util.List;

public class CityListAdapter extends BaseAdapter {

	private final Context mContext;
	private final LayoutInflater mInflater;
	private List<String> mDataList;

	public CityListAdapter(Context context, List<String> dataList) {
		mContext = context;
		mDataList = dataList;
		mInflater = LayoutInflater.from(context);
	}

	public void updateData(List<String> data) {
		mDataList = data;
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.list_item_city, parent, false);
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(mDataList.get(position));

		return convertView;
	}

}
