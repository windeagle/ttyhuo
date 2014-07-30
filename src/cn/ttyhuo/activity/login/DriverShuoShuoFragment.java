package cn.ttyhuo.activity.login;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.StringUtils;
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
public class DriverShuoShuoFragment extends LoginNeedBaseFragment {

    private Button okBtn;
    private ProgressBar progressBar;
    private EditText edit_driver_shuoshuo;

    protected DriverShuoShuoFragment getInstance()
    {
        return new DriverShuoShuoFragment();
    }

    protected void setupViewOfHasLogin(JSONObject jObject)
    {
        try {
            Activity context = getActivity();
            okBtn = (Button)context.findViewById(R.id.okBtn);
            progressBar = (ProgressBar) context.findViewById(R.id.progressBar1);
            edit_driver_shuoshuo = (EditText)context.findViewById(R.id.edit_driver_shuoshuo);
            int verifyFlag = jObject.getJSONObject("loggedUser").getInt("verifyFlag");
            int industryType = jObject.getJSONObject("loggedUser").getInt("industryType");
            if((verifyFlag & 2) == 2 || ((verifyFlag & 4) == 4 && industryType == 1))
                okBtn.setOnClickListener(okClick);
            else
                okBtn.setText("通过车主认证后退出并重新登录才能发表车源说说！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setupViewFromLocalCache()
    {
        try {
            //从本地缓存中取用户信息
            JSONObject jObject = new JSONObject(GetLoginUser());
            setupViewOfHasLogin(jObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected View createViewOfHasLogin(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUrl(UrlList.MAIN + "mvc/loginJson");
        return inflater.inflate(R.layout.activity_driver_shuoshuo, null);
    }

    View.OnClickListener okClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!StringUtils.isEmpty(edit_driver_shuoshuo.getText().toString()))
                new MyTask().execute(v.getContext());
        }
    };

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        String shuoshuo;
        @Override
        protected void onPreExecute() {
            okBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            shuoshuo = edit_driver_shuoshuo.getText().toString();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            //第二个执行方法,onPreExecute()执行完后执行
            context = params[0];
            try {
                return ok();
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
            okBtn.setEnabled(true);
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    //服务器未登录
                    if("success".equals(viewName))
                    {
                        Toast.makeText(context, "发布成功！", Toast.LENGTH_SHORT).show();
                        DriverShuoShuoFragment.this.getActivity().finish();
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

        private String ok() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/newDriverShuoShuo");
            Map<String, String> params = new HashMap<String, String>();
            params.put("shuoshuo", edit_driver_shuoshuo.getText().toString());
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
