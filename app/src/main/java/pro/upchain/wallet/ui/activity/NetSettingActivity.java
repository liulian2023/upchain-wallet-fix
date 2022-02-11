package pro.upchain.wallet.ui.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.repository.EthereumNetworkRepository;


import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.SharePreferencesUtil;

import static pro.upchain.wallet.C.BSC_MAIN_NETWORK_NAME;
import static pro.upchain.wallet.C.BSC_TEST_NETWORK_NAME;
import static pro.upchain.wallet.C.ETHEREUM_MAIN_NETWORK_NAME;
import static pro.upchain.wallet.C.KOVAN_NETWORK_NAME;
import static pro.upchain.wallet.C.LOCAL_DEV_NETWORK_NAME;
import static pro.upchain.wallet.C.ROPSTEN_NETWORK_NAME;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class NetSettingActivity extends BaseActivity {


    EthereumNetworkRepository ethereumNetworkRepository;

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @BindView(R.id.iv_mainnet)
    ImageView ivMainnet;

    @BindView(R.id.iv_kovan)
    ImageView ivKovan;

    @BindView(R.id.iv_ropsten)
    ImageView ivRopsten;


    @BindView(R.id.iv_local_dev)
    ImageView ivLocalDev;

    @BindView(R.id.iv_bsc_main)
    ImageView iv_bsc_main;

    @BindView(R.id.iv_bsc_test)
    ImageView iv_bsc_test;
    private String networkName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_net_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_net);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        ethereumNetworkRepository = MyApplication.repositoryFactory().ethereumNetworkRepository;

        networkName = ethereumNetworkRepository.getDefaultNetwork().name;

        if (ETHEREUM_MAIN_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.VISIBLE);
            ivKovan.setVisibility(View.GONE);
            ivRopsten.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.GONE);
            iv_bsc_main.setVisibility(View.GONE);
            iv_bsc_test.setVisibility(View.GONE);
        } else if (KOVAN_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.GONE);
            ivKovan.setVisibility(View.VISIBLE);
            ivRopsten.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.GONE);
            iv_bsc_main.setVisibility(View.GONE);
            iv_bsc_test.setVisibility(View.GONE);
        } else if (ROPSTEN_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.GONE);
            ivKovan.setVisibility(View.GONE);
            ivRopsten.setVisibility(View.VISIBLE);
            ivLocalDev.setVisibility(View.GONE);
            iv_bsc_main.setVisibility(View.GONE);
            iv_bsc_test.setVisibility(View.GONE);
        } else if (LOCAL_DEV_NETWORK_NAME.equals(networkName)) {
            ivMainnet.setVisibility(View.GONE);
            ivKovan.setVisibility(View.GONE);
            ivRopsten.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.VISIBLE);
            iv_bsc_main.setVisibility(View.GONE);
            iv_bsc_test.setVisibility(View.GONE);
        }else if(BSC_MAIN_NETWORK_NAME.equals(networkName)){
            ivMainnet.setVisibility(View.GONE);
            ivKovan.setVisibility(View.GONE);
            ivRopsten.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.GONE);
            iv_bsc_main.setVisibility(View.VISIBLE);
            iv_bsc_test.setVisibility(View.GONE);

        }else if(BSC_TEST_NETWORK_NAME.equals(networkName)){
            ivMainnet.setVisibility(View.GONE);
            ivKovan.setVisibility(View.GONE);
            ivRopsten.setVisibility(View.GONE);
            ivLocalDev.setVisibility(View.GONE);
            iv_bsc_main.setVisibility(View.GONE);
            iv_bsc_test.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_mainnet, R.id.rl_kovan, R.id.rl_ropsten, R.id.rl_local_dev, R.id.rl_btn,R.id.rl_sbc_main,R.id.rl_sbc_test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_mainnet:
                networkName = ETHEREUM_MAIN_NETWORK_NAME;
                ivMainnet.setVisibility(View.VISIBLE);
                ivKovan.setVisibility(View.GONE);
                ivRopsten.setVisibility(View.GONE);
                ivLocalDev.setVisibility(View.GONE);
                iv_bsc_main.setVisibility(View.GONE);
                iv_bsc_test.setVisibility(View.GONE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"2");
                break;
            case R.id.rl_kovan:
                networkName = KOVAN_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivKovan.setVisibility(View.VISIBLE);
                ivRopsten.setVisibility(View.GONE);
                ivLocalDev.setVisibility(View.GONE);
                iv_bsc_main.setVisibility(View.GONE);
                iv_bsc_test.setVisibility(View.GONE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"2");
                break;
            case R.id.rl_ropsten:
                networkName = ROPSTEN_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivKovan.setVisibility(View.GONE);
                ivRopsten.setVisibility(View.VISIBLE);
                ivLocalDev.setVisibility(View.GONE);
                iv_bsc_main.setVisibility(View.GONE);
                iv_bsc_test.setVisibility(View.GONE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"2");

                break;
            case R.id.rl_local_dev:
                 networkName = LOCAL_DEV_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivKovan.setVisibility(View.GONE);
                ivRopsten.setVisibility(View.GONE);
                ivLocalDev.setVisibility(View.VISIBLE);
                iv_bsc_main.setVisibility(View.GONE);
                iv_bsc_test.setVisibility(View.GONE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"2");
                 break;
            case R.id.rl_sbc_main:
                networkName = BSC_MAIN_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivKovan.setVisibility(View.GONE);
                ivRopsten.setVisibility(View.GONE);
                ivLocalDev.setVisibility(View.GONE);
                iv_bsc_main.setVisibility(View.VISIBLE);
                iv_bsc_test.setVisibility(View.GONE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"3");
                break;
            case R.id.rl_sbc_test:
                networkName = BSC_TEST_NETWORK_NAME;
                ivMainnet.setVisibility(View.GONE);
                ivKovan.setVisibility(View.GONE);
                ivRopsten.setVisibility(View.GONE);
                ivLocalDev.setVisibility(View.GONE);
                iv_bsc_main.setVisibility(View.GONE);
                iv_bsc_test.setVisibility(View.VISIBLE);
                SharePreferencesUtil.putString(CommonStr.CHAIN_TYPE,"3");
                break;
            case R.id.rl_btn:// 设置语言并保存
//                SharedPreferencesUtil.getInstance().putString("pref_rpcServer",networkName );

                NetworkInfo[] networks = ethereumNetworkRepository.getAvailableNetworkList();
                for (NetworkInfo networkInfo : networks) {
                    if (networkInfo.name.equals(networkName)) {
                        ethereumNetworkRepository.setDefaultNetworkInfo(networkInfo);
                    }
                }
                finish();
                break;
        }
    }
}
