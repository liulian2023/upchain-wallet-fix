package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.ui.activity.ConfirmPinActivity.TO_IMPORT_WALLET;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.StatusBarUtils2;

public class CreatePinActivity extends BaseActivity {
    @BindView(R.id.create_pin_back_iv)
    ImageView create_pin_back_iv;

    public static void startAty (Context context, String mnemonic,String walletName){
        Intent intent = new Intent(context, CreatePinActivity.class);
        intent.putExtra("mnemonic",mnemonic);
        intent.putExtra("walletName",walletName);
        context.startActivity(intent);
    }

    public static void startAty (Context context,String walletName,boolean isCreate){
        Intent intent = new Intent(context, CreatePinActivity.class);
        intent.putExtra("walletName",walletName) ;
        intent.putExtra("isCreate",isCreate) ;
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_cteate_pin;
    }

    @Override
    public void initToolBar() {
        StatusBarUtils2.setFullImage(this,create_pin_back_iv);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == TO_IMPORT_WALLET){
            if(resultCode == RESULT_OK){
                Intent intent = new Intent(CreatePinActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                CreatePinActivity.this.startActivity(intent);
            }
        }
    }
    @OnClick({R.id.create_pin_back_iv,R.id.create_pin_constraintLayout})
    public void  onClick(View view){
        switch (view.getId()){
            case R.id.create_pin_back_iv:
                hideSoftInputView();
                finish();
                break;
            case R.id.create_pin_constraintLayout:
                if(getIntent().getBooleanExtra("isCreate",false)){
                    ConfirmPinActivity.startAty(CreatePinActivity.this,getIntent().getStringExtra("walletName"),true);
                }else {
                    ConfirmPinActivity.startAty(CreatePinActivity.this,getIntent().getStringExtra("mnemonic"),getIntent().getStringExtra("walletName"));
                }
                break;
            default:
                break;
        }
    }
}