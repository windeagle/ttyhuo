package cn.ttyhuo.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateUtils;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.user.UserInfoActivity;
import cn.ttyhuo.adapter.ContactAdapter;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.model.ContactModel;
import cn.ttyhuo.utils.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.*;

public class ContactActivity extends ActionBarActivity {

	private Context mContext;
	private List<ContactModel> mDatas;
	private List<ContactModel> mTotalDatas;
	private ListView mList;
    private EditText search;
    private ImageView iv_search;
    private TextView tv_invite;
	private PullToRefreshListView mListView;
	private ContactAdapter mAdapter;
	private Set<Integer> mCheck;
	private View mFoot;
	private int mPage;
	private static final int PAGE_SIZE = Integer.MAX_VALUE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);

		mContext = this;

		ListView lv = (ListView) findViewById(R.id.list);
        search = (EditText) findViewById(R.id.tv_search);

        iv_search =(ImageView)findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPage = 1;
                mDatas = new ArrayList<ContactModel>();
                new GetDataTask(true).execute();
            }
        });

        tv_invite = (TextView) findViewById(R.id.tv_invite);
        tv_invite.setOnClickListener(new View.OnClickListener() {
            // 点击按钮 追加数据 并通知适配器
            @Override
            public void onClick(View v) {
                if(!NetworkUtils.isNetworkAvailable(mContext))
                {
                    Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(mContext, "正在操作", Toast.LENGTH_SHORT).show();
                new UrlThread(innerHandler, UrlList.MAIN + "mvc/inviteUserByPhoneNo_" + search.getText().toString().replace(" ","").replace("-",""), 1).start();
            }
        });
		ViewGroup parent = (ViewGroup) lv.getParent();

		// Remove ListView and add PullToRefreshListView in its place
		int lvIndex = parent.indexOfChild(lv);
		parent.removeViewAt(lvIndex);
		mListView = new PullToRefreshListView(mContext);
		parent.addView(mListView, lvIndex, lv.getLayoutParams());

		mFoot = LayoutInflater.from(mContext).inflate(R.layout.list_load_more,
				null);
		AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, PxUtils.dip2px(mContext, 50));
		mFoot.setLayoutParams(lp);

		mList = mListView.getRefreshableView();

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(
                        mContext.getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);

				// Update the LastUpdatedLabel
				refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

				// Do work to refresh the list here.
				mPage = 1;
				mDatas = new ArrayList<ContactModel>();
				new GetDataTask(true).execute();

			}
		});
		// 获取通讯录好友信息
		mCheck = new HashSet<Integer>();
		mAdapter = new ContactAdapter(mContext, mDatas, mCheck);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
                if(mDatas.get(position - 1).getUserID() != null)
                {
                    Intent intent = new Intent(ContactActivity.this, UserInfoActivity.class);
                    try{
                        intent.putExtra("userID", mDatas.get(position - 1).getUserID());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mContext.startActivity(intent);
                }

//				int location = position - 1;
//				if (mCheck.contains(location)) {
//					mCheck.remove(location);
//				} else {
//					mCheck.add(location);
//				}
//
//				mAdapter.notifyDataSetChanged();
			}
		});

		mListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				if (mDatas.size() < mTotalDatas.size()) {
					mPage++;
					new GetDataTask(false).execute();
				}
			}
		});

		mPage = 1;
		mDatas = new ArrayList<ContactModel>();
		new GetDataTask(true).execute();
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
			Intent intent = getIntent();
			ArrayList<ContactModel> list = new ArrayList<ContactModel>();
			for (int a : mCheck) {
				list.add(mDatas.get(a));
			}
			intent.putParcelableArrayListExtra("models", list);
			setResult(RESULT_OK, intent);
			finish();
			return false;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class GetDataTask extends AsyncTask<Void, Void, Void> {
		private Dialog dialog;
		private final boolean show;

		public GetDataTask(boolean show) {
			this.show = show;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if (show) {
				dialog = ProgressDialog.show(mContext,
                        mContext.getString(R.string.app_name), "刷新数据...", true,
                        true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                        if (GetDataTask.this.getStatus() != AsyncTask.Status.FINISHED)
                            GetDataTask.this.cancel(true);
                    }
                });
				dialog.setCancelable(true);
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//if (mTotalDatas == null) {
                List<ContactModel> tmpTotalDatas = ContentUtils.getContactMap(mContext);
                if(!search.getText().toString().trim().isEmpty())
                {
                    mTotalDatas = new ArrayList<ContactModel>();
                    for(ContactModel c : tmpTotalDatas)
                    {
                        if(c == null || c.getName() == null)
                            continue;
                        if(c.getName().contains(search.getText().toString().trim()) || c.getPhone().replace(" ","").replace("-","").contains(search.getText().toString().trim()))
                            mTotalDatas.add(c);
                    }
                }
                else
                    mTotalDatas = tmpTotalDatas;
			//}
			// 0-9;10-19
			int start = (mPage - 1) * PAGE_SIZE;
			int end = mPage * PAGE_SIZE;
			if (end > mTotalDatas.size()) {
				end = mTotalDatas.size();
			}

            if(end == 0)
                return null;

			LogUtils.d("size:" + mTotalDatas.size());
			LogUtils.d("start:" + start + ",end:" + end);
			List<ContactModel> subList = mTotalDatas.subList(start, end);

            try {

                Map<String, String> paramMap = new HashMap<String, String>();

                StringBuilder phoneNoStr = new StringBuilder();
                HashMap<String, ContactModel> contactMap = new HashMap<String, ContactModel>(subList.size());
                for(ContactModel c : subList)
                {
                    phoneNoStr.append(c.getPhone().replace(" ","").replace("-",""));
                    phoneNoStr.append(",");
                    contactMap.put(c.getPhone().replace(" ","").replace("-",""), c);
                }
                paramMap.put("phoneNoList", phoneNoStr.deleteCharAt(phoneNoStr.length() - 1).toString().replace(" ","").replace("-",""));

                HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(UrlList.MAIN + "mvc/getUsersByPhoneNoList", paramMap, null);
                if(conn.getResponseCode()==200)
                {
                    String result = HttpRequestUtil.read2String(conn.getInputStream());
                    JSONArray jsonArray = new JSONArray(result);
                    for(int ji = 0; ji < jsonArray.length(); ji++)
                    {
                        JSONObject jsonObject = jsonArray.getJSONObject(ji).getJSONObject("userWithLatLng");
                        String phoneNo = jsonObject.getString("mobileNo");
                        if(contactMap.containsKey(phoneNo))
                        {
                            contactMap.get(phoneNo).setUserID(jsonObject.getInt("userID"));
                            contactMap.get(phoneNo).setHasFellow(jsonObject.getBoolean("alreadyFellow"));
                        }
                    }
                }
                MyApplication.setUpPersistentCookieStore();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            List<ContactModel> mTotalDatas1 = new ArrayList<ContactModel>();
            List<ContactModel> mTotalDatas2 = new ArrayList<ContactModel>();
            List<ContactModel> mTotalDatas3 = new ArrayList<ContactModel>();

            for(ContactModel c : subList)
            {
                if(c.getUserID() == null)
                    mTotalDatas1.add(c);
                else if(c.getHasFellow() == null || !c.getHasFellow())
                    mTotalDatas2.add(c);
                else
                    mTotalDatas3.add(c);
            }

            subList = new ArrayList<ContactModel>();
            subList.addAll(mTotalDatas1);
            subList.addAll(mTotalDatas2);
            subList.addAll(mTotalDatas3);

			mDatas.addAll(subList);
			LogUtils.d("size:" + mDatas.size());

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

			mList.removeFooterView(mFoot); // 确保只有一个footview
			if (mDatas.size() < mTotalDatas.size()) {
				mList.addFooterView(mFoot);
			}

			mAdapter.updateData(mDatas);
			mAdapter.notifyDataSetChanged();
			mListView.onRefreshComplete();

		}
	}

    Handler innerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);

                boolean notLogin = jObject.has("haslogin");
                boolean success = jObject.has("success");   //not_login
                //服务器未登录
                if(notLogin)
                {
                    Toast.makeText(mContext, "您尚未登录！", Toast.LENGTH_LONG).show();
                }
                else if(success)
                {
                    Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(mContext, jObject.getString("errMsg"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
