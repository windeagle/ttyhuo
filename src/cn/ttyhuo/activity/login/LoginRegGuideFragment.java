package cn.ttyhuo.activity.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.*;
import cn.ttyhuo.activity.product.NewProductActivity;
import cn.ttyhuo.activity.product.QuickNewProductActivity;
import cn.ttyhuo.activity.user.UserInfoActivity;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import cn.ttyhuo.view.RoutesJSONArrayAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-14
 * Time: 下午2:25
 * To change this template use File | Settings | File Templates.
 */
public class LoginRegGuideFragment extends LoginNeedBaseFragment {

    private Button viewMyselfBtn;
    private Button logoutBtn;
    private Button changePasswordBtn;
    private LinearLayout ly_exit;
    private LinearLayout ly_change_password;
    private LinearLayout ly_myself;
    private LinearLayout ly_editBasic;
    private LinearLayout ly_real_verify;
    private LinearLayout ly_truck;
    private LinearLayout ly_company;
    private LinearLayout ly_fensi;
    private LinearLayout ly_guanzhu;
    private LinearLayout ly_shoucang;
    private LinearLayout ly_huoyuan;
    private LinearLayout ly_zhaobiao;
    private LinearLayout ly_toubiao;
    private LinearLayout ly_link;
    private LinearLayout ly_fabu;
    private LinearLayout ly_yonghu;

    private LinearLayout ly_footer_home_btn;
    private LinearLayout ly_footer_cube_btn;
    private LinearLayout ly_footer_truck_btn;
    private LinearLayout ly_footer_my_btn;

    private ImageView iv_huoyuan;
    private ImageView iv_yonghu;
    private ImageView iv_fabu;
    private ImageView iv_quick_fabu;
    private ImageView iv_invite;

    JSONObject jsonObject;
    private boolean isDoingUpdate = true;

    @Override
    protected void setupViewOfHasLogin(JSONObject jObject)
    {
        try {
            isDoingUpdate = false;
            Context context = getActivity();
            jsonObject = jObject;
            initRoutes(jObject);
            //UserView oneView = new UserView(getView());
            //oneView.setupViews(jObject, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRoutes(JSONObject routes) {
        Activity activity = getActivity();
        ListView lv_routes = (ListView) activity.findViewById(R.id.lv_routes);
        try {
            JSONArray jsonArray = routes.getJSONArray("userRoutes");
            RoutesJSONArrayAdapter routeAdapter = new RoutesJSONArrayAdapter(jsonArray, activity);
            lv_routes.setAdapter(routeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

    @Override
    protected LoginNeedBaseFragment getInstance()
    {
        return new LoginRegGuideFragment();
    }

    @Override
    protected void setupViewFromLocalCache()
    {
        try {
            //从本地缓存中取用户信息
            JSONObject jObject = new JSONObject(GetLoginUser());
            Context context = getActivity();
            jsonObject = jObject;
            initRoutes(jObject);
            //UserView oneView = new UserView(getView());
            //oneView.setupViews(jObject, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected View createViewOfHasLogin(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUrl(UrlList.MAIN + "mvc/loginJson");
        return inflater.inflate(R.layout.fragment_my, null);
    }

    @Override
    protected ProgressBar setProgressBar()
    {
        return (ProgressBar) getView().findViewById(R.id.progressBar1);
    }

    @Override
    protected void innerOnActivityCreated()
    {
        viewMyselfBtn=(Button)getView().findViewById(R.id.viewMyselfBtn);
        viewMyselfBtn.setOnClickListener(viewMyselfClick);

        logoutBtn=(Button)getView().findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(logoutClick);

        changePasswordBtn=(Button)getView().findViewById(R.id.changePasswordBtn);
        changePasswordBtn.setOnClickListener(changePasswordClick);

        ly_exit = (LinearLayout)getView().findViewById(R.id.ly_exit);
        ly_exit.setOnClickListener(logoutClick);
        ly_change_password = (LinearLayout)getView().findViewById(R.id.ly_change_password);
        ly_change_password.setOnClickListener(changePasswordClick);
        ly_myself = (LinearLayout)getView().findViewById(R.id.ly_myself);
        ly_myself.setOnClickListener(viewMyselfClick);

        ly_editBasic = (LinearLayout)getView().findViewById(R.id.ly_editBasic);
        ly_editBasic.setOnClickListener(editClick);
        ly_real_verify = (LinearLayout)getView().findViewById(R.id.ly_real_verify);
        ly_real_verify.setOnClickListener(editClick);
        ly_truck = (LinearLayout)getView().findViewById(R.id.ly_truck);
        ly_truck.setOnClickListener(editClick);
        ly_company = (LinearLayout)getView().findViewById(R.id.ly_company);
        ly_company.setOnClickListener(editClick);
        ly_fabu = (LinearLayout)getView().findViewById(R.id.ly_fabu);
        ly_fabu.setOnClickListener(editClick);

        Activity context = getActivity();

        iv_huoyuan = (ImageView)getView().findViewById(R.id.iv_huoyuan);
        iv_huoyuan.getLayoutParams().width = (PxUtils.getDeviceWidth(context) - PxUtils.dip2px(context, 5) * 3)/2;
        iv_huoyuan.getLayoutParams().height = iv_huoyuan.getLayoutParams().width;
        iv_huoyuan.setOnClickListener(editClick);

        iv_yonghu = (ImageView)getView().findViewById(R.id.iv_yonghu);
        iv_yonghu.getLayoutParams().width = iv_huoyuan.getLayoutParams().width;
        iv_yonghu.getLayoutParams().height = iv_huoyuan.getLayoutParams().width;
        iv_yonghu.setOnClickListener(editClick);

        iv_fabu = (ImageView)getView().findViewById(R.id.iv_fabu);
        iv_fabu.getLayoutParams().width = (PxUtils.getDeviceWidth(context) - PxUtils.dip2px(context, 5) * 3)/2;
        iv_fabu.getLayoutParams().height = (int)(iv_fabu.getLayoutParams().width * 263.0f/532.0f);
        iv_fabu.setOnClickListener(editClick);

        iv_quick_fabu = (ImageView)getView().findViewById(R.id.iv_quick_fabu);
        iv_quick_fabu.getLayoutParams().width = (PxUtils.getDeviceWidth(context) - PxUtils.dip2px(context, 5) * 3)/2;
        iv_quick_fabu.getLayoutParams().height = (int)(iv_quick_fabu.getLayoutParams().width * 263.0f/532.0f);
        iv_quick_fabu.setOnClickListener(editClick);

        iv_invite = (ImageView)getView().findViewById(R.id.iv_invite);
        iv_invite.getLayoutParams().width = iv_quick_fabu.getLayoutParams().width;
        iv_invite.getLayoutParams().height = (int)(iv_quick_fabu.getLayoutParams().width * 263.0f/532.0f);
        iv_invite.setOnClickListener(editClick);

        ly_fensi = (LinearLayout)getView().findViewById(R.id.ly_fensi);
        ly_fensi.setOnClickListener(editClick);
        ly_guanzhu = (LinearLayout)getView().findViewById(R.id.ly_guanzhu);
        ly_guanzhu.setOnClickListener(editClick);

        ly_huoyuan = (LinearLayout)getView().findViewById(R.id.ly_huoyuan);
        ly_huoyuan.setOnClickListener(editClick);
        ly_shoucang = (LinearLayout)getView().findViewById(R.id.ly_shoucang);
        ly_shoucang.setOnClickListener(editClick);
        ly_zhaobiao = (LinearLayout)getView().findViewById(R.id.ly_zhaobiao);
        ly_zhaobiao.setOnClickListener(editClick);
        ly_toubiao = (LinearLayout)getView().findViewById(R.id.ly_toubiao);
        ly_toubiao.setOnClickListener(editClick);

        ly_link = (LinearLayout)getView().findViewById(R.id.ly_link);
        ly_link.setOnClickListener(editClick);
        ly_yonghu = (LinearLayout)getView().findViewById(R.id.ly_yonghu);
        ly_yonghu.setOnClickListener(editClick);

        ly_footer_home_btn = (LinearLayout)getView().findViewById(R.id.ly_footer_home_btn);
        ly_footer_home_btn.setOnClickListener(editClick);
        ly_footer_cube_btn = (LinearLayout)getView().findViewById(R.id.ly_footer_cube_btn);
        ly_footer_cube_btn.setOnClickListener(editClick);
        ly_footer_truck_btn = (LinearLayout)getView().findViewById(R.id.ly_footer_truck_btn);
        ly_footer_truck_btn.setOnClickListener(editClick);
        ly_footer_my_btn = (LinearLayout)getView().findViewById(R.id.ly_footer_my_btn);
        ly_footer_my_btn.setOnClickListener(editClick);

        MyApplication myApplication = ((MyApplication)getActivity().getApplication());
        myApplication.versionHandler = versionHandler;
    }

    Handler versionHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            String result = msg.obj.toString().trim();
            if(!StringUtils.isEmpty(result))
            {
                String[] versionStrArr = result.split("\\|");
                if(versionStrArr.length > 1)
                {
                    MyApplication myApplication = ((MyApplication)getActivity().getApplication());
                    if(myApplication.info.versionCode != Integer.parseInt(versionStrArr[0]))
                    {
                        DialogUtils.createNormalDialog(getActivity(), 0,
                                "发现新版本：" + versionStrArr[1], "是否更新",
                                "确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("http://ttyh-document.oss-cn-qingdao.aliyuncs.com/TTyhuo.apk");
                                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        getActivity().startActivity(it);
                                    }
                                }, "取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }
                        ).show();
                    }
                }
            }
        }
    };

    View.OnClickListener editClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = null;
            Context mContext = getActivity();
            switch (v.getId()) {
                case R.id.ly_editBasic:
                    intent = new Intent(mContext, InfoBasicActivity.class);
                    break;
                case R.id.ly_real_verify:
                    intent = new Intent(mContext, InfoRealActivity.class);
                    break;
                case R.id.ly_truck:
                    intent = new Intent(mContext, InfoOwnerActivity.class);
                    break;
                case R.id.ly_company:
                    intent = new Intent(mContext, InfoCompanyActivity.class);
                    break;
                case R.id.ly_guanzhu:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "MyFellowUserFragment");
                    intent.putExtra("windowTitle", "我的关注");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_fensi:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "UserFellowMeFragment");
                    intent.putExtra("windowTitle", "我的粉丝");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_shoucang:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "MyFavoriteProductFragment");
                    intent.putExtra("windowTitle", "收藏货源");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_huoyuan:
                case R.id.iv_huoyuan:
                    //case R.id.ly_footer_home_btn:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "ProductListViewFragment");
                    intent.putExtra("windowTitle", "货源搜索");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_toubiao:
                case R.id.ly_footer_cube_btn:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "PurchaseContainerFragment");
                    intent.putExtra("windowTitle", "我接的货");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_zhaobiao:
                case R.id.ly_footer_truck_btn:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "PurchaseToContainerFragment");
                    intent.putExtra("windowTitle", "我召的车");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_link:
                case R.id.iv_invite:
                    intent = new Intent(mContext,ContactActivity.class);
                    break;
                case R.id.ly_yonghu:
                case R.id.iv_yonghu:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "UserListViewFragment");
                    intent.putExtra("windowTitle", "用户列表");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_fabu:
                case R.id.iv_fabu:
                    intent = new Intent(mContext,NewProductActivity.class);
                    break;
                case R.id.iv_quick_fabu:
                    intent = new Intent(mContext,QuickNewProductActivity.class);
                    break;
                case R.id.ly_footer_my_btn:
                    toggleRightMenu();
                    break;
            }

            if (intent != null) {
                if(isDoingUpdate)
                {
                    Toast.makeText(mContext, "正在加载数据！", Toast.LENGTH_SHORT).show();
                    return;
                }
                mContext.startActivity(intent);
                //getActivity().finish();
            }
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);

                ProgressBar progressBar = setProgressBar();
                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);

                String viewName = jObject.getString("viewName");
                //服务器未登录
                if("user/login".equals(viewName))
                {
                    //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                    SetLoginFlag(false);
                    switchFragment(getInstance());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    View.OnClickListener viewMyselfClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context mContext = getActivity();
            if(isDoingUpdate)
            {
                Toast.makeText(mContext, "正在加载数据！", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            try{
                intent.putExtra("userID", jsonObject.getJSONObject("loggedUser").getInt("userID"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mContext.startActivity(intent);
            //getActivity().finish();
        }
    };

    View.OnClickListener logoutClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context mContext = getActivity();
            ProgressBar progressBar = setProgressBar();
            if(progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            if(!NetworkUtils.isNetworkAvailable(mContext))
            {
                Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
            }
            new UrlThread(handler, UrlList.MAIN + "mvc/logoutJson", 1).start();
        }
    };

    View.OnClickListener changePasswordClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new ChangePasswordTask().execute(v.getContext());
        }
    };

    @SuppressWarnings("unused")
    private class  ChangePasswordTask extends AsyncTask<Context, Integer, String> {
        Context context;
        @Override
        protected void onPreExecute() {
            changePasswordBtn.setEnabled(false);
            ProgressBar progressBar = setProgressBar();
            if(progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            context = params[0];
            try {
                return changePassword();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //这个函数在doInBackground调用publishProgress时触发
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            ProgressBar progressBar = setProgressBar();
            if(progressBar != null)
                progressBar.setVisibility(View.GONE);
            changePasswordBtn.setEnabled(true);
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    if("user/changePassword".equals(viewName))
                    {
                        switchFragment(new ChangePasswordFragment(jObject));
                    }
                    else if("not_login".equals(viewName))
                    {
                        SetLoginFlag(false);
                        switchFragment(getInstance());
                    }
                    else if("err".equals(viewName))
                    {
                        String errMsg = jObject.getString("errMsg");
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(context, "系统异常！", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

        private String changePassword() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/changePasswordJson");
            Map<String, String> params = new HashMap<String, String>();
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(url, params, null);
            if(conn.getResponseCode()==200){
                MyApplication.setUpPersistentCookieStore();
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }
            MyApplication.setUpPersistentCookieStore();

            return null;
        }
    }
}
