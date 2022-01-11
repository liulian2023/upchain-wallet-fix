package pro.upchain.wallet.ui.activity;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.viewmodel.AddTokenViewModel;
import pro.upchain.wallet.viewmodel.AddTokenViewModelFactory;
import com.gyf.barlibrary.ImmersionBar;


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
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny熊
 * WeiXin: xlbxiong
 */

public class AddCustomTokenActivity extends BaseActivity {

    // 0xB8c77482e45F1F44dE1745F52C74426C631bDD52  BNB 18
    private static String emptyAddress = "0x0000000000000000000000000000000000000000";
    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel viewModel;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.decimals)
    TextView decimals;

    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;

    @BindView(R.id.save)
    TextView save;

    @BindView(R.id.address_input_layout)
    TextInputLayout addressLayout;

    @BindView(R.id.symbol_input_layout)
    TextInputLayout symbolLayout;

    @BindView(R.id.decimal_input_layout)
    TextInputLayout decimalsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_add_custom_token;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
    }

    @Override
    public void initDatas() {
        addTokenViewModelFactory = new AddTokenViewModelFactory();
        viewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);
//        viewModel.progress().observe(this, systemView::showProgress);
        viewModel.error().observe(this, this::onError);
        viewModel.result().observe(this, this::onSaved);

    }

    private void onError(ErrorEnvelope errorEnvelope) {
//        showDialog("出错~");

    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .navigationBarColor(R.color.white)
                .init();
    }


    private void onSave() {
        final boolean[] isValid = {true};
        String address = this.address.getText().toString();
        if (TextUtils.isEmpty(address)) {
            addressLayout.setError(getString(R.string.error_field_required));
            isValid[0] = false;
        }
        if (!Address.isAddress(address)) {
            addressLayout.setError(getString(R.string.error_invalid_address));
            isValid[0] = false;
        }

        showDialog(getString(R.string.important_contract));

        RepositoryFactory rf = MyApplication.repositoryFactory();
        Web3j web3j = Web3j.build(new HttpService(rf.ethereumNetworkRepository.getDefaultNetwork().rpcServerUrl));

        new Thread(new Runnable() {
            @Override
            public void run() {
               String symbol = getTokenSymbol(web3j, address);
               BigInteger decimals = erc20Decimals(web3j, address);
               if(StringMyUtil.isEmptyString(symbol) || decimals == null){
                   isValid[0] = false;
                   ToastUtils.showToast(getString(R.string.important_contract_fail));

               }
                dismissDialog();
               if(isValid[0]){
                   viewModel.save(address, symbol, decimals.intValue(),"");
               }
            }
        }).start();
    }

    private void onSaved(boolean result) {
        if (result) {
            viewModel.showTokens(this);

            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);

            finish();
        }
    }


    @OnClick({R.id.save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                onSave();
                break;
        }
    }
/**
        * 查询erc20的精度
     *
             * @param
     * @param web3j
     * @param contract 合约地址
     * @return
             */
    public  BigInteger erc20Decimals(Web3j web3j, String contract)  {

        //ERC20代币合约方法
        Function function = new Function(
                "decimals",
                Arrays.asList(),
                Collections.singletonList(new TypeReference<Type>() {
                }));
        //创建RawTransaction交易对象
        String encodedFunction = FunctionEncoder.encode(function);
        String value = null;
        try {
            value = web3j.ethCall(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(null,
                    contract, encodedFunction), DefaultBlockParameterName.LATEST).send().getValue();
        } catch (IOException e) {
            LogUtils.e("erc20Decimals:{}",e);
        }
        System.out.println("erc20Decimals="+value);
        BigInteger bigInteger = new BigInteger(value.substring(2), 16);
        System.out.println("erc20Decimals="+bigInteger);
        return bigInteger;
    }

    /**
     * 查询代币符号
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static String getTokenSymbol(Web3j web3j, String contractAddress) {
        String methodName = "symbol";
        String symbol = null;
        String fromAddr = emptyAddress;
        List<Type> inputParameters = new ArrayList<>();
        List<TypeReference<?>> outputParameters = new ArrayList<>();

        TypeReference<Utf8String> typeReference = new TypeReference<Utf8String>() {
        };
        outputParameters.add(typeReference);

        Function function = new Function(methodName, inputParameters, outputParameters);

        String data = FunctionEncoder.encode(function);
        Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

        EthCall ethCall;
        try {
            ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
            List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
            symbol = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return symbol;
    }
}
