package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.View;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;



public class GuideActivity extends BaseActivity  {
    public static int TO_IMPORT_WALLET = 111;
    @Override
    public int getLayoutId() {
        return R.layout.activity_guide;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        ImmersionBar.with(this).transparentBar().init();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TO_IMPORT_WALLET){
            if(resultCode == RESULT_OK){
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                GuideActivity.this.startActivity(intent);
            }
        }
    }

    @OnClick({R.id.create_wallet_btn,R.id.recover_wallet_tv})
    public void  onClick(View view){
    switch (view.getId()){
        case R.id.create_wallet_btn:
            Intent intent = new Intent(this, CreateWalletActivity.class);
            intent.putExtra("first_account", true);
            startActivity(intent);
            break;
        case R.id.recover_wallet_tv:
            intent = new Intent(this, RecoverWalletActivity.class);
            startActivityForResult(intent,TO_IMPORT_WALLET);
            break;
        default:
            break;
    }
    }

}


