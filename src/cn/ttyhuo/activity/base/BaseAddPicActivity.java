package cn.ttyhuo.activity.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.ShowPicActivity;
import cn.ttyhuo.activity.login.LoginNeedBaseFragment;
import cn.ttyhuo.adapter.PicListAdapter;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.utils.*;
import cn.ttyhuo.view.ExpandableHeightGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BaseAddPicActivity extends ActionBarActivity implements
		OnClickListener {

	protected Context mContext;
	protected ExpandableHeightGridView mPicGrid;
	protected PicListAdapter mPicAdapter;
	protected ArrayList<String> mPicData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	protected void initView() {
		mPicGrid = (ExpandableHeightGridView) findViewById(R.id.pic_grid);
		mPicData = new ArrayList<String>();
		mPicData.add("plus");
		mPicAdapter = new PicListAdapter(mContext, mPicData, 0);
		mPicGrid.setAdapter(mPicAdapter);

		mPicGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String url = mPicData.get(arg2);
				if (url.equals("plus")) {
					showAttachDialog();

				} else {
					Intent intent = new Intent(mContext, ShowPicActivity.class);
					ArrayList<String> newList = new ArrayList<String>();
					newList.addAll(mPicData);
					newList.remove(mPicData.size() - 1);
                    ArrayList<String> tmpNewList = new ArrayList<String>();
                    for(String p : newList)
                    {
                        tmpNewList.add(p.replace("_small", ""));
                    }
					intent.putStringArrayListExtra("pics", tmpNewList);
					intent.putExtra("position", arg2);
					mContext.startActivity(intent);
				}

			}
		});

		mPicGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				String url = mPicData.get(position);
				if (url.equals("plus")) {
				} else {
					final CharSequence[] items = new CharSequence[] { "删除" };
					DialogUtils.createListDialog(mContext, 0,
							mContext.getString(R.string.app_name), items,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
                                    Map<String, String> params = new HashMap<String, String>();
                                    params.put("imgUrl", mPicData.get(position));

									mPicData.remove(position);
									mPicAdapter.notifyDataSetChanged();

                                    new UrlThread(new Handler() {
                                        public void handleMessage(android.os.Message msg) {}
                                    }, getPicDeleteUrl(), params, 1).start();
								}
							}).show();
				}

				return true;
			}
		});
	}

	@Override
	public void onClick(View v) {
	}

	/**
	 * 获取图片模块
	 */
	protected static final int PiC_FROM_CAMERA = 1024;
	protected static final int PiC_FROM_ALBUM = 1025;

	protected void showAttachDialog() {
		DialogUtils.createListDialog(mContext, 0, "选择图片来源", R.array.menu_pic,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0:
							Intent cameraIntent = new Intent(
									"android.media.action.IMAGE_CAPTURE");
							cameraIntent.putExtra("output",
									PhotoUtils.getCameraStrImgPathUri(mContext));
							cameraIntent.putExtra(
									"android.intent.extra.videoQuality", 1);

							BaseAddPicActivity.this.startActivityForResult(
									cameraIntent, PiC_FROM_CAMERA);
							break;
						case 1:
							Intent getAlbum = new Intent(
									Intent.ACTION_GET_CONTENT);
							getAlbum.setType("image/*");
							BaseAddPicActivity.this.startActivityForResult(
									getAlbum, PiC_FROM_ALBUM);
							break;
						}
					}
				}).show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i("Fragment Result:" + requestCode + "," + resultCode);
		if (resultCode == RESULT_OK) {
			if (requestCode == PiC_FROM_CAMERA) {
				String imgFile = SharedUtils.getString(mContext, "imgFile");

				if (!TextUtils.isEmpty(imgFile)) {
					doHttpUploadPic(imgFile);
				} else {
					Toast.makeText(mContext,
							mContext.getString(R.string.op_error),
							Toast.LENGTH_SHORT).show();
				}

			} else if (requestCode == PiC_FROM_ALBUM) {
				if (data != null) {
					String imgFile = PhotoUtils.getPath(mContext,
							data.getData());
					if (!TextUtils.isEmpty(imgFile)) {
						doHttpUploadPic(imgFile);
						return;
					}
				}
				Toast.makeText(mContext, mContext.getString(R.string.op_error),
						Toast.LENGTH_SHORT).show();

			}

		} else if (resultCode == RESULT_CANCELED) {
			Toast.makeText(mContext, "操作取消", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
		}
	}

    protected String getPicUploadUrl()
    {
        return UrlList.MAIN + "mvc/uploadTmpProductImg";
    }

    protected String getPicDeleteUrl()
    {
        return UrlList.MAIN + "mvc/deleteTmpProductImg";
    }

    protected RequestParams getRequestParams()
    {
        RequestParams params = new RequestParams();
        return params;
    }

    private void doHttpUploadPic(String imgFile) {
        mPicData.add(mPicData.size() - 1, imgFile);
        mPicAdapter.notifyDataSetChanged();

        if (XHttpHelper.checkHttp(mContext)) {
            final Dialog dialog = ProgressDialog.show(mContext,
                    mContext.getString(R.string.app_name),
                    mContext.getText(R.string.operating), true, false, null);

            String url = getPicUploadUrl();
            RequestParams params = getRequestParams();

//		params.add("taskId", mModel.getId());
//		params.add("userId", mLoginModel.getUserid());

            if (params != null) {
                LogUtils.d("==========发送请求\n" + url + "?" + params.toString());
            }

            if (!TextUtils.isEmpty(imgFile)) {
                Bitmap map = PhotoUtils.decodeBitmap(imgFile, 1000, 1000);
                params.put("upload_file", PhotoUtils.Bitmap2InputStream(map),
                        "picture.png");

                LogUtils.d("picture:" + imgFile);
            }

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(100000);

            client.setCookieStore(((MyApplication)getApplication()).cookieStore);

            client.post(url, params, new TextHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      String responseBody) {
                    LogUtils.d("-----Http Success:" + responseBody);

                    try {
                        JSONObject jObject = new JSONObject(responseBody);
                        boolean notLogin = jObject.has("haslogin");
                        boolean success = jObject.has("success");
                        boolean hasErr = jObject.has("errMsg");
                        //服务器未登录
                        if(notLogin)
                        {
                            Toast.makeText(mContext, "尚未登录！", Toast.LENGTH_SHORT).show();
                        }
                        else if(success)
                        {
                            Toast.makeText(mContext, "操作成功", Toast.LENGTH_SHORT).show();
                            if(jObject.has("msg"))
                            {
                                mPicData.remove(mPicData.size() - 2);
                                mPicData.add(mPicData.size() - 1, jObject.getString("msg"));
                                mPicAdapter.notifyDataSetChanged();
                            }

                        } else {
                            String errorMsg = jObject.optString("errMsg");
                            if (TextUtils.isEmpty(errorMsg)) {
                                Toast.makeText(mContext,
                                        mContext.getString(R.string.op_error),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(mContext, errorMsg,
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        return;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(mContext, "上传图片出错，请稍候再试", Toast.LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onFinish() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }

                }

                @Override
                public void onFailure(int arg0, Header[] arg1, String arg2,
                                      Throwable arg3) {
                    // TODO Auto-generated method stub
                    Toast.makeText(mContext, "上传图片出错，请稍候再试", Toast.LENGTH_SHORT)
                            .show();
                }

            });
        }
    }

    public static void SetLoginFlag(Activity activity, boolean loginFlag)
    {
        //设置本地存储
        SharedPreferences settings = activity.getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putBoolean(LoginNeedBaseFragment.LOGIN_FLAG, loginFlag).commit();
    }

    public static void SetLoginUser(Activity activity, String loginUser)
    {
        //设置本地存储
        SharedPreferences settings = activity.getSharedPreferences(LoginNeedBaseFragment.LOGIN, 0); //首先获取一个 SharedPreferences 对象
        settings.edit().putString(LoginNeedBaseFragment.LOGIN_USER, loginUser).commit();
    }

    protected void setupViewOfHasLogin(JSONObject jObject)
    {

    }
}
