package cn.ttyhuo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.login.ChangePasswordFragment;
import cn.ttyhuo.activity.login.LoginNeedBaseFragment;
import cn.ttyhuo.activity.user.UserInfoActivity;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.StringUtils;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yangyu
 *         功能描述：列表Fragment，用来显示滑动菜单打开后的内容
 */
public class LeftSlidingMenuFragment extends Fragment {

    private LinearLayout ly_exit;
    private LinearLayout ly_help;
    private LinearLayout ly_change_password;

    private LinearLayout ly_myself;
    private LinearLayout ly_real_verify;
    private LinearLayout ly_truck;
    private LinearLayout ly_company;
    private LinearLayout ly_shuoshuo;

    private LinearLayout ly_fensi;
    private LinearLayout ly_guanzhu;
    private LinearLayout ly_shoucang;
    private LinearLayout ly_my_huoyuan;
    private LinearLayout ly_link;

    private boolean isLogin;

    protected boolean GetLoginFlag()
    {
        return true;
//        //从本地存储中读取
//        SharedPreferences settings = getActivity().getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0);
//        boolean loginFlag = settings.getBoolean(LoginNeedBaseFragment.LOGIN_FLAG, false);
//        return loginFlag;
    }

    protected String GetLoginUser()
    {
        //从本地存储中读取
        SharedPreferences settings = getActivity().getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0);
        String loginUser = settings.getString(LoginNeedBaseFragment.LOGIN_USER, "");
        return loginUser;
    }

    protected View createViewOfHasLogin(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_left_sliding_menu, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isLogin = GetLoginFlag();

        if(isLogin)
        {
            //本地已登录,具体显示哪个VIEW由子类确定
            return createViewOfHasLogin(inflater, container, savedInstanceState);
        }

        return inflater.inflate(R.layout.fragment_login_reg_guide, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isLogin)
        {
            //本地已登录，先显示本地信息
//            setupViewFromLocalCache();

            innerOnActivityCreated();
//
//            //然后自动地异步去服务器看看是否真的登录
//            progressBar = setProgressBar();
//            if(progressBar != null)
//                progressBar.setVisibility(View.VISIBLE);
//            new UrlThread(handler, url.toString(), 1).start();
        }
        else
        {
//            progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
//            loginBtn=(Button)getView().findViewById(R.id.loginBtn);
//            userAccountNo=(EditText)getView().findViewById(R.id.accountNo);
//            loginBtn.setOnClickListener(loginClick);
        }
    }

    // 切换Fragment视图内ring
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainPage) {
            MainPage fca = (MainPage) getActivity();
            fca.switchContent(fragment);
        }
    }

    protected void innerOnActivityCreated()
    {
        ly_exit = (LinearLayout)getView().findViewById(R.id.ly_exit);
        ly_exit.setOnClickListener(logoutClick);
        ly_change_password = (LinearLayout)getView().findViewById(R.id.ly_change_password);
        ly_change_password.setOnClickListener(changePasswordClick);
        ly_myself = (LinearLayout)getView().findViewById(R.id.ly_myself);
        ly_myself.setOnClickListener(viewMyselfClick);

        ly_real_verify = (LinearLayout)getView().findViewById(R.id.ly_real_verify);
        ly_real_verify.setOnClickListener(editClick);
        ly_truck = (LinearLayout)getView().findViewById(R.id.ly_truck);
        ly_truck.setOnClickListener(editClick);
        ly_company = (LinearLayout)getView().findViewById(R.id.ly_company);
        ly_company.setOnClickListener(editClick);
        ly_shuoshuo = (LinearLayout)getView().findViewById(R.id.ly_shuoshuo);
        ly_shuoshuo.setOnClickListener(editClick);

        ly_fensi = (LinearLayout)getView().findViewById(R.id.ly_fensi);
        ly_fensi.setOnClickListener(editClick);
        ly_guanzhu = (LinearLayout)getView().findViewById(R.id.ly_guanzhu);
        ly_guanzhu.setOnClickListener(editClick);

        ly_my_huoyuan = (LinearLayout)getView().findViewById(R.id.ly_huoyuan);
        ly_my_huoyuan.setOnClickListener(editClick);
        ly_shoucang = (LinearLayout)getView().findViewById(R.id.ly_shoucang);
        ly_shoucang.setOnClickListener(editClick);

        ly_link = (LinearLayout)getView().findViewById(R.id.ly_link);
        ly_link.setOnClickListener(editClick);
    }

    View.OnClickListener editClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Intent intent = null;
            Context mContext = getActivity();
            switch (v.getId()) {
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
                case R.id.ly_huoyuan:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "MyProductFragment");
                    intent.putExtra("windowTitle", "我的货源");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_shoucang:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "MyFavoriteProductFragment");
                    intent.putExtra("windowTitle", "收藏货源");
                    intent.putExtra("hasWindowTitle", true);
                    break;
                case R.id.ly_link:
                    intent = new Intent(mContext,ContactActivity.class);
                    break;
                case R.id.ly_shuoshuo:
                    intent = new Intent(mContext, MainPage.class);
                    intent.putExtra("contentFragment", "DriverShuoShuoFragment");
                    intent.putExtra("windowTitle", "车源说说");
                    intent.putExtra("hasWindowTitle", true);
                    break;
            }

            if (intent != null) {
                mContext.startActivity(intent);
                //getActivity().finish();
            }
        }
    };

    View.OnClickListener viewMyselfClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String loggedUserStr = GetLoginUser();
            if(StringUtils.isEmpty(loggedUserStr))
                return;

            Context mContext = getActivity();
            Intent intent = new Intent(mContext, UserInfoActivity.class);
            try{
                JSONObject jObject = new JSONObject(loggedUserStr);
                intent.putExtra("userID", jObject.getJSONObject("loggedUser").getInt("userID"));
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
            final Context context = getActivity();
            final ProgressDialog progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), "操作进行中", true,
                    true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
            new UrlThread(new Handler() {
                public void handleMessage(android.os.Message msg) {
                    Toast.makeText(context, "退出成功", Toast.LENGTH_SHORT);
                    progressDialog.dismiss();
                    getActivity().finish();
                }}, UrlList.MAIN + "mvc/logoutJson", 1).start();
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
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context = getActivity();
            progressDialog = ProgressDialog.show(context,
                    context.getString(R.string.app_name), "操作进行中", true,
                    true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
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
            progressDialog.dismiss();
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    if("user/changePassword".equals(viewName))
                    {
                        switchFragment(new ChangePasswordFragment(jObject));
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
