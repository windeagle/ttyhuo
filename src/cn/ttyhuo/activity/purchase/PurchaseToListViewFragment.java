package cn.ttyhuo.activity.purchase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseListFragment;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.view.PurchaseToListJSONArrayAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-11
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseToListViewFragment extends BaseListFragment {

    private PurchaseToListJSONArrayAdapter mJson;

    private int pageFlag = 0;

    public PurchaseToListViewFragment() {
        this(0);
    }

    public PurchaseToListViewFragment(int pageFlag) {
        this.pageFlag = pageFlag;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            pageFlag = savedInstanceState.getInt("pageFlag");
        return super.onCreateView(inflater,container, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("pageFlag", pageFlag);
    }

    protected int getLayoutID()
    {
        return R.layout.fragmment_purchase_list;
    }

    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/purchases_to_userJson");
    }

    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_purchaseList);
        ViewGroup parent = (ViewGroup) lv.getParent();
        // Remove ListView and add PullToRefreshListView in its place
        int lvIndex = parent.indexOfChild(lv);
        parent.removeViewAt(lvIndex);
        mListView = new PullToRefreshListView(mContext);
        parent.addView(mListView, lvIndex, lv.getLayoutParams());
        lv = mListView.getRefreshableView();
        return lv;
    }

    protected View getEmptyView()
    {
        View view_page_footer = LayoutInflater.from(getActivity()).inflate(R.layout.view_page_footer, null);
        TextView text_page = (TextView) view_page_footer.findViewById(R.id.text_page);
        if(pageFlag == 0)
            text_page.setText("您没有等待接受的召车单！");
        else if(pageFlag == 1)
            text_page.setText("您没有等待确认的召车单！");
        else if(pageFlag == 2)
            text_page.setText("您没有历史召车单！");
        return view_page_footer;
    }

    protected Map<String, String> geParams()
    {
        Map<String, String> ret = new HashMap<String, String>();

        ret.put("pageFlag", String.valueOf(pageFlag));

        return ret;
    }

    protected void processJSON(String result)
    {
        try {
            JSONArray object = new JSONObject(result).getJSONArray("purchases");

            JSONArray mDataDelta = object;//.get("items");
            //totalPage = 3;//(Integer) object.get("totalPage");
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new PurchaseToListJSONArrayAdapter(mData, getActivity());
                mListView.setAdapter(mJson);//为ListView绑定适配器
            }
            else if (page == 0)
            {
                mData = mDataDelta;
                mJson.setList(mData);
                mJson.notifyDataSetChanged();
            } else {
                mData = JSONUtil.joinJSONArray(mData, mDataDelta);
                mJson.setList(mData);
                mJson.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void processItemClick(JSONObject jsonObject)
    {
//        Context mContext = getActivity();
//        Intent intent = new Intent(mContext, UserInfoActivity.class);
//        try{
//            intent.putExtra("userID", jsonObject.getJSONObject("userWithLatLng").getInt("userID"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        mContext.startActivity(intent);
    }
}
