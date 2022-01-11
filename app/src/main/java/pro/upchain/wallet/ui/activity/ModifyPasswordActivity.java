package pro.upchain.wallet.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.ModifyWalletInteract;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.InputPwdDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class ModifyPasswordActivity extends BaseActivity {
    private static final int MODIFY_PWD_RESULT = 2201;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_old_pwd)
    EditText etOldPwd;
    @BindView(R.id.et_new_pwd)
    EditText etNewPwd;
    @BindView(R.id.et_new_pwd_again)
    EditText etNewPwdAgain;

    @BindView(R.id.tv_import_wallet)
    TextView tvImportWallet;
    @BindView(R.id.modify_pwd_finish_btn)
    Button modify_pwd_finish_btn;

    private long walletId;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private boolean walletIsBackup;
    private ModifyWalletInteract  modifyWalletInteract;
    private InputPwdDialog inputPwdDialog;
    private String walletMnemonic;
    ETHWallet ethWallet;



    @Override
    public int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.modify_password_title);
    }
    public static void startAty(Context context,ETHWallet ethWallet){
        Intent intent = new Intent(context, ModifyPasswordActivity.class);
        intent.putExtra("ethWallet",ethWallet);
        context.startActivity(intent);
    }
    public static void startAtyForResult(Activity activity, ETHWallet ethWallet, int modifyPasswordRequest) {
        Intent intent = new Intent(activity, ModifyPasswordActivity.class);
        intent.putExtra("ethWallet",ethWallet);
        activity.startActivityForResult(intent,modifyPasswordRequest);

    }
    @Override
    public void initDatas() {
        modifyWalletInteract = new ModifyWalletInteract();

        Intent intent = getIntent();
        ethWallet = (ETHWallet) intent.getSerializableExtra("ethWallet");

        walletId = ethWallet.getId();
        walletPwd = ethWallet.getPassword();
        walletAddress = ethWallet.getAddress();
        walletName = ethWallet.getName();
        walletIsBackup = ethWallet.getIsBackup();
        walletMnemonic = ethWallet.getMnemonic();
    }

    @Override
    public void configViews() {
    }


    @OnClick({R.id.tv_import_wallet, R.id.modify_pwd_finish_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_import_wallet:
                startActivity(new Intent(this, RecoverWalletActivity.class));
                finish();
                break;
            case R.id.modify_pwd_finish_btn:
                String oldPwd = etOldPwd.getText().toString().trim();
                String newPwd = etNewPwd.getText().toString().trim();
                String newPwdAgain = etNewPwdAgain.getText().toString().trim();
                if (verifyPassword(oldPwd, newPwd, newPwdAgain)) {
                    showDialog(getString(R.string.saving_wallet_tip));
                    modifyWalletInteract.modifyWalletPwd(walletId, walletName, oldPwd, newPwd).subscribe(this::modifyPwdSuccess);
                }
                break;
        }
    }

    private boolean verifyPassword(String oldPwd, String newPwd, String newPwdAgain) {
        if (!TextUtils.equals(oldPwd, walletPwd)) {
            ToastUtils.showToast(R.string.modify_password_alert4);
            return false;
        } else if (!TextUtils.equals(newPwd, newPwdAgain)) {
            ToastUtils.showToast(R.string.modify_password_alert5);
            return false;
        }
        return true;
    }

    public void modifySuccess() {

    }

    public void modifyPwdSuccess(ETHWallet ethWallet) {
        dismissDialog();
        ToastUtils.showToast(R.string.modify_password_success);
        Intent data = new Intent();
        data.putExtra("newPwd", ethWallet.getPassword());
        setResult(MODIFY_PWD_RESULT, data);
        finish();
    }

    public void showDerivePrivateKeyDialog(String privateKey) {

    }

    public void showDeriveKeystore(String keystore) {

    }

    public void deleteSuccess(boolean isDelete) {

    }

}
