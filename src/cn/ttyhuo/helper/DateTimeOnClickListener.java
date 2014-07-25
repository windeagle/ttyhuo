package cn.ttyhuo.helper;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
* Created with IntelliJ IDEA.
* User: Andrew
* Date: 14-7-13
* Time: 上午10:44
* To change this template use File | Settings | File Templates.
*/ /*
 * 成员内部类,此处为提高可重用性，也可以换成匿名内部类
 */
public class DateTimeOnClickListener implements View.OnClickListener {

    private int dialogId = 0;   //默认为0则不显示对话框
    private final int DATE_DIALOG = 1;
    private final int TIME_DIALOG = 2;

    private Context context;
    private TextView editText;
    private String dateStr;
    private String timeStr;

    public DateTimeOnClickListener(int dialogId, Context context, TextView editText) {
        this.dialogId = dialogId;
        this.context=context;
        this.editText=editText;
    }

    @Override
    public void onClick(View view) {
        Dialog d = createDialog(dialogId);
        if(d != null)
            d.show();
    }

    protected Dialog createDialog(int id) {
        //用来获取日期和时间的
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        final SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");
        final SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm");
        final Calendar calendar = Calendar.getInstance();
        try{
            calendar.setTime(sdf.parse(editText.getText().toString()));
            dateStr = dateSdf.format(calendar.getTime());
            timeStr = timeSdf.format(calendar.getTime());
        }
        catch (Exception e)
        {
        }

        Dialog dialog = null;
        switch(id) {
            case DATE_DIALOG:
                DatePickerDialog.OnDateSetListener dateListener =
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker,
                                                  int year, int month, int dayOfMonth) {
                                dateStr = year + "-" + (month+1) + "-" + dayOfMonth;
                                if(dateStr == null || dateStr.isEmpty())
                                    dateStr = dateSdf.format(calendar.getTime());
                                if(timeStr == null || timeStr.isEmpty())
                                    timeStr = timeSdf.format(calendar.getTime());
                                editText.setText(dateStr + " " + timeStr);
                            }
                        };
                dialog = new DatePickerDialog(context,
                        dateListener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        createDialog(TIME_DIALOG).show();
                    }
                });
                break;
            case TIME_DIALOG:
                TimePickerDialog.OnTimeSetListener timeListener =
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker timerPicker,
                                                  int hourOfDay, int minute) {
                                timeStr = hourOfDay + ":" + minute;
                                if(dateStr == null || dateStr.isEmpty())
                                    dateStr = dateSdf.format(calendar.getTime());
                                if(timeStr == null || timeStr.isEmpty())
                                    timeStr = timeSdf.format(calendar.getTime());
                                editText.setText(dateStr + " " + timeStr);
                            }
                        };
                dialog = new TimePickerDialog(context, timeListener,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true);   //是否为二十四制
                break;
            default:
                break;
        }
        return dialog;
    }
}
