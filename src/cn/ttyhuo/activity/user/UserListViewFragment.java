package cn.ttyhuo.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseListFragment;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import cn.ttyhuo.view.UserListJSONArrayAdapter;
import com.baidu.location.BDLocation;
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
public class UserListViewFragment extends BaseListFragment {

    private UserListJSONArrayAdapter mJson;

    final CharSequence items[] = new CharSequence[] { "全部", "短途小货", "普卡", "平板", "高栏",
            "厢式", "集装箱", "全封闭", "特种", "危险", "自卸", "冷藏", "保温", "沙石",
            "其他" };
    boolean truckTypeFlags[] = new boolean[] { true, false, false, false, false, false, false, false, false, false, false, false, false, false};

    final CharSequence loadLimitItems[] = new CharSequence[] { "全部", "0～5吨", "5～10吨", "10～20吨",
            "20～30吨", "30～40吨", "40～50吨", ">50吨", "不限" };
    boolean loadLimitFlags[] = new boolean[] { true, false, false, false, false, false, false, false, false};

    final CharSequence driverAgeItems[] = new CharSequence[] { "全部", "一年以内", "1～3年", "3～8年",
            "8～15年", ">15年", "不限" };
    boolean driverAgeFlags[] = new boolean[] { true, false, false, false, false, false, false};

    final CharSequence truckLengthItems[] = new CharSequence[] { "全部", "1～5米", "5～10米",
            "10～15米", ">15米", "不限" };
    boolean truckLengthFlags[] = new boolean[] { true, false, false, false, false, false};

    final CharSequence orderByItems[] = new CharSequence[] { "综合", "距离", "最后在线时间",
            "车主等级", "最新注册" };
    int orderFlag = 0;

    private TextView tv_more, tv_search;//更多
    private LinearLayout ly_vehicle_type, ly_loadLimit,  ly_driverAge, ly_truckLength, ly_orderFlag;  //车型   载重  驾龄
    private TextView ly_vehicle_type_text, ly_loadLimit_text, ly_driverAge_text, ly_truckLength_text, ly_orderFlag_text;
    //private Dialog dialog;
    private ImageView iv_search;
    private LinearLayout detail;
    private Button tv_btn_do ,tv_btn_reset;

    protected int getLayoutID()
    {
        return R.layout.user_search;
    }

    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/searchUsers");
    }

    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_userList);
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
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new UserListJSONArrayAdapter(mData, mContext);
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
        tv_more=(TextView)mContext.findViewById(R.id.tv_more);
        tv_search=(TextView)mContext.findViewById(R.id.tv_search);
        detail=(LinearLayout)mContext.findViewById(R.id.detail);

        ly_vehicle_type=(LinearLayout)mContext.findViewById(R.id.ly_vehicle_type);
        ly_loadLimit=(LinearLayout)mContext.findViewById(R.id.ly_loadLimit);
        ly_driverAge=(LinearLayout)mContext.findViewById(R.id.ly_driverAge);
        ly_truckLength=(LinearLayout)mContext.findViewById(R.id.ly_truckLength);
        ly_orderFlag=(LinearLayout)mContext.findViewById(R.id.ly_orderFlag);

        ly_vehicle_type_text=(TextView)mContext.findViewById(R.id.ly_vehicle_type_text);
        ly_loadLimit_text=(TextView)mContext.findViewById(R.id.ly_loadLimit_text);
        ly_driverAge_text=(TextView)mContext.findViewById(R.id.ly_driverAge_text);
        ly_truckLength_text=(TextView)mContext.findViewById(R.id.ly_truckLength_text);
        ly_orderFlag_text=(TextView)mContext.findViewById(R.id.ly_orderFlag_text);

        tv_btn_do=(Button)mContext.findViewById(R.id.tv_btn_do);
        tv_btn_reset =(Button)mContext.findViewById(R.id.tv_btn_reset);
        iv_search =(ImageView)mContext.findViewById(R.id.iv_search);

        ly_vehicle_type.setOnClickListener(theClick);
        ly_loadLimit.setOnClickListener(theClick);
        ly_driverAge.setOnClickListener(theClick);
        ly_truckLength.setOnClickListener(theClick);
        ly_orderFlag.setOnClickListener(theClick);

        tv_more.setOnClickListener(theClick);
        tv_btn_do.setOnClickListener(theClick);
        tv_btn_reset.setOnClickListener(theClick);
        iv_search.setOnClickListener(theClick);
    }

    View.OnClickListener theClick = new View.OnClickListener() {
        // 点击按钮 追加数据 并通知适配器
        @Override
        public void onClick(View v){
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
            switch (v.getId()) {
                case R.id.ly_vehicle_type:
                    Dialog cDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "选择车型",
                            items,
                            truckTypeFlags,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_vehicle_type_text, items, truckTypeFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    cDialog.show();
                    break;
                case R.id.ly_loadLimit:
                    Dialog lDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "选择载重",
                            loadLimitItems,
                            loadLimitFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_loadLimit_text, loadLimitItems, loadLimitFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    lDialog.show();
                    break;
                case R.id.ly_driverAge:
                    Dialog pDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "选择驾龄",
                            driverAgeItems,
                            driverAgeFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_driverAge_text, driverAgeItems, driverAgeFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    pDialog.show();
                    break;
                case R.id.ly_truckLength:
                    Dialog dDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "选择车长",
                            truckLengthItems,
                            truckLengthFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_truckLength_text, truckLengthItems, truckLengthFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    dDialog.show();
                    break;
                case R.id.ly_orderFlag:
                    Dialog oDialog = DialogUtils.createRadioDialog(mContext,
                            R.drawable.icon_search,
                            "排序选项",
                            orderByItems, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            orderFlag = which;
                            ly_orderFlag_text.setText(orderByItems[which]);
                        }
                    }, "确定", null);
                    oDialog.show();
                    break;
                case R.id.tv_btn_reset:
                    CommonUtils.processCheckBoxList(ly_driverAge_text, driverAgeItems, driverAgeFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_loadLimit_text, loadLimitItems, loadLimitFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_truckLength_text, truckLengthItems, truckLengthFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_vehicle_type_text, items, truckTypeFlags, 0, true);
                    orderFlag = 0;
                    ly_orderFlag_text.setText(orderByItems[0]);
                    break;
                case R.id.tv_btn_do:
                case R.id.iv_search:
                    if(progressBar != null)
                        progressBar.setVisibility(View.VISIBLE);
                    detail.setVisibility(View.GONE);
                    mJson = null;
                    if(!NetworkUtils.isNetworkAvailable(mContext))
                    {
                        Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
                    }
                    new UrlThread(handler, getUrl(), geParams(), 1).start();
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

    @Override
    protected Map<String, String> geParams()
    {
        Map<String, String> ret = new HashMap<String, String>();

        //ret.put("onlyDriver", "true");
        ret.put("orderFlag", String.valueOf(orderFlag));
        CommonUtils.setOptionsPostStr(ret, "truckTypeFlags", truckTypeFlags);
        CommonUtils.setOptionsPostStr(ret, "loadLimitFlags", loadLimitFlags);
        CommonUtils.setOptionsPostStr(ret, "truckLengthFlags", truckLengthFlags);
        CommonUtils.setOptionsPostStr(ret, "driverAgeFlags", driverAgeFlags);

        ret.put("searchStr", tv_search.getText().toString());
        ret.put("searchStrFlags", "1,2,3,4,5,6,7,8,9,10,11,12");

        final BDLocation location = ((MyApplication)mContext.getApplication()).nowLocation;
        if(location != null)
        {
            ret.put("lat", String.valueOf(location.getLatitude()));
            ret.put("lng", String.valueOf(location.getLongitude()));
        }

        return ret;
    }
}
