package cn.ttyhuo.view;

import java.lang.reflect.Field;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import cn.ttyhuo.utils.LogUtils;

public class YearDialog extends DatePickerDialog {
	public YearDialog(Context context, OnDateSetListener callBack, int year,
			int monthOfYear) {
		super(context, callBack, year, monthOfYear, 3);
		this.setTitle(year + "年" + (monthOfYear + 1) + "月");
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		super.onDateChanged(view, year, month, day);
		this.setTitle(year + "年" + (month + 1) + "月");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.DatePickerDialog#show()
	 */
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		DatePicker datePicker = findDatePicker((ViewGroup) this.getWindow()
				.getDecorView());
		if (datePicker != null) {
			Class pickerClass = datePicker.getClass();
			Field[] fields = pickerClass.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				LogUtils.d(fieldName);
				// if (field.getName().equals("mYearPicker")) ||
				// field.getName().equals("mYearSpinner")))
				if ("mDayPicker".equals(fieldName)
						|| "mDaySpinner".equals(fieldName)
						|| "mMonthSpinner".equals(fieldName)
						|| "mMonthPicker".equals(fieldName)) {
					field.setAccessible(true);
					try {
						View dayView = (View) field.get(datePicker);
						dayView.setVisibility(View.GONE);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	/**
	 * 从当前Dialog中查找DatePicker子控件
	 * 
	 * @param group
	 * @return
	 */
	private DatePicker findDatePicker(ViewGroup group) {
		if (group != null) {
			for (int i = 0, j = group.getChildCount(); i < j; i++) {
				View child = group.getChildAt(i);
				if (child instanceof DatePicker) {
					return (DatePicker) child;
				} else if (child instanceof ViewGroup) {
					DatePicker result = findDatePicker((ViewGroup) child);
					if (result != null)
						return result;
				}
			}
		}
		return null;

	}

}
