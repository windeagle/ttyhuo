package cn.ttyhuo.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.model.ContactModel;
import cn.ttyhuo.utils.NetworkUtils;
import cn.ttyhuo.utils.UrlThread;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class ContactAdapter extends BaseAdapter {

	private final LayoutInflater mInflater;
	private List<ContactModel> mDataList;
	private Context mContext;
	private final Set<Integer> mCheck;

	public ContactAdapter(Context context, List<ContactModel> dataList,
                          Set<Integer> check) {
		mDataList = dataList;
		mInflater = LayoutInflater.from(context);
		mCheck = check;
        mContext = context;
	}

	public void updateData(List<ContactModel> dataList) {
		mDataList = dataList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        final ContactModel mModel = mDataList.get(position);

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_contact, parent,
					false);
			holder = new ViewHolder();

			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phone = (TextView) convertView.findViewById(R.id.phone);
            holder.follow = (TextView) convertView.findViewById(R.id.follow);
            holder.invite = (TextView) convertView.findViewById(R.id.invite);
			//holder.check = (CheckBox) convertView.findViewById(R.id.check);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

        final TextView inviteTV = holder.invite;
        final TextView followTV = holder.follow;

        final ProgressDialog progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle(mContext.getString(R.string.app_name));
        progressDialog.setMessage("操作进行中");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });

        final Handler innerHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                try {
                    progressDialog.dismiss();
                    String result = msg.obj.toString();
                    JSONObject jObject = new JSONObject(result);

                    boolean notLogin = jObject.has("haslogin");
                    boolean success = jObject.has("success");   //not_login
                    //服务器未登录
                    if(notLogin)
                    {
                        Toast.makeText(mContext, "您尚未登录！", Toast.LENGTH_LONG).show();
                    }
                    else if(success)
                    {
                        Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();

                        if(msg.what == 0)
                        {
                            followTV.setText("已关注");
                            followTV.setTextColor(R.color.tw_light_black);
                            followTV.setOnClickListener(null);
                        }
                        if(msg.what == 1)
                        {
                            inviteTV.setText("已邀请");
                            inviteTV.setTextColor(R.color.tw_light_black);
                            inviteTV.setOnClickListener(null);
                        }
                    }
                    else
                    {
                        Toast.makeText(mContext, jObject.getString("errMsg"), Toast.LENGTH_LONG).show();

                        if(msg.what == 0)
                        {
                            followTV.setText("已关注");
                            followTV.setTextColor(R.color.tw_light_black);
                            followTV.setOnClickListener(null);
                        }
                        if(msg.what == 1)
                        {
                            inviteTV.setText("已邀请");
                            inviteTV.setTextColor(R.color.tw_light_black);
                            inviteTV.setOnClickListener(null);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

		holder.name.setText(mModel.getName());
		holder.phone.setText(mModel.getPhone());
        if(mModel.getUserID() != null)
        {
            holder.invite.setVisibility(View.GONE);
            holder.follow.setVisibility(View.VISIBLE);
            if(mModel.getHasFellow() != null && mModel.getHasFellow())
            {
                holder.follow.setText("已关注");
                holder.follow.setTextColor(R.color.tw_light_black);
            }
            else
            {
                holder.follow.setText("关注");
                holder.follow.setTextColor(R.color.tw_hard_black);

                holder.follow.setOnClickListener(new View.OnClickListener() {
                    // 点击按钮 追加数据 并通知适配器
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "正在操作", Toast.LENGTH_SHORT).show();
                        progressDialog.show();
                        if(!NetworkUtils.isNetworkAvailable(mContext))
                        {
                            Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
                        }
                        new UrlThread(innerHandler, UrlList.MAIN + "mvc/follow_" + mModel.getUserID(), 0).start();
                    }
                });
            }
        }
        else
        {
            holder.invite.setVisibility(View.VISIBLE);
            holder.follow.setVisibility(View.GONE);
            holder.invite.setText("邀请");

            holder.invite.setOnClickListener(new View.OnClickListener() {
                // 点击按钮 追加数据 并通知适配器
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "正在操作", Toast.LENGTH_SHORT).show();
                    progressDialog.show();
                    if(!NetworkUtils.isNetworkAvailable(mContext))
                    {
                        Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
                    }
                    new UrlThread(innerHandler, UrlList.MAIN + "mvc/inviteUserByPhoneNo_" + mModel.getPhone().replace(" ",""), 1).start();
                }
            });
        }

//		if (mCheck.contains(position)) {
//			holder.check.setChecked(true);
//		} else {
//			holder.check.setChecked(false);
//		}

		return convertView;
	}

	public class ViewHolder {
		TextView name;
		TextView phone;
        TextView follow;
        TextView invite;
		//CheckBox check;
	}
}
