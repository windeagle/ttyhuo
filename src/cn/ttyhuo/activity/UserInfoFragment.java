package cn.ttyhuo.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.ttyhuo.R;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-5-11
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public class UserInfoFragment extends Fragment {
    private String url = new String(UrlList.MAIN + "mvc/viewUserJson_");
    private int userID;

    public UserInfoFragment(int userID)
    {
       this.userID = userID;
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            try {
                String result = msg.obj.toString();
                JSONObject jObject = new JSONObject(result);
                Context context = getActivity();
                //UserView oneView = new UserView(getView());
                //oneView.setupViews(jObject, context);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tmp_user_info, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new UrlThread(handler, url.toString() + userID, 1).start();
    }
}
