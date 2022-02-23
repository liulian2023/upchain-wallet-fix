package pro.upchain.wallet.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BasePopupWindow;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.web3.Web3View;
import pro.upchain.wallet.web3.entity.Address;

public class AddNetWordDialog extends Dialog {
    NetworkInfo networkInfo;
    TextView tip_tv;
    Button cancel_btb;
    Button sure_btn;
    Web3View web3View;
    public AddNetWordDialog(@NonNull Context context, NetworkInfo networkInfo,Web3View web3View) {
        super(context);
        this.networkInfo = networkInfo;
        this.web3View = web3View;
        setContentView(R.layout.add_network_dialog_layout);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getWindow().setGravity(Gravity.BOTTOM);
        tip_tv = findViewById(R.id.tip_tv);
        cancel_btb = findViewById(R.id.cancel_btb);
        sure_btn = findViewById(R.id.sure_btn);
        tip_tv.setText(getContext().getResources().getString(R.string.switch_to)+" "+networkInfo.name);
        cancel_btb.setOnClickListener(v->dismiss());
        sure_btn.setOnClickListener(v-> {
            RepositoryFactory repositoryFactory = MyApplication.repositoryFactory();
            repositoryFactory.ethereumNetworkRepository.setDefaultNetworkInfo(networkInfo);
            web3View.post(new Runnable() {
                @Override
                public void run() {
                    web3View.setChainId(networkInfo.chainId);
                    String rpcURL = networkInfo.rpcServerUrl;
                    web3View.setRpcUrl(rpcURL);
                    web3View.reload();
                    dismiss();
                }
            });
        });
    }

}
