package cn.ttyhuo.activity.base;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.ShowPicActivity;
import cn.ttyhuo.activity.login.LoginNeedBaseFragment;
import cn.ttyhuo.adapter.PicListAdapter;
import cn.ttyhuo.view.ExpandableHeightGridView;

import java.util.ArrayList;

public class BaseWithPicActivity extends ActionBarActivity implements
        View.OnClickListener {

	protected Context mContext;
	protected ExpandableHeightGridView mPicGrid;
	protected PicListAdapter mPicAdapter;
	protected ArrayList<String> mPicData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	protected void initView() {
		mPicGrid = (ExpandableHeightGridView) findViewById(R.id.pic_grid);
		mPicData = new ArrayList<String>();
		mPicData.add("plus");
		mPicAdapter = new PicListAdapter(mContext, mPicData, 0);
		mPicGrid.setAdapter(mPicAdapter);

		mPicGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String url = mPicData.get(arg2);
				if (!url.equals("plus")) {
					Intent intent = new Intent(mContext, ShowPicActivity.class);
					ArrayList<String> newList = new ArrayList<String>();
					//newList.addAll(mPicData);
                    for(String p : mPicData)
                    {
                        newList.add(p.replace("_small", ""));
                    }
					intent.putStringArrayListExtra("pics", newList);
					intent.putExtra("position", arg2);
					mContext.startActivity(intent);
				}

			}
		});
	}

    protected boolean GetLoginFlag()
    {
        //从本地存储中读取
        SharedPreferences settings = getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0);
        boolean loginFlag = settings.getBoolean(LoginNeedBaseFragment.LOGIN_FLAG, false);
        return loginFlag;
    }

    protected String GetLoginUser()
    {
        //从本地存储中读取
        SharedPreferences settings = getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0);
        String loginUser = settings.getString(LoginNeedBaseFragment.LOGIN_USER, "");
        return loginUser;
    }

    @Override
    public void onClick(View v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
