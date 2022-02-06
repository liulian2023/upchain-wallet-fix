package pro.upchain.wallet.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;

public class TransferDialog extends Dialog {
    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;
    private GasSettings gasSettings;

    TextView net_url_tv;
    TextView chain_name_tv;
    TextView miner_fee_tv;
    TextView miner_fee_rate_tv;
    Button cancel_btb;
    Button sure_btn;
    Activity activity;
    public TransferDialog(@NonNull Context context) {
        super(context);
        activity = (Activity) context;
        setContentView(R.layout.dapp_transfer_layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        initView();


    }

    private void initView() {


        net_url_tv = findViewById(R.id.net_url_tv);
        chain_name_tv = findViewById(R.id.chain_name_tv);
        miner_fee_tv = findViewById(R.id.miner_fee_tv);
        miner_fee_rate_tv = findViewById(R.id.miner_fee_rate_tv);
        cancel_btb = findViewById(R.id.cancel_btb);
        sure_btn = findViewById(R.id.sure_btn);

    }

    public TransferDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public TransferDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

}
