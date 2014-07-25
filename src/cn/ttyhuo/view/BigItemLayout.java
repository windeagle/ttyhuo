package cn.ttyhuo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
public class BigItemLayout extends LinearLayout {
    private TextView bigItemText;
    private ImageView bigItemIcon;
    private LinearLayout ly_harfline;

    public BigItemLayout(Context context) {
        super(context);
    }

    public BigItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ctl_big_item_layout, this);
        bigItemText = (TextView) findViewById(R.id.big_item_text);
        bigItemIcon = (ImageView) findViewById(R.id.big_item_icon);
        ly_harfline=(LinearLayout)findViewById(R.id.ly_harfline);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.BigItemLayout);
        setBigItemText(a.getString(R.styleable.BigItemLayout_bigItemText));
        setBigItemIcon(a.getResourceId(R.styleable.BigItemLayout_bigItemIcon, R.drawable.item_icon_info));
        if(!a.getBoolean(R.styleable.BigItemLayout_showHarfLine, true))
            ly_harfline.setVisibility(View.GONE);
    }

    public void setBigItemText(String bigItemTxt) {
        bigItemText.setText(bigItemTxt);
    }

    public void setBigItemIcon(int resId) {
        bigItemIcon.setImageResource(resId);
    }
}
