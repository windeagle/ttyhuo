package cn.ttyhuo.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.View;

public class DialogUtils {

	public static Dialog createNormalDialog(Context context, int iconId,
			String title, String message, String btn1,
			OnClickListener listener1, String btn2, OnClickListener listener2) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);

		if (iconId != 0) {
			builder.setIcon(iconId);
		}

		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		if (!TextUtils.isEmpty(message)) {
			builder.setMessage(message);
		}

		if (!TextUtils.isEmpty(btn1)) {
			builder.setPositiveButton(btn1, listener1);
		}

		if (!TextUtils.isEmpty(btn2)) {
			builder.setNegativeButton(btn2, listener2);
		}

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createListDialog(Context context, int iconId,
			String title, int itemsId, OnClickListener listener) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		// 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
		builder.setItems(itemsId, listener);
		// 创建一个列表对话框
		dialog = builder.create();

		return dialog;
	}

	public static Dialog createListDialog(Context context, int iconId,
			String title, CharSequence[] items, OnClickListener listener) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		// 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
		builder.setItems(items, listener);
		// 创建一个列表对话框
		dialog = builder.create();
		return dialog;
	}

	public static Dialog createRadioDialog(Context context, int iconId,
			String title, int itemsId, OnClickListener listener,
			String btnName, OnClickListener listener2) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		// 0: 默认第一个单选按钮被选中
		builder.setSingleChoiceItems(itemsId, 0, listener);
		// 添加一个按钮
		builder.setPositiveButton(btnName, listener2);
		// 创建一个单选按钮对话框
		dialog = builder.create();
		return dialog;
	}

	public static Dialog createRadioDialog(Context context, int iconId,
			String title, CharSequence[] items, int position,
			OnClickListener listener, String btnName, OnClickListener listener2) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		// 0: 默认第一个单选按钮被选中
		builder.setSingleChoiceItems(items, position, listener);
		// 添加一个按钮
		builder.setPositiveButton(btnName, listener2);
		// 创建一个单选按钮对话框
		dialog = builder.create();
		return dialog;
	}

	public static Dialog createCheckBoxDialog(
			Context context,
			int iconId,
			String title,
			int itemsId,
			boolean[] flags,
			android.content.DialogInterface.OnMultiChoiceClickListener listener,
			String btnName, OnClickListener listener2) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		builder.setMultiChoiceItems(itemsId, flags, listener);
		// 添加一个按钮
		builder.setPositiveButton(btnName, listener2);
		// 创建一个复选对话框
		dialog = builder.create();
		return dialog;
	}

	public static Dialog createCheckBoxDialog(
			Context context,
			int iconId,
			String title,
			CharSequence[] items,
			boolean[] flags,
			android.content.DialogInterface.OnMultiChoiceClickListener listener,
			String btnName, OnClickListener listener2, String btnName2,
			OnClickListener listener3) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);
		// 设置对话框的图标
		builder.setIcon(iconId);
		// 设置对话框的标题
		builder.setTitle(title);
		builder.setMultiChoiceItems(items, flags, listener);

		// 添加一个按钮
		builder.setPositiveButton(btnName, listener2);
		builder.setNegativeButton(btnName2, listener3);
		// 创建一个复选对话框
		dialog = builder.create();
		return dialog;
	}

	public static Dialog createCustomDialog(Context context, int iconId,
			String title, View v, String btnName, OnClickListener listener,
			String btnName2, OnClickListener listener2) {
		Dialog dialog = null;
		android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(
				context);

		if (iconId != 0) {
			// 设置对话框的图标
			builder.setIcon(iconId);
		}

		// 设置对话框的标题
		builder.setTitle(title);
		// 设置对话框的显示内容
		builder.setView(v);
		// 添加按钮，android.content.DialogInterface.OnClickListener.OnClickListener
		builder.setPositiveButton(btnName, listener);
		builder.setNegativeButton(btnName2, listener2);

		// 创建一个普通对话框
		dialog = builder.create();
		return dialog;
	}

}