package cn.ttyhuo.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.InfoBasicActivity;
import cn.ttyhuo.activity.base.BaseWithPicActivity;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import cn.ttyhuo.view.RoutesJSONArrayAdapter;
import cn.ttyhuo.view.UserView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserInfoActivity extends BaseWithPicActivity {

    ImageView iv_userEdit;
    LinearLayout ly_call_footer;
    private RelativeLayout progressBar;
    UserView oneView;
    LinearLayout root;
    ListView lv_routes;
    boolean isMyself = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);
        initView();
    }

    @Override
    protected void initView() {
        super.initView();

        root = (LinearLayout) findViewById(R.id.root);
        iv_userEdit = (ImageView) findViewById(R.id.iv_userEdit);
        oneView = new UserView(root);
        ly_call_footer = (LinearLayout) findViewById(R.id.ly_call_footer);
        lv_routes = (ListView) findViewById(R.id.lv_routes);
        TextView tv_hasProduct = (TextView) findViewById(R.id.tv_hasProduct);

        progressBar = (RelativeLayout) findViewById(R.id.progressBar1);
        if(progressBar != null)
        {
            root.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

        int userID = getIntent().getIntExtra("userID", 0);

        int loggedUserID = 0;
        try {
            boolean isLogin = GetLoginFlag();
            if(isLogin)
            {
                //从本地缓存中取用户信息
                JSONObject jObject = new JSONObject(GetLoginUser());
                loggedUserID =  jObject.getJSONObject("loggedUser").getInt("userID");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(userID == loggedUserID)
        {
            isMyself = true;
            iv_userEdit.setOnClickListener(this);
            tv_hasProduct.setText("我的货源");
            ly_call_footer.setVisibility(View.GONE);
        }
        else
        {
            isMyself = false;
            iv_userEdit.setVisibility(View.GONE);
            ly_call_footer.setVisibility(View.VISIBLE);
        }

        if(!NetworkUtils.isNetworkAvailable(mContext))
        {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
        }
        new UrlThread(handler, UrlList.MAIN + "mvc/viewUserJson_" + userID, 1).start();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent intent = null;
        switch (v.getId()) {
            case R.id.iv_userEdit:
                intent = new Intent(mContext, InfoBasicActivity.class);
                break;
        }

        if (intent != null) {
            mContext.startActivity(intent);
            finish();
        }
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

                    if(isMyself)
                    {
                        TextView tv_hasProduct = (TextView) findViewById(R.id.tv_hasProduct);
                        tv_hasProduct.setVisibility(View.GONE);
                        TextView tv_favoriteUserCount = (TextView) findViewById(R.id.tv_favoriteUserCount);
                        tv_favoriteUserCount.setVisibility(View.GONE);
                        TextView tv_thumbUpCount = (TextView) findViewById(R.id.tv_thumbUpCount);
                        tv_thumbUpCount.setVisibility(View.GONE);
                    }
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
            JSONArray jsonArray = jsonObject.optJSONArray("imageList");
            if(jsonArray != null)
            {
                for(int s = 0; s < jsonArray.length(); s++)
                    pics.add(jsonArray.getString(s));
            }
            mPicData = pics;
            mPicAdapter.updateData(pics);

            if(pics.isEmpty())
                mPicGrid.setVisibility(View.GONE);
            else
                mPicGrid.setVisibility(View.VISIBLE);

            initRoutes(jsonObject);

            oneView.setupViews(jsonObject, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRoutes(JSONObject routes) {
        if(routes.has("userRoutes") && lv_routes != null)
        {
            try {
                JSONArray jsonArray = routes.getJSONArray("userRoutes");
                RoutesJSONArrayAdapter routeAdapter = new RoutesJSONArrayAdapter(jsonArray, this);
                lv_routes.setAdapter(routeAdapter);
            } catch (JSONException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        setListViewHeightBasedOnChildren(lv_routes);
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