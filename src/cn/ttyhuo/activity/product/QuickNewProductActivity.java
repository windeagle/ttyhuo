package cn.ttyhuo.activity.product;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import baidumapsdk.demo.LocationDemo;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.MainPage;
import cn.ttyhuo.activity.base.BaseAddPicActivity;
import cn.ttyhuo.common.AddressData;
import cn.ttyhuo.common.ConstHolder;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.helper.DateTimeOnClickListener;
import cn.ttyhuo.utils.DialogUtils;
import cn.ttyhuo.utils.HttpRequestUtil;
import cn.ttyhuo.utils.LogUtils;
import cn.ttyhuo.utils.StringUtils;
import com.baidu.location.BDLocation;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-13
 * Time: 上午9:22
 * To change this template use File | Settings | File Templates.
 */
public class QuickNewProductActivity extends BaseAddPicActivity {

    private final int DATE_DIALOG = 1;

    private final int TIME_DIALOG = 2;

    TextView mEditChexing;
    EditText mEditZaizhong;
    EditText mEditChechang;
    EditText mEditZuowei;
    TextView edit_fromCity;
    TextView edit_toCity;
    TextView fromAddr;
    TextView toAddr;
    Double fromLng;
    Double toLng;
    Double fromLat;
    Double toLat;

    ImageView iv_action_done;
    TextView tv_multi_truck_fabu;

//    int width,height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quick_new_product);
        //getActionBar().setTitle("发布货源");
        initView();

        setupEditAndBtn(R.id.edit_fromTime, false);
        setupEditAndBtn(R.id.edit_toTime, true);

        mEditChexing = (TextView) findViewById(R.id.edit_truckType);
        mEditZaizhong = (EditText) findViewById(R.id.edit_loadLimit);
        mEditChechang = (EditText) findViewById(R.id.edit_truckLength);
        mEditZuowei = (EditText) findViewById(R.id.edit_gendan);
        iv_action_done = (ImageView) findViewById(R.id.iv_action_done);
        iv_action_done.setOnClickListener(this);
        tv_multi_truck_fabu = (TextView) findViewById(R.id.tv_multi_truck_fabu);
        tv_multi_truck_fabu.setOnClickListener(this);

        fromAddr = (TextView) findViewById(R.id.edit_fromAddr);
        fromAddr.setOnClickListener(this);
        if(((MyApplication)getApplication()).nowLocation != null)
        {
            BDLocation l = ((MyApplication)getApplication()).nowLocation;
            fromAddr.setText(l.getAddrStr());
            fromLat = l.getLatitude();
            fromLng = l.getLongitude();
        }
        toAddr = (TextView) findViewById(R.id.edit_toAddr);
        toAddr.setOnClickListener(this);

        edit_fromCity = (TextView) findViewById(R.id.edit_fromCity);

        LinearLayout fromCity_spinner_ly = (LinearLayout) findViewById(R.id.fromCity_spinner_ly);

        setupCitySpinner(edit_fromCity, fromAddr, fromCity_spinner_ly);

        edit_toCity = (TextView) findViewById(R.id.edit_toCity);

        LinearLayout toCity_spinner_ly = (LinearLayout) findViewById(R.id.toCity_spinner_ly);

        setupCitySpinner(edit_toCity, toAddr, toCity_spinner_ly);

        mEditChexing.setOnClickListener(this);
        ImageView btn_toAddr = (ImageView) findViewById(R.id.btn_toAddr);
        btn_toAddr.setOnClickListener(this);
        ImageView btn_fromAddr = (ImageView) findViewById(R.id.btn_fromAddr);
        btn_fromAddr.setOnClickListener(this);

//        // 获取屏幕的高度和宽度
//        Display display = this.getWindowManager().getDefaultDisplay();
//        width = display.getWidth();
//        height = display.getHeight();
//
//        // 获取弹出的layout
//        final ScrollView test_pop_layout = (ScrollView)findViewById(R.id.root);
//
//        edit_fromCity.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                // 显示 popupWindow
//                PopupWindow popupWindow = makePopupWindow(NewProductActivity.this, edit_fromCity);
////                int[] xy = new int[2];
////                test_pop_layout.getLocationOnScreen(xy);
//                popupWindow.showAtLocation(test_pop_layout, Gravity.CENTER, 0, 0);
//            }
//        });
//
//        edit_toCity.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v)
//            {
//                // 显示 popupWindow
//                PopupWindow popupWindow = makePopupWindow(NewProductActivity.this, edit_toCity);
////                int[] xy = new int[2];
////                test_pop_layout.getLocationOnScreen(xy);
//                popupWindow.showAtLocation(test_pop_layout, Gravity.CENTER, 0, 0);
//            }
//        });
    }

    private void setupCitySpinner(final TextView cityText, final TextView addrText, LinearLayout city_spinner_ly) {
        final Spinner country = (Spinner) city_spinner_ly.findViewById(R.id.country);

        final Spinner city = (Spinner) city_spinner_ly.findViewById(R.id.city);
        // 地区选择
        final Spinner ccity = (Spinner) city_spinner_ly.findViewById(R.id.ccity);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AddressData.PROVINCES);
        country.setAdapter(adapter);
        int pIndex = 0;
        if(((MyApplication)getApplication()).nowLocation != null)
        {
            BDLocation l = ((MyApplication)getApplication()).nowLocation;
            if(!StringUtils.isEmpty(l.getProvince()))
            {
                int tmpIndex = 0;
                for (String p : AddressData.PROVINCES)
                {
                    if(l.getProvince().contains(p) || p.contains(l.getProvince()))
                    {
                        pIndex = tmpIndex;
                        break;
                    }
                    tmpIndex ++;
                }
            }
        }
        country.setSelection(pIndex, true);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(
                QuickNewProductActivity.this, android.R.layout.simple_spinner_item, AddressData.CITIES[pIndex]);
        // 设置二级下拉列表的选项内容适配器
        city.setAdapter(cityAdapter);
        int cIndex = 0;
        if(AddressData.CITIES[pIndex].length == 2 && AddressData.CITIES[pIndex][0].equals("不限"))
        {
            cIndex = 1;
        }
        else
        {
            if(((MyApplication)getApplication()).nowLocation != null)
            {
                BDLocation l = ((MyApplication)getApplication()).nowLocation;
                if(!StringUtils.isEmpty(l.getCity()))
                {
                    int tmpIndex = 0;
                    for (String p : AddressData.CITIES[pIndex])
                    {
                        if(l.getCity().contains(p) || p.contains(l.getCity()))
                        {
                            cIndex = tmpIndex;
                            break;
                        }
                        tmpIndex ++;
                    }
                }
            }
        }
        city.setSelection(cIndex, true);

        ArrayAdapter<String> countyAdapter = new ArrayAdapter<String>(QuickNewProductActivity.this,
                android.R.layout.simple_spinner_item, AddressData.COUNTIES[pIndex][cIndex]);
        ccity.setAdapter(countyAdapter);
        int ccIndex = 0;
        if(AddressData.COUNTIES[pIndex][cIndex].length == 2 && AddressData.COUNTIES[pIndex][cIndex][0].equals("不限"))
            ccIndex = 1;
        else
        {
            if(((MyApplication)getApplication()).nowLocation != null)
            {
                BDLocation l = ((MyApplication)getApplication()).nowLocation;
                if(!StringUtils.isEmpty(l.getDistrict()))
                {
                    int tmpIndex = 0;
                    for (String p : AddressData.COUNTIES[pIndex][cIndex])
                    {
                        if(l.getDistrict().contains(p) || p.contains(l.getDistrict()))
                        {
                            ccIndex = tmpIndex;
                            break;
                        }
                        tmpIndex ++;
                    }
                }
            }
        }
        ccity.setSelection(ccIndex, true);

        if(!AddressData.PROVINCES[pIndex].equals("不限"))
            cityText.setText(AddressData.PROVINCES[pIndex]);
        if(!AddressData.CITIES[country.getSelectedItemPosition()][cIndex].equals("不限"))
            cityText.setText(
                    AddressData.PROVINCES[country.getSelectedItemPosition()] + " " +
                            AddressData.CITIES[country.getSelectedItemPosition()][cIndex]);
        if(!AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][ccIndex].equals("不限"))
            cityText.setText(
                    AddressData.PROVINCES[country.getSelectedItemPosition()] + " " +
                            AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()] + " " +
                            AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][ccIndex]);

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                updateCities(city, ccity, position);
                if(!AddressData.PROVINCES[position].equals("不限"))
                    cityText.setText(AddressData.PROVINCES[position]);
                else
                    cityText.setText("");
                if (addrText.getText() != null && !addrText.getText().toString().contains(AddressData.PROVINCES[position]))
                    addrText.setText("");
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                updateCcities(ccity, country.getSelectedItemPosition(), position);

                if(!AddressData.CITIES[country.getSelectedItemPosition()][position].equals("不限"))
                    cityText.setText(
                            AddressData.PROVINCES[country.getSelectedItemPosition()] + " " +
                                    AddressData.CITIES[country.getSelectedItemPosition()][position]);
                else
                {
                    if(!AddressData.PROVINCES[country.getSelectedItemPosition()].equals("不限"))
                        cityText.setText(AddressData.PROVINCES[country.getSelectedItemPosition()]);
                    else
                        cityText.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        ccity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                if(!AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][position].equals("不限"))
                    cityText.setText(
                            AddressData.PROVINCES[country.getSelectedItemPosition()] + " " +
                                    AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()] + " " +
                                    AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][position]);
                else
                {
                    if(!AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()].equals("不限"))
                        cityText.setText(
                                AddressData.PROVINCES[country.getSelectedItemPosition()] + " " +
                                        AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()]);
                    else
                    {
                        if(!AddressData.PROVINCES[country.getSelectedItemPosition()].equals("不限"))
                            cityText.setText(AddressData.PROVINCES[country.getSelectedItemPosition()]);
                        else
                            cityText.setText("");
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });
    }

    private TextView tv;

    // 创建一个包含自定义view的PopupWindow
    private PopupWindow makePopupWindow(Context cx, final TextView tt)
    {
        View contentView = LayoutInflater.from(this).inflate(R.layout.cities_layout, null, false);
        final PopupWindow window = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, true);

        tv = (TextView)contentView.findViewById(R.id.tv_cityName);

        final Spinner country = (Spinner) contentView.findViewById(R.id.country);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, AddressData.PROVINCES);
        country.setAdapter(adapter);
        country.setSelection(0, true);

        final Spinner city = (Spinner) contentView.findViewById(R.id.city);
        // 地区选择
        final Spinner ccity = (Spinner) contentView.findViewById(R.id.ccity);

        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            // 表示选项被改变的时候触发此方法，主要实现办法：动态改变地级适配器的绑定值
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                updateCities(city, ccity, position);
                tv.setText(AddressData.PROVINCES[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                updateCcities(ccity, country.getSelectedItemPosition(), position);

                tv.setText(
                        AddressData.PROVINCES[country.getSelectedItemPosition()] + "-" +
                                AddressData.CITIES[country.getSelectedItemPosition()][position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        ccity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long arg3)
            {
                tv.setText(
                        AddressData.PROVINCES[country.getSelectedItemPosition()] + "-" +
                                AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()] + "-" +
                                AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0)
            {

            }
        });

        // 点击事件处理
        Button button_ok = (Button)contentView.findViewById(R.id.button_ok);
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tt.setText(AddressData.PROVINCES[country.getSelectedItemPosition()] + "-" +
                        AddressData.CITIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()] + "-" +
                        AddressData.COUNTIES[country.getSelectedItemPosition()][city.getSelectedItemPosition()][ccity.getSelectedItemPosition()]);
                window.dismiss(); // 隐藏
            }
        });

        // 设置PopupWindow外部区域是否可触摸
        window.setFocusable(true); //设置PopupWindow可获得焦点
        window.setTouchable(true); //设置PopupWindow可触摸
        window.setOutsideTouchable(true); //设置非PopupWindow区域可触摸
        return window;
    }

    private void updateCities(Spinner city, Spinner ccity, int index) {
        //position为当前省级选中的值的序号
        //将地级适配器的值改变为city[position]中的值
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(
                QuickNewProductActivity.this, android.R.layout.simple_spinner_item, AddressData.CITIES[index]);
        // 设置二级下拉列表的选项内容适配器
        city.setAdapter(cityAdapter);
        if(AddressData.CITIES[index].length == 2 && AddressData.CITIES[index][0].equals("不限"))
        {
            city.setSelection(1, true);
            updateCcities(ccity, index, 1);
        }
        else
        {
            city.setSelection(0, true);
            updateCcities(ccity, index, 0);
        }
    }

    private void updateCcities(Spinner ccity, int index, int index2) {

        ArrayAdapter<String> countyAdapter = new ArrayAdapter<String>(QuickNewProductActivity.this,
            android.R.layout.simple_spinner_item, AddressData.COUNTIES[index][index2]);
        ccity.setAdapter(countyAdapter);
        if(AddressData.COUNTIES[index][index2].length == 2 && AddressData.COUNTIES[index][index2][0].equals("不限"))
            ccity.setSelection(1, true);
        else
            ccity.setSelection(0, true);
    }

    private void setupEditAndBtn(int editTextId, boolean isTo) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, isTo ? 3 : 1);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) / 5 * 5);

        TextView editText =
                (TextView) findViewById(editTextId);
        editText.setText(sdf.format(calendar.getTime()));

        View.OnClickListener dateBtnListener =
                new DateTimeOnClickListener(DATE_DIALOG, this, editText);
        editText.setOnClickListener(dateBtnListener);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.edit_truckType:
                DialogUtils.createListDialog(mContext, 0, "请选择车型", ConstHolder.TruckTypeItems,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mEditChexing.setText(ConstHolder.TruckTypeItems[which]);
                            }
                        }).show();
                break;
            case R.id.btn_fromAddr:
            case R.id.edit_fromAddr:
                intent = new Intent(mContext, LocationDemo.class);
                if(fromAddr.getText() != null && !fromAddr.getText().toString().isEmpty())
                    intent.putExtra("intentAddress", fromAddr.getText().toString());
                else if(edit_fromCity.getText() != null && !edit_fromCity.getText().toString().isEmpty())
                    intent.putExtra("intentAddress", edit_fromCity.getText().toString());
                intent.putExtra("intentCity", edit_fromCity.getText().toString());
                QuickNewProductActivity.this.startActivityForResult(intent, 0);
                break;
            case R.id.btn_toAddr:
            case R.id.edit_toAddr:
                intent = new Intent(mContext, LocationDemo.class);
                if(toAddr.getText() != null && !toAddr.getText().toString().isEmpty())
                    intent.putExtra("intentAddress", toAddr.getText().toString());
                else if(edit_toCity.getText() != null && !edit_toCity.getText().toString().isEmpty())
                    intent.putExtra("intentAddress", edit_toCity.getText().toString());
                intent.putExtra("intentCity", edit_toCity.getText().toString());
                QuickNewProductActivity.this.startActivityForResult(intent, 1);
                break;
            case R.id.iv_action_done:
                if(isDoingUpdate)
                    Toast.makeText(this, "更新操作正在进行,不可退出！", Toast.LENGTH_SHORT).show();
                else
                {
                    saveParams();
                    if(params.containsKey("truckType") &&
                            params.containsKey("limitCount") &&
                            params.containsKey("fromCity") &&
                            params.containsKey("toCity"))
                    {
                        new MyTask().execute(QuickNewProductActivity.this);
                    }
                }
                break;
            case R.id.tv_multi_truck_fabu:
                intent = new Intent(mContext, NewProductActivity.class);
                QuickNewProductActivity.this.startActivity(intent);
                QuickNewProductActivity.this.finish();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtils.i("Fragment Result:" + requestCode + "," + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                if (data != null) {
                    String addr = data.getStringExtra("addr");
                    fromAddr.setText(addr);

                    fromLat = data.getDoubleExtra("lat", 0.0);
                    fromLng = data.getDoubleExtra("lng", 0.0);

                    LinearLayout fromCity_spinner_ly = (LinearLayout) findViewById(R.id.fromCity_spinner_ly);
                    final Spinner country = (Spinner) fromCity_spinner_ly.findViewById(R.id.country);

                    int pIndex = country.getSelectedItemPosition();
                    int tmpIndex = 0;
                    for (String p : AddressData.PROVINCES)
                    {
                        if(addr.contains(p) || p.contains(addr))
                        {
                            if(pIndex != tmpIndex)
                                country.setSelection(tmpIndex, true);
                            break;
                        }
                        tmpIndex ++;
                    }
                }
            } else if (requestCode == 1) {
                if (data != null) {
                    String addr = data.getStringExtra("addr");
                    toAddr.setText(addr);
                    toLat = data.getDoubleExtra("lat", 0.0);
                    toLng = data.getDoubleExtra("lng", 0.0);

                    LinearLayout toCity_spinner_ly = (LinearLayout) findViewById(R.id.toCity_spinner_ly);
                    final Spinner country = (Spinner) toCity_spinner_ly.findViewById(R.id.country);

                    int pIndex = country.getSelectedItemPosition();
                    int tmpIndex = 0;
                    for (String p : AddressData.PROVINCES)
                    {
                        if(addr.contains(p) || p.contains(addr))
                        {
                            if(pIndex != tmpIndex)
                                country.setSelection(tmpIndex, true);
                            break;
                        }
                        tmpIndex ++;
                    }
                }
            }

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(mContext, "操作取消", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(isDoingUpdate)
            {
                Toast.makeText(this, "更新操作正在进行,不可退出！", Toast.LENGTH_SHORT).show();
                return true;
            }

            DialogUtils.createNormalDialog(mContext, 0,
                    mContext.getText(R.string.app_name).toString(), "是否保存",
                    "确定", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            saveParams();
                            if(params.containsKey("truckType") &&
                                    params.containsKey("limitCount") &&
                                    params.containsKey("fromCity") &&
                                    params.containsKey("toCity"))
                            {
                                new MyTask().execute(QuickNewProductActivity.this);
                            }
                        }
                    }, "取消", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            QuickNewProductActivity.this.finish();
                        }
                    }).show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isDoingUpdate = false;
    private Dialog dialog;
    private Map<String, String> params = new HashMap<String, String>();

    private void saveParams()
    {
        params = new HashMap<String, String>();

        TextView edit_fromTime =
                (TextView) findViewById(R.id.edit_fromTime);
        TextView edit_toTime =
                (TextView) findViewById(R.id.edit_toTime);
        if(edit_fromTime.getText() != null && !edit_fromTime.getText().toString().isEmpty())
             params.put("departureTimeStr", edit_fromTime.getText().toString());
        if(edit_fromTime.getText() != null && !edit_fromTime.getText().toString().isEmpty())
            params.put("arrivalTimeStr", edit_toTime.getText().toString());

        EditText edit_price =
                (EditText) findViewById(R.id.edit_price);
        EditText edit_limitCount =
                (EditText) findViewById(R.id.edit_limitCount);
        if(edit_price.getText() != null && !edit_price.getText().toString().isEmpty())
            params.put("price", edit_price.getText().toString());
        if(edit_limitCount.getText() != null && !edit_limitCount.getText().toString().isEmpty())
            params.put("limitCount", edit_limitCount.getText().toString());

        if(edit_fromCity.getText() != null && !edit_fromCity.getText().toString().isEmpty())
            params.put("fromCity", edit_fromCity.getText().toString());
        if(fromAddr.getText() != null && !fromAddr.getText().toString().isEmpty())
            params.put("fromAddr", fromAddr.getText().toString());
        else
            params.put("fromAddr", "");
        params.put("fromLat", String.valueOf(fromLat));
        params.put("fromLng", String.valueOf(fromLng));

        if(edit_toCity.getText() != null && !edit_toCity.getText().toString().isEmpty())
            params.put("toCity", edit_toCity.getText().toString());
        if(toAddr.getText() != null && !toAddr.getText().toString().isEmpty())
            params.put("toAddr", toAddr.getText().toString());
        else
            params.put("toAddr", "");
        params.put("toLat", String.valueOf(toLat));
        params.put("toLng", String.valueOf(toLng));

        EditText edit_loadLimit =
                (EditText) findViewById(R.id.edit_loadLimit);
        EditText edit_truckLength =
                (EditText) findViewById(R.id.edit_truckLength);
        EditText edit_gendan =
                (EditText) findViewById(R.id.edit_gendan);
        if(edit_loadLimit.getText() != null && !edit_loadLimit.getText().toString().isEmpty())
            params.put("loadLimit", edit_loadLimit.getText().toString());
        if(edit_truckLength.getText() != null && !edit_truckLength.getText().toString().isEmpty())
            params.put("truckLength", edit_truckLength.getText().toString());

        Integer sIndex = 0;
        Integer tmpIndex = 0;
        for(CharSequence s : ConstHolder.TruckTypeItems)
        {
            tmpIndex ++;
            if(s.equals(mEditChexing.getText().toString()))
            {
                sIndex = tmpIndex;
                break;
            }
        }
        if(sIndex > 0)
            params.put("truckType", sIndex.toString());

        if(edit_gendan.getText() != null && !edit_gendan.getText().toString().isEmpty())
            params.put("merchandiserNum", edit_gendan.getText().toString());


        EditText edit_title =
                (EditText) findViewById(R.id.edit_title);
        EditText edit_description =
                (EditText) findViewById(R.id.edit_description);
        if(edit_title.getText() != null && !edit_title.getText().toString().isEmpty())
            params.put("title", edit_title.getText().toString());
        if(edit_description.getText() != null && !edit_description.getText().toString().isEmpty())
            params.put("description", edit_description.getText().toString());
    }

    @SuppressWarnings("unused")
    private class MyTask extends AsyncTask<Context, Integer, String> {
        Context context;
        @Override
        protected void onPreExecute() {
            isDoingUpdate = true;
            dialog = ProgressDialog.show(QuickNewProductActivity.this,
                    QuickNewProductActivity.this.getString(R.string.app_name),
                    QuickNewProductActivity.this.getText(R.string.operating), true, false, null);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Context... params) {
            context = params[0];
            //第二个执行方法,onPreExecute()执行完后执行
            try {
                return update();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //这个函数在doInBackground调用publishProgress时触发
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            isDoingUpdate = false;
            dialog.dismiss();
            MyApplication.setUpPersistentCookieStore();
            if(result != null){
                try {
                    JSONObject jObject = new JSONObject(result);
                    String viewName = jObject.getString("viewName");
                    //服务器未登录
                    if("not_login".equals(viewName))
                    {
                        //以服务器的为准，设置本地为未登录，然后赶快去登录啊
                        SetLoginFlag(QuickNewProductActivity.this, false);
                        Intent intent = new Intent(mContext, MainPage.class);
                        Toast.makeText(QuickNewProductActivity.this, "尚未登录！", Toast.LENGTH_SHORT).show();
                        mContext.startActivity(intent);
                        QuickNewProductActivity.this.finish();
                    }
                    else if("success".equals(viewName))
                    {
                        Toast.makeText(context, "发布完成!", Toast.LENGTH_SHORT).show();
                        QuickNewProductActivity.this.finish();
                    }
                    else if("err".equals(viewName))
                    {
                        String errMsg = jObject.getString("errMsg");
                        Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(context, "系统异常！", Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

        private String update() throws Exception {
            String url = new String(UrlList.MAIN + "mvc/product_add_json");

            HttpURLConnection conn = (HttpURLConnection) HttpRequestUtil.sendPostRequest(url, params, null);
            if(conn.getResponseCode()==200){
                String result = HttpRequestUtil.read2String(conn.getInputStream());
                return result;
            }

            return null;
        }
    }
}
