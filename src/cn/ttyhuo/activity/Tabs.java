package cn.ttyhuo.activity;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import cn.ttyhuo.R;

/**
 * Tab导航
 */
//public class Tabs extends TabActivity implements View.OnClickListener{
public class Tabs extends TabActivity {
    public static TabHost mTabHost;

    public static TabHost getmTabHost() {
        return mTabHost;
    }

    private RadioGroup main_radio;
    private RadioButton tab_icon_weixin, tab_icon_address, tab_icon_find, tab_icon_myself;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TabHost、TabWidget 、FrameLayout 三者的ID必须是android.R.id.tabhost、android.R.id.tabs、android.R.id.tabcontent
        setContentView(R.layout.tmp_tabs);
        mTabHost = getTabHost();
//        LayoutInflater li = LayoutInflater.from(this);
//        li.inflate(R.layout.tabdemo_0, tabHost.getTabContentView(), true);
        final TabWidget tabWidget = mTabHost.getTabWidget();
        tabWidget.setStripEnabled(false);// 圆角边线不启用
        //添加n个tab选项卡，定义他们的tab名，指示名，目标屏对应的类
//        mTabHost.addTab(mTabHost.newTabSpec("TAG1").setIndicator("0").setContent(new Intent(this, WXEntryActivity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec("TAG2").setIndicator("1").setContent(new Intent(this, GetFromWXActivity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec("TAG3").setIndicator("2").setContent(new Intent(this, SendToWXActivity.class)));
//        mTabHost.addTab(mTabHost.newTabSpec("TAG4").setIndicator("3").setContent(new Intent(this, ShowFromWXActivity.class)));
        // 视觉上,用单选按钮替代TabWidget
        main_radio = (RadioGroup) findViewById(R.id.main_radio);
        tab_icon_weixin = (RadioButton) findViewById(R.id.tab_icon_weixin);
        tab_icon_address = (RadioButton) findViewById(R.id.tab_icon_address);
        tab_icon_find = (RadioButton) findViewById(R.id.tab_icon_find);
        tab_icon_myself = (RadioButton) findViewById(R.id.tab_icon_myself);
        main_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                if (id == tab_icon_weixin.getId()) {
                    mTabHost.setCurrentTab(0);
                } else if (id == tab_icon_address.getId()) {
                    mTabHost.setCurrentTab(1);
                } else if (id == tab_icon_find.getId()) {
                    mTabHost.setCurrentTab(2);
                } else if (id == tab_icon_myself.getId()) {
                    mTabHost.setCurrentTab(3);
                }
            }
        });

        // 设置当前显示哪一个标签
        mTabHost.setCurrentTab(0);
        // 遍历tabWidget每个标签，设置背景图片 无
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            View vv = tabWidget.getChildAt(i);
            vv.getLayoutParams().height = 45;
            /**设置tab中标题文字的颜色，不然默认为黑色 */
            final TextView tv = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(this.getResources().getColorStateList(android.R.color.white));
            // vv.getLayoutParams().width = 65;
            vv.setBackgroundDrawable(null);
        }
//		findViewById(R.id.tab_icon_brand).setOnClickListener(this);
    }
}
