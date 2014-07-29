package cn.ttyhuo.activity.product;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.base.BaseListFragment;
import cn.ttyhuo.adapter.CityListAdapter;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.helper.CityList;
import cn.ttyhuo.utils.*;
import cn.ttyhuo.view.ProductListJSONArrayAdapter;
import com.baidu.location.BDLocation;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-11
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public class ProductListViewFragment extends BaseListFragment {

    private ProductListJSONArrayAdapter mJson;
    final CharSequence items[] = new CharSequence[] { "全部", "短途小货", "普卡", "平板", "高栏",
            "厢式", "集装箱", "全封闭", "特种", "危险", "自卸", "冷藏", "保温", "沙石",
            "其他" };
    boolean truckTypeFlags[] = new boolean[] { true, false, false, false, false, false, false, false, false, false, false, false, false, false};

    final CharSequence loadLimitItems[] = new CharSequence[] { "全部", "0～5吨", "5～10吨", "10～20吨",
            "20～30吨", "30～40吨", "40～50吨", ">50吨", "不限" };
    boolean loadLimitFlags[] = new boolean[] { true, false, false, false, false, false, false, false, false};

    final CharSequence departureTimeItems[] = new CharSequence[] { "全部", "1小时内", "1～3小时内", "3小时～1天内",
            "1～3天内", "3天后", "不限" };
    boolean departureTimeFlags[] = new boolean[] { true, false, false, false, false, false, false};

    final CharSequence providerTypeItems[] = new CharSequence[] { "全部", "未认证用户", "认证个人", "物流货运公司",
            "其他行业公司" };
    boolean providerTypeFlags[] = new boolean[] { true, false, false, false, false};

    final CharSequence openTimeItems[] = new CharSequence[] { "不限", "5分钟内", "30分钟内", "3小时内",
            "1天内", "3天内" };
    int openTimeFlag = 0;

    final CharSequence orderByItems[] = new CharSequence[] { "综合", "距离", "发布时间",
             "货主等级", "运价", "剩余需求车辆数"};
    int orderFlag = 0;

    @Override
    protected Map<String, String> geParams()
    {
        Map<String, String> ret = new HashMap<String, String>();

        String[] cityArr = tv_start_city.getText().toString().split(" ");
        if(cityArr.length > 0)
            ret.put("fromProvince", cityArr[0]);
        if(cityArr.length > 1)
            ret.put("fromCity", cityArr[1]);
        if(cityArr.length > 2)
            ret.put("fromCity", cityArr[1] + " " + cityArr[2]);


        cityArr = tv_end_city.getText().toString().split(" ");
        if(cityArr.length > 0)
            ret.put("toProvince", cityArr[0]);
        if(cityArr.length > 1)
            ret.put("toCity", cityArr[1]);
        if(cityArr.length > 2)
            ret.put("toCity", cityArr[1] + " " + cityArr[2]);

        ret.put("orderFlag", String.valueOf(orderFlag));
        ret.put("windowOpenTimeFlag", String.valueOf(openTimeFlag));
        CommonUtils.setOptionsPostStr(ret, "truckTypeFlags", truckTypeFlags);
        CommonUtils.setOptionsPostStr(ret, "loadLimitFlags", loadLimitFlags);
        CommonUtils.setOptionsPostStr(ret, "userTypeFlags", providerTypeFlags);
        CommonUtils.setOptionsPostStr(ret, "departureTimeFlags", departureTimeFlags);

        ret.put("searchStr", tv_search.getText().toString());
        ret.put("searchStrFlags", "1,2,3,4,5,6,7,8,9,10,11,12,13");

        final BDLocation location = ((MyApplication)mContext.getApplication()).nowLocation;
        if(location != null)
        {
            ret.put("lat", String.valueOf(location.getLatitude()));
            ret.put("lng", String.valueOf(location.getLongitude()));
        }

        return ret;
    }

    private TextView tv_more, tv_search;//更多
    private LinearLayout ly_start_city ,ly_end_city, ly_vehicle_type, ly_loadLimit,  ly_providerType, ly_departureTime, ly_windowOpenTime, ly_orderFlag; //车型   载重  驾龄
    private TextView ly_vehicle_type_text, ly_loadLimit_text,  ly_providerType_text, ly_departureTime_text, ly_windowOpenTime_text, ly_orderFlag_text; //车型   载重  驾龄
    //private Dialog dialog;
    private LinearLayout detail;
    private ImageView iv_search,iv_start_city ,iv_end_city;//城市下拉列表
    private EditText tv_start_city ,tv_end_city;//城市下拉列表
    private Button tv_btn_do ,tv_btn_reset;//城市下拉列表

    @Override
    protected int getLayoutID()
    {
        return R.layout.product_search;
    }

    @Override
    protected String getUrl()
    {
        return new String(UrlList.MAIN + "mvc/searchProducts");
    }

    @Override
    protected ListView getListView()
    {
        lv = (ListView) getView().findViewById(R.id.lv_productList);
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
        text_page.setText("对不起，没找到货源！");
        return view_page_footer;
    }

    @Override
    protected void processJSON(String result)
    {
        try {
            JSONArray object = new JSONArray(result);

            JSONArray mDataDelta = object;//.get("items");
            //totalPage = (Integer) object.get("totalPage");
            if (mJson == null) {
                mData = mDataDelta;
                mJson = new ProductListJSONArrayAdapter(mData, mContext);
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

    @Override
    protected void processItemClick(JSONObject jsonObject)
    {
        Intent intent = new Intent(mContext, ProductInfoActivity.class);
        try{
            JSONObject jObject = jsonObject.has("product") ? jsonObject.getJSONObject("product") : jsonObject;
            intent.putExtra("productID", jObject.getInt("productID"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mContext.startActivity(intent);
    }

    @Override
    protected void initView(){
        //dialog=new DialogActivity(mContext);
        tv_more=(TextView)mContext.findViewById(R.id.tv_more);
        tv_search=(TextView)mContext.findViewById(R.id.tv_search);
        ly_vehicle_type=(LinearLayout)mContext.findViewById(R.id.ly_vehicle_type);
        ly_loadLimit=(LinearLayout)mContext.findViewById(R.id.ly_loadLimit);
        ly_providerType=(LinearLayout)mContext.findViewById(R.id.ly_providerType);
        ly_departureTime=(LinearLayout)mContext.findViewById(R.id.ly_departureTime);
        ly_windowOpenTime=(LinearLayout)mContext.findViewById(R.id.ly_windowOpenTime);
        ly_orderFlag=(LinearLayout)mContext.findViewById(R.id.ly_orderFlag);

        ly_vehicle_type_text=(TextView)mContext.findViewById(R.id.ly_vehicle_type_text);
        ly_loadLimit_text=(TextView)mContext.findViewById(R.id.ly_loadLimit_text);
        ly_providerType_text=(TextView)mContext.findViewById(R.id.ly_providerType_text);
        ly_departureTime_text=(TextView)mContext.findViewById(R.id.ly_departureTime_text);
        ly_windowOpenTime_text=(TextView)mContext.findViewById(R.id.ly_windowOpenTime_text);
        ly_orderFlag_text=(TextView)mContext.findViewById(R.id.ly_orderFlag_text);

        detail=(LinearLayout)mContext.findViewById(R.id.detail);
        ly_start_city=(LinearLayout)mContext.findViewById(R.id.ly_start_city);
        ly_end_city=(LinearLayout)mContext.findViewById(R.id.ly_end_city);
        tv_start_city=(EditText)mContext.findViewById(R.id.tv_start_city);
        tv_end_city=(EditText)mContext.findViewById(R.id.tv_end_city);
        iv_start_city=(ImageView)mContext.findViewById(R.id.iv_start_city);
        iv_end_city=(ImageView)mContext.findViewById(R.id.iv_end_city);

        tv_btn_do=(Button)mContext.findViewById(R.id.tv_btn_do);
        tv_btn_reset =(Button)mContext.findViewById(R.id.tv_btn_reset);
        iv_search =(ImageView)mContext.findViewById(R.id.iv_search);

        tv_more.setOnClickListener(theClick);
        ly_vehicle_type.setOnClickListener(theClick);
        ly_loadLimit.setOnClickListener(theClick);
        ly_providerType.setOnClickListener(theClick);
        ly_departureTime.setOnClickListener(theClick);
        ly_windowOpenTime.setOnClickListener(theClick);
        ly_orderFlag.setOnClickListener(theClick);

        ly_start_city.setOnClickListener(theClick);
        ly_end_city.setOnClickListener(theClick);
        iv_start_city.setOnClickListener(theClick);
        iv_end_city.setOnClickListener(theClick);
        tv_start_city.setOnClickListener(theClick);
        tv_end_city.setOnClickListener(theClick);

        tv_btn_do.setOnClickListener(theClick);
        tv_btn_reset.setOnClickListener(theClick);
        iv_search.setOnClickListener(theClick);

        initPopup();
    }

    View.OnClickListener theClick = new View.OnClickListener() {
        // 点击按钮 追加数据 并通知适配器
        @Override
        public void onClick(View v){
            ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar1);
            switch (v.getId()) {
                case R.id.ly_start_city:
                case R.id.tv_start_city:
                case R.id.iv_start_city:
                    showPopup(tv_start_city);
                    break;
                case R.id.ly_end_city:
                case R.id.tv_end_city:
                case R.id.iv_end_city:
                    showPopup(tv_end_city);
                    break;
                case R.id.ly_vehicle_type:
                    Dialog cDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "选择车型",
                            items,
                            truckTypeFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
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
                case R.id.ly_providerType:
                    Dialog pDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "货主类型",
                            providerTypeItems,
                            providerTypeFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_providerType_text, providerTypeItems, providerTypeFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    pDialog.show();
                    break;
                case R.id.ly_departureTime:
                    Dialog dDialog = DialogUtils.createCheckBoxDialog(mContext,
                            R.drawable.icon_search,
                            "提货时间",
                            departureTimeItems,
                            departureTimeFlags,
                            new DialogInterface.OnMultiChoiceClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                    CommonUtils.processCheckBoxList(ly_departureTime_text, departureTimeItems, departureTimeFlags, which, isChecked);
                                }
                            }, "确定", null, "取消", null);
                    dDialog.show();
                    break;
                case R.id.ly_windowOpenTime:
                    Dialog wDialog = DialogUtils.createRadioDialog(mContext,
                            R.drawable.icon_search,
                            "发布时间",
                            openTimeItems, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openTimeFlag = which;
                            ly_windowOpenTime_text.setText(openTimeItems[which]);
                        }
                    }, "确定", null);
                    wDialog.show();
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
                    CommonUtils.processCheckBoxList(ly_providerType_text, providerTypeItems, providerTypeFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_loadLimit_text, loadLimitItems, loadLimitFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_departureTime_text, departureTimeItems, departureTimeFlags, 0, true);
                    CommonUtils.processCheckBoxList(ly_vehicle_type_text, items, truckTypeFlags, 0, true);
                    openTimeFlag = 0;
                    ly_windowOpenTime_text.setText(openTimeItems[0]);
                    orderFlag = 0;
                    ly_orderFlag_text.setText(orderByItems[0]);
                    tv_start_city.setText("");
                    tv_end_city.setText("");
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

    PopupWindow mPopupWindow;
    View popupView;

    ListView mProvinceList;
    ListView mCityList;
    ListView mCountyList;

    CityListAdapter mProvinceAdapter;
    CityListAdapter mCityAdapter;
    CityListAdapter mCountyAdapter;

    List<String> mProvinceData;
    List<String> mCityData;
    List<String> mCountyData;

    TextView currentCityTextView;
    String mCheckProvince;
    String mCheckCity;

    private void initPopup() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        popupView = inflater.inflate(R.layout.popup_hometown, null, false);
        mPopupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mProvinceList = (ListView) popupView.findViewById(R.id.province);
        mCityList = (ListView) popupView.findViewById(R.id.city);
        mCountyList = (ListView) popupView.findViewById(R.id.county);

        mProvinceData = CityList.getProvinceData();
        mCityData = CityList.getCity(mProvinceData.get(0));
        mCheckProvince = mProvinceData.get(0);
        mCountyData = CityList.getCounty(mProvinceData.get(0), mCityData.get(0));
        mCheckCity = mCityData.get(0);

        mProvinceAdapter = new CityListAdapter(mContext, mProvinceData);
        mCityAdapter = new CityListAdapter(mContext, mCityData);
        mCountyAdapter = new CityListAdapter(mContext, mCountyData);

        mProvinceList.setAdapter(mProvinceAdapter);
        mCityList.setAdapter(mCityAdapter);
        mCountyList.setAdapter(mCountyAdapter);

        mProvinceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mCheckProvince = mProvinceData.get(arg2);
                mCityData = CityList.getCity(mCheckProvince);
                mCityAdapter.updateData(mCityData);
                mCountyData = CityList.getCounty(mCheckProvince, mCityData.get(0));
                mCountyAdapter.updateData(mCountyData);
                mCheckCity = mCityData.get(0);
            }
        });

        mCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mCheckCity = mCityData.get(arg2);
                mCountyData = CityList.getCounty(mCheckProvince, mCheckCity);
                mCountyAdapter.updateData(mCountyData);
            }
        });

        mCountyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String showCity = null;
                String county = mCountyData.get(arg2);

                if (!county.equals("不限"))
                {
                    showCity = mCheckProvince + " " + mCheckCity + " " + county;
                }
                else if (!mCheckCity.equals("不限"))
                {
                    showCity = mCheckProvince + " " + mCheckCity;
                }
                else if(!mCheckProvince.equals("不限"))
                {
                    showCity = mCheckProvince;
                }

                if(showCity != null)
                    currentCityTextView.setText(showCity);
                dismissPopup();
            }
        });
    }

    private void showPopup(TextView cityTextView) {
        mPopupWindow.showAtLocation(mContext.findViewById(R.id.root), Gravity.CENTER, 0,
                0);
        currentCityTextView = cityTextView;
    }

    private void dismissPopup() {
        mPopupWindow.dismiss();
    }
}