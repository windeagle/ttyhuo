package cn.ttyhuo.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;
import cn.ttyhuo.R;
import cn.ttyhuo.model.ImageModel;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PhotoUtils {

	public static Uri getCameraStrImgPathUri(Context context) {
		String strImgPath = "";
		strImgPath = Environment.getExternalStorageDirectory().toString()
				+ "/DCIM/Camera/";
		String str = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ ".jpg";
		File dir = new File(strImgPath);
		if (!dir.exists())
			dir.mkdirs();
		File file = new File(strImgPath, str);
		strImgPath += str;
		SharedUtils.setString(context, "imgFile", strImgPath);
		return Uri.fromFile(file);
	}

	public static String getPath(Context context, Uri uri) {
		String[] filePathColumn = { MediaStore.Images.Media.DATA };

		Cursor cursor = context.getContentResolver().query(uri, filePathColumn,
				null, null, null);
		cursor.moveToFirst();

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();

		return picturePath;
	}

	public static Bitmap decodeBitmap(String imgFile, int width, int height) {
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imgFile, o);
		int scale = 0;
		while ((o.outWidth >> scale) > width || (o.outHeight >> scale) > height) {
			scale++;
		}

		o.inSampleSize = 1 << scale;
		LogUtils.e("o.inSampleSize:" + o.inSampleSize);
		o.inJustDecodeBounds = false;

		return BitmapFactory.decodeFile(imgFile, o);
	}

	public static Bitmap decodeBitmap(String imgFile) {
		return decodeBitmap(imgFile, 200, 200);
	}

	public static void savePhotoToAlbum(final Context context, ImageView img) {
		if (!Environment.getExternalStorageState().equals("mounted")) {
			Toast.makeText(context, "您手机里没有内存卡，无法保存图片", 1).show();
			return;
		}

		Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
		if (bitmap != null) {
			new SaveTask(context).execute(bitmap);
			return;
		}
		Toast.makeText(context, "保存失败", 0).show();
	}

	private static class SaveTask extends AsyncTask<Bitmap, Void, Boolean> {
		private final Context mContext;

		public SaveTask(Context context) {
			mContext = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Bitmap... bitmaps) {
			Bitmap bitmap = bitmaps[0];
			try {
				File fileDir = new File(
						Environment.getExternalStorageDirectory(), "yellowpage");
				if (!fileDir.exists())
					fileDir.mkdirs();

				String filename = "yp" + System.currentTimeMillis() + ".jpg";
				File file = new File(fileDir, filename);
				FileOutputStream fileOutputStream = new FileOutputStream(file);

				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				fileOutputStream = null;

				ContentValues contentValues = new ContentValues(7);
				contentValues.put("title", filename);
				contentValues.put("_display_name", filename);
				contentValues.put("datetaken",
						Long.valueOf(System.currentTimeMillis()));
				contentValues.put("mime_type", "image/jpeg");
				contentValues.put("_data", file.getAbsolutePath());

				LogUtils.i("file.getAbsolutePath():" + file.getAbsolutePath());
				LogUtils.i("file.getAbsolutePath():" + Uri.fromFile(file));

				ContentResolver contentResolver = mContext.getContentResolver();

				String[] arrayOfString = new String[1];
				arrayOfString[0] = file.getAbsolutePath();

				Cursor cursor = contentResolver.query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
						"_data=?", arrayOfString, null);

				if (cursor.moveToFirst()) {
					long l = cursor.getLong(cursor.getColumnIndex("_id"));
					contentResolver.update(Uri.withAppendedPath(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
									+ l), contentValues, null, null);
				} else {
					contentResolver.insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							contentValues);
				}

				cursor.close();
				return true;

			} catch (Exception exception) {
			}
			return false;
		}

		@Override
		public void onPostExecute(Boolean paramBoolean) {
			if (paramBoolean) {
				Toast.makeText(mContext, "保存成功", 0).show();
				return;
			}
			Toast.makeText(mContext, "保存失败", 0).show();
		}

	}

	public static void sharePhoto(final Context context, ImageView img,
			ImageModel model) {
		if (!Environment.getExternalStorageState().equals("mounted")) {
			Toast.makeText(context, "您手机里没有内存卡，无法分享图片", 1).show();
			return;
		}

		Bitmap bitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
		if (bitmap != null) {
			new ShareTask(context, model).execute(bitmap);
			return;
		}
		Toast.makeText(context, "分享失败", 0).show();
	}

	private static class ShareTask extends AsyncTask<Bitmap, Void, Boolean> {
		private final Context mContext;
		private Uri mUri;
		private final ImageModel mModel;

		public ShareTask(Context context, ImageModel model) {
			mContext = context;
			mModel = model;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Boolean doInBackground(Bitmap... bitmaps) {
			Bitmap bitmap = bitmaps[0];
			try {
				File fileDir = new File(
						Environment.getExternalStorageDirectory(), "yellowpage");
				if (!fileDir.exists())
					fileDir.mkdirs();

				String filename = "yp" + System.currentTimeMillis() + ".jpg";
				File file = new File(fileDir, filename);
				FileOutputStream fileOutputStream = new FileOutputStream(file);

				bitmap.compress(Bitmap.CompressFormat.JPEG, 100,
						fileOutputStream);
				fileOutputStream.close();
				fileOutputStream = null;

				ContentValues contentValues = new ContentValues(7);
				contentValues.put("title", filename);
				contentValues.put("_display_name", filename);
				contentValues.put("datetaken",
						Long.valueOf(System.currentTimeMillis()));
				contentValues.put("mime_type", "image/jpeg");
				contentValues.put("_data", file.getAbsolutePath());

				mUri = Uri.fromFile(file);
				LogUtils.i("file.getAbsolutePath():" + file.getAbsolutePath());
				LogUtils.i("file.getAbsolutePath():" + Uri.fromFile(file));

				ContentResolver contentResolver = mContext.getContentResolver();

				String[] arrayOfString = new String[1];
				arrayOfString[0] = file.getAbsolutePath();

				Cursor cursor = contentResolver.query(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
						"_data=?", arrayOfString, null);

				if (cursor.moveToFirst()) {
					long l = cursor.getLong(cursor.getColumnIndex("_id"));
					contentResolver.update(Uri.withAppendedPath(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, ""
									+ l), contentValues, null, null);
				} else {
					contentResolver.insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							contentValues);
				}

				cursor.close();
				return true;

			} catch (Exception exception) {
			}
			return false;
		}

		@Override
		public void onPostExecute(Boolean paramBoolean) {
			if (paramBoolean) {
				Toast.makeText(mContext, "保存成功", 0).show();
				Intent shareIntent = new Intent();
				shareIntent.setAction(Intent.ACTION_SEND);
				shareIntent.putExtra(Intent.EXTRA_STREAM, mUri);

				StringBuilder text = new StringBuilder();
				text.append(mContext.getString(R.string.app_name) + "分享图片：");

				String name = mModel.getName();
				String content = mModel.getContent();

				if (!TextUtils.isEmpty(name)) {
					text.append(mModel.getName());
					if (!TextUtils.isEmpty(content)) {
						text.append(",");
					}
				}

				if (!TextUtils.isEmpty(content)) {
					text.append(content);
				}

				shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
				shareIntent.setType("image/png");
				mContext.startActivity(Intent.createChooser(shareIntent, "分享至"));

				return;
			}
			Toast.makeText(mContext, "保存失败", 0).show();
		}
	}

	public static InputStream Bitmap2InputStream(Bitmap bm) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bm.compress(CompressFormat.PNG, 95, out);
		InputStream inputStream = new ByteArrayInputStream(out.toByteArray());
		return inputStream;
	}
}
