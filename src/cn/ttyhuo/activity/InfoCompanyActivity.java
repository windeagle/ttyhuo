package cn.ttyhuo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.RadioGroup.OnCheckedChangeListener;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseAddPic2Activity;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.DialogUtils;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfoCompanyActivity extends BaseAddPic2Activity {

    protected String getVerifyFlag()
    {
        return "Company";
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_company);

		getActionBar().setTitle("编辑公司认证资料");
		initView();
	}

	RadioGroup mRadio;
	View mRadioRoot;
	EditText mEditName;
    TextView tv_edit_tips;
    TextView tv_table_sample;
	EditText mEditTitle;
	EditText mEditPhone;
	EditText mEditIndustry;
	EditText mEditAddress;
    private ProgressBar progressBar;

	@Override
	protected void initView() {
		super.initView();

		mRadio = (RadioGroup) findViewById(R.id.radio);
		mRadioRoot = findViewById(R.id.radio_root);
		mEditName = (EditText) findViewById(R.id.edit_name);
		mEditTitle = (EditText) findViewById(R.id.edit_title);
		mEditPhone = (EditText) findViewById(R.id.edit_phone);
		mEditIndustry = (EditText) findViewById(R.id.edit_industry);
		mEditAddress = (EditText) findViewById(R.id.edit_address);
        tv_table_sample = (TextView) findViewById(R.id.tv_table_sample);
        tv_edit_tips = (TextView) findViewById(R.id.tv_edit_tips);

        tv_table_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://ttyhuo-document.stor.sinaapp.com/company.doc");
                Intent it = new Intent(Intent.ACTION_VIEW, uri);
                InfoCompanyActivity.this.startActivity(it);
            }
        });

		mRadio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				String flag = ((RadioButton) group.findViewById(checkedId))
						.getText().toString();
				if (flag.equals("是")) {
					mRadioRoot.setVisibility(View.GONE);
				} else {
					mRadioRoot.setVisibility(View.VISIBLE);
				}
			}
		});
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if(progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        isDoingUpdate = true;
        if(!NetworkUtils.isNetworkAvailable(mContext))
        {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
        }
        new UrlThread(handler, UrlList.MAIN + "mvc/editCompanyInfoJson".toString(), 1).start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);

                isDoingUpdate = false;
                if(progressBar != null)
                    progressBar.setVisibility(View.GONE);

                String viewName = jObject.getString("viewName");
                //服务器未登录
                if("not_login".equals(viewName))
                {
                    //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                    InfoBasicActivity.SetLoginFlag(InfoCompanyActivity.this, false);
                    Intent intent = new Intent(mContext, MainPage.class);
                    Toast.makeText(InfoCompanyActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                    InfoCompanyActivity.this.finish();
                }
                else if("err".equals(viewName))
                {
                    String errMsg = jObject.getString("errMsg");
                    Toast.makeText(InfoCompanyActivity.this, errMsg, Toast.LENGTH_SHORT).show();
                }
                else
                {
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

            ArrayList<String> pics = new ArrayList<String>();
            JSONArray jsonArray = jObject.optJSONArray("imageList");
            if(jsonArray != null)
            {
                for(int s = 0; s < jsonArray.length(); s++)
                    pics.add(jsonArray.getString(s));
            }
            while(pics.size() < 2)
                pics.add("plus");
            mPicData = pics;
            mPicAdapter.updateData(pics);

            JSONObject userJsonObj = jObject.getJSONObject("userWithCompanyInfo");
            if((userJsonObj.getInt("verifyFlag") & 4) == 4)
            {
                tv_edit_tips.setText("您提交的公司认证资料已经审核通过！");
            }
            else if(jsonArray != null && jsonArray.length() > 1 && !userJsonObj.optString("company").isEmpty() && userJsonObj.optString("company") != "null")
            {
                tv_edit_tips.setText("您已经提交了公司认证资料，我们将在您提交后的一个工作日内审核，审核通过前您可以继续修改。");
            }
            else
            {
                tv_edit_tips.setText("上传公司认证图片并提交以下的表单才能申请公司认证。");
            }

            if(!userJsonObj.optString("company").isEmpty() && userJsonObj.optString("company") != "null")
                mEditName.setText(userJsonObj.getString("company"));
            if(!userJsonObj.optString("jobPosition").isEmpty() && userJsonObj.optString("jobPosition") != "null")
                mEditTitle.setText(userJsonObj.getString("jobPosition"));
            if(!userJsonObj.optString("fixPhoneNo").isEmpty() && userJsonObj.optString("fixPhoneNo") != "null")
                mEditPhone.setText(userJsonObj.getString("fixPhoneNo"));
            if(!userJsonObj.optString("address").isEmpty() && userJsonObj.optString("address") != "null")
                mEditAddress.setText(userJsonObj.getString("address"));
            if(!userJsonObj.optString("industryName").isEmpty() && userJsonObj.optString("industryName") != "null")
                mEditIndustry.setText(userJsonObj.getString("industryName"));

            int industryType = userJsonObj.getInt("industryType");
            if(industryType == 0)
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setChecked(true);
            if(industryType == 1)
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setChecked(true);

            saveParams(true);

            if((userJsonObj.getInt("verifyFlag") & 4) == 0)
            {
                mEditName.setFocusable(true);
                mEditName.setEnabled(true);
                mEditTitle.setFocusable(true);
                mEditTitle.setEnabled(true);
                mEditPhone.setFocusable(true);
                mEditPhone.setEnabled(true);
                mEditAddress.setFocusable(true);
                mEditAddress.setEnabled(true);
                mEditIndustry.setFocusable(true);
                mEditIndustry.setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setEnabled(true);
                canUpdate = true;
            }
            else
            {
                mPicGrid.setOnItemLongClickListener(null);

                mEditName.setFocusable(false);
                mEditName.setEnabled(false);
                mEditTitle.setFocusable(false);
                mEditTitle.setEnabled(false);
                mEditPhone.setFocusable(false);
                mEditPhone.setEnabled(false);
                mEditAddress.setFocusable(false);
                mEditAddress.setEnabled(false);
                mEditIndustry.setFocusable(false);
                mEditIndustry.setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setEnabled(false);
                canUpdate = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.done_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
            if(isDoingUpdate)
            {
                Toast.makeText(this, "更新操作正在进行！", Toast.LENGTH_SHORT).show();
                return false;
            }
            saveParams(false);
            if(!canUpdate)
            {
                return false;
            }
            if(!isChanged())
            {
                Toast.makeText(this, "没有修改,不用更新！", Toast.LENGTH_SHORT).show();
                return false;
            }
            new MyTask().execute(InfoCompanyActivity.this);
            return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if(isDoingUpdate)
            {
                Toast.makeText(this, "更新操作正在进行,不可退出！", Toast.LENGTH_SHORT).show();
                return true;
            }

            saveParams(false);
            if(!isChanged())
            {
                return super.onKeyDown(keyCode, event);
            }
            if(!canUpdate)
            {
                return super.onKeyDown(keyCode, event);
            }

            DialogUtils.createNormalDialog(mContext, 0,
                    mContext.getText(R.string.app_name).toString(), "是否保存",
                    "确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            new MyTask().execute(InfoCompanyActivity.this);
                        }
                    }, "取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            restore();
                        }
                    }
            ).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    protected void restore()
    {
        try {

            mEditName.setText(oldParams.get("company"));
            mEditTitle.setText(oldParams.get("jobPosition"));
            mEditPhone.setText(oldParams.get("fixPhoneNo"));
            mEditAddress.setText(oldParams.get("address"));
            mEditIndustry.setText(oldParams.get("industryName"));

            String industryType = oldParams.get("industryType");
            if(industryType.equals("0"))
                ((RadioButton) findViewById(R.id.radio_industryType_0)).setChecked(true);
            if(industryType.equals("1"))
                ((RadioButton) findViewById(R.id.radio_industryType_1)).setChecked(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveParams(Boolean resetOld)
    {
        String company = mEditName.getText().toString();
        String industryType = "0";
        int checkedId = mRadio.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_industryType_1)
            industryType = "1";

        String jobPosition = mEditTitle.getText().toString();
        String fixPhoneNo = mEditPhone.getText().toString();
        String address = mEditAddress.getText().toString();
        String industryName = mEditIndustry.getText().toString();

        Map<String, String> tmpParams = new HashMap<String, String>();
        tmpParams.put("company", company);
        tmpParams.put("jobPosition", jobPosition);
        tmpParams.put("fixPhoneNo", fixPhoneNo);
        tmpParams.put("address", address);
        tmpParams.put("industryName", industryName);
        tmpParams.put("industryType", industryType);

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
    private boolean canUpdate = false;

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            isDoingUpdate = true;
            if(progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            progressDialog = ProgressDialog.show(InfoCompanyActivity.this,
                    InfoCompanyActivity.this.getString(R.string.app_name), "操作进行中", true,
                    true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                }
            });
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
            MyApplication.setUpPersistentCookieStore();
            if(progressBar != null)
                progressBar.setVisibility(View.GONE);
            progressDialog.dismiss();
            isDoingUpdate = false;
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    //服务器未登录
                    if("not_login".equals(viewName))
                    {
                        //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                        InfoBasicActivity.SetLoginFlag(InfoCompanyActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(InfoCompanyActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        InfoCompanyActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        oldParams = params;
                        Toast.makeText(context, "更新完成!", Toast.LENGTH_SHORT).show();
                        InfoCompanyActivity.this.finish();
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
            String url = new String(UrlList.MAIN + "mvc/editCompanyInfoJson");
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }

}
