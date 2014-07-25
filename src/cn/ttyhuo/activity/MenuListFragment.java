package cn.ttyhuo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.login.LoginRegGuideFragment;

/**
 * @author yangyu
 *         功能描述：列表Fragment，用来显示滑动菜单打开后的内容
 */
public class MenuListFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, null);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SampleAdapter adapter = new SampleAdapter(getActivity());

        for (int i = 0; i < 20; i++) {
            adapter.add(new SampleItem("Sample List", android.R.drawable.ic_menu_search));
        }
        setListAdapter(adapter);

//        String[] colors = getResources().getStringArray(R.array.color_names);
//
//        ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, android.R.id.text1, colors);
//
//        setListAdapter(colorAdapter);
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        Fragment newContent = null;
        Context mContext = getActivity();
        Intent intent = null;
        switch (position) {
//            case 0:
//                newContent = new ColorFragment(R.color.red);
//                break;
//            case 1:
//                newContent = new ColorFragment(R.color.green);
//                break;
//            case 2:
//                newContent = new ColorFragment(R.color.blue);
//                break;
//            case 3:
//                newContent = new ColorFragment(android.R.color.white);
//                break;
//            case 4:
//                newContent = new ColorFragment(android.R.color.black);
//                break;
            case 5:
                intent = new Intent(mContext, MainPage.class);
                intent.putExtra("contentFragment", "ProductListViewFragment");
                intent.putExtra("windowTitle", "货源列表");
                intent.putExtra("hasWindowTitle", true);
                mContext.startActivity(intent);
                break;
            case 6:
                newContent = new LoginRegGuideFragment();
                break;
            case 7:
                intent = new Intent(mContext, MainPage.class);
                intent.putExtra("contentFragment", "UserListViewFragment");
                intent.putExtra("windowTitle", "用户列表");
                intent.putExtra("hasWindowTitle", true);
                mContext.startActivity(intent);
                break;
        }
        if (newContent != null)
            switchFragment(newContent);
    }

    // 切换Fragment视图内ring
    private void switchFragment(Fragment fragment) {
        if (getActivity() == null)
            return;

        if (getActivity() instanceof MainPage) {
            MainPage fca = (MainPage) getActivity();
            fca.switchContent(fragment);
        }
    }

    public class SampleAdapter extends ArrayAdapter<SampleItem> {
        public SampleAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).tag);

            return convertView;
        }
    }

    private class SampleItem {
        public String tag;
        public int iconRes;

        public SampleItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }
}
