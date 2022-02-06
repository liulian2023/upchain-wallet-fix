package pro.upchain.wallet.ui.activity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import pro.upchain.wallet.C;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.PendingHistoryEntity;
import pro.upchain.wallet.entity.RateEntity;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.entity.TransferHistoryEntity;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.ui.adapter.TransactionsAdapter;
import pro.upchain.wallet.ui.adapter.TransferHistoryAdapter;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.RefreshUtils;
import pro.upchain.wallet.utils.TransferDaoUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.viewmodel.TransactionsViewModel;
import pro.upchain.wallet.viewmodel.TransactionsViewModelFactory;

import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.gyf.barlibrary.ImmersionBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

import static pro.upchain.wallet.C.ETHEREUM_MAIN_NETWORK_NAME;
import static pro.upchain.wallet.C.EXTRA_ADDRESS;
import static pro.upchain.wallet.C.Key.TRANSACTION;
import static pro.upchain.wallet.C.ROPSTEN_NETWORK_NAME;

public class PropertyDetailActivity extends BaseActivity {
    @BindView(R.id.wallet_balance_tv)
    TextView wallet_balance_tv;
    @BindView(R.id.total_amount_tv)
    TextView total_amount_tv;
    @BindView(R.id.tv_title)
    TextView tv_title;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refresh_layout;
    @BindView(R.id.history_recycler)
    RecyclerView history_recycler;

    @BindView(R.id.loading_linear)
    ConstraintLayout loading_linear;
    @BindView(R.id.error_linear)
    LinearLayout error_linear;
    @BindView(R.id.reload_tv)
    TextView reload_tv;
    @BindView(R.id.nodata_linear)
    LinearLayout nodata_linear;
    TransactionsViewModelFactory transactionsViewModelFactory;
    private TransactionsViewModel viewModel;

    private TransactionsAdapter adapter;

    private String currWallet;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;

    List<Transaction> transactionLists;
    private String ETH2USDTRate;
    private String ETH2OtherRate;
    TransferHistoryAdapter transferHistoryAdapter;
    ArrayList<TransferHistoryEntity.ListBean> listBeanArrayList = new ArrayList<>();
    ArrayList<TransferHistoryEntity.ListBean> pendingList = new ArrayList<>();
    int pageNo =1;
    @Override
    public int getLayoutId() {
        return R.layout.activity_property_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        currWallet = intent.getStringExtra(C.EXTRA_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = StringMyUtil.isEmptyString(symbol) ? C.ETH_SYMBOL : symbol;
        tv_title.setText(symbol);
        wallet_balance_tv.setText(balance);

        transactionsViewModelFactory = new TransactionsViewModelFactory();
        viewModel = ViewModelProviders.of(this, transactionsViewModelFactory)
                .get(TransactionsViewModel.class);

        viewModel.transactions().observe(this, this::onTransactions);
        viewModel.progress().observe(this, this::onProgress);
        requestETHUSDTRate();
        if(!symbol.equals(C.ETH_SYMBOL)){
            requestETHoTHERRate();
        }
        initRefresh();
    }

    private void initRefresh() {
        RefreshUtils.initRefresh(this, refresh_layout, new RefreshUtils.OnRefreshLintener() {
                    @Override
                    public void onRefresh(RefreshLayout refreshLayout) {
                        pageNo=1;
                        requestHistory(true,false);
                    }
                }
        );
    }

    @Override
    public void errorRefresh() {
        super.errorRefresh();
        requestHistory(false,false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refresh_layout.autoRefresh();
    }

    private void initRecycler(String rate) {
        transferHistoryAdapter = new TransferHistoryAdapter(listBeanArrayList,symbol,rate);
        history_recycler.setLayoutManager(new LinearLayoutManager(this));
        history_recycler.setAdapter(transferHistoryAdapter);
        RepositoryFactory rf = MyApplication.repositoryFactory();
        transferHistoryAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                TransferHistoryEntity.ListBean bean = listBeanArrayList.get(position);
                String currentNetName = rf.ethereumNetworkRepository.getDefaultNetwork().name;
                String url = "";
                if(currentNetName.equals(ETHEREUM_MAIN_NETWORK_NAME)){
                    url = String.format("https://etherscan.io/tx/%s",bean.getHash());
                }else if(currentNetName.equals(ROPSTEN_NETWORK_NAME)){
                    url = String.format("https://Ropsten.etherscan.io/tx/%s",bean.getHash());
                }else if(currentNetName.equals(C.BSC_MAIN_NETWORK_NAME)){
                    url = String.format("https://bscscan.com/tx/%s",bean.getHash());
                }else if(currentNetName.equals(C.BSC_TEST_NETWORK_NAME)){
                    url = String.format("https://testnet.bscscan.com/tx/%s",bean.getHash());
                }
                if(StringMyUtil.isNotEmpty(url)){
                    Intent intent = new Intent(PropertyDetailActivity.this, TransferDetailsActivity.class);
                    intent.putExtra("URL",url);
                    startActivity(intent);
                }
            }
        });
        requestHistory(false,false);
    }

    private void requestHistory(boolean isRefresh, boolean isLoadMore) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("walletAddress",WalletDaoUtils.getCurrent().getAddress());
        if(StringMyUtil.isNotEmpty(contractAddress)){
            data.put("contract",contractAddress);
        }else {
            data.put("contract",symbol);
        }
        data.put("moneyType","3");
        data.put("pageNum",1);
        data.put("pageSize",1000);
        data.put("blockchainType",2);

        HttpApiUtils.wwwShowLoadRequest(this, null, RequestUtils.transfer_history, data, loading_linear,error_linear,reload_tv,refresh_layout,isLoadMore,isRefresh,new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                if(isRefresh){
                    refresh_layout.finishRefresh(true);
                }
                TransferHistoryEntity transferHistoryEntity = JSONObject.parseObject(result, TransferHistoryEntity.class);
                List<TransferHistoryEntity.ListBean> list = transferHistoryEntity.getList();
//                RefreshUtils.succse(pageNo,refresh_layout,loading_linear,nodata_linear,list.size(),isLoadMore,isRefresh,listBeanArrayList);
                listBeanArrayList.clear();
                    List<PendingHistoryEntity> historyEntities = TransferDaoUtils.loadAll();
                    for (int i = 0; i < list.size(); i++) {
                        TransferHistoryEntity.ListBean listBean = list.get(i);
                        listBean.setItemType(1);
                        for (int j = 0; j < historyEntities.size(); j++) {
                            PendingHistoryEntity pendingHistoryEntity = historyEntities.get(j);
                            if(listBean.getHash().equals(pendingHistoryEntity.getHash())){
                                TransferDaoUtils.delete(pendingHistoryEntity);
                            }
                        }
                    }

                    if(historyEntities.size()>0){
                        TransferHistoryEntity.ListBean bean = new TransferHistoryEntity.ListBean();
                        bean.setItemType(0);
                        bean.setTitleName(getString(R.string.pending));
                        listBeanArrayList.add(bean);
                    }

                    for (int i = 0; i < TransferDaoUtils.loadAll().size(); i++) {
                        PendingHistoryEntity pendingHistoryEntity = TransferDaoUtils.loadAll().get(i);
                        String mineAddress = pendingHistoryEntity.getMineAddress();
                        String symbol = pendingHistoryEntity.getSymbol();
                        String netWork = pendingHistoryEntity.getNetWork();
                        /**
                         * 同一钱包同一代币 同一网络的记录才添加
                         */
                        if(mineAddress.equalsIgnoreCase(WalletDaoUtils.getCurrent().address)
                                && symbol.equalsIgnoreCase(PropertyDetailActivity.this.symbol)
                                && netWork.equals(MyApplication.repositoryFactory().ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl)){
                            TransferHistoryEntity.ListBean listBean = new TransferHistoryEntity.ListBean();
                            listBean.setToAddress(pendingHistoryEntity.getToAddress());
                            listBean.setMoney(pendingHistoryEntity.getMoney());
                            listBean.setCreateTime(pendingHistoryEntity.getCreateTime());
                            listBean.setHash(pendingHistoryEntity.getHash());
                            listBean.setItemType(1);
                            listBeanArrayList.add(listBean);
                        }
                    }

                if(list.size()!=0){
                    TransferHistoryEntity.ListBean bean = new TransferHistoryEntity.ListBean();
                    bean.setItemType(0);
                    bean.setTitleName(getString(R.string.finished));
                    listBeanArrayList.add(bean);
                }
                listBeanArrayList.addAll(list);
                if(listBeanArrayList.size()==0){
                    if(refresh_layout!=null){
                        refresh_layout.setVisibility(View.GONE);
                    }
                    if(nodata_linear!=null) {
                        nodata_linear.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(nodata_linear!=null){
                        nodata_linear.setVisibility(View.GONE);
                    }
                    if(refresh_layout!=null){
                        refresh_layout.setVisibility(View.VISIBLE);
                    }
                }
                transferHistoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(String msg) {
                RefreshUtils.fail(isRefresh,isLoadMore,pageNo,refresh_layout);
            }
        });
    }

    private void requestETHoTHERRate() {
        HttpApiUtils.requestETHoTHERRate("EOS",new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                ETH2OtherRate = rateEntity.getPrice();
                initRecycler(ETH2OtherRate);
            }

            @Override
            public void onFail(String msg) {
            }
        });
    }

    private void requestETHUSDTRate() {
        HttpApiUtils.requestETHUSDTRate(new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                ETH2USDTRate = rateEntity.getPrice();
                BigDecimal bigDecimal = new BigDecimal(balance);
                BigDecimal multiply = bigDecimal.multiply(new BigDecimal(ETH2USDTRate));
                BigDecimal usdt = multiply.setScale(2,BigDecimal.ROUND_HALF_UP);
                total_amount_tv.setText("≈$"+usdt);
                if(symbol.equals(C.ETH_SYMBOL)){
                    initRecycler(ETH2USDTRate);
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.d("contractAddress " + contractAddress);

        if (!TextUtils.isEmpty(contractAddress)) {
            viewModel.prepare(contractAddress);
        } else {
            viewModel.prepare(null);
        }

    }



    private void onTransactions(List<Transaction> transactions) {
        LogUtils.d("onTransactions", "size: " + transactions.size());
        transactionLists = transactions;
        adapter.addTransactions(transactionLists, currWallet, symbol);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();
        history_recycler.setLayoutManager(new LinearLayoutManager(this));


        adapter = new TransactionsAdapter(R.layout.list_item_transaction, null );
        history_recycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> baseQuickAdapter, @NonNull View view, int position) {
                Transaction t = transactionLists.get(position);
                Intent intent = new Intent(PropertyDetailActivity.this, TransactionDetailActivity.class);
                intent.putExtra(TRANSACTION, t);
                startActivity(intent);
            }
        });
    }


    private void onProgress(boolean shouldShow) {

        if (shouldShow) {
//            if (transactionLists.size() > 0) {
//                refreshLayout.setRefreshing(true);
//            }
        } else {
            refresh_layout.finishRefresh();
        }
    }


    @OnClick({R.id.lly_back, R.id.send_iv, R.id.receive_iv,R.id.send_tv,R.id.receive_tv})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.send_iv:
            case R.id.send_tv:
                intent = new Intent(mContext, SendActivity.class);
                intent.putExtra(C.EXTRA_BALANCE, balance);
                intent.putExtra(C.EXTRA_ADDRESS, currWallet);
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);
                startActivity(intent);
                break;
            case R.id.receive_tv:
            case R.id.receive_iv:
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                ETHWallet wallet = WalletDaoUtils.getCurrent();

                intent.putExtra(EXTRA_ADDRESS, wallet.getAddress());
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);

                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
