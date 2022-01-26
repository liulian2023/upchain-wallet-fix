package pro.upchain.wallet.ui.activity;


import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.Ticker;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.ui.adapter.AssetRecyclerAdapter;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonToolBarUtils;
import pro.upchain.wallet.viewmodel.DeleteTokenViewModel;
import pro.upchain.wallet.viewmodel.DeleteTokenViewModelFactory;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;

public class AssetManagementActivity extends BaseActivity {

    @BindView(R.id.asset_management_recycler)
    RecyclerView asset_management_recycler;
    @BindView(R.id.asset_management_refresh)
    SmartRefreshLayout asset_management_refresh;
    private LinearLayoutManager linearLayoutManager;
    private AssetRecyclerAdapter recyclerAdapter;
    private TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;
    private List<Token> tokenItems = new ArrayList<>();
    private ETHWallet currEthWallet;
    protected DeleteTokenViewModelFactory deleteTokenViewModelFactory;
    DeleteTokenViewModel deleteTokenViewModel;
    @Override
    public int getLayoutId() {
        return R.layout.activity_asset_management;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        CommonToolBarUtils.initToolbar(this,R.string.home_amount_manage);
        deleteTokenViewModelFactory= new DeleteTokenViewModelFactory();
        deleteTokenViewModel = ViewModelProviders.of(this, deleteTokenViewModelFactory)
                .get(DeleteTokenViewModel.class);
        initRefresh();
        initRecycler();
        tokensViewModel.prepare();
    }

    @Override
    public void configViews() {

    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initRecycler() {
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //设置布局管理器
        asset_management_recycler.setLayoutManager(linearLayoutManager);

        //设置适配器
        recyclerAdapter = new AssetRecyclerAdapter(R.layout.asset_management_item_layout, tokenItems);  //

        asset_management_recycler.setAdapter(recyclerAdapter);

        recyclerAdapter.addChildClickViewIds(R.id.delete_iv);
        recyclerAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                Token token = tokenItems.get(position);
                deleteTokenViewModel.delete(token.tokenInfo.address, token.tokenInfo.symbol, token.tokenInfo.decimals,token.tokenInfo.imgUrl,token.tokenInfo.name);
                tokenItems.remove(token);
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);


//        tokensViewModel.progress().observe(this, systemView::showProgress);
//        tokensViewModel.error().observe(this, this::onError);
//        tokensViewModel.defaultWallet().observe(this,  this::showWallet);
        tokensViewModel.tokens().observe(this, this::onTokens);
        tokensViewModel.prices().observe(this, this::onPrices);
    }
    private void onTokens(Token[] tokens) {
        tokenItems.clear();
        tokenItems.addAll(Arrays.asList(tokens));
        recyclerAdapter.notifyDataSetChanged();
    }
    public void showWallet(ETHWallet wallet) {
        currEthWallet = wallet;
//        EventBus.getDefault().postSticky(new WalletInfoEvenEntity(wallet));
        //       openOrCloseDrawerLayout();
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

//        EventBus.getDefault().postSticky(new WalletAmountEvenEntity(sum));
//        recyclerAdapter.notifyDataSetChanged();
    }

    private void initRefresh() {
        asset_management_refresh.setEnableRefresh(false);
    }
}