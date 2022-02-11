package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.utils.Web3jUtils.erc20Decimals;
import static pro.upchain.wallet.utils.Web3jUtils.getTokenSymbol;

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
    public static String emptyAddress = "0x0000000000000000000000000000000000000000";
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
                String tokenName = getTokenName(web3j, address);
                BigInteger decimals = erc20Decimals(web3j, address,null);
               if(StringMyUtil.isEmptyString(symbol) || decimals == null ||StringMyUtil.isEmptyString(tokenName)){
                   isValid[0] = false;
                   ToastUtils.showToast(getString(R.string.important_contract_fail));

               }
                dismissDialog();
               if(isValid[0]){
                   viewModel.save(address, symbol, decimals.intValue(),"",tokenName);
               }
            }
        }).start();
    }
    /**
     * 查询代币名称
     *
     * @param web3j
     * @param contractAddress
     * @return
     */
    public static String getTokenName(Web3j web3j, String contractAddress) {
        String methodName = "name";
        String name = null;
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
            name = results.get(0).getValue().toString();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return name;
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

}
