package cn.ttyhuo.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import cn.ttyhuo.R;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.ApacheHttpRequestUtil;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GridViewSimple extends Activity {
    private GridView gridView1;
    private String url = new String(UrlList.MAIN + "mvc/getAllProducts");

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONArray object = new JSONArray(result);

                ArrayList<Map<String, Object>> arrayList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < object.length(); i++) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("imgUrl", Drawable.createFromStream(ApacheHttpRequestUtil.getInputStream(((JSONObject) object.get(i)).getString("imgUrl")), "src"));
                    map.put("title", ((JSONObject) object.get(i)).get("title"));
                    arrayList.add(map);
                }

                SimpleAdapter simpleAdapter = new SimpleAdapter(GridViewSimple.this, arrayList,
                        R.layout.tmp_gridview_item, new java.lang.String[]{"imgUrl", "title"},
                        new int[]{R.id.imageView1, R.id.textView1});
                gridView1.setAdapter(simpleAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tmp_gridview);
        gridView1 = (GridView) findViewById(R.id.gridView1);
        new UrlThread(handler, url.toString(), 1).start();
    }
}