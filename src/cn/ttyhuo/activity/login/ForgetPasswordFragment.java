package cn.ttyhuo.activity.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
public class ForgetPasswordFragment extends Fragment {

    private int seconds = 60;

    private ProgressBar progressBar;
    private Button loginBtn;
    private Button sendCodeBtn;
    private TextView userName;
    private TextView mobileNo;
    private EditText password;
    private EditText password_confirm;
    private EditText confirmCode;
    private JSONObject jsonObject;

    public ForgetPasswordFragment(JSONObject jObject){
        jsonObject = jObject;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_password, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
        loginBtn=(Button)getView().findViewById(R.id.loginBtn);
        sendCodeBtn =(Button)getView().findViewById(R.id.sendConfirmCode);
        mobileNo=(TextView)getView().findViewById(R.id.tv_mobileNo);
        userName=(TextView)getView().findViewById(R.id.tv_userName);
        password=(EditText)getView().findViewById(R.id.password);
        password_confirm=(EditText)getView().findViewById(R.id.password_confirm);
        confirmCode=(EditText)getView().findViewById(R.id.confirmCode);
        try {
            mobileNo.setText(jsonObject.getJSONObject("loggingUser").getString("accountNo"));
            userName.setText(jsonObject.getJSONObject("loggingUser").getString("userName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        seconds = 60;
        time();
        loginBtn.setOnClickListener(loginClick);
        sendCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendCodeTask().execute(v.getContext());
            }
        });
    }

    private void time()
    {
        Handler handler =  new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(seconds < 1)
                {
                    sendCodeBtn.setText("重发");
                    sendCodeBtn.setEnabled(true);
                    sendCodeBtn.setBackgroundColor(Color.rgb(59,180,60));
                    return;
                }
                sendCodeBtn.setText("重发(" + seconds + ")");
                seconds --;
                time();
            }
        }, 1000);
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
        String _password_confirm;
        String _confirmCode;
        @Override
        protected void onPreExecute() {
            loginBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            _password = password.getText().toString();
            _password_confirm = password_confirm.getText().toString();
            _confirmCode = confirmCode.getText().toString();
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
                    else if("success".equals(viewName))
                    {
                        switchFragment(new LoginRegGuideFragment());
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
            String url = new String(UrlList.MAIN + "mvc/forgetPasswordSubmitJson");
            Map<String, String> params = new HashMap<String, String>();
            params.put("password", _password);
            params.put("confirmCode", _confirmCode);
            params.put("comparePassword", _password_confirm);
            params.put("forgetPasswordKey", jsonObject.getString("forgetPasswordKey"));
            params.put("forgetPassword_loggingUserSnapShotKey", jsonObject.getString("loggingUserSnapShotKey"));
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }


    @SuppressWarnings("unused")
    private class SendCodeTask extends AsyncTask<Context, Integer, String> {
        Context context;
        @Override
        protected void onPreExecute() {
            sendCodeBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            context = params[0];
            try {
                return sendCode();
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
            seconds = 60;
            sendCodeBtn.setText("重发(" + seconds + ")");
            sendCodeBtn.setBackgroundColor(Color.rgb(102, 102, 102));
            time();
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    if("success".equals(viewName))
                    {
                        Toast.makeText(context, "重发验证码成功！", Toast.LENGTH_SHORT).show();
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

        private String sendCode() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/forgetPasswordJson_changeConfirmCode");
            Map<String, String> params = new HashMap<String, String>();
            params.put("confirmCodeKey", jsonObject.getString("forgetPasswordKey"));
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