package cn.ttyhuo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ttyhuo.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-3
 * Time: 下午9:37
 * To change this template use File | Settings | File Templates.
 */
public class FieldLayout extends LinearLayout {
    private TextView fieldName;
    private TextView fieldValue;
    private LinearLayout ly_harfline;

    public FieldLayout(Context context) {
        super(context);
    }

    public FieldLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ctl_field_layout, this);
        fieldName=(TextView) findViewById(R.id.fieldName);
        fieldValue=(TextView)findViewById(R.id.fieldValue);
        ly_harfline=(LinearLayout)findViewById(R.id.ly_harfline);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.FieldLayout);
        setFieldNameText(a.getString(R.styleable.FieldLayout_fieldName));
        if(!a.getBoolean(R.styleable.FieldLayout_showLine, true))
            ly_harfline.setVisibility(View.GONE);
    }

    public void setFieldNameText(String fieldNameText) {
        fieldName.setText(fieldNameText);
    }

    public void setFieldValueText(String fieldValueText) {
        fieldValue.setText(fieldValueText);
    }
}
