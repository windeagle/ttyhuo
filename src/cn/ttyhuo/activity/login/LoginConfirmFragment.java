package cn.ttyhuo.activity.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.MainPage;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.HttpRequestUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-14
 * Time: 下午7:31
 * To change this template use File | Settings | File Templates.
 */
public class LoginConfirmFragment extends Fragment {

    private ProgressBar progressBar;
    private Button loginBtn;
    private Button forgetBtn;
    private TextView userName;
    private TextView mobileNo;
    private EditText password;
    private JSONObject jsonObject;

    public LoginConfirmFragment(JSONObject jObject){
        jsonObject = jObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login_confirm, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
        loginBtn=(Button)getView().findViewById(R.id.loginBtn);
        forgetBtn=(Button)getView().findViewById(R.id.forgetBtn);
        password=(EditText)getView().findViewById(R.id.password);
        mobileNo=(TextView)getView().findViewById(R.id.tv_mobileNo);
        userName=(TextView)getView().findViewById(R.id.tv_userName);
        try {
            mobileNo.setText(jsonObject.getJSONObject("loggingUser").getString("accountNo"));
            userName.setText(jsonObject.getJSONObject("loggingUser").getString("userName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loginBtn.setOnClickListener(loginClick);
        forgetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ForgetTask().execute(v.getContext());
            }
        });
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

    View.OnClickListener loginClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new MyTask().execute(v.getContext());
        }
    };

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        String _password;
        @Override
        protected void onPreExecute() {
            loginBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            _password = password.getText().toString();
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
                    if("user/home".equals(viewName))
                    {
                        //服务器登录了
                        SetLoginFlag(true);
                        //设置本地缓存的用户信息
                        SetLoginUser(result);
                        //重新加载此Fragment
                        switchFragment(new LoginRegGuideFragment());
                    }
                    else if("user/login/confirm".equals(viewName))
                    {
                        Toast.makeText(context, "密码不正确！", Toast.LENGTH_SHORT).show();
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
            String url = new String(UrlList.MAIN + "mvc/login_confirmJson");
            Map<String, String> params = new HashMap<String, String>();
            params.put("password", _password);
            params.put("loggingUserSnapShotKey", jsonObject.getString("loggingUserSnapShotKey"));
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }

    @SuppressWarnings("unused")
    private class ForgetTask extends AsyncTask<Context, Integer, String> {
        Context context;
        @Override
        protected void onPreExecute() {
            forgetBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            context = params[0];
            try {
                return forgetPassword();
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
            forgetBtn.setEnabled(true);
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    if("user/forgetPassword".equals(viewName))
                    {
                        switchFragment(new ForgetPasswordFragment(jObject));
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

        private String forgetPassword() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/forgetPasswordJson");
            Map<String, String> params = new HashMap<String, String>();
            params.put("loggingUserSnapShotKey", jsonObject.getString("loggingUserSnapShotKey"));
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }

    protected void SetLoginFlag(boolean loginFlag)
    {
        //设置本地存储
        SharedPreferences settings = getActivity().getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putBoolean(LoginNeedBaseFragment.LOGIN_FLAG, loginFlag).commit();
    }

    protected void SetLoginUser(String loginUser)
    {
        //设置本地存储
        SharedPreferences settings = getActivity().getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putString(LoginNeedBaseFragment.LOGIN_USER, loginUser).commit();
    }
}