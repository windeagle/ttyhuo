package cn.ttyhuo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.TextView.OnEditorActionListener;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseAddPic2Activity;
import cn.ttyhuo.common.ConstHolder;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InfoOwnerActivity extends BaseAddPic2Activity {

    protected String getVerifyFlag()
    {
        return "Truck";
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info_owner);

		getActionBar().setTitle("编辑车主认证资料");
		initView();
	}

	TextView mEditDate;
	TextView mEditChexing;
	EditText mEditZaizhong;
	EditText mEditChechang;
	EditText mEditZuowei;
	EditText mEditChepai;
	EditText mEditXinghao;
	EditText mEditKuan;
	EditText mEditGao;
    TextView tv_edit_tips;
    private ProgressBar progressBar;

	@Override
	protected void initView() {
		super.initView();

		mEditDate = (TextView) findViewById(R.id.edit_date);
		mEditChexing = (TextView) findViewById(R.id.edit_chexing);

		mEditZaizhong = (EditText) findViewById(R.id.edit_zaizhong);
		mEditChechang = (EditText) findViewById(R.id.edit_chechang);
		mEditZuowei = (EditText) findViewById(R.id.edit_zuowei);
		mEditChepai = (EditText) findViewById(R.id.edit_chepaihao);
		mEditXinghao = (EditText) findViewById(R.id.edit_xinghao);
		mEditKuan = (EditText) findViewById(R.id.edit_kuan);
		mEditGao = (EditText) findViewById(R.id.edit_gao);
        tv_edit_tips = (TextView) findViewById(R.id.tv_edit_tips);

		// 示范数字范围
		mEditZaizhong.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				String numStr = v.getText().toString();
				if (!StringNumberUtils.isPositiveDecimal(numStr)
						&& !StringNumberUtils.isPositiveInteger(numStr)) {
					Toast.makeText(mContext, "请输入1.0-100.0之间的数字",
							Toast.LENGTH_SHORT).show();
				} else {
					float num = Float.parseFloat(numStr);
					if (num > 10.0 || num < 1.0) {
						Toast.makeText(mContext, "请输入1.0-100.0之间的数字",
								Toast.LENGTH_SHORT).show();
					}
				}
				return false;
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
        new UrlThread(handler, UrlList.MAIN + "mvc/editTruckInfoJson".toString(), 1).start();
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
                    InfoBasicActivity.SetLoginFlag(InfoOwnerActivity.this, false);
                    Intent intent = new Intent(mContext, MainPage.class);
                    Toast.makeText(InfoOwnerActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                    mContext.startActivity(intent);
                    InfoOwnerActivity.this.finish();
                }
                else if("err".equals(viewName))
                {
                    String errMsg = jObject.getString("errMsg");
                    Toast.makeText(InfoOwnerActivity.this, errMsg, Toast.LENGTH_SHORT).show();
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
            while(pics.size() < 3)
                pics.add("plus");
            mPicData = pics;
            mPicAdapter.updateData(pics);

            if(jObject.getBoolean("isTruckVerified"))
            {
                tv_edit_tips.setText("您提交的车主认证资料已经审核通过！");
            }
            else if(jsonArray != null && jsonArray.length() > 2 && jObject.has("truckInfo"))
            {
                tv_edit_tips.setText("您已经提交了车主认证资料，我们将在您提交后的一个工作日内审核，审核通过前您可以继续修改。");
            }
            else
            {
                tv_edit_tips.setText("上传车主认证图片并提交以下的表单才能申请车主认证。");
            }

            if(jObject.has("truckInfo"))
            {
                JSONObject userJsonObj = jObject.getJSONObject("truckInfo");
                if(!userJsonObj.optString("licensePlate").isEmpty() && userJsonObj.optString("licensePlate") != "null")
                    mEditChepai.setText(userJsonObj.getString("licensePlate"));
                Integer truckType = userJsonObj.getInt("truckType");
                if(truckType != null && truckType > 0)
                    mEditChexing.setText(ConstHolder.TruckTypeItems[truckType - 1]);
                else
                    mEditChexing.setText("未知");
                if(!userJsonObj.optString("loadLimit").isEmpty() && userJsonObj.optString("loadLimit") != "null")
                    mEditZaizhong.setText(userJsonObj.getString("loadLimit"));
                if(!userJsonObj.optString("truckLength").isEmpty() && userJsonObj.optString("truckLength") != "null")
                    mEditChechang.setText(userJsonObj.getString("truckLength"));
                if(!userJsonObj.optString("modelNumber").isEmpty() && userJsonObj.optString("modelNumber") != "null")
                    mEditXinghao.setText(userJsonObj.getString("modelNumber"));

                if(!userJsonObj.optString("seatingCapacity").isEmpty() && userJsonObj.optString("seatingCapacity") != "null")
                    mEditZuowei.setText(userJsonObj.getString("seatingCapacity"));
                if(!userJsonObj.optString("releaseYear").isEmpty() && userJsonObj.optString("releaseYear") != "null")
                    mEditDate.setText(userJsonObj.getString("releaseYear") + "年");
                if(!userJsonObj.optString("truckWidth").isEmpty() && userJsonObj.optString("truckWidth") != "null")
                    mEditKuan.setText(userJsonObj.getString("truckWidth"));
                if(!userJsonObj.optString("truckHeight").isEmpty() && userJsonObj.optString("truckHeight") != "null")
                    mEditGao.setText(userJsonObj.getString("truckHeight"));
            }

            if(!jObject.getBoolean("isTruckVerified"))
            {
                mEditChexing.setOnClickListener(this);
                mEditDate.setOnClickListener(this);
                mEditChepai.setFocusable(true);
                mEditChepai.setEnabled(true);
                mEditZaizhong.setFocusable(true);
                mEditZaizhong.setEnabled(true);
                mEditChechang.setFocusable(true);
                mEditChechang.setEnabled(true);
                mEditZuowei.setFocusable(true);
                mEditZuowei.setEnabled(true);
                mEditKuan.setFocusable(true);
                mEditKuan.setEnabled(true);
                mEditGao.setFocusable(true);
                mEditGao.setEnabled(true);
                mEditXinghao.setFocusable(true);
                mEditXinghao.setEnabled(true);
                canUpdate = true;
            }
            else
            {
                mPicGrid.setOnItemLongClickListener(null);

                mEditChepai.setFocusable(false);
                mEditChepai.setEnabled(false);
                mEditZaizhong.setFocusable(false);
                mEditZaizhong.setEnabled(false);
                mEditChechang.setFocusable(false);
                mEditChechang.setEnabled(false);
                mEditZuowei.setFocusable(false);
                mEditZuowei.setEnabled(false);
                mEditKuan.setFocusable(false);
                mEditKuan.setEnabled(false);
                mEditGao.setFocusable(false);
                mEditGao.setEnabled(false);
                mEditXinghao.setFocusable(false);
                mEditXinghao.setEnabled(false);
                canUpdate = false;
            }

            saveParams(true);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_date:
			Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			final CharSequence yearItems[] = new CharSequence[20];
			for (int i = 0; i < 20; i++) {
				yearItems[i] = (year - i) + "年";
			}

			DialogUtils.createListDialog(mContext, 0, "请选择年份", yearItems,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mEditDate.setText(yearItems[which]);
						}
					}).show();
			break;

		case R.id.edit_chexing:
			DialogUtils.createListDialog(mContext, 0, "请选择车型", ConstHolder.TruckTypeItems,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							mEditChexing.setText(ConstHolder.TruckTypeItems[which]);
						}
					}).show();
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
            new MyTask().execute(InfoOwnerActivity.this);
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
                            new MyTask().execute(InfoOwnerActivity.this);
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

            mEditChepai.setText(oldParams.get("licensePlate"));
            Integer truckType = new Integer(oldParams.get("truckType"));
            if(truckType != null && truckType > 0)
                mEditChexing.setText(ConstHolder.TruckTypeItems[truckType - 1]);
            else
                mEditChexing.setText("未知");
            mEditZaizhong.setText(oldParams.get("loadLimit"));
            mEditChechang.setText(oldParams.get("truckLength"));
            mEditXinghao.setText(oldParams.get("modelNumber"));

            mEditZuowei.setText(oldParams.get("seatingCapacity"));
            mEditDate.setText(oldParams.get("releaseYear") + "年");
            mEditKuan.setText(oldParams.get("truckWidth"));
            mEditGao.setText(oldParams.get("truckHeight"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveParams(Boolean resetOld)
    {

        Map<String, String> tmpParams = new HashMap<String, String>();
        tmpParams.put("licensePlate", mEditChepai.getText().toString());
        Integer sIndex = 0;
        Integer tmpIndex = 0;
        for(CharSequence s : ConstHolder.TruckTypeItems)
        {
            tmpIndex ++;
             if(s.equals(mEditChexing.getText().toString()))
             {
                 sIndex = tmpIndex;
                 break;
             }
        }
        tmpParams.put("truckType", sIndex.toString());
        tmpParams.put("loadLimit", mEditZaizhong.getText().toString());
        tmpParams.put("truckLength", mEditChechang.getText().toString());
        tmpParams.put("modelNumber", mEditXinghao.getText().toString());

        tmpParams.put("seatingCapacity", mEditZuowei.getText().toString());
        tmpParams.put("releaseYear", mEditDate.getText().toString().replace("年", ""));
        tmpParams.put("truckWidth", mEditKuan.getText().toString());
        tmpParams.put("truckHeight", mEditGao.getText().toString());

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
            progressDialog = ProgressDialog.show(InfoOwnerActivity.this,
                    InfoOwnerActivity.this.getString(R.string.app_name), "操作进行中", true,
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
                        InfoBasicActivity.SetLoginFlag(InfoOwnerActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(InfoOwnerActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        InfoOwnerActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        oldParams = params;
                        Toast.makeText(context, "更新完成!", Toast.LENGTH_SHORT).show();
                        InfoOwnerActivity.this.finish();
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
            String url = new String(UrlList.MAIN + "mvc/editTruckInfoJson");
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }

}
