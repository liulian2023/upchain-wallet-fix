package pro.upchain.wallet.ui.fragment;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.SignatureException;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.BuildConfig;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.CanGoBackNextEvenEntity;
import pro.upchain.wallet.entity.DAppFunction;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.SignTransactionInterface;
import pro.upchain.wallet.entity.WalletAmountEvenEntity;
import pro.upchain.wallet.ui.activity.DappActivity;
import pro.upchain.wallet.ui.adapter.AutoCompleteUrlAdapter;
import pro.upchain.wallet.ui.entity.ItemClickListener;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.utils.dapp.WebAppInterface;
import pro.upchain.wallet.utils.dapp.WebViewExtensionKt;
import pro.upchain.wallet.view.AWalletAlertDialog;
import pro.upchain.wallet.view.SelectNetworkDialog;
import pro.upchain.wallet.view.SignMessageDialog;
import pro.upchain.wallet.viewmodel.DappBrowserViewModel;
import pro.upchain.wallet.viewmodel.DappBrowserViewModelFactory;
import pro.upchain.wallet.web3.OnRequestAccountListener;
import pro.upchain.wallet.web3.OnSignMessageListener;
import pro.upchain.wallet.web3.OnSignPersonalMessageListener;
import pro.upchain.wallet.web3.OnSignTransactionListener;
import pro.upchain.wallet.web3.OnSignTypedMessageListener;
import pro.upchain.wallet.web3.Web3View;
import pro.upchain.wallet.web3.entity.Address;
import pro.upchain.wallet.web3.entity.Message;
import pro.upchain.wallet.web3.entity.TypedData;
import pro.upchain.wallet.web3.entity.Web3Transaction;

import static pro.upchain.wallet.C.DAPP_DEFAULT_URL;
import static pro.upchain.wallet.entity.CryptoFunctions.sigFromByteArray;
import static pro.upchain.wallet.web3.Web3View.JS_PROTOCOL_ON_SUCCESSFUL;


public class DappBrowserFragment extends Fragment implements ItemClickListener, OnSignMessageListener, OnSignPersonalMessageListener, OnSignTransactionListener, OnSignTypedMessageListener, OnRequestAccountListener
{

    private static final String TAG = DappBrowserFragment.class.getSimpleName();
    String REQUEST_ACCOUNT_SUCCESS = "window.ethereum.setAddress(\"%1$s\");window.ethereum.selectedAddress = window.ethereum.address";
    private static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    DappBrowserViewModelFactory dappBrowserViewModelFactory;
    private DappBrowserViewModel viewModel;

    private SwipeRefreshLayout swipeRefreshLayout;
    private Web3View web3;

    private AutoCompleteTextView urlTv;
    private ProgressBar progressBar;

    private ETHWallet wallet;

    private NetworkInfo networkInfo;
    private SignMessageDialog dialog;
    private AWalletAlertDialog resultDialog;
    private AutoCompleteUrlAdapter adapter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    ImageView back_iv;
    ImageView next_iv;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        initView(view);
        initViewModel();
        setupAddressBar();
        viewModel.prepare(getContext());
        // Load url from a link within the app
        if (getArguments() != null && getArguments().getString("url") != null) {
            String url = getArguments().getString("url");
            loadUrl(url);
        }

        return view;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView(View view) {

        back_iv = view.findViewById(R.id.back_iv);
        next_iv = view.findViewById(R.id.next_iv);
        web3 = view.findViewById(R.id.web3view);
        web3.setActivity(getActivity());
        progressBar = view.findViewById(R.id.progressBar);
        urlTv = view.findViewById(R.id.url_tv);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> web3.reload());
        back_iv.setOnClickListener(v ->{
            backPressed();
        });
        next_iv.setOnClickListener(v ->{
            goToNextPage();
        });
    }

    private void setupAddressBar() {
        urlTv.setText(viewModel.getLastUrl(getContext()));

        adapter = new AutoCompleteUrlAdapter(getContext(), C.DAPP_BROWSER_HISTORY);
        adapter.setListener(this);
        urlTv.setAdapter(adapter);

        urlTv.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_GO)
            {
                String urlText = urlTv.getText().toString();
                handled = loadUrl(urlText);
                urlTv.dismissDropDown();
            }
            return handled;
        });

        urlTv.setOnClickListener(v -> urlTv.showDropDown());
    }

    private void dismissKeyboard(View view)
    {
        try
        {
            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        catch (NullPointerException e)
        {
            System.out.println("Pre - init");
        }
    }

    private void initViewModel() {

        dappBrowserViewModelFactory = new DappBrowserViewModelFactory();

        viewModel = ViewModelProviders.of(this, dappBrowserViewModelFactory).get(DappBrowserViewModel.class);
        viewModel.defaultNetwork().observe(getViewLifecycleOwner(), this::onDefaultNetwork);
        viewModel.defaultWallet().observe(getViewLifecycleOwner(), this::onDefaultWallet);
    }

    private void onDefaultWallet(ETHWallet wallet) {
        LogUtils.d("onDefaultWallet :"  + wallet);
        this.wallet = wallet;

        if (networkInfo != null) {
            setupWeb3();
        }

        // Default to last opened site
        if (web3.getUrl() == null) {
            loadUrl(viewModel.getLastUrl(getContext()));
        }
    }

    private void onDefaultNetwork(NetworkInfo networkInfo)
    {
        LogUtils.d(TAG,"onDefaultNetwork:"  + networkInfo);
        this.networkInfo = networkInfo;

        if (wallet != null) {
            setupWeb3();
        }
    }

    private void setupWeb3() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        web3.setChainId(networkInfo.chainId);
        String rpcURL = networkInfo.rpcServerUrl;
        web3.setRpcUrl(rpcURL);
        web3.setWalletAddress(new Address(wallet.address));

        web3.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webview, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        web3.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                urlTv.setText(url);
                return false;
            }
        });

        web3.setOnRequestAccountListener(this);
        web3.setOnSignMessageListener(this);
        web3.setOnSignPersonalMessageListener(this);
        web3.setOnSignTransactionListener(this);
        web3.setOnSignTypedMessageListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter == null || !adapter.hasContext()) setupAddressBar();
    }

    @Override
    public void onSignMessage(Message<String> message) {
        Log.d(TAG, "onSignMessage");

        DAppFunction dAppFunction = new DAppFunction() {
            @Override
            public void DAppError(Throwable error, Message<String> message) {
                Log.d(TAG, "onSignMessage error: " +error.getMessage());
                web3.onSignCancel(message);
                dialog.dismiss();
            }

            @Override
            public void DAppReturn(byte[] data, Message<String> message) {
                String signHex = Numeric.toHexString(data);
                Log.d(TAG, "Initial Msg: " + message.value);
                web3.onSignMessageSuccessful(message, signHex);
                dialog.dismiss();
            }
        };

        dialog = new SignMessageDialog(getActivity(), message);
        dialog.setAddress(wallet.address);
        dialog.setOnApproveListener(v -> {
            //ensure we generate the signature correctly:
            byte[] signRequest = message.value.getBytes();
            if (message.value.substring(0, 2).equals("0x"))
            {
                signRequest = Numeric.hexStringToByteArray(message.value);
            }
            viewModel.signMessage(signRequest, dAppFunction, message, wallet.getPassword());
        });
        dialog.setOnRejectListener(v -> {
            web3.onSignCancel(message);
            dialog.dismiss();
        });
        dialog.show();
    }


    @Override
    public void onSignTransaction(Web3Transaction transaction, String url) {
        Log.d(TAG, "onSignTransaction " + transaction);
        if (transaction.payload == null || transaction.payload.length() < 1)
        {
            //display transaction error
            onInvalidTransaction();
            web3.onSignCancel(transaction);
        }
        else
        {
            // 打开确认窗口， 输入密码
            viewModel.openConfirmation(getContext(), transaction, url);
        }
    }
    @Override
    public void onSignPersonalMessage(Message<String> message) {
        Log.d(TAG, "onSignPersonalMessage");

        DAppFunction dAppFunction = new DAppFunction() {
            @Override
            public void DAppError(Throwable error, Message<String> message) {
                Log.d(TAG, "onSignPersonalMessage error: " +error.getMessage());
                web3.onSignCancel(message);
                dialog.dismiss();
            }

            @Override
            public void DAppReturn(byte[] data, Message<String> message) {
                String signHex = Numeric.toHexString(data);
                Log.d(TAG, "sendResult " + signHex);
                web3.onSignPersonalMessageSuccessful(message, signHex);
                //Test Sig
                dialog.dismiss();
            }
        };

        dialog = new SignMessageDialog(getActivity(), message);
        dialog.setAddress(wallet.address);
        dialog.setOnApproveListener(v -> {
            String convertedMessage = hexToUtf8(message.value);
//            String convertedMessage = message.value;
            String signMessage = PERSONAL_MESSAGE_PREFIX
                    + convertedMessage.length()
                    + convertedMessage;
//            viewModel.signMessage(getEthereumMessage(message.value), dAppFunction, message, wallet.getPassword());
            viewModel.signMessage(signMessage.getBytes(), dAppFunction, message, wallet.getPassword());
        });

        dialog.setOnRejectListener(v -> {
            web3.onSignCancel(message);
            dialog.dismiss();
        });
        dialog.show();
    }
    private boolean isHex(String testMsg)
    {
        if (testMsg == null || testMsg.length() == 0) return false;
        testMsg = Numeric.cleanHexPrefix(testMsg);

        for (int i = 0; i < testMsg.length(); i++)
        {
            if (Character.digit(testMsg.charAt(i), 16) == -1) { return false; }
        }

        return true;
    }
    private byte[] getEthereumMessage(String message) {
        byte[] encodedMessage;
        if (isHex(message))
        {
            encodedMessage = Numeric.hexStringToByteArray(message);
        }
        else
        {
            encodedMessage = message.getBytes();
        }

        byte[] result;
            byte[] prefix = getEthereumMessagePrefix(encodedMessage.length);

            result = new byte[prefix.length + encodedMessage.length];
            System.arraycopy(prefix, 0, result, 0, prefix.length);
            System.arraycopy(encodedMessage, 0, result, prefix.length, encodedMessage.length);


        return result;
    }


    private byte[] getEthereumMessagePrefix(int messageLength) {
        return PERSONAL_MESSAGE_PREFIX.concat(String.valueOf(messageLength)).getBytes();
    }
    @Override
    public void onSignTypedMessage(Message<String> message,String raw) {
        Log.d(TAG, "onSignTypedMessage");
        DAppFunction dAppFunction = new DAppFunction() {
            @Override
            public void DAppError(Throwable error, Message<String> message) {
                Log.d(TAG, "onSignTypedMessage error: " +error.getMessage());
                web3.onSignCancel(message);
                dialog.dismiss();
            }

            @Override
            public void DAppReturn(byte[] data, Message<String> message) {
                String signHex = Numeric.toHexString(data);
                Log.d(TAG, "Initial Msg: " + message.value);
                web3.onSignMessageSuccessful(message, signHex);
                dialog.dismiss();
            }
        };

        dialog = new SignMessageDialog(getActivity(), message,raw,getResources().getString(R.string.Sign_Typed_Message));
        dialog.setAddress(wallet.address);
        dialog.setOnApproveListener(v -> {
            //ensure we generate the signature correctly:
            byte[] signRequest = message.value.getBytes();
            if (message.value.substring(0, 2).equals("0x"))
            {
                signRequest = Numeric.hexStringToByteArray(message.value);
            }
            viewModel.signMessage(signRequest, dAppFunction, message, wallet.getPassword());
        });
        dialog.setOnRejectListener(v -> {
            web3.onSignCancel(message);
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onRequestAccount(String url, Long id) {
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.Request_Accounts)
                .setMessage(getString(R.string.requests_your_address))
                .setPositiveButton(R.string.dialog_approve, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String address =  String.format(REQUEST_ACCOUNT_SUCCESS, WalletDaoUtils.getCurrent().address);
                        String callBack = String.format(JS_PROTOCOL_ON_SUCCESSFUL,id,WalletDaoUtils.getCurrent().address);
                        web3.post(new Runnable() {
                            @Override
                            public void run() {
                                web3.evaluateJavascript(address, null);
                                web3.evaluateJavascript(callBack, null);
                            }
                        });

                    }
                })
                .setNegativeButton(R.string.dialog_reject, null)
                .create();
        dialog.show();
    }


    public static String hexToUtf8(String hex) {
        hex = org.web3j.utils.Numeric.cleanHexPrefix(hex);
        ByteBuffer buff = ByteBuffer.allocate(hex.length() / 2);
        for (int i = 0; i < hex.length(); i += 2) {
            buff.put((byte) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        buff.rewind();
        Charset cs = Charset.forName("UTF-8");
        CharBuffer cb = cs.decode(buff);
        return cb.toString();
    }

    private void onProgress() {
        resultDialog = new AWalletAlertDialog(getActivity());
        resultDialog.setIcon(AWalletAlertDialog.NONE);
        resultDialog.setTitle(R.string.title_dialog_sending);
        resultDialog.setMessage(R.string.transfer);
        resultDialog.setProgressMode();
        resultDialog.setCancelable(false);
        resultDialog.show();
    }

    private void onInvalidTransaction() {
        resultDialog = new AWalletAlertDialog(getActivity());
        resultDialog.setIcon(AWalletAlertDialog.ERROR);
        resultDialog.setTitle(getString(R.string.invalid_transaction));
        resultDialog.setMessage(getString(R.string.contains_no_data));
        resultDialog.setProgressMode();
        resultDialog.setCancelable(false);
        resultDialog.show();
    }


    public void homePressed()
    {
        urlTv.setText(DAPP_DEFAULT_URL);
        loadUrl(DAPP_DEFAULT_URL);
    }

    private boolean loadUrl(String urlText)
    {
        urlTv.setText(urlText);

        web3.loadUrl(Utils.formatUrl(urlText));
        web3.requestFocus();

        viewModel.setLastUrl(getContext(), urlText);
        adapter.add(urlText);
        adapter.notifyDataSetChanged();
        dismissKeyboard(urlTv);

        return true;
    }

    public void addBookmark()
    {
        if (urlTv != null && urlTv.getText() != null)
        {
            viewModel.addBookmark(getContext(), urlTv.getText().toString());
        }
    }

    public void viewBookmarks()
    {
        if (viewModel == null) return;
        List<String> bookmarks = viewModel.getBookmarks();
        //display in popup
        if (getActivity() == null) return;
        SelectNetworkDialog dialog = new SelectNetworkDialog(getActivity(), bookmarks.toArray(new String[bookmarks.size()]), urlTv.getText().toString());
        dialog.setTitle(R.string.bookmarks);
        dialog.setButtonText(R.string.visit);
        dialog.setOnClickListener(v1 -> {
            String url = dialog.getSelectedItem();
            urlTv.setText(url);
            loadUrl(url);
            dialog.dismiss();
        });
        dialog.show();
    }

    public void removeBookmark()
    {
        viewModel.removeBookmark(getContext(), urlTv.getText().toString());
    }

    public boolean getUrlIsBookmark()
    {
        return viewModel != null && urlTv != null && viewModel.getBookmarks().contains(urlTv.getText().toString());
    }

    public void reloadPage() {
        web3.reload();
    }

    public void share() {
        if (web3.getUrl() != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, web3.getUrl());
            intent.setType("text/plain");
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(String url)
    {
        loadUrl(url);
    }


    public boolean onBackPressed() {
        if (web3.canGoBack()) {
            web3.goBack();
            return true;
        }
        return false;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.e(TAG, "onCreateOptionsMenu()");
        menu.clear();
//        inflater.inflate(R.menu.menu_parent_fragment, menu);
    }


    private void goToNextPage()
    {
        if (web3.canGoForward())
        {
            checkForwardClickArrowVisibility();
            web3.goForward();
        }
    }
    public void backPressed() {
        if (web3.canGoBack()) {
            web3.goBack();
        }
        if(web3.canGoBack()){
           back_iv.setAlpha(1.0f);
        }else {
            back_iv.setAlpha(0.3f);
        }

    }
    private void checkForwardClickArrowVisibility()
    {
        WebBackForwardList sessionHistory = web3.copyBackForwardList();
        int nextIndex = sessionHistory.getCurrentIndex() + 1;
        if (nextIndex >= sessionHistory.getSize() - 1) next_iv.setAlpha(0.3f);
        else next_iv.setAlpha(1.0f);
    }

    private boolean isAddressValid(String address)
    {
        try
        {
            new org.web3j.abi.datatypes.Address(address);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void walletAmountEven(CanGoBackNextEvenEntity canGoBackNextEvenEntity){
        if(back_iv!=null){
            if(canGoBackNextEvenEntity.isCanGoBack()){
                back_iv.setAlpha(1.0f);

            }else {
                back_iv.setAlpha(0.3f);
            }
        }
        if(next_iv!=null){
            if(canGoBackNextEvenEntity.isCanNextPage()){
                next_iv.setAlpha(1.0f);
            }else {
                next_iv.setAlpha(0.3f);
            }
        }

    }


  /*  public void scanQR()
    {
        //scanning intent
        Intent intent = new Intent(getContext(), QRCodeScannerActivity.class);
        startActivityForResult(intent, DAPP_BARCODE_READER_REQUEST_CODE);
    }

    public void handleQRCode(int resultCode, Intent data, FragmentMessenger messenger)
    {
        //result
        String qrCode = null;
        if (resultCode == FullScannerFragment.SUCCESS && data != null)
        {
            qrCode = data.getStringExtra(FullScannerFragment.BarcodeObject);
        }

        if (qrCode != null)
        {
            //detect if this is an address
            if (isAddressValid(qrCode))
            {
                DisplayAddressFound(qrCode, messenger);
            }
            else
            {
                //attempt to go to site
                loadUrl(qrCode);
            }
        }
        else
        {
            Toast.makeText(getContext(), R.string.toast_invalid_code, Toast.LENGTH_SHORT).show();
        }
    }

    private void DisplayAddressFound(String address, FragmentMessenger messenger)
    {
        resultDialog = new AWalletAlertDialog(getActivity());
        resultDialog.setIcon(AWalletAlertDialog.ERROR);
        resultDialog.setTitle(getString(R.string.address_found));
        resultDialog.setMessage(getString(R.string.is_address));
        resultDialog.setButtonText(R.string.dialog_load_as_contract);
        resultDialog.setButtonListener(v -> {
            messenger.AddToken(address);
            resultDialog.dismiss();
        });
        resultDialog.setSecondaryButtonText(R.string.action_cancel);
        resultDialog.setSecondaryButtonListener(v -> {
            resultDialog.dismiss();
        });
        resultDialog.setCancelable(true);
        resultDialog.show();
    }*/
}
