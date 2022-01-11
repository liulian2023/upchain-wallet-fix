package pro.upchain.wallet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.utils.BalanceUtils;

import java.math.BigInteger;

public class ConfirmTransactionView extends FrameLayout {

    public ConfirmTransactionView(Context context, OnClickListener onClickListener) {
        super(context);

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_confrim_transcation, this, true);

        findViewById(R.id.confirm_button).setOnClickListener(onClickListener);
    }


    public void setTitle(String title) {

        TextView titleTv = (TextView)findViewById(R.id.title);
        titleTv.setText(title);
    }

    public void setRequest(String url) {
        TextView labelWebsite = (TextView)findViewById(R.id.label_website);
        labelWebsite.setVisibility(VISIBLE);

        TextView text_website = (TextView)findViewById(R.id.text_website);
        text_website.setVisibility(VISIBLE);
        text_website.setText(url);
    }

    public void fillInfo(String fromAddr, String addr, String amount, String fee, BigInteger gasPrice, BigInteger gasLimit) {


        TextView fromAddressText = (TextView)findViewById(R.id.text_from);
        fromAddressText.setText(fromAddr);

        TextView toAddressText = (TextView)findViewById(R.id.text_to);
        toAddressText.setText(addr);

        TextView valueText = (TextView)findViewById(R.id.text_value);
        valueText.setText(amount);

        TextView gasPriceText = (TextView)findViewById(R.id.text_gas_price);
        String gasPriceStr = BalanceUtils.weiToGwei(gasPrice) + " " + C.GWEI_UNIT;
        gasPriceText.setText(gasPriceStr);

        TextView gasLimitText = (TextView)findViewById(R.id.text_gas_limit);
        gasLimitText.setText(gasLimit.toString());

        TextView networkFeeText = (TextView)findViewById(R.id.text_network_fee);
        networkFeeText.setText(fee);


    }


}
