package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.InputPwdDialog;

import butterknife.BindView;
import butterknife.OnClick;
// 进行备份提示

public class WalletBackupActivity extends BaseActivity {

    @BindView(R.id.btn_backup)
    Button btnBackup;
    @BindView(R.id.backup_later_tv)
    TextView backup_later_btn;
    @BindView(R.id.imageView)
    ImageView imageView;

    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private String walletMnemonic;
    private long walletId;
    private boolean firstAccount;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_backup;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletPwd = intent.getStringExtra("walletPwd");
        walletAddress = intent.getStringExtra("walletAddress");
        walletName = intent.getStringExtra("walletName");
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        firstAccount = getIntent().getBooleanExtra("first_account", false);
        LogUtils.d("WalletBackupActivity", "walletMnemonic:" + walletMnemonic);
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.btn_backup, R.id.backup_later_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
/*                inputPwdDialog = new InputPwdDialog(this);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.isEmpty(pwd)) {
                            ToastUtils.showToast(R.string.input_pwd_dialog_tip);
                            return;
                        }
                        if (TextUtils.equals(pwd, walletPwd)) {

                            if (firstAccount) {
                                Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            Intent intent = new Intent(WalletBackupActivity.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletId", walletId);
                            intent.putExtra("walletMnemonic", walletMnemonic);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                inputPwdDialog.show();*/
                if (firstAccount) {
                    Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                Intent intent = new Intent(WalletBackupActivity.this, MnemonicBackupActivity.class);
                intent.putExtra("walletId", walletId);
                intent.putExtra("walletMnemonic", walletMnemonic);
                startActivity(intent);
                finish();
                break;
            case R.id.backup_later_tv:
                if (firstAccount) {
                    Intent intent1 = new Intent(WalletBackupActivity.this, MainActivity.class);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent1);
                }

                finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .titleBarMarginTop(imageView)
                .statusBarDarkFont(true)
                .init();
    }
}
