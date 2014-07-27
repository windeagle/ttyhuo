package cn.ttyhuo.activity.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.activity.BigImageActivity;
import cn.ttyhuo.activity.InfoOwnerActivity;
import cn.ttyhuo.adapter.PicListAdapter;
import cn.ttyhuo.common.MyApplication;
import cn.ttyhuo.common.UrlList;
import cn.ttyhuo.helper.UpYunException;
import cn.ttyhuo.utils.*;
import cn.ttyhuo.view.ExpandableHeightGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.*;

public class BaseAddPic2Activity extends ActionBarActivity implements
		OnClickListener {

	protected Context mContext;
	protected ExpandableHeightGridView mPicGrid;
	protected PicListAdapter mPicAdapter;
	protected List<String> mPicData;

	private int picPos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
	}

	protected void initView() {
		mPicGrid = (ExpandableHeightGridView) findViewById(R.id.pic_grid);
		mPicData = new ArrayList<String>();
		mPicData.add("plus");
		mPicData.add("plus");
		if (this instanceof InfoOwnerActivity) {
			mPicData.add("plus");
			mPicGrid.setNumColumns(3);
		} else {
			mPicGrid.setNumColumns(2);
		}

		mPicAdapter = new PicListAdapter(mContext, mPicData, 1);
		mPicGrid.setAdapter(mPicAdapter);

		mPicGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				picPos = position;
				String url = mPicData.get(position);
				if (url.equals("plus")) {
					showAttachDialog();

				} else {
					Intent intent = new Intent(mContext, BigImageActivity.class);
					intent.putExtra("url", url.replace("_small", ""));
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
                                    params.put("verifyFlag", getVerifyFlag());

                                    if(!NetworkUtils.isNetworkAvailable(mContext))
                                    {
                                        Toast.makeText(mContext, "网络不可用", Toast.LENGTH_LONG).show();
                                    }
                                    new UrlThread(new Handler() {
                                        public void handleMessage(android.os.Message msg) {}
                                    }, getPicDeleteUrl(), params, 1).start();

                                    mPicData.remove(position);
									mPicData.add(position, "plus");
                                    mPicAdapter.notifyDataSetChanged();
								}
							}).show();
				}

				return true;
			}
		});
	}

    protected String getPicUploadUrl()
    {
        return UrlList.MAIN + "mvc/uploadTmpVerifyImgFromUPYun";
    }

    protected String getPicDeleteUrl()
    {
        return UrlList.MAIN + "mvc/deleteTmpVerifyImg";
    }

    protected String getVerifyFlag()
    {
        return "Sfz";
    }

    protected RequestParams getRequestParams()
    {
        RequestParams params = new RequestParams();
        return params;
    }

    protected Map<String, String> getParams()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("verifyFlag", getVerifyFlag());
        return params;
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

							BaseAddPic2Activity.this.startActivityForResult(
									cameraIntent, PiC_FROM_CAMERA);
							break;
						case 1:
							Intent getAlbum = new Intent(
									Intent.ACTION_GET_CONTENT);
							getAlbum.setType("image/*");
							BaseAddPic2Activity.this.startActivityForResult(
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
                    doHttpUploadPicToUPYun(imgFile);
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
                        doHttpUploadPicToUPYun(imgFile);
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

    private void doHttpUploadPicToUPYun(String imgFile) {
        mPicData.add(mPicData.size() - 1, imgFile);
        mPicAdapter.notifyDataSetChanged();

        if (XHttpHelper.checkHttp(mContext)) {
            final Dialog dialog = ProgressDialog.show(mContext,
                    mContext.getString(R.string.app_name),
                    mContext.getText(R.string.operating), true, false, null);

            // 空间名
            String bucket = "ttyhuo-img";
            // 表单 API 验证密匙：需要访问又拍云管理后台的空间管理页面获取
            String form_api_secret = "dQSH8ws5Bz6mZOIBzQWZva6Saos=";

            Map<String, Object> options = new HashMap<String, Object>();
            options.put("bucket", bucket);

            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.MINUTE,100);

            // 授权过期时间：以页面加载完毕开始计时，10分钟内有效
            options.put("expiration", (c.getTime().getTime()/1000));

            // 保存路径：最终将以"/年/月/日/upload_待上传文件名"的形式进行保存
            options.put("save-key", "/{year}/{mon}/{day}/{hour}/{min}_{sec}_upload_{filename}{.suffix}");

            // 文件类型限制：仅允许上传扩展名为 jpg,gif,png 三种类型的文件
            options.put("allow-file-type", "jpg,gif,png");

            // 图片宽度限制：仅允许上传宽度在 0～1024px 范围的图片文件
            options.put("image-width-range", "0,4096");

            // 图片高度限制：仅允许上传高度在 0～1024px 范围的图片文件
            options.put("image-height-range", "0,4096");

            // 同步跳转 url：表单上传完成后，使用 http 302 的方式跳转到该 URL
            //options.put("return-url", "http://localhost/return.php");

            // 异步回调 url：表单上传完成后，云存储服务端主动把上传结果 POST 到该 URL
            // 请注意该地址必须公网可以正常访问
            //options.put("notify-url", "http://www.demobucket.com/notify.php");

            // 缩略类型：固定宽度和高度，若宽高不足时进行拉伸
            options.put("x-gmkerl-type", "fix_max");

            // 保证最终的图片宽高为 200*160
            options.put("x-gmkerl-value", "1024");


            // 计算 policy 内容，具体说明请参阅"Policy 内容详解"
            String tmpStr = (new JSONObject(options)).toString();
            //String policy = Base64Coder.encodeString(tmpStr);
            String policy = null;
            try {
                policy = Base64.encodeToString(tmpStr.getBytes("UTF-8"), Base64.DEFAULT);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //String policy = UpYunUtils.makePolicy(SAVE_KEY, EXPIRATION, BUCKET);

            //根据表单api签名密钥对policy进行签名
            //通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
            //String signature = UpYunUtils.signature(policy + "&" + form_api_secret);
            // 计算签名值，具体说明请参阅"Signature 签名"
            String signature = BaseAddPicActivity.UPYunDigestUtil.getInstance().encipher(policy + '&' + form_api_secret);

            //http://v0.api.upyun.com/ttyhuo-img
            //ttyhuo-img.b0.upaiyun.com
            //psVvg+Y2tNd+ZZrCyJKMuY2UPJE=
            //t7X7+F33Ab7rF+7k0LqChk3n0io=String url = getPicUploadUrl();

            String url = "http://v0.api.upyun.com/ttyhuo-img";
            RequestParams params = getRequestParams();
            params.put("policy", policy);
            params.put("signature", signature);

            if (params != null) {
                LogUtils.d("==========发送请求\n" + url + "?" + params.toString());
            }

            if (!TextUtils.isEmpty(imgFile)) {
                Bitmap map = PhotoUtils.decodeBitmap(imgFile, 1024, 1024);
                params.put("file", PhotoUtils.Bitmap2InputStream(map),
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
                        int code = statusCode;
                        JSONObject obj = new JSONObject(responseBody);
                        if (code != HttpStatus.SC_OK) {

                            String msg = obj.getString("message");
                            msg = new String(msg.getBytes("UTF-8"), "UTF-8");
                            String url = obj.getString("url");
                            long time = obj.getLong("time");
                            boolean isSigned = false;
                            String signString = "";
                            if (!obj.isNull("sign")) {
                                signString = obj.getString("sign");
                                isSigned = true;
                            } else if (!obj.isNull("non-sign")) {
                                signString = obj.getString("non-sign");
                                isSigned = false;
                            }

                            UpYunException exception = new UpYunException(code, msg);
                            exception.isSigned = isSigned;
                            exception.url = url;
                            exception.time = time;
                            exception.signString = signString;
                            throw exception;

                        } else {

                            String returnStr = obj.getString("url");

                            Map<String, String> params = getParams();
                            params.put("upyunImgUrl", "http://ttyhuo-img.b0.upaiyun.com" + returnStr);

                            Handler handler = new Handler() {
                                public void handleMessage(android.os.Message msg) {
                                    String result = msg.obj.toString();
                                    try {
                                        JSONObject jObject = new JSONObject(result);
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
                            };

                            new UrlThread(handler, getPicUploadUrl(), params, 1).start();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
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

	private void doHttpUploadPic(String imgFile) {
		mPicData.remove(picPos);
		mPicData.add(picPos, imgFile);
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
                                mPicData.remove(picPos);
                                mPicData.add(picPos, jObject.getString("msg"));
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
}
