package cn.ttyhuo.activity.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.MainPage;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-14
 * Time: 下午5:35
 * To change this template use File | Settings | File Templates.
 */
public class LoginNeedBaseFragment extends Fragment {

    private String url;
    private ProgressBar progressBar;
    private Button loginBtn;
    private EditText userAccountNo;
    private boolean isLogin;
    public static final String LOGIN = "LOGIN";
    public static final String LOGIN_FLAG = "LOGIN_FLAG";
    public static final String LOGIN_USER = "LOGIN_USER";

    protected void setUrl(String urlStr)
    {
        url = urlStr;
    }

    protected LoginNeedBaseFragment getInstance()
    {
        return new LoginRegGuideFragment();
    }

    protected boolean GetLoginFlag()
    {
        //从本地存储中读取
        SharedPreferences settings = getActivity().getSharedPreferences(LOGIN, 0);
        boolean loginFlag = settings.getBoolean(LOGIN_FLAG, false);
        return loginFlag;
    }

    protected void SetLoginFlag(boolean loginFlag)
    {
        //设置本地存储
        SharedPreferences settings = getActivity().getSharedPreferences(LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putBoolean(LOGIN_FLAG, loginFlag).commit();
    }

    protected String GetLoginUser()
    {
        //从本地存储中读取
        SharedPreferences settings = getActivity().getSharedPreferences(LOGIN, 0);
        String loginUser = settings.getString(LOGIN_USER, "");
        return loginUser;
    }

    protected void SetLoginUser(String loginUser)
    {
        //设置本地存储
        SharedPreferences settings = getActivity().getSharedPreferences(LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putString(LOGIN_USER, loginUser).commit();
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
                if("user/login".equals(viewName))
                {
                    //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                    SetLoginFlag(false);
                    switchFragment(getInstance());
                }
                else
                {
                    //服务器登录了
                    SetLoginFlag(true);
                    //设置本地缓存的用户信息
                    SetLoginUser(result);
                    setupViewOfHasLogin(jObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    protected void setupViewOfHasLogin(JSONObject jObject)
    {
        try {
            Context context = getActivity();
            //UserView oneView = new UserView(getView());
            //oneView.setupViews(jObject, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setupViewFromLocalCache()
    {
        try {
            //从本地缓存中取用户信息
            JSONObject jObject = new JSONObject(GetLoginUser());
            Context context = getActivity();
            //UserView oneView = new UserView(getView());
            //oneView.setupViews(jObject, context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected View createViewOfHasLogin(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUrl(UrlList.MAIN + "mvc/loginJson");
        return inflater.inflate(R.layout.fragment_my, null);
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
            setupViewFromLocalCache();

            innerOnActivityCreated();

            //然后自动地异步去服务器看看是否真的登录
            progressBar = setProgressBar();
            if(progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            new UrlThread(handler, url.toString(), 1).start();
        }
        else
        {
            progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
            loginBtn=(Button)getView().findViewById(R.id.loginBtn);
            userAccountNo=(EditText)getView().findViewById(R.id.accountNo);
            loginBtn.setOnClickListener(loginClick);
        }
    }

    protected ProgressBar setProgressBar()
    {
        return (ProgressBar) getView().findViewById(R.id.progressBar1);
    }

    protected void innerOnActivityCreated()
    {
    }

    // 切换Fragment视图内ring
    protected void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainPage) {
            MainPage fca = (MainPage) getActivity();
            fca.switchContent(fragment);
        }
    }

    // 切换Fragment视图内ring
    protected void toggleRightMenu() {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainPage) {
            MainPage fca = (MainPage) getActivity();
            fca.toggleRightMenu();
        }
    }

    View.OnClickListener loginClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MyTask().execute(v.getContext());
        }
    };

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        String accountNo;
        @Override
        protected void onPreExecute() {
            loginBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            accountNo = userAccountNo.getText().toString();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            context = params[0];
            try {
                return login();
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
            MyApplication.setUpPersistentCookieStore();
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    //服务器未登录
                    if("user/login".equals(viewName))
                    {
                        Toast.makeText(context, "手机号格式不正确！", Toast.LENGTH_SHORT).show();
                    }
                    else if("user/home".equals(viewName))
                    {
                        //服务器登录了
                        SetLoginFlag(true);
                        //设置本地缓存的用户信息
                        SetLoginUser(result);
                        //重新加载此Fragment
                        switchFragment(getInstance());
                    }
                    else if("user/register".equals(viewName))
                    {
                        switchFragment(new RegFragment(jObject));
                    }
                    else if("user/login/confirm".equals(viewName))
                    {
                        switchFragment(new LoginConfirmFragment(jObject));
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

        private String login() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/loginJson");
            Map<String, String> params = new HashMap<String, String>();
            params.put("accountNo", accountNo);
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
