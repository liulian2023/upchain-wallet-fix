package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.repository.TokenRepository.createTokenTransferData;

import android.app.Dialog;
import androidx.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.PendingHistoryEntity;
import pro.upchain.wallet.entity.RateEntity;
import pro.upchain.wallet.entity.TransferHistoryEntity;
import pro.upchain.wallet.interact.CreateTransactionInteract;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.TransferDaoUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.ConfirmTransactionView;
import pro.upchain.wallet.view.InputPwdView;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SendActivity extends BaseActivity {

    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;

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

    private String walletAddr;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;

    private String netCost;
    private  BigInteger gasPrice;
    private BigInteger gasLimit;


    private boolean sendingTokens = false;

    private Dialog dialog;

    private static final int QRCODE_SCANNER_REQUEST = 1100;

    private static final double miner_min = 5 ;
    private static final double miner_max = 55;

    private String scanResult;

    String ETH2USDTRate;
    String ETH2OtherRate;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;
        sendingTokens = StringMyUtil.isEmptyString(contractAddress)?false:true;
        tvTitle.setText(symbol + getString(R.string.transfer_title));
        wallet_amount_tv.setText(balance+symbol);

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);

        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.gasSettings().observe(this, this::onGasSettings);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

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
        HttpApiUtils.requestETHUSDTRate(new HttpApiUtils.OnRequestLintener() {
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
        viewModel.prepare(this, sendingTokens? ConfirmationType.ETH: ConfirmationType.ERC20);
    }

    @Override
    public void configViews() {

        final DecimalFormat gasformater = new DecimalFormat();
        //保留几位小数
        gasformater.setMaximumFractionDigits(2);
        //模式  四舍五入
        gasformater.setRoundingMode(RoundingMode.CEILING);


        final String etherUnit = getString(R.string.transfer_ether_unit);

        try {
            netCost = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit), 4) + etherUnit;
        } catch (Exception e) {
        }


    }

    private void updateNetworkFee() {

        try {
            netCost = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit),  4) + " " + C.ETH_SYMBOL;
            tvGasCost.setText(String.valueOf(netCost ));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onGasSettings(GasSettings gasSettings) {
        gasPrice = gasSettings.gasPrice;
        gasLimit = gasSettings.gasLimit;

    }

    private boolean verifyInfo(String address, String amount) {

        try {
            new Address(address);
        } catch (Exception e) {
            ToastUtils.showToast(R.string.addr_error_tips);
            return false;
        }

        try {
            String wei = BalanceUtils.EthToWei(amount);
            return wei != null;
        } catch (Exception e) {
            ToastUtils.showToast(R.string.amount_error_tips);

            return false;
        }
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
                    ConfirmTransactionView confirmView = new ConfirmTransactionView(this, this::onClick);
                    confirmView.fillInfo(walletAddr, toAddr, " - " + amount + " " +  symbol, netCost, gasPrice, gasLimit);
                    dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                    dialog.setContentView(confirmView);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
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
                                new BigInteger("250000")
                        );
   /*                         new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ETHWallet current = WalletDaoUtils.getCurrent();
                                    try {
                                        CreateTransactionInteract. transferERC20Token(current.getAddress(),etTransferAddress.getText().toString().trim(),BalanceUtils.tokenToWei(new BigDecimal(amountText.getText().toString().trim()), decimals).toBigInteger()
                                                , ETHWalletUtils.derivePrivateKey(current.getId(), current.getPassword()),contractAddress);
                                    } catch (ExecutionException e) {
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
                                gasLimit );
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
