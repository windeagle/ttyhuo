package cn.ttyhuo.activity.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ttyhuo.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-10
 * Time: 上午5:08
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseToContainerFragment extends Fragment {

    private TextView tv_page0;
    private TextView tv_page1;
    private TextView tv_page2;

    private int pageIndex = 0;

    public PurchaseToContainerFragment() {
        this(0);
    }

    public PurchaseToContainerFragment(int pageIndex) {
        this.pageIndex = pageIndex;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            pageIndex = savedInstanceState.getInt("pageIndex");

        View v = inflater.inflate(R.layout.fragment_purchaseto_container, null);

        final LinearLayout lv_page0 = (LinearLayout)v.findViewById(R.id.lv_page0);
        final LinearLayout lv_page1 = (LinearLayout)v.findViewById(R.id.lv_page1);
        final LinearLayout lv_page2 = (LinearLayout)v.findViewById(R.id.lv_page2);
        lv_page0.setBackgroundResource(R.color.activity_footer_bg);
        lv_page1.setBackgroundResource(R.color.activity_header_bg);
        lv_page2.setBackgroundResource(R.color.activity_header_bg);

        tv_page0 = (TextView)v.findViewById(R.id.tv_page0);
        tv_page1 = (TextView)v.findViewById(R.id.tv_page1);
        tv_page2 = (TextView)v.findViewById(R.id.tv_page2);

        tv_page0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseToContainerFragment.this.pageIndex = 0;
                lv_page0.setBackgroundResource(R.color.activity_footer_bg);
                lv_page1.setBackgroundResource(R.color.activity_header_bg);
                lv_page2.setBackgroundResource(R.color.activity_header_bg);
                Fragment tmpContent = new PurchaseToListViewFragment(PurchaseToContainerFragment.this.pageIndex);
                PurchaseToContainerFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tabcontent, tmpContent).commit();
            }
        });
        tv_page1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseToContainerFragment.this.pageIndex = 1;
                lv_page0.setBackgroundResource(R.color.activity_header_bg);
                lv_page1.setBackgroundResource(R.color.activity_footer_bg);
                lv_page2.setBackgroundResource(R.color.activity_header_bg);
                Fragment tmpContent = new PurchaseToListViewFragment(PurchaseToContainerFragment.this.pageIndex);
                PurchaseToContainerFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tabcontent, tmpContent).commit();
            }
        });
        tv_page2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurchaseToContainerFragment.this.pageIndex = 2;
                lv_page0.setBackgroundResource(R.color.activity_header_bg);
                lv_page1.setBackgroundResource(R.color.activity_header_bg);
                lv_page2.setBackgroundResource(R.color.activity_footer_bg);
                Fragment tmpContent = new PurchaseToListViewFragment(PurchaseToContainerFragment.this.pageIndex);
                PurchaseToContainerFragment.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tabcontent, tmpContent).commit();
            }
        });
        Fragment mContent = new PurchaseToListViewFragment(pageIndex);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.tabcontent, mContent).commit();
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageIndex", pageIndex);
    }
}
