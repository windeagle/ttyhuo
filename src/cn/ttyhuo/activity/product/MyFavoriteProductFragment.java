package cn.ttyhuo.activity.product;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseListFragment;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.view.ProductListJSONArrayAdapter;
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
public class MyFavoriteProductFragment extends BaseListFragment {

    private ProductListJSONArrayAdapter mJson;

    protected int getLayoutID()
    {
        return R.layout.product_list;
    }

    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/mineFavoriteProductJson");
    }

    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_productList);
        return lv;
    }

    protected View getEmptyView()
    {
        View view_page_footer = LayoutInflater.from(getActivity()).inflate(R.layout.view_page_footer, null);
        TextView text_page = (TextView) view_page_footer.findViewById(R.id.text_page);
        text_page.setText("您没有收藏的货源！");
        return view_page_footer;
    }

    protected void processJSON(String result)
    {
        try {
            JSONArray object = new JSONObject(result).getJSONArray("productList");

            JSONArray mDataDelta = object;//.get("items");
            totalPage = 3;//(Integer) object.get("totalPage");
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new ProductListJSONArrayAdapter(mData, getActivity());
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
        Context mContext = getActivity();
        Intent intent = new Intent(mContext, ProductInfoActivity.class);
        try{
            JSONObject jObject = jsonObject.has("product") ? jsonObject.getJSONObject("product") : jsonObject;
            intent.putExtra("productID", jObject.getInt("productID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext.startActivity(intent);
    }
}