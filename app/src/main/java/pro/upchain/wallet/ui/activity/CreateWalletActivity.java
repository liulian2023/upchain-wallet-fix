package pro.upchain.wallet.ui.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.HashMap;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.LoginEntity;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;


import butterknife.BindView;
import butterknife.OnClick;


public class CreateWalletActivity extends BaseActivity {

    private static final int CREATE_WALLET_RESULT = 2202;
    private static final int LOAD_WALLET_REQUEST = 1101;

/*    @BindView(R.id.tv_title)
    TextView tvTitle;*/
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;
/*    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;*/
/*    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;*/

    @BindView(R.id.btn_create_wallet)
    TextView btnCreateWallet;

    private CreateWalletInteract createWalletInteract;

    private static final int REQUEST_WRITE_STORAGE = 112;
    private int REQUEST_PERMISSION_SETTING = 112;

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public void initToolBar() {
//        tvTitle.setText(R.string.property_drawer_create_wallet);
    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {
/*        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btnCreateWallet.setEnabled(isChecked);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();

    }

    private void checkPermissions() {
        String[] PERMISSIONS = {WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE};
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEachCombined(PERMISSIONS)
                .subscribe(permission -> {
                    if (permission.granted) {
//        }
                    } else {
                        permissionFail();
                    }
                });
    }
    private void permissionFail() {
        if(!ActivityCompat.shouldShowRequestPermissionRationale(this,WRITE_EXTERNAL_STORAGE)||!ActivityCompat.shouldShowRequestPermissionRationale(this,READ_EXTERNAL_STORAGE)){
            //用户勾选了不再询问
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            isExit.setTitle(getResources().getString(R.string.Apply_Permission));
            isExit.setMessage(getResources().getString(R.string.disabling_permission));
            isExit.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel_btn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkPermissions();
                }
            });
            isExit.setButton(DialogInterface.BUTTON_POSITIVE,  getResources().getString(R.string.sure_btn), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                }
            });
            isExit.show();
        }else {
            checkPermissions();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_PERMISSION_SETTING){
            //从权限设置页面返回
            checkPermissions();
        }
    }

    @OnClick({/*R.id.tv_agreement,*/ R.id.create_wallet_back_iv,R.id.btn_create_wallet
            /*, R.id.lly_wallet_agreement*/})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_wallet_back_iv:
                finish();
                break;
            case R.id.tv_agreement:
                break;
            case R.id.btn_create_wallet:
                String walletName = etWalletName.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
//                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(walletName, walletPwd, confirmPwd, "");

                if (verifyWalletInfo) {
                    showDialog(getString(R.string.creating_wallet_tip));
                    createWalletInteract.create(walletName, walletPwd, confirmPwd, "")
                            .subscribe(CreateWalletActivity.this::jumpToWalletBackUp, CreateWalletActivity.this::showError);
                }
                break;
/*            case R.id.lly_wallet_agreement:
                if (cbAgreement.isChecked()) {
                    cbAgreement.setChecked(false);
                } else {
                    cbAgreement.setChecked(true);
                }
                break;*/
 /*           case R.id.btn_input_wallet:
                Intent intent = new Intent(this, ImportWalletActivity.class);
                startActivityForResult(intent, LOAD_WALLET_REQUEST);
                break;*/
        }
    }

    private boolean verifyInfo(String walletName, String walletPwd, String confirmPwd, String pwdReminder) {
        if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_repeat_tips);
            // 同时不可重复
            return false;
        } else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_input_tips);
            // 同时不可重复
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }


    public void showError(Throwable errorInfo) {
        dismissDialog();
        LogUtils.e("CreateWalletActivity", errorInfo);
        ToastUtils.showToast(errorInfo.toString());
    }

    public void jumpToWalletBackUp(ETHWallet wallet) {
        ToastUtils.showToast("创建钱包成功");
        dismissDialog();

        boolean firstAccount = getIntent().getBooleanExtra("first_account", false);

       HttpApiUtils.addAddress(this,null,wallet);

        setResult(CREATE_WALLET_RESULT, new Intent());
        Intent intent = new Intent(this, WalletBackupActivity.class);
        intent.putExtra("walletId", wallet.getId());
        intent.putExtra("walletPwd", wallet.getPassword());
        intent.putExtra("walletAddress", wallet.getAddress());
        intent.putExtra("walletName", wallet.getName());
        intent.putExtra("walletMnemonic", wallet.getMnemonic());
        intent.putExtra("first_account", true);
        startActivity(intent);

    }


}
