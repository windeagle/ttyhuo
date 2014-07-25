package cn.ttyhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.user.UserInfoActivity;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mContext = this;

		findViewById(R.id.page1).setOnClickListener(this);
		findViewById(R.id.page2).setOnClickListener(this);
		findViewById(R.id.page3).setOnClickListener(this);
        findViewById(R.id.page4).setOnClickListener(this);
        findViewById(R.id.page5).setOnClickListener(this);
        findViewById(R.id.page6).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = null;
		switch (v.getId()) {
		case R.id.page1:
			intent = new Intent(mContext, InfoBasicActivity.class);
			break;
		case R.id.page2:
			intent = new Intent(mContext, InfoOwnerActivity.class);
			break;
		case R.id.page3:
			intent = new Intent(mContext, InfoCompanyActivity.class);
			break;
		case R.id.page4:
			intent = new Intent(mContext, InfoRealActivity.class);
			break;
        case R.id.page5:
            intent = new Intent(mContext, MainPage.class);
            break;
        case R.id.page6:
            intent = new Intent(mContext, UserInfoActivity.class);
            break;
		}

		if (intent != null) {
			mContext.startActivity(intent);
		}
	}
}
