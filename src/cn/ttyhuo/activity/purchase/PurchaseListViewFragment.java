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
import cn.ttyhuo.view.PurchaseListJSONArrayAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-11
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public class PurchaseListViewFragment extends BaseListFragment {

    private PurchaseListJSONArrayAdapter mJson;

    private int pageFlag = 0;

    public PurchaseListViewFragment() {
        this(0);
    }

    public PurchaseListViewFragment(int pageFlag) {
        this.pageFlag = pageFlag;
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            pageFlag = savedInstanceState.getInt("pageIndex");
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
        return new String(UrlList.MAIN + "mvc/user_purchasesJson?all=0&pageFlag=" + pageFlag);
    }

    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_purchaseList);
        return lv;
    }

    protected View getEmptyView()
    {
        View view_page_footer = LayoutInflater.from(getActivity()).inflate(R.layout.view_page_footer, null);
        TextView text_page = (TextView) view_page_footer.findViewById(R.id.text_page);
        if(pageFlag == 0)
            text_page.setText("您没有等待货主接受的接货单！");
        else if(pageFlag == 1)
            text_page.setText("您没有等待货主确认的接货单！");
        else if(pageFlag == 2)
            text_page.setText("您没有历史接货单！");
        return view_page_footer;
    }

    protected void processJSON(String result)
    {
        try {
            JSONArray object = new JSONObject(result).getJSONArray("purchases");

            JSONArray mDataDelta = object;//.get("items");
            totalPage = 3;//(Integer) object.get("totalPage");
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new PurchaseListJSONArrayAdapter(mData, getActivity());
                lv.setAdapter(mJson);//为ListView绑定适配器
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
