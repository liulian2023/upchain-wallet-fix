package pro.upchain.wallet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.CommonToolBarUtils;
import pro.upchain.wallet.utils.LocalManageUtil;
import pro.upchain.wallet.utils.ToastUtils;

public class ChangeLanguageActivity extends BaseActivity {



    @Override
    public int getLayoutId() {
        return R.layout.activity_change_language;
    }

    @Override
    public void initToolBar() {
        CommonToolBarUtils.initToolbar(this,R.string.change_language);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }
    @OnClick({R.id.chinese_tv,R.id.english_tv,R.id.yuenan_tv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.chinese_tv:
                LocalManageUtil.saveSelectLanguage(this,"zh");
                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            case R.id.english_tv:
                LocalManageUtil.saveSelectLanguage(this,"en");
                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            case R.id.yuenan_tv:
                LocalManageUtil.saveSelectLanguage(this,"vi");
                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            default:
                break;
        }
    }
}