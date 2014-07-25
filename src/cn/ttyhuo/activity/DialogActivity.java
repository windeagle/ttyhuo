package cn.ttyhuo.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import cn.ttyhuo.R;

@SuppressLint("Instantiatable")
public class DialogActivity extends Dialog {

    @SuppressLint("Instantiatable")
    public DialogActivity(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_dialog);
    }
}