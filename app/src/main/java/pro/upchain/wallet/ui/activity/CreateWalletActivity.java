package pro.upchain.wallet.ui.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static pro.upchain.wallet.utils.StatusBarUtils2.getStatusBarHeight;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.LoginEntity;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.StatusBarUtils2;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;


import butterknife.BindView;
import butterknife.OnClick;


public class CreateWalletActivity extends BaseActivity {

    private static final int CREATE_WALLET_RESULT = 2202;
    private static final int LOAD_WALLET_REQUEST = 1101;

/*    @BindView(R.id.tv_title)
    TextView tvTitle;*/
    @BindView(R.id.wallet_name_clear_iv)
    ImageView wallet_name_clear_iv;
    @BindView(R.id.available_iv)
    ImageView available_iv;
    @BindView(R.id.available_tv)
    TextView available_tv;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
/*    @BindView(R.id.et_wallet_pwd)
    EditText etWalletPwd;
    @BindView(R.id.et_wallet_pwd_again)
    EditText etWalletPwdAgain;*/
/*    @BindView(R.id.et_wallet_pwd_reminder_info)
    EditText etWalletPwdReminderInfo;*/
/*    @BindView(R.id.cb_agreement)
    CheckBox cbAgreement;*/

    @BindView(R.id.btn_create_wallet)
    Button btn_create_wallet;
    MyHandler myHandler;
    private CreateWalletInteract createWalletInteract;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private int REQUEST_PERMISSION_SETTING = 112;
    static int EDIT_OK = 111;
    boolean nameIsAvailable = false;
    @Override
    public int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    public void initToolBar() {
//        tvTitle.setText(R.string.property_drawer_create_wallet);
        StatusBarUtils2.setFullImage(this,btn_create_wallet);
    }

    @Override
    public void initDatas() {
        myHandler = new MyHandler();
        createWalletInteract = new CreateWalletInteract();
        etWalletName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String toString = etWalletName.getText().toString();
                if(StringMyUtil.isNotEmpty(toString)){
                    wallet_name_clear_iv.setVisibility(View.VISIBLE);
                }else {
                    wallet_name_clear_iv.setVisibility(View.GONE);
                }
                myHandler.removeCallbacks(gasRunnable);
                    myHandler.postDelayed(gasRunnable,500);
            }
        });
    }
    private Runnable gasRunnable = new Runnable() {
        @Override
        public void run() {
            myHandler.sendEmptyMessage(EDIT_OK);
        }
    };
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if(EDIT_OK == msg.what){
                String name = etWalletName.getText().toString();
                if(StringMyUtil.isNotEmpty(name)){
                    available_iv.setVisibility(View.VISIBLE);
                    available_tv.setVisibility(View.VISIBLE);
                    boolean nameChecking = WalletDaoUtils.walletNameChecking(name);
                    if(nameChecking){
                        initNameIsAvailable(R.drawable.unavailable, R.string.name_un_available, "#DF5F67");
                    }else {
                        if(name.trim().length()<5 || name.trim().length()>20){
                            initNameIsAvailable(R.drawable.unavailable, R.string.name_un_available, "#DF5F67");
                        }else {
                            if(!Utils.isContainsLetter(name)){
                                initNameIsAvailable(R.drawable.unavailable, R.string.name_un_available, "#DF5F67");
                            }else {
                                nameIsAvailable = true;
                                initNameIsAvailable(R.drawable.available, R.string.name_available, "#05B169");
                            }
                        }
                    }
                }else {
                    available_iv.setVisibility(View.GONE);
                    available_tv.setVisibility(View.GONE);
                }

            }
        }
    }

    private void initNameIsAvailable(int p, int p2, String s) {
        available_iv.setImageResource(p);
        available_tv.setText(getString(p2));
        available_tv.setTextColor(Color.parseColor(s));
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

    @OnClick({/*R.id.tv_agreement,*/ R.id.create_wallet_back_iv,R.id.btn_create_wallet,R.id.wallet_name_clear_iv
            /*, R.id.lly_wallet_agreement*/})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wallet_name_clear_iv:
                etWalletName.setText("");
                break;

            case R.id.create_wallet_back_iv:
                finish();
                break;
            case R.id.tv_agreement:
                break;
            case R.id.btn_create_wallet:
                String walletName = etWalletName.getText().toString().trim();
//                String walletPwd = etWalletPwd.getText().toString().trim();
//                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(walletName);

                if (verifyWalletInfo) {
/*                    showDialog(getString(R.string.creating_wallet_tip));
                    createWalletInteract.create(walletName, walletPwd, confirmPwd, "")
                            .subscribe(CreateWalletActivity.this::jumpToWalletBackUp, CreateWalletActivity.this::showError);*/

                    CreatePinActivity.startAty(CreateWalletActivity.this,etWalletName.getText().toString().trim(),true);
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

    private boolean verifyInfo(String walletName) {
        if (WalletDaoUtils.walletNameChecking(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_repeat_tips);
            // 同时不可重复
            return false;
        }else if (TextUtils.isEmpty(walletName)) {
            ToastUtils.showToast(R.string.create_wallet_name_input_tips);
            // 同时不可重复
            return false;
        }else if(!nameIsAvailable){
            ToastUtils.showToast(getString(R.string.name_un_available));
            return  false;
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
