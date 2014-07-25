package cn.ttyhuo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.utils.LogUtils;
import cn.ttyhuo.utils.PhotoUtils;
import cn.ttyhuo.utils.PxUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ShowPicActivity extends Activity {

	Context mContext;
	LayoutInflater mInflater;
	ViewPager mViewPager;
	List<String> mData;
	List<View> mViewList;
	PagerAdapter pagerAdapter;
	int firstPosition;
	TextView indicator;
    private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_pic);

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisc(true).build();

		mContext = this;
		mInflater = LayoutInflater.from(mContext);
		mData = getIntent().getStringArrayListExtra("pics");
		firstPosition = getIntent().getIntExtra("position", 0);

		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		indicator = (TextView) findViewById(R.id.indicator);

		initViewPager();
	}

	private void initViewPager() {
		if (mData != null && mData.size() > 0) {
			mViewList = new ArrayList<View>();
			for (String s : mData) {
				if (!TextUtils.isEmpty(s)) {
					LogUtils.d(s);
					View v = mInflater.inflate(R.layout.list_item_show_pic,
							null);
					ImageView img = (ImageView) v.findViewById(R.id.img);

                    if (s.startsWith("http")) {
                        ImageLoader.getInstance().displayImage(s,img, options);
                    } else {
                        Bitmap mBitmap = PhotoUtils.decodeBitmap(s,
                                PxUtils.dip2px(mContext, 1000),
                                PxUtils.dip2px(mContext, 1000));

                        img.setImageBitmap(mBitmap);
                    }

					mViewList.add(v);
				}
			}

			pagerAdapter = new PagerAdapter() {

				@Override
				public boolean isViewFromObject(View arg0, Object arg1) {
					return arg0 == arg1;
				}

				@Override
				public int getCount() {
					return mViewList == null ? 0 : mViewList.size();
				}

				@Override
				public void destroyItem(ViewGroup container, int position,
						Object object) {
					container.removeView(mViewList.get(position));

				}

				@Override
				public int getItemPosition(Object object) {
					return super.getItemPosition(object);
				}

				@Override
				public CharSequence getPageTitle(int position) {
					return null;
				}

				@Override
				public Object instantiateItem(ViewGroup container, int position) {
					container.addView(mViewList.get(position));
					return mViewList.get(position);
				}

			};
			mViewPager.setAdapter(pagerAdapter);
			mViewPager.setCurrentItem(firstPosition);
			indicator.setText((firstPosition + 1) + "/" + mData.size());

			mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					// TODO Auto-generated method stub
					indicator.setText((arg0 + 1) + "/" + mData.size());
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}
			});
		}
	}

}
