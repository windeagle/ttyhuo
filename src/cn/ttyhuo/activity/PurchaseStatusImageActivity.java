package cn.ttyhuo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseAddPicActivity;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PurchaseStatusImageActivity extends BaseAddPicActivity {

    private int purchaseDetailID;

    @Override
    protected String getPicUploadUrl()
    {
        return UrlList.MAIN + "mvc/uploadPurchaseStatusImgFromUPYun";
    }

    @Override
    protected String getPicDeleteUrl()
    {
        return UrlList.MAIN + "mvc/deletePurchaseStatusImg";
    }

    protected Map<String, String> getParams()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("purchaseDetailID", String.valueOf(purchaseDetailID));
        return params;
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        purchaseDetailID = getIntent().getIntExtra("purchaseDetailID", 0);
		setContentView(R.layout.activity_purchase_status_image);

		getActionBar().setTitle("货物状态照片");
		initView();
	}

    private ProgressBar progressBar;

	@Override
	protected void initView() {
		super.initView();

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if(progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        if(!NetworkUtils.isNetworkAvailable(mContext))
        {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
        }
        new UrlThread(handler, UrlList.MAIN + "mvc/getPurchaseStatusImg?purchaseDetailID=" + purchaseDetailID, 1).start();
	}

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);

                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);

                String viewName = jObject.getString("viewName");
                //服务器未登录
                if("not_login".equals(viewName))
                {
                    //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                    SetLoginFlag(PurchaseStatusImageActivity.this, false);
                    Intent intent = new Intent(mContext, MainPage.class);
                    Toast.makeText(PurchaseStatusImageActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                    PurchaseStatusImageActivity.this.finish();
                }
                else if("err".equals(viewName))
                {
                    String errMsg = jObject.getString("errMsg");
                    Toast.makeText(PurchaseStatusImageActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
                else {
                    //服务器登录了
                    SetLoginFlag(PurchaseStatusImageActivity.this,true);
                    //设置本地缓存的用户信息
                    SetLoginUser(PurchaseStatusImageActivity.this, result);
                    setupViewOfHasLogin(jObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void setupViewOfHasLogin(JSONObject jObject)
    {
        try {
            ArrayList<String> pics = new ArrayList<String>();
            JSONArray jsonArray = jObject.optJSONArray("imageList");
            if(jsonArray != null)
            {
                for(int s = 0; s < jsonArray.length(); s++)
                    pics.add(jsonArray.getString(s));
            }
            pics.add("plus");
            mPicData = pics;
            mPicAdapter.updateData(pics);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
