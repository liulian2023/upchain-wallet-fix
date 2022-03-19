package pro.upchain.wallet.ui.activity;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
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
import pro.upchain.wallet.ui.adapter.AllCoinAdapter;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.RefreshUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;

public class AllCoinActivity extends BaseActivity {
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
    ArrayList<ContractEntity>allCoinEntityArrayList = new ArrayList<>();
    ArrayList<ContractEntity>searchList = new ArrayList<>();
    AllCoinAdapter allCoinAdapter;
    AllCoinAdapter searchAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_all_coin_acitivity;
    }

    @Override
    public void initToolBar() {

    }
    public static void startAty (Context context,boolean toSend){
        Intent intent = new Intent(context, AllCoinActivity.class);
        intent.putExtra("toSend",toSend);
        context.startActivity(intent);
    }
    @Override
    public void initDatas() {
        boolean toSend = getIntent().getBooleanExtra("toSend", false);
        if(toSend){
            all_coin_title_tv.setText(getResources().getString(R.string.action_send));
        }else {
            all_coin_title_tv.setText(getResources().getString(R.string.Receive));

        }
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
                        ContractEntity contractEntity = allCoinEntityArrayList.get(i);
                        if(contractEntity.getItemType() == 1){
                            String tokenSymbol = contractEntity.getTokenSymbol();
                            String tokenName = contractEntity.getTokenName();
                            if(tokenName.toUpperCase().contains(editStr.toUpperCase())|| tokenSymbol.toUpperCase().contains(editStr.toUpperCase())){
                                searchList.add(contractEntity);
                            }
                        }
                    }
                    if(searchList.size()==0){
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
                    }
                    ContractEntity titleEntity = new ContractEntity();
                    titleEntity.setItemType(0);
                    titleEntity.setTitleName(getResources().getString(R.string.search_result));
                    searchList.add(0,titleEntity);
                    searchAdapter.notifyDataSetChanged();
                }


            }
        });
    }

    private void initSearchRecycler() {
        searchAdapter = new AllCoinAdapter(searchList);
        search_coin_recycler.setLayoutManager(new LinearLayoutManager(this));
        search_coin_recycler.setAdapter(searchAdapter);
        searchAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                itemClick(position,searchList);
            }
        });
    }

    private void itemClick(int position,ArrayList<ContractEntity>list) {
        ETHWallet currEthWallet = WalletDaoUtils.getCurrent();
        ContractEntity contractEntity = list.get(position);
        if(contractEntity.getItemType() ==1 ){
            boolean toSend = getIntent().getBooleanExtra("toSend",false);
            if(toSend){
                //send
                if(contractEntity.getTokenSymbol().equals(C.ETH_SYMBOL) || contractEntity.getTokenSymbol().equals(C.BSC_SYMBOL)){
                    SendActivity.startAty(AllCoinActivity.this,currEthWallet.getAddress(),"","",contractEntity.getDecimals(),contractEntity.getTokenSymbol());
                }else {
                    SendActivity.startAty(AllCoinActivity.this,currEthWallet.getAddress(),contractEntity.getContract(),"",contractEntity.getDecimals(),contractEntity.getTokenSymbol());
                }
            }else {
                //receive
                GatheringQRCodeActivity.startAty(AllCoinActivity.this,currEthWallet.getAddress(),contractEntity.getTokenSymbol(),contractEntity.getContract(),contractEntity.getDecimals());
            }
        }
    }

    private void initRefresh() {
        RefreshUtils.initRefresh(this, all_coin_refresh, new RefreshUtils.OnRefreshLintener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                handlerContractList(true);
            }
        });
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
        allCoinAdapter = new AllCoinAdapter(allCoinEntityArrayList);
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
        String contractListJson = SharePreferencesUtil.getString(CommonStr.CONTRACT_LIST, "");
        if(!isRefresh){
           if(StringMyUtil.isNotEmpty(contractListJson)){
               handContractJson(contractListJson, isRefresh);
               requestContractList(isRefresh,false);
           }else {
               requestContractList(isRefresh,true);
           }
        }else {
            requestContractList(isRefresh,true);
        }

    }

    private void requestContractList(boolean isRefresh,boolean handlerRecycler) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("blockchainType",Utils.getCurrentChain());
        HttpApiUtils.wwwShowLoadRequest(this, null, RequestUtils.CONTRACT_LIST, data, loading_linear, error_linear, reload_tv, all_coin_refresh, false, isRefresh, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                SharePreferencesUtil.putString(CommonStr.CONTRACT_LIST,result);
                if(handlerRecycler){

                    handContractJson(result, isRefresh);
                }
            }

            @Override
            public void onFail(String msg) {
                if(handlerRecycler){
                    RefreshUtils.fail(isRefresh,false,1,all_coin_refresh);
                }
            }
        });
    }

    private void handContractJson(String result, boolean isRefresh) {
        List<ContractEntity> contractEntityList = JSONArray.parseArray(result, ContractEntity.class);
        RefreshUtils.succse(1,all_coin_refresh,loading_linear,nodata_linear,contractEntityList.size(),false, isRefresh,allCoinEntityArrayList);
        for (int i = 0; i < contractEntityList.size(); i++) {
            ContractEntity contractEntity = contractEntityList.get(i);
            int isSuggested = contractEntity.getIsSuggested();
            if(isSuggested == 1){
                ContractEntity titleEntity = new ContractEntity();
                titleEntity.setTitleName(getResources().getString(R.string.Suggested));
                titleEntity.setItemType(0);
                allCoinEntityArrayList.add(titleEntity);
                break;
            }
        }
        for (int i = 0; i < contractEntityList.size(); i++) {
            int isSuggested = contractEntityList.get(i).getIsSuggested();
            if(isSuggested == 1){
                allCoinEntityArrayList.add(contractEntityList.get(i));
            }
        }
        if(contractEntityList.size()!=0){
            ContractEntity contractEntity = new ContractEntity();
            contractEntity.setTitleName(getResources().getString(R.string.all_coins));
            contractEntity.setItemType(0);
            allCoinEntityArrayList.add(contractEntity);
        }
        for (int i = 0; i < contractEntityList.size(); i++) {
            int isSuggested = contractEntityList.get(i).getIsSuggested();
            if(isSuggested == 2){
                allCoinEntityArrayList.add(contractEntityList.get(i));
            }
        }
        allCoinAdapter.notifyDataSetChanged();
    }

    @Override
    public void configViews() {

    }
}