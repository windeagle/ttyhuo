package cn.ttyhuo.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
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
import java.text.SimpleDateFormat;
import java.util.*;

public class InfoRealActivity extends BaseAddPic2Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_real);

		getActionBar().setTitle("编辑实名认证资料");
		initView();
	}

	TextView mEditDate;
	EditText mEditName;
	RadioGroup mRadioSex;
    TextView tv_pic_sample;
    TextView tv_edit_tips;
    private ProgressBar progressBar;

	@Override
	protected void initView() {
		super.initView();

		mEditDate = (TextView) findViewById(R.id.edit_date);
		mEditName = (EditText) findViewById(R.id.edit_name);
		mRadioSex = (RadioGroup) findViewById(R.id.radio_sex);
        tv_pic_sample = (TextView) findViewById(R.id.tv_pic_sample);
        tv_edit_tips = (TextView) findViewById(R.id.tv_edit_tips);

        tv_pic_sample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, BigImageActivity.class);
                intent.putExtra("url", "http://ttyh-document.oss-cn-qingdao.aliyuncs.com/pic_sample.jpg");
                InfoRealActivity.this.startActivity(intent);
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
        new UrlThread(handler, UrlList.MAIN + "mvc/editUserVerifyJson".toString(), 1).start();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_date:
			int year,
			month,
			day;
			String date = mEditDate.getText().toString();
			if (!TextUtils.isEmpty(date)) {
				String[] dates = date.split("年|月|日");
				year = Integer.parseInt(dates[0]);
				month = Integer.parseInt(dates[1]) - 1;
				day = Integer.parseInt(dates[2]);
			} else {
				Calendar c = Calendar.getInstance();

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DAY_OF_MONTH);
			}
			Dialog dialog = new DatePickerDialog(this,
					new DatePickerDialog.OnDateSetListener() {
						@Override
						public void onDateSet(DatePicker datePicker, int year,
								int month, int dayOfMonth) {
							mEditDate.setText(year + "年" + (month + 1) + "月"
									+ dayOfMonth + "日");
						}
					}, year, month, day);
			dialog.show();
			break;
		}
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
                    InfoBasicActivity.SetLoginFlag(InfoRealActivity.this, false);
                    Intent intent = new Intent(mContext, MainPage.class);
                    Toast.makeText(InfoRealActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                    InfoRealActivity.this.finish();
                }
                else if("err".equals(viewName))
                {
                    String errMsg = jObject.getString("errMsg");
                    Toast.makeText(InfoRealActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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

            JSONObject userJsonObj = jObject.getJSONObject("loggedUser");

            if((userJsonObj.getInt("verifyFlag") & 1) == 1)
            {
                tv_edit_tips.setText("您提交的实名认证资料已经审核通过！");
            }
            else if(jsonArray != null && jsonArray.length() > 1 && jObject.has("userVerify"))
            {
                tv_edit_tips.setText("您已经提交了实名认证资料，我们将在您提交后的一个工作日内审核，审核通过前您可以继续修改。");
            }
            else
            {
                tv_edit_tips.setText("上传实名认证图片并提交以下的表单才能申请实名认证。");
            }

            Date d = new Date();
            if(!userJsonObj.optString("birthday").isEmpty() && userJsonObj.optString("birthday") != "null")
            {
                d.setTime(new Long(userJsonObj.getString("birthday")));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                mEditDate.setText(formatter.format(d));
            }
            Integer gender = userJsonObj.getInt("gender");
            if(gender == 2)
                ((RadioButton) findViewById(R.id.radio_sex_2)).setChecked(true);
            else
                ((RadioButton) findViewById(R.id.radio_sex_1)).setChecked(true);

            if(jObject.has("userVerify"))
            {
                JSONObject realJsonObj = jObject.getJSONObject("userVerify");
                if(!realJsonObj.optString("identityName").isEmpty() && realJsonObj.optString("identityName") != "null")
                    mEditName.setText(realJsonObj.getString("identityName"));
            }

            saveParams(true);
            //oldParams.put("gender", gender.toString());

            if((userJsonObj.getInt("verifyFlag") & 1) == 0)
            {
                mEditDate.setOnClickListener(this);
                mEditName.setFocusable(true);
                mEditName.setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setEnabled(true);
                canUpdate = true;
            }
            else
            {
                mPicGrid.setOnItemLongClickListener(null);

                mEditName.setFocusable(false);
                mEditName.setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setEnabled(false);
                canUpdate = false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                    Toast.makeText(this, "操作正在进行！", Toast.LENGTH_SHORT).show();
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
                new MyTask().execute(InfoRealActivity.this);
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
                Toast.makeText(this, "操作正在进行,不可退出！", Toast.LENGTH_SHORT).show();
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
                            new MyTask().execute(InfoRealActivity.this);
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

            mEditName.setText(oldParams.get("identityName"));

            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(oldParams.get("birthdayStr"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
            mEditDate.setText(formatter.format(d));

            String gender = oldParams.get("gender");
            if(gender.equals("2"))
                ((RadioButton) findViewById(R.id.radio_sex_2)).setChecked(true);
            else
                ((RadioButton) findViewById(R.id.radio_sex_1)).setChecked(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveParams(Boolean resetOld)
    {
        String name = mEditName.getText().toString();
        String sex = "0";
        int checkedId = mRadioSex.getCheckedRadioButtonId();
        if (checkedId == R.id.radio_sex_1)
            sex = "1";
        if (checkedId == R.id.radio_sex_2)
            sex = "2";

        String birth = mEditDate.getText().toString();
        Map<String, String> tmpParams = new HashMap<String, String>();
        tmpParams.put("identityName", name);
        tmpParams.put("gender", sex);
        tmpParams.put("birthdayStr", birth.replace("年","-").replace("月","-").replace("日","-"));

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
            progressDialog = ProgressDialog.show(InfoRealActivity.this,
                    InfoRealActivity.this.getString(R.string.app_name), "操作进行中", true,
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
                        InfoBasicActivity.SetLoginFlag(InfoRealActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(InfoRealActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        InfoRealActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        oldParams = params;
                        Toast.makeText(context, "更新完成!", Toast.LENGTH_SHORT).show();
                        InfoRealActivity.this.finish();
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
            String url = new String(UrlList.MAIN + "mvc/editUserVerifyJson");
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
