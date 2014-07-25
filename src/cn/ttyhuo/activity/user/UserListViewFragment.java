package cn.ttyhuo.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.DialogActivity;
import cn.ttyhuo.activity.base.BaseListFragment;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.JSONUtil;
import cn.ttyhuo.view.UserListJSONArrayAdapter;
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
public class UserListViewFragment extends BaseListFragment {

    private UserListJSONArrayAdapter mJson;

    private TextView tv_more;//更多
    private LinearLayout ly_vehicle_type, ly_loadLimit,  ly_driveAge; //车型   载重  驾龄
    private Dialog dialog;
    private LinearLayout detail;
    private Spinner sp_start_city ,sp_end_city;//城市下拉列表

    protected int getLayoutID()
    {
        return R.layout.user_list;
    }

    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/getAllUsers");
    }

    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_userList);
        return lv;
    }

    protected View getEmptyView()
    {
        View view_page_footer = LayoutInflater.from(getActivity()).inflate(R.layout.view_page_footer, null);
        TextView text_page = (TextView) view_page_footer.findViewById(R.id.text_page);
        text_page.setText("对不起，没找到用户！");
        return view_page_footer;
    }

    protected void processJSON(String result)
    {
        try {
            JSONArray object = new JSONArray(result);

            JSONArray mDataDelta = object;//.get("items");
            totalPage = 3;//(Integer) object.get("totalPage");
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new UserListJSONArrayAdapter(mData, getActivity());
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
        Intent intent = new Intent(mContext, UserInfoActivity.class);
        try{
            intent.putExtra("userID", jsonObject.getJSONObject("userWithLatLng").getInt("userID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext.startActivity(intent);
    }

    @Override
    protected void initView(){
        Activity mContext = getActivity();
        dialog=new DialogActivity(mContext);
        tv_more=(TextView)mContext.findViewById(R.id.tv_more);
        ly_vehicle_type=(LinearLayout)mContext.findViewById(R.id.ly_vehicle_type);
        ly_loadLimit=(LinearLayout)mContext.findViewById(R.id.ly_loadLimit);
        ly_driveAge=(LinearLayout)mContext.findViewById(R.id.ly_driveAge);
        detail=(LinearLayout)mContext.findViewById(R.id.detail);
        sp_start_city=(Spinner)mContext.findViewById(R.id.sp_start_city);
        sp_end_city=(Spinner)mContext.findViewById(R.id.sp_end_city);

        tv_more.setOnClickListener(theClick);
        ly_vehicle_type.setOnClickListener(theClick);
        ly_loadLimit.setOnClickListener(theClick);
        ly_driveAge.setOnClickListener(theClick);
    }

    View.OnClickListener theClick = new View.OnClickListener() {
        // 点击按钮 追加数据 并通知适配器
        @Override
        public void onClick(View v){
            switch (v.getId()) {
                case R.id.ly_vehicle_type:
                    dialog.show();
                    break;
                case R.id.ly_loadLimit:
                    dialog.show();
                    break;
                case R.id.ly_driveAge:
                    dialog.show();
                    break;

                case R.id.tv_more:
                    if(View.VISIBLE==detail.getVisibility()){
                        detail.setVisibility(View.GONE);
                    }else{
                        detail.setVisibility(View.VISIBLE);
                    }

                    break;

                default:
                    break;
            }
        }
    };
}
