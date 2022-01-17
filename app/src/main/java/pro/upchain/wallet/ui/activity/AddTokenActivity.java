package pro.upchain.wallet.ui.activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.AddTokenEntity;
import pro.upchain.wallet.entity.ContractEntity;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.ui.adapter.AddTokenAdapter;
import pro.upchain.wallet.utils.ReadAssetsJsonUtil;
import pro.upchain.wallet.utils.RefreshUtils;
import pro.upchain.wallet.viewmodel.AddTokenViewModel;
import pro.upchain.wallet.viewmodel.AddTokenViewModelFactory;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;



public class AddTokenActivity extends BaseActivity {
    @BindView(R.id.add_token_recycler)
    RecyclerView add_token_recycler;
    @BindView(R.id.token_refresh)
    SmartRefreshLayout token_refresh;
    @BindView(R.id.loading_linear)
    ConstraintLayout loading_linear;
    @BindView(R.id.error_linear)
    LinearLayout error_linear;
    @BindView(R.id.reload_tv)
    TextView reload_tv;
    @BindView(R.id.nodata_linear)
    LinearLayout nodata_linear;
    @BindView(R.id.home_asset_management_linear)
    LinearLayout home_asset_management_linear;
    ArrayList<TokenItem> mItems = new ArrayList<TokenItem>();
    @BindView(R.id.add_token_top_linear)
    LinearLayout add_token_top_linear;
    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel addTokenViewModel;
    AddTokenAdapter addTokenAdapter;
    private static final int SEARCH_ICO_TOKEN_REQUEST = 1000;
    @Override
    public int getLayoutId() {
        return R.layout.activity_add_token;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        initRefresh();
        initRecycler();
    }

    private void initRefresh() {
        RefreshUtils.initRefresh(this, token_refresh, new RefreshUtils.OnRefreshLintener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                requestContractList(true);
            }
        });
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this).titleBarMarginTop(add_token_top_linear).init();
    }

    @Override
    public void errorRefresh() {
        super.errorRefresh();
        requestContractList(false);
    }

    private void initRecycler() {

        addTokenAdapter = new AddTokenAdapter( R.layout.add_token_item_layout,mItems);
        add_token_recycler.setLayoutManager(new LinearLayoutManager(this));
        add_token_recycler.setAdapter(addTokenAdapter);

        requestContractList(false);
        addTokenAdapter.addChildClickViewIds(R.id.add_token_add_iv);
        addTokenAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                TokenItem tokenItem = mItems.get(position);
                tokenItem.added = true;
                addTokenViewModel.save(tokenItem.tokenInfo.address, tokenItem.tokenInfo.symbol, tokenItem.tokenInfo.decimals,tokenItem.tokenInfo.imgUrl);
                addTokenAdapter.notifyDataSetChanged();
            }
        });
        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);
//        tokensViewModel.tokens().observe(this, this::onTokens);

        tokensViewModel.prepare();

        addTokenViewModelFactory = new AddTokenViewModelFactory();
        addTokenViewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);
    }

    private void requestContractList(boolean isRefresh) {
        HttpApiUtils.wwwShowLoadRequest(this, null, RequestUtils.CONTRACT_LIST, new HashMap<String, Object>(), loading_linear, error_linear, reload_tv, token_refresh, false, isRefresh, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                List<ContractEntity> contractEntityList = JSONArray.parseArray(result, ContractEntity.class);
                RefreshUtils.succse(1,token_refresh,loading_linear,nodata_linear,contractEntityList.size(),false,isRefresh,mItems);
                for (int i = 0; i < contractEntityList.size(); i++) {
                    ContractEntity contractEntity = contractEntityList.get(i);
                    TokenItem tokenItem = new TokenItem(new TokenInfo(contractEntity.getContract(), contractEntity.getTokenName(), contractEntity.getTokenSymbol(), contractEntity.getDecimals(), contractEntity.getIcon()), false);
                    mItems.add(tokenItem);
                }
                addTokenAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String msg) {
                RefreshUtils.fail(isRefresh,false,1,token_refresh);
            }
        });
    }

    private void onTokens(Token[] tokens) {

        for (TokenItem item : mItems) {
            for (Token token: tokens) {
                if (item.tokenInfo.address.equals(token.tokenInfo.address)) {
                    item.added = true;
                }
            }
        }
        addTokenAdapter.notifyDataSetChanged();
    }
    @OnClick({R.id.lly_back,R.id.home_asset_management_linear,R.id.classification_search_linear})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.lly_back:
                finish();
                break;
            case R.id.home_asset_management_linear:
                startActivity(new Intent(AddTokenActivity.this,AssetManagementActivity.class));
                break;
            case R.id.classification_search_linear:
                Intent intent = new Intent(this, AddCustomTokenActivity.class);
                startActivityForResult(intent, SEARCH_ICO_TOKEN_REQUEST);
                break;

            default:
                break;
        }
    }
    public static class TokenItem {
        public final TokenInfo tokenInfo;
        public boolean added =true;
        public TokenItem(TokenInfo tokenInfo, boolean added) {
            this.tokenInfo = tokenInfo;
            this.added = added;
        }


    }
}
