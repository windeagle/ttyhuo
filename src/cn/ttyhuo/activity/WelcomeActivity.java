package cn.ttyhuo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setContentView(R.layout.send_to_wx);

        final Intent localIntent = new Intent(this, Tabs.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(localIntent);
            }
        }, 1500);

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                startActivity(localIntent);
//            }
//        }, 1500);

    }
}