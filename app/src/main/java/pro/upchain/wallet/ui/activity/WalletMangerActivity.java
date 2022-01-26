package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.ui.adapter.WalletManagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class WalletMangerActivity extends BaseActivity {
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int WALLET_DETAIL_REQUEST = 1102;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.wallet_manage_recycler)
    RecyclerView wallet_manage_recycler;
    private ArrayList<ETHWallet> walletList = new ArrayList<>();
    private WalletManagerAdapter walletManagerAdapter;
    private FetchWalletInteract fetchWalletInteract;

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_manager;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_wallet_manage);
    }

    @Override
    public void initDatas() {

        walletManagerAdapter = new WalletManagerAdapter( R.layout.list_item_wallet_manager, walletList);
        wallet_manage_recycler.setLayoutManager(new LinearLayoutManager(this));
        wallet_manage_recycler.setAdapter(walletManagerAdapter);
        walletManagerAdapter.addChildClickViewIds(R.id.copy_address_tv);
        walletManagerAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                ETHWallet wallet = walletList.get(position);
                Utils.copyStr("wallet_address",wallet.getAddress());
            }
        });
        walletManagerAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                ETHWallet wallet = walletList.get(position);
//                WalletDetailActivity.startAtyForResult(WalletMangerActivity.this,wallet,true,WALLET_DETAIL_REQUEST);

                WalletDaoUtils.updateCurrent(wallet.getId());
                ToastUtils.showToast(R.string.change_success);
                finish();
            }
        });

        fetchWalletInteract = new FetchWalletInteract();

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchWalletInteract.fetch().subscribe(
                this::showWalletList
        );
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.import_wallet_btn, R.id.create_wallet_btn})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.create_wallet_btn:
                intent = new Intent(this, CreateWalletActivity.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                break;
            case R.id.import_wallet_btn:
//                intent = new Intent(this, ImportWalletActivity.class);
                intent = new Intent(this, RecoverWalletActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_WALLET_REQUEST) {
            if (data != null) {
                finish();

            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
                fetchWalletInteract.fetch().subscribe(
                        this::showWalletList
                );
            }
        }
    }

    public void showWalletList(List<ETHWallet> ethWallets) {
        walletList.clear();
        walletList.addAll(ethWallets);
        walletManagerAdapter.notifyDataSetChanged();
    }


    public void onError(Throwable throwable) {

    }
}
