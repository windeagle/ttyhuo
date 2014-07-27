package cn.ttyhuo.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseAddPicActivity;
import cn.ttyhuo.adapter.CityListAdapter;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.helper.CityList;
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

public class InfoBasicActivity extends BaseAddPicActivity {

    @Override
    protected String getPicUploadUrl()
    {
        return UrlList.MAIN + "mvc/uploadUserImgFromUPYun";
    }

    @Override
    protected String getPicDeleteUrl()
    {
        return UrlList.MAIN + "mvc/deleteTmpUserImg";
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_basic);

		getActionBar().setTitle("编辑基本资料");
		initView();
	}

	TextView mEditHometown;
	TextView mEditDate;
	EditText mEditName;
	EditText mEditSign;
	EditText mEditHobby;
	EditText mEditDesc;
	RadioGroup mRadioSex;
    private ProgressBar progressBar;

	@Override
	protected void initView() {
		super.initView();

		mEditHometown = (TextView) findViewById(R.id.edit_hometown);
		mEditDate = (TextView) findViewById(R.id.edit_date);
		mEditName = (EditText) findViewById(R.id.edit_name);
		mEditSign = (EditText) findViewById(R.id.edit_sign);
		mEditHobby = (EditText) findViewById(R.id.edit_hobby);
		mEditDesc = (EditText) findViewById(R.id.edit_personal);
		mRadioSex = (RadioGroup) findViewById(R.id.radio_sex);

		mEditHometown.setOnClickListener(this);

		initPopup();

        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        if(progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
        isDoingUpdate = true;
        if(!NetworkUtils.isNetworkAvailable(mContext))
        {
            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
        }
        new UrlThread(handler, UrlList.MAIN + "mvc/editUserInfoJson".toString(), 1).start();
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
                    SetLoginFlag(InfoBasicActivity.this, false);
                    Intent intent = new Intent(mContext, MainPage.class);
                    Toast.makeText(InfoBasicActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                    InfoBasicActivity.this.finish();
                }
                else
                {
                    //服务器登录了
                    SetLoginFlag(InfoBasicActivity.this,true);
                    //设置本地缓存的用户信息
                    SetLoginUser(InfoBasicActivity.this, result);
                    setupViewOfHasLogin(jObject);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
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
            pics.add("plus");
            mPicData = pics;
            mPicAdapter.updateData(pics);

            JSONObject userJsonObj = jObject.getJSONObject("loggedUser");
            if(!userJsonObj.optString("userName").isEmpty() && userJsonObj.optString("userName") != "null")
                mEditName.setText(userJsonObj.getString("userName"));
            if(!userJsonObj.optString("hobby").isEmpty() && userJsonObj.optString("hobby") != "null")
                mEditHobby.setText(userJsonObj.getString("hobby"));
            if(!userJsonObj.optString("description").isEmpty() && userJsonObj.optString("description") != "null")
                mEditDesc.setText(userJsonObj.getString("description"));
            if(!userJsonObj.optString("title").isEmpty() && userJsonObj.optString("title") != "null")
                mEditSign.setText(userJsonObj.getString("title"));
            if(!userJsonObj.optString("homeTown").isEmpty() && userJsonObj.optString("homeTown") != "null")
                mEditHometown.setText(userJsonObj.getString("homeTown"));

            Date d = new Date();
            if(!userJsonObj.optString("birthday").isEmpty() && userJsonObj.optString("birthday") != "null")
            {
                d.setTime(new Long(userJsonObj.getString("birthday")));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
                mEditDate.setText(formatter.format(d));
            }
            int gender = userJsonObj.getInt("gender");
            if(gender == 0)
                ((RadioButton) findViewById(R.id.radio_sex_0)).setChecked(true);
            if(gender == 1)
                ((RadioButton) findViewById(R.id.radio_sex_1)).setChecked(true);
            if(gender == 2)
                ((RadioButton) findViewById(R.id.radio_sex_2)).setChecked(true);

            saveParams(true);

            if((userJsonObj.getInt("verifyFlag") & 1) == 0)
            {
                mEditDate.setOnClickListener(this);
                mEditName.setFocusable(true);
                mEditName.setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setEnabled(true);
                ((RadioButton) findViewById(R.id.radio_sex_0)).setFocusable(true);
                ((RadioButton) findViewById(R.id.radio_sex_0)).setEnabled(true);
            }
            else
            {
                mEditName.setFocusable(false);
                mEditName.setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_sex_2)).setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_sex_1)).setEnabled(false);
                ((RadioButton) findViewById(R.id.radio_sex_0)).setFocusable(false);
                ((RadioButton) findViewById(R.id.radio_sex_0)).setEnabled(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	PopupWindow mPopupWindow;
	View popupView;

    ListView mProvinceList;
    ListView mCityList;
    ListView mCountyList;

    CityListAdapter mProvinceAdapter;
    CityListAdapter mCityAdapter;
    CityListAdapter mCountyAdapter;

    List<String> mProvinceData;
    List<String> mCityData;
    List<String> mCountyData;

    TextView currentCityTextView;
    String mCheckProvince;
    String mCheckCity;

	private void initPopup() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        popupView = inflater.inflate(R.layout.popup_hometown, null, false);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mProvinceList = (ListView) popupView.findViewById(R.id.province);
        mCityList = (ListView) popupView.findViewById(R.id.city);
        mCountyList = (ListView) popupView.findViewById(R.id.county);

        mProvinceData = CityList.getProvinceData();
        mCityData = CityList.getCity(mProvinceData.get(0));
        mCountyData = CityList.getCounty(mProvinceData.get(0), mCityData.get(0));

        mProvinceAdapter = new CityListAdapter(mContext, mProvinceData);
        mCityAdapter = new CityListAdapter(mContext, mCityData);
        mCountyAdapter = new CityListAdapter(mContext, mCountyData);

        mProvinceList.setAdapter(mProvinceAdapter);
        mCityList.setAdapter(mCityAdapter);
        mCountyList.setAdapter(mCountyAdapter);

        mProvinceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mCheckProvince = mProvinceData.get(arg2);
                mCityData = CityList.getCity(mCheckProvince);
                mCityAdapter.updateData(mCityData);
                mCountyData = CityList.getCounty(mCheckProvince, mCityData.get(0));
                mCountyAdapter.updateData(mCountyData);
            }
        });

        mCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mCheckCity = mCityData.get(arg2);
                mCountyData = CityList.getCounty(mCheckProvince, mCheckCity);
                mCountyAdapter.updateData(mCountyData);
            }
        });

        mCountyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String showCity = null;
                String county = mCountyData.get(arg2);

                if (!county.equals("不限"))
                {
                    showCity = mCheckProvince + " " + mCheckCity + " " + county;
                }
                else if (!mCheckCity.equals("不限"))
                {
                    showCity = mCheckProvince + " " + mCheckCity;
                }
                else
                {
                    showCity = mCheckProvince;
                }

                currentCityTextView.setText(showCity);
                dismissPopup();
            }
        });
	}

	private void showPopup() {
		mPopupWindow.showAtLocation(findViewById(R.id.root), Gravity.CENTER, 0,
				0);
        currentCityTextView = mEditHometown;
	}

	private void dismissPopup() {
		mPopupWindow.dismiss();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.edit_hometown:
			showPopup();
			break;
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
            if(!isChanged())
            {
                Toast.makeText(this, "没有修改,不用更新！", Toast.LENGTH_SHORT).show();
                return false;
            }
            new MyTask().execute(InfoBasicActivity.this);
			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPopupWindow.isShowing()) {
				dismissPopup();
				return true;
			}

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

			DialogUtils.createNormalDialog(mContext, 0,
					mContext.getText(R.string.app_name).toString(), "是否保存",
					"确定", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
                            new MyTask().execute(InfoBasicActivity.this);
						}
					}, "取消", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
                            restore();
						}
					}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


    protected void restore()
    {
        try {

            mEditName.setText(oldParams.get("userName"));
            mEditHobby.setText(oldParams.get("hobby"));
            mEditDesc.setText(oldParams.get("description"));
            mEditSign.setText(oldParams.get("title"));
            mEditHometown.setText(oldParams.get("homeTown"));

            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(oldParams.get("birthdayStr"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日");
            mEditDate.setText(formatter.format(d));

            String gender = oldParams.get("gender");
            if(gender.equals("0"))
                ((RadioButton) findViewById(R.id.radio_sex_0)).setChecked(true);
            if(gender.equals("1"))
                ((RadioButton) findViewById(R.id.radio_sex_1)).setChecked(true);
            if(gender.equals("2"))
                ((RadioButton) findViewById(R.id.radio_sex_2)).setChecked(true);

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
        String sign = mEditSign.getText().toString();
        String hobby = mEditHobby.getText().toString();
        String hometown = mEditHometown.getText().toString();
        String desc = mEditDesc.getText().toString();

        Map<String, String> tmpParams = new HashMap<String, String>();
        tmpParams.put("userName", name);
        tmpParams.put("gender", sex);
        tmpParams.put("birthdayStr", birth.replace("年","-").replace("月","-").replace("日","-"));
        tmpParams.put("title", sign);
        tmpParams.put("hobby", hobby);
        tmpParams.put("homeTown", hometown);
        tmpParams.put("description", desc);

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

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            isDoingUpdate = true;
            if(progressBar != null)
                progressBar.setVisibility(View.VISIBLE);
            progressDialog = ProgressDialog.show(InfoBasicActivity.this,
                    InfoBasicActivity.this.getString(R.string.app_name), "操作进行中", true,
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
                        SetLoginFlag(InfoBasicActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(InfoBasicActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        InfoBasicActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        oldParams = params;
                        Toast.makeText(context, "更新完成!", Toast.LENGTH_SHORT).show();
                        InfoBasicActivity.this.finish();
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
            String url = new String(UrlList.MAIN + "mvc/editUserInfoJson");
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
