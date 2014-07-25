package cn.ttyhuo.activity.product;

import cn.ttyhuo.R;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 14-7-4
 * Time: 上午1:06
 * To change this template use File | Settings | File Templates.
 */
public class MyProductInfoActivity extends ProductInfoActivity {

    @Override
    protected int getLayoutResID()
    {
        isMy = true;
        return R.layout.activity_myproduct_info;
    }
}
