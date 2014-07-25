package cn.ttyhuo.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.login.LoginRegGuideFragment;
import cn.ttyhuo.activity.product.MyFavoriteProductFragment;
import cn.ttyhuo.activity.product.MyProductFragment;
import cn.ttyhuo.activity.product.ProductListViewFragment;
import cn.ttyhuo.activity.product.UserProductFragment;
import cn.ttyhuo.activity.purchase.PurchaseContainerFragment;
import cn.ttyhuo.activity.purchase.PurchaseToContainerFragment;
import cn.ttyhuo.activity.user.MyFellowUserFragment;
import cn.ttyhuo.activity.user.UserFellowMeFragment;
import cn.ttyhuo.activity.user.UserListViewFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * 点击 追加数据的ListView
 *
 * @author 402-9
 */
public class MainPage extends SlidingFragmentActivity {

    private Fragment mContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_container);

        String contentFragment = getIntent().getStringExtra("contentFragment");
        boolean hasWindowTitle = getIntent().getBooleanExtra("hasWindowTitle", true);
        int extraID = getIntent().getIntExtra("extraID", 0);
        if(hasWindowTitle)
        {
            //设置是否显示Home图标按钮
            //getActionBar().setDisplayHomeAsUpEnabled(true);

            String windowTitle = getIntent().getStringExtra("windowTitle");
            if(windowTitle != null)
                getActionBar().setTitle(windowTitle);
            else
            {
                getActionBar().setTitle("天天有货");
            }
        }
        //else
            //requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (savedInstanceState != null)
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        if(mContent == null)
        {
            if(contentFragment == null)
                mContent = new LoginRegGuideFragment();
            else if(contentFragment.equals("UserListViewFragment"))
                mContent = new UserListViewFragment();
            else if(contentFragment.equals("MyFellowUserFragment"))
                mContent = new MyFellowUserFragment();
            else if(contentFragment.equals("UserFellowMeFragment"))
                mContent = new UserFellowMeFragment();
            else if(contentFragment.equals("ProductListViewFragment"))
                mContent = new ProductListViewFragment();
            else if(contentFragment.equals("MyFavoriteProductFragment"))
                mContent = new MyFavoriteProductFragment();
            else if(contentFragment.equals("MyProductFragment"))
                mContent = new MyProductFragment();
            else if(contentFragment.equals("UserProductFragment"))
                mContent = new UserProductFragment(extraID);
            else if(contentFragment.equals("PurchaseContainerFragment"))
                mContent = new PurchaseContainerFragment();
            else if(contentFragment.equals("PurchaseToContainerFragment"))
                mContent = new PurchaseToContainerFragment();
            else
                mContent = new LoginRegGuideFragment();

            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
        }

        initSlidingMenu(savedInstanceState);

    }

    /**
     * 初始化滑动菜单
     */
    private void initSlidingMenu(Bundle savedInstanceState) {

//        // check if the content frame contains the menu frame
//        if (findViewById(R.id.menu_frame) == null) {
//            setBehindContentView(R.layout.menu_frame);
//            getSlidingMenu().setSlidingEnabled(true);
//            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//            // show home as up so we can toggle
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        } else {
//            // add a dummy view
//            View v = new View(this);
//            setBehindContentView(v);
//            getSlidingMenu().setSlidingEnabled(false);
//            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//        }
        //设置是否能够使用ActionBar来滑动
        setSlidingActionBarEnabled(true);
//        // 设置当打开滑动菜单时，ActionBar不能够跟随着一起滑动
//        setSlidingActionBarEnabled(false);

        // 实例化滑动菜单对象
        SlidingMenu sm = getSlidingMenu();
        sm.setMode(SlidingMenu.RIGHT);

        // 设置滑动菜单的视图
        setBehindContentView(R.layout.menu_frame);
//        sm.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        sm.setMenu(R.layout.menu_frame);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new MenuListFragment()).commit();
        sm.setSecondaryMenu(R.layout.menu_frame_two);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, new LeftSlidingMenuFragment()).commit();

        // 设置滑动阴影的宽度
        sm.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动阴影的图像资源
        sm.setShadowDrawable(R.drawable.shadow);
        sm.setSecondaryShadowDrawable(R.drawable.shadowright);

        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        //sm.setBehindWidth(300);

        // 设置渐入渐出效果的值
        sm.setFadeEnabled(true);
        sm.setFadeDegree(0.35f);
        sm.setBehindScrollScale(0.35f);

        // 设置触摸屏幕的模式
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
    }

    /**
     * 菜单按钮点击事件，通过点击ActionBar的Home图标按钮来打开滑动菜单
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SlidingMenu sm = getSlidingMenu();
        //点击返回键关闭滑动菜单
        if (sm.isMenuShowing()) {
            sm.showContent();
        } else {
            super.onBackPressed();
        }
    }

    public void toggleRightMenu()
    {
        toggle();
    }

    /**
     * 切换Fragment，也是切换视图的内容
     */
    public void switchContent(Fragment fragment) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
        getSlidingMenu().showContent();

//        Handler h = new Handler();
//        h.postDelayed(new Runnable() {
//            public void run() {
//                getSlidingMenu().showContent();
//            }
//        }, 50);
    }

    /**
     * 保存Fragment的状态
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }
}