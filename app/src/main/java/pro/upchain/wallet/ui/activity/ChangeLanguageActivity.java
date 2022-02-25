package pro.upchain.wallet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.CommonToolBarUtils;
import pro.upchain.wallet.utils.LocalManageUtil;
import pro.upchain.wallet.utils.ToastUtils;

public class ChangeLanguageActivity extends BaseActivity {
    @BindView(R.id.yuenan_iv)
    ImageView yuenan_iv;
    @BindView(R.id.chinese_iv)
    ImageView chinese_iv;
    @BindView(R.id.english_iv)
    ImageView english_iv;

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
        initLocalLanguage();
    }
    private void initLocalLanguage(){
        String localSaveLanguage = LocalManageUtil.getLocalSaveLanguage(this);
        if(localSaveLanguage.equals("en")){
            yuenan_iv.setVisibility(View.GONE);
            chinese_iv.setVisibility(View.GONE);
            english_iv.setVisibility(View.VISIBLE);
        }else if(localSaveLanguage.equals("zh-CN")){
            yuenan_iv.setVisibility(View.GONE);
            chinese_iv.setVisibility(View.VISIBLE);
            english_iv.setVisibility(View.GONE);
        }else {
            yuenan_iv.setVisibility(View.VISIBLE);
            chinese_iv.setVisibility(View.GONE);
            english_iv.setVisibility(View.GONE);
        }
    }
    @Override
    public void configViews() {

    }
    @OnClick({R.id.chinese_tv,R.id.english_tv,R.id.yuenan_tv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.chinese_tv:
                LocalManageUtil.saveSelectLanguage(this,"zh");
                initLocalLanguage();
//                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            case R.id.english_tv:
                LocalManageUtil.saveSelectLanguage(this,"en");
                initLocalLanguage();
//                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            case R.id.yuenan_tv:
                LocalManageUtil.saveSelectLanguage(this,"vi");
                initLocalLanguage();
//                ToastUtils.showToast(R.string.change_ssuccess);
                startActivity(new Intent(ChangeLanguageActivity.this,SplashActivity.class));
                break;
            default:
                break;
        }
    }
}