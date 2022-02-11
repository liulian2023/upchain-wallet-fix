package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.C.PRUNE_ACTIVITY;
import static pro.upchain.wallet.entity.ConfirmationType.WEB3TRANSACTION;
import static pro.upchain.wallet.ui.activity.AddCustomTokenActivity.emptyAddress;
import static pro.upchain.wallet.view.AWalletAlertDialog.ERROR;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProviders;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pro.upchain.wallet.C;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.RateEntity;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.TransactionData;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.AWalletAlertDialog;
import pro.upchain.wallet.view.InputPwdView;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;
import pro.upchain.wallet.web3.entity.Web3Transaction;

public class DappTransferActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = ConfirmationActivity.class.getSimpleName();
    AWalletAlertDialog dialog;
    Dialog bottomSheetDialog;
    String symbol = null;
    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;
    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;
    MyHandler myHandler = new MyHandler();
    private BigDecimal amount;
    private int decimals;
    private String contractAddress;
    private String amountStr;
    private String toAddress;
    private String isApprove;
    private ConfirmationType confirmationType;
    private byte[] transactionBytes = null;
    private Web3Transaction transaction;
    private boolean isMainNet;
    private String networkName;
    private  GasSettings gasSettings;
    TextView net_url_tv;
    TextView chain_name_tv;
    TextView miner_fee_tv;
    TextView miner_fee_rate_tv;
    TextView miner_fee_tip_tv;
    TextView not_enough_tip_tv;
    Button cancel_btb;
    Button sure_btn;
    String ETH2USDTRate = SharePreferencesUtil.getString(CommonStr.ETH2USDTRate,"");
    String ETH2OtherRate;
    private BigInteger currentLimit = BigInteger.ZERO;
    private String netCost;
    private BigInteger gasPrice =BigInteger.ZERO;
    private String walletAmount;

    @Override
    public int getLayoutId() {
        return R.layout.dapp_transfer_layout;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

    }

    private void requestGasLimit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String to= transaction.recipient.toString();
                String data = SendActivity.createTokenTransferData(to, BalanceUtils.tokenToWei(new BigDecimal(transaction.value+"").setScale(2), decimals).toBigInteger());
                if(StringMyUtil.isEmptyString(data)){
                    return;
                }
                Transaction transaction = new Transaction (WalletDaoUtils.getCurrent().address, null, null, null, to, BigInteger.ZERO, data);
                RepositoryFactory rf = MyApplication.repositoryFactory();
                Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));
                try {
                    EthEstimateGas send = web3j.ethEstimateGas(transaction)
                            .send();
                    BigInteger amountUsed = send.getAmountUsed();
                    if(amountUsed !=null){
                        currentLimit = amountUsed;
                        myHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateNetworkFee();
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void requestSymbol() {
        RepositoryFactory rf = MyApplication.repositoryFactory();
        Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));
        String methodName = "symbol";

        String fromAddr = emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, DappTransferActivity.this.transaction.recipient.toString(), data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            if(results!=null && results.size()!=0){
                symbol = results.get(0).getValue().toString();
                requestETHoTHERRate(symbol);
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }


    }
    private void updateNetworkFee() {
        if(gasPrice!=BigInteger.ZERO && currentLimit!=BigInteger.ZERO && StringMyUtil.isNotEmpty(ETH2USDTRate)){
            try {
                netCost = BalanceUtils.weiToEth(gasPrice.multiply(currentLimit),  4);
                miner_fee_rate_tv.setText(netCost+ Utils.getCurrentSymbol());
                if(StringMyUtil.isNotEmpty(ETH2USDTRate)){
                    miner_fee_tv.setText("US$"+(new BigDecimal(netCost).multiply(new BigDecimal(ETH2USDTRate)).setScale(2,BigDecimal.ROUND_HALF_UP))+" ");
                }
                if(StringMyUtil.isNotEmpty(walletAmount)){
                    if(new BigDecimal(walletAmount).setScale(8).compareTo(new BigDecimal(netCost).setScale(8))> -1){
                        //  余额够
                        not_enough_tip_tv.setVisibility(View.INVISIBLE);
                        miner_fee_tv.setTextColor(Color.BLACK);
                        miner_fee_tip_tv.setTextColor(Color.parseColor("#CECECE"));
                        sure_btn.setEnabled(true);
                        sure_btn.setBackgroundResource(R.drawable.my_button_selector);
                    }else {
                        //  余额不够
                        not_enough_tip_tv.setVisibility(View.VISIBLE);
                        not_enough_tip_tv.setText(String.format(getResources().getString(R.string.miner_fee_not_enough),C.ETC_SYMBOL,miner_fee_rate_tv.getText().toString()));
                        miner_fee_tip_tv.setTextColor(Color.parseColor("#DB4C4E"));
                        miner_fee_tv.setTextColor(Color.parseColor("#DB4C4E"));
                        sure_btn.setEnabled(false);
                        sure_btn.setBackgroundResource(R.drawable.sure_button_enable_flase_sbape);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    private class MyHandler extends Handler {}
    @Override
    public void configViews() {
        initView();
        transaction = getIntent().getParcelableExtra(C.EXTRA_WEB3TRANSACTION);

        String urlRequester = getIntent().getStringExtra(C.EXTRA_CONTRACT_NAME);
        net_url_tv.setText(urlRequester);
        transactionBytes = Numeric.hexStringToByteArray(transaction.payload);
        RepositoryFactory rf = MyApplication.repositoryFactory();
        String name = rf.ethereumNetworkRepository.getDefaultNetwork().name;
        chain_name_tv.setText(name);
        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);
        tokensViewModel.defaultWallet().observe(this,  this::showWallet);
        tokensViewModel.tokens().observe(this, this::onTokens);

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);
        viewModel.defaultWallet().observe(this, this::onDefaultWallet);
        viewModel.gasSettings().observe(this, this::    onGasSettings);
        viewModel.sendDappTransaction().observe(this, this::onDappTransaction);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

    }

    @Override
    protected void onResume() {
        super.onResume();
        tokensViewModel.prepare();
        viewModel.prepare(this, confirmationType);
        if(transaction!=null){
//            requestSymbol();
        }
        requestETHUSDTRate();
        if(transaction.gasLimit!=BigInteger.ZERO){
            requestGasLimit();
        }else {
            currentLimit =   transaction.gasLimit;
            updateNetworkFee();
        }
    }

    private void onTokens(Token[] tokens) {
        for (int i = 0; i < tokens.length; i++) {
            Token token = tokens[i];
            if(token.tokenInfo.symbol.equalsIgnoreCase(C.ETH_SYMBOL)){
                walletAmount = token.balance;
            }
        }
        updateNetworkFee();
    }
    public void showWallet(ETHWallet wallet) {}
    private void onProgress(boolean shouldShowProgress) {
        hideDialog();
        if (shouldShowProgress) {
            dialog = new AWalletAlertDialog(this);
            dialog.setProgressMode();
            dialog.setTitle(R.string.title_dialog_sending);
            dialog.setCancelable(false);
            dialog.show();
        }
    }
    private void onDefaultWallet(ETHWallet wallet) {
        viewModel.calculateGasSettings(transactionBytes, false);
    }

    private void requestETHUSDTRate() {
        HttpApiUtils.requestETHUSDTRate(new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                ETH2USDTRate = rateEntity.getPrice();
                SharePreferencesUtil.putString(CommonStr.ETH2USDTRate,ETH2USDTRate);
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
    private void requestETHoTHERRate(String symbol) {
        HttpApiUtils.requestETHoTHERRate(symbol,new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                ETH2OtherRate = rateEntity.getPrice();
                updateNetworkFee();
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
    private void onGasSettings(GasSettings gasSettings) {
        LogUtils.d(TAG, "onGasSettings:" + gasSettings);
        this.gasSettings = gasSettings;
        gasPrice= gasSettings.gasPrice;
        BigDecimal networkFeeBD = new BigDecimal(gasSettings
                .gasPrice.multiply(gasSettings.gasLimit));

        String networkFee = BalanceUtils.weiToEth(networkFeeBD).toPlainString() + " " +Utils.getCurrentSymbol();
//        networkFeeText.setText(networkFee);
        updateNetworkFee();
        if (confirmationType == WEB3TRANSACTION)
        {
//            //update amount
//            BigDecimal ethValueBD = amount.add(networkFeeBD);
//
//            //convert to ETH
//            ethValueBD = Convert.fromWei(ethValueBD, Convert.Unit.ETHER);
//            String valueUpdate = getEthString(ethValueBD.doubleValue()) + " " ;
//            valueText.setText(valueUpdate);
        }
    }


    private void onTransaction(String hash) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.transaction_succeeded);
        dialog.setMessage(hash);
        dialog.setButtonText(R.string.copy);
        dialog.setButtonListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("transaction hash", hash);
            clipboard.setPrimaryClip(clip);
            dialog.dismiss();
            sendBroadcast(new Intent(PRUNE_ACTIVITY));
        });
        dialog.setOnDismissListener(v -> {
            dialog.dismiss();
//            new HomeRouter().open(this, true);
            finish();
        });
        dialog.show();
    }

    private void onDappTransaction(TransactionData txData) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.transaction_succeeded);
        dialog.setMessage(txData.txHash);
        dialog.setButtonText(R.string.copy);
        dialog.setButtonListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("transaction hash", txData.txHash);
            clipboard.setPrimaryClip(clip);
            dialog.dismiss();
        });
        dialog.setOnDismissListener(v -> {
            dialog.dismiss();
/*          Intent intent = new Intent(C.SIGN_DAPP_TRANSACTION);
            intent.putExtra(C.EXTRA_WEB3TRANSACTION, transaction);
            intent.putExtra(C.EXTRA_HEXDATA, txData.signature);
            intent.putExtra(C.EXTRA_SUCCESS, true);
            sendBroadcast(intent);*/

            finish();
        });
        dialog.show();
    }



    private void onError(ErrorEnvelope error) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.error_transaction_failed);
        dialog.setMessage(error.message);
        dialog.setIcon(ERROR);
        dialog.setButtonText(R.string.button_ok);
        dialog.setButtonListener(v -> {
            dialog.dismiss();
            if (confirmationType == WEB3TRANSACTION)
            {
                Intent intent = new Intent(C.SIGN_DAPP_TRANSACTION);
                intent.putExtra(C.EXTRA_WEB3TRANSACTION, transaction);
                intent.putExtra(C.EXTRA_HEXDATA, "0x0000"); //Placeholder signature - transaction failed
                intent.putExtra(C.EXTRA_SUCCESS, false);
                sendBroadcast(intent);
            }
            finish();
        });
        dialog.show();
    }
    private void initView() {
        //设置布局在底部
        getWindow().setGravity(Gravity.BOTTOM);
        //设置布局填充满宽度
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width= WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(layoutParams);
        //设置进出动画
//        overridePendingTransition(R.anim.activity_int_400,R.anim.activity_out_400);

        not_enough_tip_tv = findViewById(R.id.not_enough_tip_tv);
        miner_fee_tip_tv = findViewById(R.id.miner_fee_tip_tv);
        net_url_tv = findViewById(R.id.net_url_tv);
        chain_name_tv = findViewById(R.id.chain_name_tv);
        miner_fee_tv = findViewById(R.id.miner_fee_tv);
        miner_fee_rate_tv = findViewById(R.id.miner_fee_rate_tv);
        cancel_btb = findViewById(R.id.cancel_btb);
        sure_btn = findViewById(R.id.sure_btn);

        sure_btn.setOnClickListener(this);
        cancel_btb.setOnClickListener(this);



    }


    public static String getEthString(double ethPrice)
    {
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(ethPrice);
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()){
                case R.id.sure_btn:
                    InputPwdView pwdView = new InputPwdView(this, this::sendTransaction);
                    bottomSheetDialog = new BottomSheetDialog(this);
                    bottomSheetDialog.setContentView(pwdView);
                    bottomSheetDialog.setCancelable(true);
                    bottomSheetDialog.setCanceledOnTouchOutside(true);
                    bottomSheetDialog.show();
                    break;
                case R.id.cancel_btb:
                    finish();
                    break;
                default:
                    break;

            }
    }

    private void sendTransaction(String pwd) {
        bottomSheetDialog.dismiss();
        viewModel.signWeb3DAppTransaction(transaction, gasSettings.gasPrice, gasSettings.gasLimit, pwd);

    }
}
