package pro.upchain.wallet.ui.fragment;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.WalletAmountEvenEntity;
import pro.upchain.wallet.ui.activity.AddTokenActivity;
import pro.upchain.wallet.ui.activity.DappActivity;
import pro.upchain.wallet.ui.activity.DappActivity2;
import pro.upchain.wallet.ui.activity.QRCodeScannerActivity;
import pro.upchain.wallet.ui.activity.SendActivity;
import pro.upchain.wallet.ui.activity.WalletMangerActivity;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;
import com.gyf.barlibrary.ImmersionBar;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class PropertyFragment extends BaseFragment {

    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;



    @BindView(R.id.home_amount_tv)
    TextView home_amount_tv;
    @BindView(R.id.home_scan_iv)
    ImageView home_scan_iv;
    @BindView(R.id.home_tab_layout)
    TabLayout home_tab_layout;
    @BindView(R.id.home_viewPager)
    ViewPager home_viewPager;
    @BindView(R.id.top_relativeLayout)
    RelativeLayout top_relativeLayout;
    private ETHWallet ethWallet;
    List<String>titleList = new ArrayList<>();

    HomeTabAdapter homeTabAdapter;




    private static final int QRCODE_SCANNER_REQUEST = 1100;
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int ADD_NEW_PROPERTY_REQUEST = 1102;
    private static final int WALLET_DETAIL_REQUEST = 1104;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_property;
    }

    @Override
    public void attachView() {
    ImmersionBar.with(this).titleBarMarginTop(top_relativeLayout).init();
    }

    @Override
    public void initDatas() {


    }
    /**
     * 钱包金额
     * @param walletAmountEvenEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void walletAmountEven(WalletAmountEvenEntity walletAmountEvenEntity){
        home_amount_tv .setText(walletAmountEvenEntity.getSum().setScale(2, RoundingMode.CEILING).toString());
    }

    @OnClick({R.id.home_scan_iv,R.id.home_add_iv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.home_scan_iv:
                Intent intent = new Intent(getContext(), QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.home_add_iv:
                startActivity(new Intent(getContext(), AddTokenActivity.class));
//                startActivity(new Intent(getContext(), DappActivity.class));
                break;
            default:
                break;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void configViews() {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
     initTab();
    }

    private void initTab() {
        titleList.add(getString(R.string.Currency));
        titleList.add(getString(R.string.Collection));
        homeTabAdapter = new HomeTabAdapter(getChildFragmentManager(),titleList);
        home_viewPager.setAdapter(homeTabAdapter);
        home_tab_layout.setupWithViewPager(home_viewPager);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                // 对扫描结果进行处理
                ToastUtils.showLongToast(scanResult);

                Intent intent = new Intent(mContext, SendActivity.class);
                intent.putExtra("scan_result", scanResult );
                startActivity(intent);

            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
//                mPresenter.loadAllWallets();
                startActivity(new Intent(mContext, WalletMangerActivity.class));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return super.onCreateView(inflater, container, state);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
        EventBus.getDefault().unregister(this);
    }


    class HomeTabAdapter extends FragmentPagerAdapter {
        List<String>titleList = new ArrayList<>();
        public HomeTabAdapter(@NonNull FragmentManager fm, List<String>titleList) {
            super(fm);
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int i) {
            return CoinFragment.newInstance(i);
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
