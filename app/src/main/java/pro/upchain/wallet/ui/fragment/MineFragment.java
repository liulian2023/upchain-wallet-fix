package pro.upchain.wallet.ui.fragment;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.barlibrary.ImmersionBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.ui.activity.AboutUsActivity;
import pro.upchain.wallet.ui.activity.ChangeLanguageActivity;
import pro.upchain.wallet.ui.activity.MnemonicBackupActivity;
import pro.upchain.wallet.ui.activity.ModifyPasswordActivity;
import pro.upchain.wallet.ui.activity.NetSettingActivity;
import pro.upchain.wallet.ui.activity.WalletMangerActivity;

import butterknife.OnClick;
import pro.upchain.wallet.utils.Store;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;


public class MineFragment extends BaseFragment {
    @BindView(R.id.backup_mnemonic_iv)
    ImageView backup_mnemonic_iv;
    @BindView(R.id.mine_title_tv)
    TextView mine_title_tv;
    @BindView(R.id.mine_wrap_linear)
    LinearLayout mine_wrap_linear;
    @BindView(R.id.wallet_first_name_tv)
    TextView wallet_first_name_tv;
    @BindView(R.id.wallet_name_tv)
    TextView wallet_name_tv;
    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;
    private ETHWallet currEthWallet;
    public static final String ACTION ="change_language.action";
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this.getActivity(), tokensViewModelFactory)
                .get(TokensViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        ImmersionBar.with(this)
                .statusBarColor(R.color.home_main_color)
                .titleBarMarginTop(mine_wrap_linear)
                .statusBarDarkFont(false)
                .init();
        tokensViewModel.defaultWallet().observe(this,  this::showWallet);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        ImmersionBar.with(this)
                .statusBarColor(R.color.home_main_color)
                .titleBarMarginTop(mine_wrap_linear)
                .statusBarDarkFont(false)
                .init();
    }

    @Override
    public void configViews() {
    }
    public void showWallet(ETHWallet wallet) {
        currEthWallet = wallet;
//        EventBus.getDefault().postSticky(new WalletInfoEvenEntity(wallet));
        //       openOrCloseDrawerLayout();
        boolean backup = currEthWallet.isBackup();
        if(backup){
            backup_mnemonic_iv.setVisibility(View.GONE);
        }else {
            backup_mnemonic_iv.setVisibility(View.VISIBLE);
        }
        String name = currEthWallet.getName();
        name = StringMyUtil.isEmptyString(name)?"UnKnow":name;
        wallet_name_tv.setText(name);
        wallet_first_name_tv.setText(name.substring(0,1));
    }
    @OnClick({R.id.backup_mnemonic_relativeLayout, R.id.transaction_password_relativeLayout, R.id.system_version_relativeLayout,
            R.id.about_us_relativeLayout, R.id.wallet_manager_relativeLayout, R.id.contact_us_relativeLayout,R.id.change_language_relativeLayout,
            R.id.switch_net_relativeLayout})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.switch_net_relativeLayout:
                intent = new Intent(getContext(), NetSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.change_language_relativeLayout:
                startActivity(new Intent(getContext(), ChangeLanguageActivity.class));
                break;
            case R.id.wallet_manager_relativeLayout:
                intent = new Intent(getActivity(), WalletMangerActivity.class);
                startActivity(intent);
                break;
     /*       case R.id.lly_system_setting:
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
                break;*/
 /*           case R.id.lly_trade_recode:
                intent = new Intent(getActivity(), TransactionsActivity.class);
                startActivity(intent);
                break;*/
      /*      case R.id.ask_help:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);

                break;*/
            case R.id.backup_mnemonic_relativeLayout:
                if(currEthWallet.isBackup()){
                    ToastUtils.showToast(R.string.mnemonic_backed_up);
                    return;
                }
                Intent intent1 = new Intent(getContext(), MnemonicBackupActivity.class);
                intent1.putExtra("walletId", currEthWallet.getId());
                intent1.putExtra("walletMnemonic", currEthWallet.getMnemonic());
                startActivity(intent1);
                break;
            case R.id.transaction_password_relativeLayout:
                if(currEthWallet==null){
                    return;
                }
                ModifyPasswordActivity.startAty(getContext(),currEthWallet);
                break;
            case R.id.about_us_relativeLayout:
                startActivity(new Intent(getContext(), AboutUsActivity.class));
                break;
            case R.id.contact_us_relativeLayout:

                break;
            default:
                break;

        }
    }
}
