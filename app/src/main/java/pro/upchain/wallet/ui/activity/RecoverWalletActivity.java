package pro.upchain.wallet.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gyf.barlibrary.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.utils.ETHWalletUtils;
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

    private LoadWalletSelectStandardPopupWindow popupWindow;

    private String ethType = ETHWalletUtils.ETH_JAXX_TYPE;
    @Override
    public int getLayoutId() {
        return R.layout.activity_recover_wallet;
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this).transparentBar().init();
    }

    @OnClick({R.id.recover_wallet_btn,R.id.recover_wallet_back_iv})
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.recover_wallet_btn:
                String mnemonic = et_mnemonic.getText().toString().trim();
                String walletPwd = et_wallet_psd.getText().toString().trim();
                String confirmPwd = et_wallet_psd.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(mnemonic, walletPwd, confirmPwd);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    createWalletInteract.loadWalletByMnemonic(ethType, mnemonic, walletPwd).subscribe(this::loadSuccess, this::onError);
                    break;
                }
            case R.id.recover_wallet_back_iv:
                finish();
                break;
                    default:
                        break;
                }


        }
    private boolean verifyInfo(String mnemonic, String walletPwd, String confirmPwd ) {
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
        else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }
    public void loadSuccess(ETHWallet wallet) {
        dismissDialog();
        ToastUtils.showToast("导入钱包成功");
        setResult(RESULT_OK);
        finish();
    }

    public void onError(Throwable e) {
        ToastUtils.showToast("导入钱包失败");
        dismissDialog();
    }
}