package cn.ttyhuo.activity.product;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseWithPicActivity;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import cn.ttyhuo.view.ProductView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-4
 * Time: 上午1:06
 * To change this template use File | Settings | File Templates.
 */
public class ProductInfoActivity extends BaseWithPicActivity {
    private RelativeLayout progressBar;
    ProductView oneView;
    LinearLayout root;
    protected boolean isMy = false;

    protected int getLayoutResID()
    {
        return R.layout.activity_product_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayoutResID());
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        root = (LinearLayout) findViewById(R.id.root);
        oneView = new ProductView(root, isMy);

        progressBar = (RelativeLayout) findViewById(R.id.progressBar1);
        if(progressBar != null)
        {
            root.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        int productID = getIntent().getIntExtra("productID", 0);
        if(!NetworkUtils.isNetworkAvailable(mContext))
        {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
        }
        new UrlThread(handler, UrlList.MAIN + "mvc/product_viewByUserJson_" + productID, 1).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);

                if(progressBar != null)
                {
                    root.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                String viewName = jObject.getString("viewName");
                //服务器未登录
                if("err".equals(viewName))
                {
                }
                else
                {
                    setupView(jObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    protected void setupView(JSONObject jsonObject)
    {
        try {
            ArrayList<String> pics = new ArrayList<String>();
            JSONArray jsonArray = jsonObject.getJSONArray("imageList");
            for(int s = 0; s < jsonArray.length(); s++)
                pics.add(jsonArray.getString(s));
            mPicData = pics;
            mPicAdapter.updateData(pics);

            if(pics.isEmpty())
                mPicGrid.setVisibility(View.GONE);
            else
                mPicGrid.setVisibility(View.VISIBLE);

            oneView.setupViews(jsonObject, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
