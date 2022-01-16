package pro.upchain.wallet.web3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import pro.upchain.wallet.R;
import pro.upchain.wallet.web3.entity.Address;
import pro.upchain.wallet.web3.entity.Message;
import pro.upchain.wallet.web3.entity.TypedData;
import pro.upchain.wallet.web3.entity.Web3Transaction;

public class Web3View extends WebView {
    private static final String JS_PROTOCOL_CANCELLED = "Cancel";
    public static final String JS_PROTOCOL_ON_SUCCESSFUL = "window.ethereum.sendResponse(%1$d,\"%2$s\")";
    private static final String JS_PROTOCOL_ON_FAILURE = "window.ethereum.sendError(%1$d, \"%2$s\")";

    @Nullable
    private OnRequestAccountListener onRequestAccountListener;
    @Nullable
    private OnSignTransactionListener onSignTransactionListener;
    @Nullable
    private OnSignMessageListener onSignMessageListener;
    @Nullable
    private OnSignPersonalMessageListener onSignPersonalMessageListener;
    @Nullable
    private OnSignTypedMessageListener onSignTypedMessageListener;

    private JsInjectorClient jsInjectorClient;
    private Web3ViewClient webViewClient;

    public Web3View(@NonNull Context context) {
        this(context, null);
    }

    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Web3View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(new WrapWebViewClient(webViewClient, client, jsInjectorClient));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        jsInjectorClient = new JsInjectorClient(getContext());
        webViewClient = new Web3ViewClient(jsInjectorClient, new UrlHandlerManager());

        WebSettings webSettings = super.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        SignCallbackJSInterface signCallbackJSInterface = new SignCallbackJSInterface(
                this ,innerOnRequestAccountListener,innerOnSignTransactionListener,
                innerOnSignMessageListener,
                innerOnSignPersonalMessageListener,
                innerOnSignTypedMessageListener);
//        addJavascriptInterface(signCallbackJSInterface, "trust");
        addJavascriptInterface(signCallbackJSInterface, "_tw_");

        super.setWebViewClient(webViewClient);
    }

    @Override
    public WebSettings getSettings() {
        return new WrapWebSettings(super.getSettings());
    }


    public void setWalletAddress(@NonNull Address address) {
        jsInjectorClient.setWalletAddress(address);
    }

    @Nullable
    public Address getWalletAddress() {
        return jsInjectorClient.getWalletAddress();
    }

    public void setChainId(int chainId) {
        jsInjectorClient.setChainId(chainId);
    }

    public int getChainId() {
        return jsInjectorClient.getChainId();
    }

    public void setRpcUrl(@NonNull String rpcUrl) {
        jsInjectorClient.setRpcUrl(rpcUrl);
    }

    @Nullable
    public String getRpcUrl() {
        return jsInjectorClient.getRpcUrl();
    }

    public void addUrlHandler(@NonNull UrlHandler urlHandler) {
        webViewClient.addUrlHandler(urlHandler);
    }

    public void removeUrlHandler(@NonNull UrlHandler urlHandler) {
        webViewClient.removeUrlHandler(urlHandler);
    }

    public void setOnSignTransactionListener(@Nullable OnSignTransactionListener onSignTransactionListener) {
        this.onSignTransactionListener = onSignTransactionListener;
    }

    public void setOnSignMessageListener(@Nullable OnSignMessageListener onSignMessageListener) {
        this.onSignMessageListener = onSignMessageListener;
    }

    public void setOnSignPersonalMessageListener(@Nullable OnSignPersonalMessageListener onSignPersonalMessageListener) {
        this.onSignPersonalMessageListener = onSignPersonalMessageListener;
    }
    
    public void setOnSignTypedMessageListener(@Nullable OnSignTypedMessageListener onSignTypedMessageListener) {
        this.onSignTypedMessageListener = onSignTypedMessageListener;
    }

    public void setOnRequestAccountListener(@Nullable OnRequestAccountListener onRequestAccountListener) {
        this.onRequestAccountListener = onRequestAccountListener;
    }

    public void onSignTransactionSuccessful(Web3Transaction transaction, String signHex) {
        long callbackId = transaction.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
    }

    public void onSignMessageSuccessful(Message message, String signHex) {
        long callbackId = message.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
    }

    public void onSignPersonalMessageSuccessful(Message message, String signHex) {
        long callbackId = message.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_SUCCESSFUL, signHex);
    }

    public void onSignError(Web3Transaction transaction, String error) {
        long callbackId = transaction.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
    }

    public void onSignError(Message message, String error) {
        long callbackId = message.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, error);
    }

    public void onSignCancel(Message message) {
        long callbackId = message.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, JS_PROTOCOL_CANCELLED);
    }
    public void onSignCancel(Web3Transaction transaction) {
        long callbackId = transaction.leafPosition;
        callbackToJS(callbackId, JS_PROTOCOL_ON_FAILURE, JS_PROTOCOL_CANCELLED);
    }
    private void callbackToJS(long callbackId, String function, String param) {
        String callback = String.format(function, callbackId, param);
        post(() -> evaluateJavascript(callback, value -> Log.d("WEB_VIEW", value)));
    }

    private final OnSignTransactionListener innerOnSignTransactionListener = new OnSignTransactionListener() {
        @Override
        public void onSignTransaction(Web3Transaction transaction, String url) {
            if (onSignTransactionListener != null) {
                onSignTransactionListener.onSignTransaction(transaction, url );
            }
        }
    };
    private final OnRequestAccountListener innerOnRequestAccountListener = new OnRequestAccountListener() {

        @Override
        public void onRequestAccount(String url, Long id) {
            if (onRequestAccountListener != null) {
                onRequestAccountListener.onRequestAccount(url, id );
            }
        }
    };

    private final OnSignMessageListener innerOnSignMessageListener = new OnSignMessageListener() {
        @Override
        public void onSignMessage(Message message) {
            if (onSignMessageListener != null) {
                onSignMessageListener.onSignMessage(message);
            }
        }
    };
    private final OnSignPersonalMessageListener innerOnSignPersonalMessageListener = new OnSignPersonalMessageListener() {
        @Override
        public void onSignPersonalMessage(Message message) {
            onSignPersonalMessageListener.onSignPersonalMessage(message);
        }
    };

    private final OnSignTypedMessageListener innerOnSignTypedMessageListener = new OnSignTypedMessageListener() {
        @Override
        public void onSignTypedMessage(Message<String> message,String raw) {
            onSignTypedMessageListener.onSignTypedMessage(message,raw);
        }
    };
    public void setActivity(FragmentActivity activity)
    {
        webViewClient.setActivity(activity);
    }

    private class WrapWebViewClient extends WebViewClient {
        private final Web3ViewClient internalClient;
        private final WebViewClient externalClient;
        private final JsInjectorClient jsInjectorClient;



        public WrapWebViewClient(Web3ViewClient internalClient, WebViewClient externalClient, JsInjectorClient jsInjectorClient) {
            this.internalClient = internalClient;
            this.externalClient = externalClient;
            this.jsInjectorClient = jsInjectorClient;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript(jsInjectorClient.loadFile(getContext(), R.raw.trust_min), null);
            view.evaluateJavascript(jsInjectorClient.loadInitJs( ), null);
        }
        /*  @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return externalClient.shouldOverrideUrlLoading(view, url)
                    || internalClient.shouldOverrideUrlLoading(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return externalClient.shouldOverrideUrlLoading(view, request)
                    || internalClient.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            WebResourceResponse response = externalClient.shouldInterceptRequest(view, request);
            if (response != null) {
                try {
                    InputStream in = response.getData();
                    int len = in.available();
                    byte[] data = new byte[len];
                    int readLen = in.read(data);
                    if (readLen == 0) {
                        throw new IOException("Nothing is read.");
                    }
                    String injectedHtml = jsInjectorClient.injectJS(new String(data));
                    response.setData(new ByteArrayInputStream(injectedHtml.getBytes()));
                } catch (IOException ex) {
                    Log.d("INJECT AFTER_EXTRNAL", "", ex);
                }
            } else {
                response = internalClient.shouldInterceptRequest(view, request);
            }
            return response;
        }*/
    }
}
