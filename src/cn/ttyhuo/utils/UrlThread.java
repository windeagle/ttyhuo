package cn.ttyhuo.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import cn.ttyhuo.R;
import cn.ttyhuo.common.MyApplication;

import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-6-26
 * Time: 下午6:55
 * To change this template use File | Settings | File Templates.
 */
public class UrlThread extends Thread {
    private Handler handler;
    private String url;
    private Map<String, String> params;
    private int what;
    public ProgressDialog dialog;

    public UrlThread(Handler handler, String url, int what) {
        this.handler = handler;
        this.url = url;
        this.what = what;
    }

    public UrlThread(Handler handler, String url, Map<String, String> params, int what) {
        this.handler = handler;
        this.url = url;
        this.params = params;
        this.what = what;
    }

    public UrlThread(Context context, Handler handler, String url, Map<String, String> params, int what) {
        dialog = ProgressDialog.show(context,
                context.getString(R.string.app_name), "操作进行中", true,
                true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        this.handler = handler;
        this.url = url;
        this.params = params;
        this.what = what;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendGetRequest(url, params, null);
            String result = "";
            if(conn.getResponseCode()==200)
                result = HttpRequestUtil.read2String(conn.getInputStream());
            MyApplication.setUpPersistentCookieStore();

            Message message = handler.obtainMessage();
            message.what = what;
            message.obj = result;
            //向handler发送消息
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
