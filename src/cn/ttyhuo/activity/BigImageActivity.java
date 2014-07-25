package cn.ttyhuo.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import cn.ttyhuo.R;
import cn.ttyhuo.utils.PhotoUtils;
import cn.ttyhuo.utils.PxUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class BigImageActivity extends Activity {

	private Context mContext;
	private Bitmap mBitmap;
    private DisplayImageOptions options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_big_image);

		mContext = this;
		String url = getIntent().getStringExtra("url");

        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true).cacheInMemory(true)
                .cacheOnDisc(true).build();

        ImageView bigImg = (ImageView) findViewById(R.id.big_img);
        if (url.startsWith("http")) {
            ImageLoader.getInstance().displayImage(url,bigImg, options);
        } else {
            mBitmap = PhotoUtils.decodeBitmap(url, PxUtils.dip2px(mContext, 1000),
                    PxUtils.dip2px(mContext, 1000));
            bigImg.setImageBitmap(mBitmap);
        }
	}

//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		mBitmap.recycle();
//		mBitmap = null;
//	}
}
