package pro.upchain.wallet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.CommonToolBarUtils;

public class AboutUsActivity extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initToolBar() {
        CommonToolBarUtils.initToolbar(this,R.string.about_us);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }
}