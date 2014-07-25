package cn.ttyhuo.activity.base;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.utils.UrlThread;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-2
 * Time: 下午1:39
 * To change this template use File | Settings | File Templates.
 */
public class BaseListFragment extends Fragment {

    protected int page = 0;// 加载页码
    protected JSONArray mData = new JSONArray();// JSON数据源
    protected int totalPage;// 总数据
    private TextView text_page;
    private ProgressBar progressBar;
    protected Activity mContext;
    protected ListView lv;
    protected PullToRefreshListView mListView;

    protected int getLayoutID()
    {
         return 0;
    }

    protected String getUrl()
    {
        return null;
    }

    protected Map<String, String> geParams()
    {
        return null;
    }

    protected ListView getListView()
    {
        return null;
    }

    protected void processJSON(String result)
    {
        return;
    }

    protected void processItemClick(JSONObject jsonObject)
    {
        return;
    }

    protected View getEmptyView()
    {
        return null;
    }

    protected void initView()
    {
        return;
    }

    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(progressBar != null)
                progressBar.setVisibility(View.GONE);

            if(lv.getEmptyView() == null)
            {
                View emptyView = getEmptyView();
//            if(mListView != null)
//                mListView.setEmptyView(emptyView);
//            else
//            {
                emptyView.setVisibility(View.GONE);
                ((ViewGroup)lv.getParent()).addView(emptyView);
                lv.setEmptyView(emptyView);
            //}
            }
            else
            {
                lv.getEmptyView().setVisibility(View.GONE);
            }

            String result = msg.obj.toString();
            processJSON(result);
            text_page.setText("下一页");
            if(mListView != null)
                mListView.onRefreshComplete();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        return inflater.inflate(getLayoutID(), null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();

        getListView();

        progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
        if(progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        new UrlThread(handler, getUrl(), geParams(), 1).start();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    JSONObject jsonObject = (JSONObject) mData.get(mListView != null ? (position - 1) : position);
                    processItemClick(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(mListView != null)
        {
            mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
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
                    page = 0;
                    new UrlThread(handler, getUrl(), geParams(), 1).start();
                }
            });

            mListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
                @Override
                public void onLastItemVisible() {
                    page++;
                    text_page.setText("正在加载中...");

                    Map<String, String> params = geParams();
                    params.put("pageIndex", String.valueOf(page));
                    new UrlThread(handler, getUrl(), params, 1).start();
                }
            });
        }


        View view_page_footer = LayoutInflater.from(getActivity()).inflate(R.layout.view_page_footer, null);
        //lv.addFooterView(view_page_footer);// 添加底部视图
        text_page = (TextView) view_page_footer.findViewById(R.id.text_page);
        text_page.setOnClickListener(new View.OnClickListener() {
            // 点击按钮 追加数据 并通知适配器
            @Override
            public void onClick(View v) {
                page++;
                text_page.setText("正在加载中...");

                Map<String, String> params = geParams();
                params.put("pageIndex", String.valueOf(page));
                new UrlThread(handler, getUrl(), params, 1).start();
            }
        });
    }
}