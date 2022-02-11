package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.C.DEFAULT_GAS_LIMIT_FOR_ETH;
import static pro.upchain.wallet.C.DEFAULT_GAS_LIMIT_FOR_TOKENS;
import static pro.upchain.wallet.utils.Web3jUtils.getBalance;
import static pro.upchain.wallet.utils.Web3jUtils.getETHBalance;

import android.app.Dialog;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.appcompat.app.AlertDialog;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import pro.upchain.wallet.C;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.PendingHistoryEntity;
import pro.upchain.wallet.entity.RateEntity;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.TransferDaoUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.utils.Web3jUtils;
import pro.upchain.wallet.view.ConfirmTransactionView;
import pro.upchain.wallet.view.InputPwdView;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SendActivity extends BaseActivity implements TextWatcher {

    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;
    MyHandler myHandler;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.et_transfer_address)
    EditText etTransferAddress;
    @BindView(R.id.send_amount)
    EditText amountText;

    @BindView(R.id.tv_gas_cost)
    TextView tvGasCost;
    @BindView(R.id.wallet_amount_tv)
    TextView wallet_amount_tv;
    @BindView(R.id.send_amount_tv)
    TextView send_amount_tv;

    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.gas_price)
    TextView tvGasPrice;

    private String walletAddr;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;

    private String netCost;
    private  BigInteger gasPrice;
    private BigInteger currentLimit;


    private boolean sendingTokens = false;
    private Dialog dialog;

    private static final int QRCODE_SCANNER_REQUEST = 1100;

    private static final double miner_min = 5 ;
    private static final double miner_max = 55;

    private String scanResult;
    static int EDIT_OK = 111;
    String ETH2USDTRate = SharePreferencesUtil.getString(CommonStr.ETH2USDTRate,"");
    String ETH2OtherRate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    public static void startAty(Context context,String walletAddr,String contractAddress,
                                String balance,int decimals,String symbol){
        Intent intent = new Intent(context, SendActivity.class);
        intent.putExtra(C.EXTRA_ADDRESS,walletAddr);
        intent.putExtra(C.EXTRA_CONTRACT_ADDRESS,contractAddress);
        intent.putExtra(C.EXTRA_BALANCE,balance);
        intent.putExtra(C.EXTRA_DECIMALS,decimals);
        intent.putExtra(C.EXTRA_SYMBOL,symbol);
        context.startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog!=null){
            if(dialog.isShowing()){
                dialog.dismiss();
            }
            dialog = null;
        }
    }

    @Override
    public int getLayoutId() {

        return R.layout.activity_transfer;
    }

    @Override
    public void initToolBar() {
        ivBtn.setImageResource(R.drawable.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {

        Intent intent = getIntent();
        walletAddr = intent.getStringExtra(C.EXTRA_ADDRESS);
        tvGasCost.setText("0.00"+Utils.getCurrentSymbol()+" ≈ 0USDT");
        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        if(StringMyUtil.isEmptyString(symbol)){
            symbol = Utils.getCurrentSymbol();
        }
        sendingTokens = StringMyUtil.isEmptyString(contractAddress)?false:true;

        updateNetworkFee();
        tvTitle.setText(getString(R.string.action_send)+" "+symbol );
        if(StringMyUtil.isNotEmpty(balance)){
            wallet_amount_tv.setText(balance+symbol);
        }else {
            RepositoryFactory rf = MyApplication.repositoryFactory();
            Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));
            if(sendingTokens){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BigDecimal balance = getBalance(walletAddr, contractAddress, web3j);
                            String textBalance = Web3jUtils.getBalanceString(decimals,balance,sendingTokens,contractAddress,web3j);
                            wallet_amount_tv.setText(textBalance+symbol);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BigDecimal balance = getETHBalance(web3j,walletAddr);
                            String textBalance = Web3jUtils.getBalanceString(decimals,balance,sendingTokens,contractAddress,web3j);

                            wallet_amount_tv.setText(textBalance+symbol);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        }

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);

        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.gasSettings().observe(this, this::onGasSettings);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

        if(sendingTokens){
            currentLimit = new BigInteger(DEFAULT_GAS_LIMIT_FOR_TOKENS);
        }else {
            currentLimit = new BigInteger(DEFAULT_GAS_LIMIT_FOR_ETH);
        }

        // 首页直接扫描进入
        scanResult = intent.getStringExtra("scan_result");
        if (!TextUtils.isEmpty(scanResult)) {
            parseScanResult(scanResult);
        }
        requestETHUSDTRate();
        requestETHoTHERRate(symbol);
    }




    private void onProgress(boolean shouldShowProgress) {
        hideDialog();
        if (shouldShowProgress) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_sending)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            dialog.show();
        }else {
            if(dialog!=null){
                dialog.dismiss();
            }
        }
    }
    private void requestETHUSDTRate() {
        HttpApiUtils.requestETHUSDTRate(new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                RateEntity rateEntity = JSONObject.parseObject(result, RateEntity.class);
                ETH2USDTRate = rateEntity.getPrice();
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
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        viewModel.prepare(this, sendingTokens? ConfirmationType.ERC20: ConfirmationType.ETH);
    }

    @Override
    public void configViews() {
        myHandler = new MyHandler();
        final String etherUnit = getString(R.string.transfer_ether_unit);


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

//                progressChanged(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        etTransferAddress.addTextChangedListener(this);
        amountText.addTextChangedListener(this);

    }

    private void progressChanged(int progress) {
        final DecimalFormat gasformater = new DecimalFormat();
        //保留几位小数
        gasformater.setMaximumFractionDigits(2);
        //模式  四舍五入
        gasformater.setRoundingMode(RoundingMode.CEILING);
        double p = progress / 100f;
        double d = (miner_max - miner_min) * p + miner_min;

        gasPrice = BalanceUtils.gweiToWei(BigDecimal.valueOf(d));
        tvGasPrice.setText(gasformater.format(d) + " " + C.GWEI_UNIT);

        updateNetworkFee();
    }

    private void updateNetworkFee() {
        try {
            netCost = BalanceUtils.weiToEth(gasPrice.multiply(currentLimit),  4);
            if(StringMyUtil.isNotEmpty(ETH2USDTRate)){
                tvGasCost.setText(String.valueOf(netCost)+ " " + Utils.getCurrentSymbol()+" ≈ "+(new BigDecimal(netCost).multiply(new BigDecimal(ETH2USDTRate)).setScale(2,BigDecimal.ROUND_HALF_UP))+" "+"USDT");
            }else {
                tvGasCost.setText(String.valueOf(netCost)+ " " + Utils.getCurrentSymbol());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onGasSettings(GasSettings gasSettings) {
        gasPrice = gasSettings.gasPrice;
//        progressChanged(10);
    }

    private boolean verifyInfo(String address, String amount) {
        //ethGetCode

        if(!Address.isAddress(address)){
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(R.string.addr_error_tips);
                }
            });

            return false;
        }
        try {
            String wei = BalanceUtils.EthToWei(amount);
            return wei != null;
        } catch (Exception e) {
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(R.string.amount_error_tips);
                }
            });

            return false;
        }
    }

    private boolean verifyContract(String toAddr,String amount) {
        showDialog();
        //ethGetCode
        new Thread(new Runnable() {
            @Override
            public void run() {
                RepositoryFactory rf = MyApplication.repositoryFactory();
                Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));
                try {
                    EthGetCode ethGetCode = web3j.ethGetCode(toAddr, DefaultBlockParameterName.LATEST).send();
                    String ethGetCodeCode = ethGetCode.getCode();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideDialog();
                            if(sendingTokens){
                                if(StringMyUtil.isNotEmpty(ethGetCodeCode)){
                                    showconfirmView(toAddr, amount);
                                }else {
                                    ToastUtils.showToast(R.string.addr_error_tips);
                                }
                            }else {
                                if(StringMyUtil.isEmptyString(ethGetCodeCode)||ethGetCodeCode.equalsIgnoreCase("0x")||ethGetCodeCode.equalsIgnoreCase("0x0")){
                                    showconfirmView(toAddr, amount);
                                }else {
                                    ToastUtils.showToast(R.string.addr_error_tips);
                                }
                            }
                        }
                    });


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return  false;
    }

    private void showconfirmView(String toAddr, String amount) {
        ConfirmTransactionView confirmView = new ConfirmTransactionView(SendActivity.this, SendActivity.this::onClick);
        confirmView.fillInfo(walletAddr, toAddr, " - " + amount + " " + symbol, netCost+"  "+Utils.getCurrentSymbol(), gasPrice, currentLimit);
        dialog = new BottomSheetDialog(SendActivity.this, R.style.BottomSheetDialog);
        dialog.setContentView(confirmView);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @OnClick({R.id.rl_btn, R.id.btn_next})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                Intent intent = new Intent(SendActivity.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.btn_next:
                // confirm info;
                String toAddr = etTransferAddress.getText().toString().trim();
                String amount = amountText.getText().toString().trim();


                if (verifyInfo(toAddr, amount)) {
                    verifyContract(toAddr,amount);
                }
                break;
            case R.id.confirm_button:
                // send
                dialog.hide();
                InputPwdView pwdView = new InputPwdView(this, pwd -> {
                    if (sendingTokens) {
                        viewModel.createTokenTransfer(pwd,
                                etTransferAddress.getText().toString().trim(),
                                contractAddress,
                                BalanceUtils.tokenToWei(new BigDecimal(amountText.getText().toString().trim()), decimals).toBigInteger(),
                                gasPrice,
//                                new BigInteger("250000")
                                currentLimit
                        );
   /*                         new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ETHWallet current = WalletDaoUtils.getCurrent();
                                    try {
                                        CreateTransactionInteract. transferERC20Token(current.getAddress(),etTransferAddress.getText().toString().trim(),BalanceUtils.tokenToWei(new BigDecimal(amountText.getText().toString().trim()), decimals).toBigInteger()
                                                , ETHWalletUtils.derivePrivateKey(current.getId(), current.getPassword()),contractAddress);
                                    } catch (ExecutionException e) {q
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();*/

                    } else {
                        viewModel.createTransaction(pwd, etTransferAddress.getText().toString().trim(),
                                Convert.toWei(amountText.getText().toString().trim(), Convert.Unit.ETHER).toBigInteger(),
                                gasPrice,
                                currentLimit);
                    }
                });

                dialog = new BottomSheetDialog(this);
                dialog.setContentView(pwdView);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        myHandler.removeCallbacks(gasRunnable);
        //1秒未输入认为输入完毕
        if(StringMyUtil.isNotEmpty(amountText.getText().toString())&&StringMyUtil.isNotEmpty(etTransferAddress.getText().toString())){
            myHandler.postDelayed(gasRunnable,200);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //限制中文输入
        if (s.length() > 0) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c >= 0x4e00 && c <= 0X9fff) {
                    s.delete(i,i+1);
                }
            }
        }


    }

    private class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String toAddr = etTransferAddress.getText().toString().trim();
            if(EDIT_OK == msg.what){
                String amount = amountText.getText().toString().trim();
                if(StringMyUtil.isEmptyString(contractAddress)){
                    //ETH
                    if(StringMyUtil.isNotEmpty(ETH2USDTRate) && !amount.startsWith(".") && StringMyUtil.isNotEmpty(amount)){
                        send_amount_tv.setText("≈$"+new BigDecimal(amount).multiply(new BigDecimal(ETH2USDTRate)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }else {
                    //合约
                    if(StringMyUtil.isNotEmpty(ETH2OtherRate) && !amount.startsWith(".") && StringMyUtil.isNotEmpty(amount)){
                        send_amount_tv.setText("≈$"+new BigDecimal(amount).multiply(new BigDecimal(ETH2OtherRate)).setScale(2, BigDecimal.ROUND_HALF_UP));
                    }
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean verifyInfo = verifyInfo(etTransferAddress.getText().toString().trim(), amountText.getText().toString().trim());
                        if(verifyInfo){
                            BigDecimal weiAmount = new BigDecimal(amountText.getText().toString().trim());
                            String data = createTokenTransferData(etTransferAddress.getText().toString().trim(), BalanceUtils.tokenToWei(weiAmount, decimals).toBigInteger());
                            if(StringMyUtil.isEmptyString(data)){
                                return;
                            }
                            String to= "";
                            if(StringMyUtil.isNotEmpty(contractAddress)){
                                to = contractAddress;
                            }else {
                                to = toAddr;
                            }

                            Transaction transaction = new Transaction (walletAddr, null, null, null, to, BigInteger.ZERO, data);
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

                    }
                }).start();
            }
        }
    }
    private Runnable gasRunnable = new Runnable() {
        @Override
        public void run() {
            myHandler.sendEmptyMessage(EDIT_OK);
        }
    };
    private void onError(ErrorEnvelope error) {
        hideDialog();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error_transaction_failed)
                .setMessage(error.message)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    // Do nothing

                })
                .create();
        dialog.show();
    }

    private void onTransaction(String hash) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PendingHistoryEntity pendingHistoryEntity = new PendingHistoryEntity();
        pendingHistoryEntity.setToUid("0");
        pendingHistoryEntity.setToAddress(etTransferAddress.getText().toString().trim());
        pendingHistoryEntity.setMoney(amountText.getText().toString().trim());
        pendingHistoryEntity.setCreateTime(simpleDateFormat.format(new Date()));
        pendingHistoryEntity.setHash(hash);
        pendingHistoryEntity.setItemType(1);
        pendingHistoryEntity.setMineAddress(WalletDaoUtils.getCurrent().address);
        pendingHistoryEntity.setSymbol(symbol);
        pendingHistoryEntity.setNetWork(MyApplication.repositoryFactory().ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl);
        TransferDaoUtils.insertNewTransfer(pendingHistoryEntity);

        hideDialog();
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transaction_succeeded)
                .setMessage(hash)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    finish();
                })
                .setNeutralButton(R.string.copy, (dialog1, id) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("transaction hash", hash);
                    clipboard.setPrimaryClip(clip);
                    finish();
                })
                .create();
        dialog.show();
    }

    private void fillAddress(String addr) {
        try {
            new Address(addr);
            etTransferAddress.setText(addr);
        } catch (Exception e) {
            ToastUtils.showToast(R.string.addr_error_tips);
        }
    }

    private void parseScanResult(String result) {
        if (result.contains(":") && result.contains("?")) {  // 符合协议格式
            String[] urlParts = result.split(":");
            if (urlParts[0].equals("ethereum")) {
                urlParts =  urlParts[1].split("\\?");

                fillAddress(urlParts[0]);

                // ?contractAddress=0xdxx & decimal=1 & value=100000
//                 String[] params = urlParts[1].split("&");
//                for (String param : params) {
//                    String[] keyValue = param.split("=");
//                }

            }


        } else {  // 无格式， 只有一个地址
            fillAddress(result);
        }


    }
    public static String createTokenTransferData(String to, BigInteger tokenAmount) {
//        try {
            List<Type> params = Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to), new Uint256(tokenAmount));

            List<TypeReference<?>> returnTypes = Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
            });

            Function function = new Function("transfer", params, returnTypes);
            return FunctionEncoder.encode(function);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                // 对扫描结果进行处理
                parseScanResult(scanResult);
//                ToastUtils.showLongToast(scanResult);
            }
        }
    }

}
