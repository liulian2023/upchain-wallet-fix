package pro.upchain.wallet.ui.activity;

import android.content.Intent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.MnemonicEntity;
import pro.upchain.wallet.ui.adapter.MnemonicRecyclerAdapter;
import pro.upchain.wallet.view.MnemonicBackupAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;

// 提示 抄下钱包助记词 界面

public class MnemonicBackupActivity extends BaseActivity {
    private static final int VERIFY_MNEMONIC_BACKUP_REQUEST = 1101;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.btn_backup)
    Button btn_backup;
    @BindView(R.id.mnemonic_recycler)
    RecyclerView mnemonic_recycler;
    private String walletMnemonic;
    MnemonicRecyclerAdapter mnemonicRecyclerAdapter;
    ArrayList<MnemonicEntity>mnemonicEntityArrayList = new ArrayList<>();

    private long walletId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
    }

    @Override
    public void initDatas() {
        MnemonicBackupAlertDialog mnemonicBackupAlertDialog = new MnemonicBackupAlertDialog(this);
        mnemonicBackupAlertDialog.show();
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        initRecycler();
    }

    private void initRecycler() {
        mnemonicRecyclerAdapter = new MnemonicRecyclerAdapter(R.layout.backup_mnemonic_recycler_item,mnemonicEntityArrayList);
        mnemonic_recycler.setLayoutManager(new GridLayoutManager(this,3));
        mnemonic_recycler.setAdapter(mnemonicRecyclerAdapter);

        String[] s = walletMnemonic.split(" ");
        for (int i = 0; i < s.length; i++) {
            mnemonicEntityArrayList.add(new MnemonicEntity(s[i]));
        }
        mnemonicRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void configViews() {

    }

    @OnClick(R.id.btn_backup)
    public void onClick(View view) {
        Intent intent = new Intent(this, VerifyMnemonicBackupActivity.class);
        intent.putExtra("walletId", walletId);
        intent.putExtra("walletMnemonic", walletMnemonic);
        startActivityForResult(intent, VERIFY_MNEMONIC_BACKUP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_MNEMONIC_BACKUP_REQUEST) {
            if (data != null) {
                finish();
            }
        }
    }
}

