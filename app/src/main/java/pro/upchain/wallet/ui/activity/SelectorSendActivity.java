package pro.upchain.wallet.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ContractEntity;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.ui.adapter.AllCoinAdapter;
import pro.upchain.wallet.ui.adapter.TokensAdapter;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.RefreshUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;

public class SelectorSendActivity extends BaseActivity {

    @BindView(R.id.all_coin_title_tv)
    TextView all_coin_title_tv;
    @BindView(R.id.all_coin_back_linear)
    LinearLayout all_coin_back_linear;
    @BindView(R.id.all_coin_search_etv)
    EditText all_coin_search_etv;
    @BindView(R.id.all_coin_refresh)
    SmartRefreshLayout all_coin_refresh;
    @BindView(R.id.all_coin_recycler)
    RecyclerView all_coin_recycler;
    @BindView(R.id.search_coin_recycler)
    RecyclerView search_coin_recycler;
    @BindView(R.id.loading_linear)
    ConstraintLayout loading_linear;
    @BindView(R.id.error_linear)
    LinearLayout error_linear;
    @BindView(R.id.reload_tv)
    TextView reload_tv;
    @BindView(R.id.nodata_linear)
    LinearLayout nodata_linear;
    @BindView(R.id.send_tip_tv)
    TextView send_tip_tv;
    ArrayList<Token> allCoinEntityArrayList = new ArrayList<>();
    ArrayList<Token>searchList = new ArrayList<>();
    TokensAdapter allCoinAdapter;
    TokensAdapter searchAdapter;
    @Override
    public int getLayoutId() {
        return R.layout.activity_selector_send;
    }

    @Override
    public void initToolBar() {

    }

    public static void startActivity(Context context,String contractList){
        Intent intent = new Intent(context, SelectorSendActivity.class);
        intent.putExtra("contractList",contractList);
        context.startActivity(intent);
    }
    @Override
    public void initDatas() {
        initRecycler();
        initSearchRecycler();
        handlerContractList(false);
        initRefresh();
        all_coin_search_etv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchList.clear();
                String editStr = all_coin_search_etv.getText().toString().trim();
                if(StringMyUtil.isEmptyString(editStr)){
                    YoYo.with(Techniques.FadeOut)
                            .duration(500)
                            .playOn(search_coin_recycler);
                    search_coin_recycler.setVisibility(View.GONE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(500)
                            .playOn(all_coin_refresh);
                    all_coin_refresh.setVisibility(View.VISIBLE);
                    send_tip_tv.setText(getString(R.string.all_coins));
                }else {
                    YoYo.with(Techniques.FadeOut)
                            .duration(500)
                            .playOn(all_coin_refresh);
                    search_coin_recycler.setVisibility(View.VISIBLE);
                    YoYo.with(Techniques.FadeIn)
                            .duration(500)
                            .playOn(search_coin_recycler);
                    all_coin_refresh.setVisibility(View.GONE);

                    for (int i = 0; i < allCoinEntityArrayList.size(); i++) {
                        Token token = allCoinEntityArrayList.get(i);
                            String tokenSymbol = token.tokenInfo.symbol;
                            String tokenName = token.tokenInfo.name;
                            if(tokenName.toUpperCase().contains(editStr.toUpperCase())|| tokenSymbol.toUpperCase().contains(editStr.toUpperCase())){
                                searchList.add(token);
                            }
                    }
/*                    if(searchList.size()==0){
                        for (int i = 0; i < allCoinEntityArrayList.size(); i++) {
                            ContractEntity contractEntity = allCoinEntityArrayList.get(i);
                            if(contractEntity.getItemType() ==1){
                                if(Utils.getCurrentChain().equals("2")){
                                    boolean equals = contractEntity.getTokenSymbol().equals(C.ETH_SYMBOL);
                                    if(equals){
                                        searchList.add(contractEntity);
                                    }
                                }else if(Utils.getCurrentChain().equals("3")){
                                    boolean equals = contractEntity.getTokenSymbol().equals(C.BSC_SYMBOL);
                                    if(equals){
                                        searchList.add(contractEntity);
                                    }
                                }

                            }

                        }
                    }*/
                    send_tip_tv.setText(getString(R.string.search_result));
                    searchAdapter.notifyDataSetChanged();
                }


            }
        });
    }

    private void initSearchRecycler() {
        searchAdapter = new TokensAdapter(R.layout.list_item_property,searchList);
        search_coin_recycler.setLayoutManager(new LinearLayoutManager(this));
        search_coin_recycler.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemClick(position,searchList);
            }
        });
    }

    private void itemClick(int position,ArrayList<Token>list) {
        ETHWallet currEthWallet = WalletDaoUtils.getCurrent();
        Token token = list.get(position);
                //send
                if(token.tokenInfo.symbol.equals(C.ETH_SYMBOL) || token.tokenInfo.symbol.equals(C.BSC_SYMBOL)){
                    SendActivity.startAty(SelectorSendActivity.this,currEthWallet.getAddress(),"","",token.tokenInfo.decimals,token.tokenInfo.symbol);
                }else {
                    SendActivity.startAty(SelectorSendActivity.this,currEthWallet.getAddress(),token.tokenInfo.address,"",token.tokenInfo.decimals,token.tokenInfo.symbol);
                }
    }

    private void initRefresh() {
/*        RefreshUtils.initRefresh(this, all_coin_refresh, new RefreshUtils.OnRefreshLintener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                handlerContractList(true);
            }
        });*/
        all_coin_refresh.setEnableRefresh(false);
        all_coin_refresh.setEnableLoadMore(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @OnClick({R.id.all_coin_back_linear})
    public void  onClick(View view){
        switch (view.getId()){
            case R.id.all_coin_back_linear:
                hideSoftInputView();
                finish();
                break;
            default:
                break;
        }
    }
    private void initRecycler() {
        allCoinAdapter = new TokensAdapter(R.layout.list_item_property,allCoinEntityArrayList);
        all_coin_recycler.setLayoutManager(new LinearLayoutManager(this));
        all_coin_recycler.setAdapter(allCoinAdapter);
        allCoinAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemClick(position,allCoinEntityArrayList);
            }
        });
    }

    private void handlerContractList(boolean isRefresh) {
        String contractList = getIntent().getStringExtra("contractList");
        List<Token> tokens = JSONArray.parseArray(contractList, Token.class);
        allCoinEntityArrayList.addAll(tokens);
        allCoinAdapter.notifyDataSetChanged();
    }



    @Override
    public void configViews() {

    }
}