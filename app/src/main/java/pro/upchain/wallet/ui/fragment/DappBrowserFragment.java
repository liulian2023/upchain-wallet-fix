package pro.upchain.wallet.ui.fragment;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.web3j.utils.Numeric;

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.base.BasePopupWindow;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.BookmarkEntity;
import pro.upchain.wallet.entity.RightMenuEntity;
import pro.upchain.wallet.entity.WebViewLoadFinishEvenEntity;
import pro.upchain.wallet.entity.DAppFunction;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.WebViewStartLoadEvenEntity;
import pro.upchain.wallet.pop.HeightProvider;
import pro.upchain.wallet.pop.RightMenuPop;
import pro.upchain.wallet.ui.activity.MainActivity;
import pro.upchain.wallet.ui.entity.ItemClickListener;
import pro.upchain.wallet.utils.KeyboardUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.StatusBarUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;
import pro.upchain.wallet.utils.WalletDaoUtils;
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
import pro.upchain.wallet.web3.entity.Web3Transaction;

import static pro.upchain.wallet.C.DAPP_DEFAULT_URL;
import static pro.upchain.wallet.web3.Web3View.JS_PROTOCOL_ON_SUCCESSFUL;
import static pro.upchain.wallet.web3.Web3View.REQUEST_ACCOUNT_JS_PROTOCOL_ON_SUCCESSFUL;


public class DappBrowserFragment extends BaseFragment implements ItemClickListener, OnSignMessageListener, OnSignPersonalMessageListener, OnSignTransactionListener, OnSignTypedMessageListener, OnRequestAccountListener
{

    private static final String TAG = DappBrowserFragment.class.getSimpleName();
    public static String REQUEST_ACCOUNT_SUCCESS = "window.ethereum.setAddress(\"%1$s\");window.ethereum.selectedAddress = window.ethereum.address";
    private static final String PERSONAL_MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    DappBrowserViewModelFactory dappBrowserViewModelFactory;
    public DappBrowserViewModel viewModel;

    HeightProvider heightProvider;


    private ETHWallet wallet;

    private NetworkInfo networkInfo;
    private SignMessageDialog dialog;
    private AWalletAlertDialog resultDialog;
    @BindView(R.id.swipe_refresh)
     SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.dapp_toolbar_constraint)
    ConstraintLayout dapp_toolbar_constraint;
    @BindView(R.id.web3view)
    public Web3View web3;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.next_iv)
    ImageView next_iv;
    @BindView(R.id.more_iv)
    ImageView more_iv;
    @BindView(R.id.url_etv)
     EditText urlEtv;
    @BindView(R.id.progressBar)
     ProgressBar progressBar;
    @BindView(R.id.back_next_linear)
    LinearLayout back_next_linear;
    @BindView(R.id.dapp_framelayout)
    FrameLayout dapp_framelayout;
    @BindView(R.id.add_bookmark_iv)
    ImageView add_bookmark_iv;
    private String currentTile;
    private BookMaskFragment bookMaskFragment;
    boolean isFirstTime = true;
    @Override
    public int getLayoutResId() {
        return R.layout.fragment_webview;
    }
    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        heightProvider = new HeightProvider(new SoftReference<>(getActivity())).init().setHeightListener(new HeightProvider.HeightListener() {
            @Override
            public void onHeightChanged(int height) {
                if(height == 0){
                    expandCollapseView(back_next_linear,true);
                    urlEtv.clearFocus();
                }else {
                    expandCollapseView(back_next_linear,false);
                }
            }
        });
        initView();
        initViewModel();
        setupAddressBar();
        viewModel.prepare(getContext());
        // Load url from a link within the app
        if (getArguments() != null && getArguments().getString("url") != null) {
            String url = getArguments().getString("url");
            loadUrl(url);
        }
    }



    @Override
    public void configViews() {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        web3.setActivity(getActivity());
        swipeRefreshLayout.setOnRefreshListener(() -> web3.reload());

        urlEtv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    loadUrl(urlEtv.getText().toString());
                    KeyboardUtils.hideKeyboard(urlEtv);
                }
                return false;
            }
        });

    }

    private void setupAddressBar() {
        String lastUrl = viewModel.getLastUrl(getContext());
        urlEtv.setText(lastUrl);
        if(StringMyUtil.isEmptyString(lastUrl)){
            viewBookmarks();
        }else {

            loadUrl(lastUrl);
        }
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
            String lastUrl = viewModel.getLastUrl(getContext());
            if(StringMyUtil.isNotEmpty(lastUrl)){
                loadUrl(lastUrl);
            }else {
                viewBookmarks();
            }
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
                urlEtv.setText(url);
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
        //避免首页跳转activity返回后状态栏变白色
        if(isFirstTime){

            StatusBarUtil.setColor(getActivity(), ContextCompat.getColor(getContext(),R.color.white));
            StatusBarUtil.setLightMode(getActivity(),true);
            setupAddressBar();
        }
        isFirstTime = false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        StatusBarUtil.setColor(getActivity(), ContextCompat.getColor(getContext(),R.color.white));
        StatusBarUtil.setLightMode(getActivity(),true);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
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

        dialog = new SignMessageDialog(getActivity(),hexToUtf8(message.value));

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
        if (transaction.payload == null || transaction.payload.length() < 1) {
            //display transaction error
            onInvalidTransaction();
            web3.onSignCancel(transaction);
        } else {
            //打开确认窗口，输入密码
            viewModel.openDappTransfer(getContext(), transaction, url);
//            viewModel.openConfirmation(getContext(), transaction, url);
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

        dialog = new SignMessageDialog(getActivity(),  hexToUtf8(message.value));
        dialog.setOnApproveListener(v -> {
            String convertedMessage = hexToUtf8(message.value);
//            String convertedMessage = message.value;
            String signMessage = PERSONAL_MESSAGE_PREFIX
                    + convertedMessage.length()
                    + convertedMessage;
            viewModel.signMessage(signMessage.getBytes(), dAppFunction, message, wallet.getPassword());
        });

        dialog.setOnRejectListener(v -> {
            web3.onSignCancel(message);
            dialog.dismiss();
        });
        dialog.show();
    }
    private boolean isHex(String testMsg) {
        if (testMsg == null || testMsg.length() == 0) return false;
        testMsg = Numeric.cleanHexPrefix(testMsg);

        for (int i = 0; i < testMsg.length(); i++) {
            if (Character.digit(testMsg.charAt(i), 16) == -1) { return false; }
        }
        return true;
    }
    private byte[] getEthereumMessage(String message) {
        byte[] encodedMessage;
        if (isHex(message)) {
            encodedMessage = Numeric.hexStringToByteArray(message);
        } else {
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

        dialog = new SignMessageDialog(getActivity(),raw);
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
/*        Dialog dialog = new AlertDialog.Builder(getContext())
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
        dialog.show();*/
        String address =  String.format(REQUEST_ACCOUNT_SUCCESS, WalletDaoUtils.getCurrent().address);
        String callBack = String.format(REQUEST_ACCOUNT_JS_PROTOCOL_ON_SUCCESSFUL,id,WalletDaoUtils.getCurrent().address);
        web3.post(new Runnable() {
            @Override
            public void run() {
//                web3.evaluateJavascript(address, null);
                web3.evaluateJavascript(callBack, null);
            }
        });
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
        urlEtv.setText(DAPP_DEFAULT_URL);
        loadUrl(DAPP_DEFAULT_URL);
    }

    public boolean loadUrl(String urlText)
    {
        urlEtv.setText(urlText);
        if(StringMyUtil.isNotEmpty(urlText)){
            web3.loadUrl(Utils.formatUrl(urlText));
            viewModel.setLastUrl(getContext(), urlText);
        }



        dismissKeyboard(urlEtv);

        return true;
    }

    public void addBookmark()
    {
        if (urlEtv != null && urlEtv.getText() != null)
        {
            viewModel.addBookmark(getContext(), urlEtv.getText().toString(),currentTile);
            ToastUtils.showToast(R.string.action_added);
        }
    }

    public void viewBookmarks()
    {
        if (viewModel == null) return;
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        bookMaskFragment = new BookMaskFragment();
        fragmentTransaction.add(R.id.dapp_framelayout,bookMaskFragment);
        fragmentTransaction.show(bookMaskFragment);
        fragmentTransaction.commit();
        dapp_framelayout.setVisibility(View.VISIBLE);
    }
    public void hideBookmarks()
    {
        if (viewModel == null) return;
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        if(bookMaskFragment!=null){
            fragmentTransaction.remove(bookMaskFragment);
        }
        dapp_framelayout.setVisibility(View.GONE);
        fragmentTransaction.commit();
    }

    public void removeBookmark()
    {
        viewModel.removeBookmark(getContext(), urlEtv.getText().toString());
    }

    public boolean getUrlIsBookmark()
    {
        return viewModel != null && urlEtv != null && viewModel.getBookmarks().contains(urlEtv.getText().toString());
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
    public void walletAmountEven(WebViewLoadFinishEvenEntity webViewLoadFinishEvenEntity){
        if(back_iv!=null){
            if(webViewLoadFinishEvenEntity.isCanGoBack()){
                back_iv.setAlpha(1.0f);

            }else {
                back_iv.setAlpha(0.3f);
            }
        }
        if(next_iv!=null){
            if(webViewLoadFinishEvenEntity.isCanNextPage()){
                next_iv.setAlpha(1.0f);
            }else {
                next_iv.setAlpha(0.3f);
            }
        }
        if(urlEtv!=null){
            urlEtv.setText(webViewLoadFinishEvenEntity.geeUrl());
        }

         currentTile = webViewLoadFinishEvenEntity.getTitle();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startLoadEven(WebViewStartLoadEvenEntity webViewStartLoadEvenEntity){
        hideBookmarks();
    }
    /**
     * Used to expand or collapse the view
     */
    private synchronized void expandCollapseView(@NotNull View view, boolean expandView)
    {
        //detect if view is expanded or collapsed
        boolean isViewExpanded = view.getVisibility() == View.VISIBLE;

        //Collapse view
        if (isViewExpanded && !expandView)
        {
            int finalWidth = view.getWidth();
            ValueAnimator valueAnimator = slideAnimator(finalWidth, 0, view);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator.start();
        }
        //Expand view
        else if (!isViewExpanded && expandView)
        {
            view.setVisibility(View.VISIBLE);

            int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

            view.measure(widthSpec, heightSpec);
            int width = view.getMeasuredWidth();
            ValueAnimator valueAnimator = slideAnimator(0, width, view);
            valueAnimator.start();
        }
    }

    @NotNull
    private ValueAnimator slideAnimator(int start, int end, final View view) {

        final ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(valueAnimator -> {
            // Update Height
            int value = (Integer) valueAnimator.getAnimatedValue();

            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = value;
            view.setLayoutParams(layoutParams);
        });
        animator.setDuration(100);
        return animator;
    }
    @OnClick({R.id.next_iv,R.id.back_iv,R.id.more_iv,R.id.back_home_iv,R.id.add_bookmark_iv,R.id.clear_iv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.clear_iv:
                urlEtv.setText("");
                hideSoftInput();
                expandCollapseView(back_next_linear,true);
                urlEtv.clearFocus();

                break;
            case R.id.next_iv:
                goToNextPage();
                break;
            case R.id.back_iv:
                backPressed();
                break;
            case R.id.more_iv:
                initRightPop();
                break;
            case R.id.back_home_iv:
                viewBookmarks();
                break;
            case R.id.add_bookmark_iv:
                boolean isAdded = isBookmarkAdded();
                if(isAdded){
                    ToastUtils.showToast(R.string.bookmark_already_added);
                }else {
                    addBookmark();
                }
                break;
            default:
                break;
        }
    }

    public boolean isBookmarkAdded() {
        boolean isAdded = false;
        for (int i = 0; i < viewModel.getBookmarks().size(); i++) {
            if( viewModel.getBookmarks().get(i).getAddress().equals(web3.getUrl())){
                isAdded =true;
                break;
            }
        }
        return isAdded;
    }

    private void initRightPop() {
        RightMenuPop rightMenuPop = new RightMenuPop(getContext(),viewModel,this);
        rightMenuPop.setOnPopItemClick(new BasePopupWindow.OnRecycleItemClick() {
            @Override
            public void onPopItemClick(View view, int position) {
                switch (position ){
                    case 0:
                        reloadPage();
                        break;
                    case 1:
                        RightMenuEntity rightMenuEntity = rightMenuPop.rightMenuEntityArrayList.get(position);
                        if(rightMenuEntity.getName().equals(getResources().getString(R.string.action_view_bookmarks))){
                            viewBookmarks();
                        }else {
                            addBookmark();
                        }
                        break;
                    case 2:
                        share();
                        break;
                    default:
                        break;
                }
            }
        });
        rightMenuPop.showAsDropDown(more_iv,0,0, Gravity.BOTTOM);
        Utils.darkenBackground(getActivity(),0.7f);
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
