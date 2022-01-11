package pro.upchain.wallet;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import java.util.HashMap;

import pro.upchain.wallet.base.BaseActivity;
import tron.walletserver.Wallet;

public class TestActivity extends BaseActivity {


    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
    findViewById(R.id.create_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Wallet wallet = new Wallet(true);
            String address = wallet.getAddress();
        }
    });

    findViewById(R.id.transition_button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            HashMap<String,Object>data = new HashMap<>();
            String to_address = "TDqa211hfDXXY2NiSW1VzNnzL2MiyNXGHh";
            data.put("to_address",to_address);
            data.put("owner_address",to_address);
       /*     EasyHttp.post("/wallet/createtransaction")
                    .baseUrl("https://api.shasta.trongrid.io")
                    .params()*/
        }
    });
    }
}