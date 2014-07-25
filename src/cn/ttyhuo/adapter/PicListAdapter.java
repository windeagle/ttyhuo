package cn.ttyhuo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import cn.ttyhuo.R;
import cn.ttyhuo.utils.PhotoUtils;
import cn.ttyhuo.utils.PxUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PicListAdapter extends BaseAdapter {

	private final Context mContext;
	private final LayoutInflater mInflater;
	private List<String> mDataList;
	private final DisplayImageOptions options;
	private final Map<String, Bitmap> mBitmapMap;
	private final int mType;

	public PicListAdapter(Context context, List<String> dataList, int type) {
		mContext = context;
		mDataList = dataList;
		mInflater = LayoutInflater.from(context);
		mBitmapMap = new HashMap<String, Bitmap>();

		options = new DisplayImageOptions.Builder()
				.resetViewBeforeLoading(true).cacheInMemory(true)
				.cacheOnDisc(true).build();

		mType = type;
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
        if(position < 0 || position >= getCount())
            return null;
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			if (mType == 0) {
				convertView = mInflater.inflate(R.layout.grid_item_pic, parent,
						false);
			} else {
				convertView = mInflater.inflate(R.layout.grid_item_pic2,
						parent, false);
			}

			holder = new ViewHolder();

			holder.img = (ImageView) convertView.findViewById(R.id.img);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String uri = mDataList.get(position);
		if (uri.equals("plus")) {
			holder.img.setImageDrawable(null);
			holder.img
					.setBackgroundResource(R.drawable.group_detail_plus_normal);

		} else if (uri.startsWith("http")) {
			ImageLoader.getInstance().displayImage(uri, holder.img, options);
		} else {

			Bitmap bitmap = mBitmapMap.get(uri);
			if (bitmap == null) {
				bitmap = PhotoUtils.decodeBitmap(uri,
						PxUtils.dip2px(mContext, 200),
						PxUtils.dip2px(mContext, 200));
				mBitmapMap.put(uri, bitmap);
			}

			holder.img.setImageBitmap(bitmap);
		}

		return convertView;
	}

	public class ViewHolder {
		ImageView img;
	}

}
