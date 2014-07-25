package cn.ttyhuo.utils;

import org.apache.http.Header;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
import cn.ttyhuo.R;

import com.loopj.android.http.TextHttpResponseHandler;

public class XHttpHandler extends TextHttpResponseHandler {

	private Dialog dialog;
	private final Context context;
	private boolean showDialog;
	private String message;
	private boolean cancelable;

	public XHttpHandler(Context context) {
		this.context = context;
	}

	public XHttpHandler(Context context, boolean showDialog, String message,
			boolean cancelable) {
		this.context = context;
		this.message = message;
		this.cancelable = cancelable;
		this.showDialog = showDialog;

	}

	@Override
	public void onStart() {
		if (showDialog) {
			dialog = ProgressDialog.show(context,
					context.getString(R.string.app_name), message, true);
			dialog.setCancelable(cancelable);
		}
	}

	@Override
	public void onSuccess(int statusCode, Header[] header, String responseBody) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFailure(int statusCode, Header[] header, String responseBody,
			Throwable throwable) {
		// TODO Auto-generated method stub
		Toast.makeText(context, "网络连接错误", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinish() {
		if (showDialog) {
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
		}

	}

}
