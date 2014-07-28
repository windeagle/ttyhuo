package cn.ttyhuo.activity.purchase;

import android.view.ViewGroup;
import android.widget.ListView;
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
public class PurchaseCurToListViewFragment extends BaseListFragment {

    private PurchaseToListJSONArrayAdapter mJson;

    protected int getLayoutID()
    {
        return R.layout.fragmment_purchase_list;
    }

    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/cur_purchases_to_userJson");
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
        lv.setDividerHeight(0);
        return lv;
    }

    protected Map<String, String> geParams()
    {
        Map<String, String> ret = new HashMap<String, String>();

        ret.put("all", "1");

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
            }
            else {
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
