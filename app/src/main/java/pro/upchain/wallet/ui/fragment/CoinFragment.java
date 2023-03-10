package pro.upchain.wallet.ui.fragment;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;


import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.RateEntity;
import pro.upchain.wallet.entity.Ticker;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.WalletAmountEvenEntity;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.ui.activity.MnemonicBackupActivity;
import pro.upchain.wallet.ui.activity.PropertyDetailActivity;
import pro.upchain.wallet.ui.adapter.TokensAdapter;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.RefreshUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.StatusBarUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;


public class CoinFragment extends BaseFragment {
    @BindView(R.id.token_refresh)
    SmartRefreshLayout token_refresh;
    @BindView(R.id.token_recycler)
    RecyclerView token_recycler;
    @BindView(R.id.no_backup_linear)
    LinearLayout no_backup_linear;
    private LinearLayoutManager linearLayoutManager;
    private TokensAdapter recyclerAdapter;
    private View headView;

    private int bannerHeight = 300;
    private View mIv;

    private ETHWallet currEthWallet;
    private static final int QRCODE_SCANNER_REQUEST = 1100;
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int ADD_NEW_PROPERTY_REQUEST = 1102;
    private static final int WALLET_DETAIL_REQUEST = 1104;
    public List<Token> tokenItems;

    // ????????????
    private long currentBackPressedTime = 0;
    // ????????????
    private static final int BACK_PRESSED_INTERVAL = 1000;
    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;
    FetchWalletInteract fetchWalletInteract;
    private String ETH2USDTRate = Utils.getETHOrBsc2USDTRate();
    private String allRate = SharePreferencesUtil.getString(CommonStr.ALL_USDT_RATE,"");

    public static CoinFragment newInstance(int positoin) {
        CoinFragment fragment = new CoinFragment();
        Bundle args = new Bundle();
        args.putInt(CommonStr.POSITION,positoin);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_coin;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        if(StringMyUtil.isEmptyString(allRate)){
           HttpApiUtils. requestAllUSDTRate();
        }
    }

    @Override
    public void configViews() {
        initRecycler();
    }
    @OnClick({R.id.no_backup_linear})
    public void  onClick(View view){
        switch (view.getId()){
            case R.id.no_backup_linear:
                Intent intent = new Intent(getContext(), MnemonicBackupActivity.class);
                intent.putExtra("walletId", currEthWallet.getId());
                intent.putExtra("walletMnemonic", currEthWallet.getMnemonic());
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ETH2USDTRate = Utils.getETHOrBsc2USDTRate();
        tokensViewModel.prepare();
        initUSPrice();

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        StatusBarUtil.setColor(getActivity(), ContextCompat.getColor(getContext(),R.color.home_main_color));
        StatusBarUtil.setLightMode(getActivity(),false);


    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //?????????????????????
        token_recycler.setLayoutManager(linearLayoutManager);

        //???????????????
        recyclerAdapter = new TokensAdapter(R.layout.list_item_property, new ArrayList<>());  //

        token_recycler.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {

                Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
                Token token = tokenItems.get(position);
                if(StringMyUtil.isEmptyString(token.balance)){
                    ToastUtils.showToast(getString(R.string.please_wait_load_data));
                    return;
                }
                intent.putExtra(C.EXTRA_BALANCE, token.balance);
                intent.putExtra(C.EXTRA_ADDRESS, currEthWallet.getAddress());
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, token.tokenInfo.address);
                intent.putExtra(C.EXTRA_SYMBOL, token.tokenInfo.symbol);
                intent.putExtra(C.EXTRA_DECIMALS, token.tokenInfo.decimals);
                intent.putExtra(C.EXTRA_WALLET_LOGO, token.tokenInfo.imgUrl);

                startActivity(intent);
            }
        });
        RefreshUtils.initRefresh(getContext(), token_refresh, new RefreshUtils.OnRefreshLintener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                initUSPrice();
                tokensViewModel.prepare();
            }
        });
        fetchWalletInteract = new FetchWalletInteract();
//        fetchWalletInteract.fetch().subscribe(this::showDrawerWallets);

        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this.getActivity(), tokensViewModelFactory)
                .get(TokensViewModel.class);

        tokensViewModel.defaultWallet().observe(this,  this::showWallet);

//        tokensViewModel.progress().observe(this, systemView::showProgress);
//        tokensViewModel.error().observe(this, this::onError);

        tokensViewModel.tokens().observe(this, this::onTokens);
        tokensViewModel.error().observe(this, this::onError);
//        tokensViewModel.prices().observe(this, this::onPrices);
        initUSPrice();
    }

    private void initUSPrice() {
        if(StringMyUtil.isEmptyString(ETH2USDTRate)){
            HttpApiUtils.requestETHUSDTRate(new HttpApiUtils.OnRequestLintener() {
                @Override
                public void onSuccess(String result) {
                    RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                    ETH2USDTRate = rateEntity.getPrice();
                    initPrice();
                }

                @Override
                public void onFail(String msg) {

                }
            });
        }else {
            initPrice();
        }
    }

    private void initPrice() {
        if(StringMyUtil.isNotEmpty(ETH2USDTRate)&&tokenItems!=null){
            for (Token token : tokenItems) {
                if (StringMyUtil.isEmptyString(token.balance) || token.balance.equals("0")) {
                    token.value = "0";
                } else {
                    if(token.tokenInfo.symbol.equalsIgnoreCase("usdt")){
                        token.value = new BigDecimal(token.balance).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
                    }else {
                        if(StringMyUtil.isNotEmpty(allRate)){
                            List<RateEntity> rateEntities = JSONArray.parseArray(allRate, RateEntity.class);
                            for (int i = 0; i < rateEntities.size(); i++) {
                                RateEntity rateEntity = rateEntities.get(i);
                                boolean aCase = rateEntity.getSymbol().equalsIgnoreCase(token.tokenInfo.symbol + "USDT");
                                if(aCase){
                                    token.value = new BigDecimal(token.balance).multiply(new BigDecimal(rateEntity.getPrice())).setScale(2,BigDecimal.ROUND_HALF_UP)+"";
                                    break;
                                }
                            }
                        }

                    }
                }
            }
            recyclerAdapter.notifyDataSetChanged();

            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < tokenItems.size(); i++) {
                Token token = tokenItems.get(i);
                String value = token.value;
                if(StringMyUtil.isNotEmpty(value)){
                    totalAmount = new BigDecimal(value).add(totalAmount).setScale(2,BigDecimal.ROUND_HALF_UP);
                }
            }
            if(totalAmount.compareTo(BigDecimal.ZERO) >0){
                EventBus.getDefault().postSticky(new WalletAmountEvenEntity(totalAmount+""));
            }
        }
    }


    public void showWallet(ETHWallet wallet) {

        currEthWallet = wallet;
      /*  HashMap<String, Object> data = new HashMap<>();
        data.put("walletAddress",currEthWallet.getAddress());
        HttpApiUtils.wwwNormalRequest(getActivity(), this, RequestUtils.COIN_LIST, data, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                System.out.println(result);
            }

            @Override
            public void onFail(String msg) {
                System.out.println(msg);
            }
        });*/

//        EventBus.getDefault().postSticky(new WalletInfoEvenEntity(wallet));
        //       openOrCloseDrawerLayout();
        boolean backup = WalletDaoUtils.getIsBackup(currEthWallet.getId());
        //todo ?????????
//        backup =true;


        if(backup){
            token_refresh.setVisibility(View.VISIBLE);
            no_backup_linear.setVisibility(View.GONE);
        }else {
            token_refresh.setVisibility(View.GONE);
            no_backup_linear.setVisibility(View.VISIBLE);
        }
        if(!wallet.getIsUpload()&&System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL){
            currentBackPressedTime = System.currentTimeMillis();
            HttpApiUtils.addAddress(getActivity(),this,wallet);
        }
    }
    private void onError(ErrorEnvelope error) {
        if(token_refresh!=null){
            token_refresh.finishRefresh(false);
        }
    }
    private void onTokens(Token[] tokens) {
        token_refresh.finishRefresh(true);
        tokenItems = Arrays.asList(tokens);
        recyclerAdapter.setTokens(tokenItems);
        initPrice();
    }

    private void onPrices(Ticker ticker) {
        BigDecimal sum = new BigDecimal(0);

        for (Token token : tokenItems) {
            if (token.tokenInfo.symbol.equals(ticker.symbol)) {
                if (token.balance == null) {
                    token.value = "0";
                } else {
                    token.value = BalanceUtils.ethToUsd(ticker.price, token.balance);
                }
            }
            if (!TextUtils.isEmpty(token.value)) {
                sum  = sum.add(new BigDecimal(token.value));
            }

        }

   /*     if (tvTolalAssetValue != null) {
            tvTolalAssetValue.setText(sum.setScale(2, RoundingMode.CEILING).toString());
        }*/
//        EventBus.getDefault().postSticky(new WalletAmountEvenEntity(sum));
        recyclerAdapter.setTokens(tokenItems);
    }
}