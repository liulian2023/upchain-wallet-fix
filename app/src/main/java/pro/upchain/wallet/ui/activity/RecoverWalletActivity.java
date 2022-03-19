package pro.upchain.wallet.ui.activity;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.StatusBarUtils2;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.LoadWalletSelectStandardPopupWindow;

public class RecoverWalletActivity extends BaseActivity {

    @BindView(R.id.et_mnemonic)
    EditText et_mnemonic;
    @BindView(R.id.et_wallet_psd)
    EditText et_wallet_psd;
    @BindView(R.id.et_sure_wallet_psd)
    EditText et_sure_wallet_psd;
    @BindView(R.id.recover_wallet_btn)
    Button recover_wallet_btn;
    CreateWalletInteract createWalletInteract;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private int REQUEST_PERMISSION_SETTING = 112;
    private LoadWalletSelectStandardPopupWindow popupWindow;

    private String ethType = ETHWalletUtils.ETH_JAXX_TYPE;
    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_wallet;
    }

    @Override
    public void initToolBar() {
        StatusBarUtils2.setFullImage(this,et_mnemonic);
    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.recover_wallet_btn,R.id.recover_wallet_back_iv})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.recover_wallet_btn:
                String mnemonic = et_mnemonic.getText().toString().trim();
                String walletPwd = et_wallet_psd.getText().toString().trim();
                String confirmPwd = et_wallet_psd.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(mnemonic);
                if (verifyWalletInfo) {
//                    showDialog(getString(R.string.loading_wallet_tip));
//                    createWalletInteract.loadWalletByMnemonic(ethType, mnemonic, walletPwd).subscribe(this::loadSuccess, this::onError);
//                    CreatePinActivity.startAty(RecoverWalletActivity.this,mnemonic);
                    CreateWalletActivity.startAty(RecoverWalletActivity.this,mnemonic);
                    break;
                }
            case R.id.recover_wallet_back_iv:
                finish();
                break;
                    default:
                        break;
                }
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
/*    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    ToastUtils.showToast("The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission");
                }
            }
        }

    }*/
    private boolean verifyInfo(String mnemonic ) {
        if (TextUtils.isEmpty(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (!WalletDaoUtils.isValid(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (WalletDaoUtils.checkRepeatByMenmonic(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_already_exist);
            return false;
        }
//        else if (TextUtils.isEmpty(walletPwd)) {
//            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
//            // 同时判断强弱
//            return false;
//        }
/*        else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }*/
        return true;
    }

    public void onError(Throwable e) {
        ToastUtils.showToast(R.string.Failed_to_import_wallet);
        dismissDialog();
    }
}