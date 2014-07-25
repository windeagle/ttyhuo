package cn.ttyhuo.activity.product;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.InfoBasicActivity;
import cn.ttyhuo.activity.MainPage;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.helper.DateTimeOnClickListener;
import cn.ttyhuo.utils.DialogUtils;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.utils.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-4
 * Time: 上午1:06
 * To change this template use File | Settings | File Templates.
 */
public class ProductEditActivity extends ProductInfoActivity {

    @Override
    protected int getLayoutResID()
    {
        isMy = true;
        return R.layout.activity_product_edit;
    }

    protected void setupView(JSONObject jsonObject)
    {
        super.setupView(jsonObject);

        TextView edit_fromTime =
                (TextView) findViewById(R.id.edit_fromTime);
        TextView edit_toTime =
                (TextView) findViewById(R.id.edit_toTime);
        EditText edit_price =
                (EditText) findViewById(R.id.edit_price);
        EditText edit_limitCount =
                (EditText) findViewById(R.id.edit_limitCount);

        try {
            JSONObject jObject = jsonObject.has("product") ? jsonObject.getJSONObject("product") : jsonObject;
            productID = jObject.getInt("productID");
            JSONObject productWindow = jObject;
            if(jsonObject.has("currentProductWindow"))
            {
                //支持ProductForList
                productWindow = jsonObject.getJSONObject("currentProductWindow");
            }
            edit_fromTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(productWindow.getLong("departureTime"))));
            edit_toTime.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(productWindow.getLong("arrivalTime"))));
            edit_price.setText(JSONUtil.getStringFromJson(productWindow, "price", "0.0"));
            edit_limitCount.setText(JSONUtil.getStringFromJson(productWindow, "limitCount", "1"));

        } catch (JSONException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        setupEditAndBtn(R.id.edit_fromTime, false);
        setupEditAndBtn(R.id.edit_toTime, true);

        saveParams(true);

        LinearLayout lv_edit_current_btn = (LinearLayout) findViewById(R.id.lv_edit_current_btn);
        lv_edit_current_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(isDoingUpdate)
                {
                    Toast.makeText(ProductEditActivity.this, "操作正在进行！", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveParams(false);
                if(!isChanged())
                {
                    Toast.makeText(ProductEditActivity.this, "没有修改,不用更新！", Toast.LENGTH_SHORT).show();
                    return;
                }
                params.put("effectAtOnce", "true");
                params.put("productID", String.valueOf(productID));
                new MyTask().execute(ProductEditActivity.this);
            }
        });
        LinearLayout lv_new_window_btn = (LinearLayout) findViewById(R.id.lv_new_window_btn);
        lv_new_window_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(isDoingUpdate)
                {
                    Toast.makeText(ProductEditActivity.this, "操作正在进行！", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveParams(false);
                if(!isChanged())
                {
                    Toast.makeText(ProductEditActivity.this, "没有修改,不用更新！", Toast.LENGTH_SHORT).show();
                    return;
                }
                params.put("effectAtOnce", "false");
                params.put("productID", String.valueOf(productID));
                new MyTask().execute(ProductEditActivity.this);
            }
        });
    }

    private final int DATE_DIALOG = 1;

    private void setupEditAndBtn(int editTextId, boolean isTo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, isTo ? 3 : 1);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) / 5 * 5);

        TextView editText =
                (TextView) findViewById(editTextId);
        if(StringUtils.isEmpty(editText.getText().toString()))
            editText.setText(sdf.format(calendar.getTime()));

        View.OnClickListener dateBtnListener =
                new DateTimeOnClickListener(DATE_DIALOG, this, editText);
        editText.setOnClickListener(dateBtnListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(isDoingUpdate)
            {
                Toast.makeText(this, "操作正在进行,不可退出！", Toast.LENGTH_SHORT).show();
                return true;
            }

            saveParams(false);
            if(!isChanged())
            {
                return super.onKeyDown(keyCode, event);
            }

            DialogUtils.createNormalDialog(mContext, 0,
                    mContext.getText(R.string.app_name).toString(), "确定退出",
                    "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProductEditActivity.this.finish();
                        }
                    }, "取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }
            ).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void saveParams(Boolean resetOld)
    {
        TextView edit_fromTime =
                (TextView) findViewById(R.id.edit_fromTime);
        TextView edit_toTime =
                (TextView) findViewById(R.id.edit_toTime);
        EditText edit_price =
                (EditText) findViewById(R.id.edit_price);
        EditText edit_limitCount =
                (EditText) findViewById(R.id.edit_limitCount);

        Map<String, String> tmpParams = new HashMap<String, String>();
        tmpParams.put("departureTimeStr", edit_fromTime.getText().toString());
        tmpParams.put("arrivalTimeStr", edit_toTime.getText().toString());
        tmpParams.put("price", edit_price.getText().toString());
        tmpParams.put("limitCount", edit_limitCount.getText().toString());

        if(resetOld)
            oldParams = tmpParams;
        else
            params = tmpParams;
    }

    private boolean isChanged()
    {
        if(params.equals(oldParams))
            return false;
        for(String k : params.keySet())
        {
            if(!params.get(k).equals(oldParams.get(k)))
                return true;
        }
        return false;
    }

    private Map<String, String> params = new HashMap<String, String>();
    private Map<String, String> oldParams = new HashMap<String, String>();
    private boolean isDoingUpdate = false;
    private int productID = 0;

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            isDoingUpdate = true;
            dialog = ProgressDialog.show(ProductEditActivity.this,
                    ProductEditActivity.this.getString(R.string.app_name),
                    ProductEditActivity.this.getText(R.string.operating), true, false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            context = params[0];
            //第二个执行方法,onPreExecute()执行完后执行
            try {
                return update();
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
            isDoingUpdate = false;
            dialog.dismiss();
            MyApplication.setUpPersistentCookieStore();
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    //服务器未登录
                    if("not_login".equals(viewName))
                    {
                        //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                        InfoBasicActivity.SetLoginFlag(ProductEditActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(ProductEditActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        ProductEditActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        Toast.makeText(context, "操作完成!", Toast.LENGTH_SHORT).show();
                        ProductEditActivity.this.finish();
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

        private String update() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/product_edit");

            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
