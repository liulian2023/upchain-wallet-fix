package pro.upchain.wallet.ui.activity;


import android.view.View;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.CommonToolBarUtils;

public class LegalActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_legal;
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
    @OnClick({R.id.private_relativeLayout,R.id.terms_relativeLayout})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.private_relativeLayout:
                AboutUsActivity.startAty(3,LegalActivity.this);
                break;
            case R.id.terms_relativeLayout:
                AboutUsActivity.startAty(4,LegalActivity.this);
                break;
            default:
                break;
        }
    }
}