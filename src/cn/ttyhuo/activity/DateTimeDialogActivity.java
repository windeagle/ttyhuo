package cn.ttyhuo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import cn.ttyhuo.R;
import cn.ttyhuo.helper.DateTimeOnClickListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateTimeDialogActivity extends Activity {

    private final int DATE_DIALOG = 1;

    private final int TIME_DIALOG = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_time_dialog_layout);

        setupEditAndBtn(R.id.editText, R.id.btnDate, R.id.btnTime);
        setupEditAndBtn(R.id.editText0, R.id.btnDate0, R.id.btnTime0);
    }

    private void setupEditAndBtn(int editTextId, int btnDateId, int btnTimeId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();

        EditText editText =
                (EditText) findViewById(editTextId);
        editText.setText(sdf.format(calendar.getTime()));

        View.OnClickListener dateBtnListener =
                new DateTimeOnClickListener(DATE_DIALOG, this, editText);
        Button btnDate = (Button) findViewById(btnDateId);
        btnDate.setOnClickListener(dateBtnListener);

        View.OnClickListener timeBtnListener =
                new DateTimeOnClickListener(TIME_DIALOG, this, editText);
        Button btnTime = (Button) findViewById(btnTimeId);
        btnTime.setOnClickListener(timeBtnListener);
    }
}