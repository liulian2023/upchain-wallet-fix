package pro.upchain.wallet.ui.activity;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;

import pro.upchain.wallet.BuildConfig;
import pro.upchain.wallet.R;
import pro.upchain.wallet.utils.dapp2.WebAppInterface;

public class DappActivity2 extends AppCompatActivity {
//    private  String DAPP_URL = "https://www.newmixf.com";
        private String DAPP_URL = "https://rstormsf.github.io/js-eth-personal-sign-examples/";
    private  int CHAIN_ID = 3;
    private  String RPC_URL = "https://ropsten.infura.io/v3/83adb0e120d94611b86bef6258e55302";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dapp3);
        String provderJs = loadFile(this,R.raw.trust_min);
        String initJs =loadInitJs();
        WebView webview = findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        WebAppInterface webAppInterface = new WebAppInterface(this, webview, webview.getUrl());
        webview.addJavascriptInterface(webAppInterface,"_tw_");
        webview.setWebViewClient(new MineWebClient(provderJs,initJs));
        webview.loadUrl(DAPP_URL);

    }
    public class MineWebClient extends WebViewClient{
        String provderJs;
        String initJs;

        public MineWebClient(String provderJs, String initJs) {
            this.provderJs = provderJs;
            this.initJs = initJs;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.evaluateJavascript(provderJs, null);
            view.evaluateJavascript(initJs, null);
        }
    }
    private String loadFile(Context context, @RawRes int rawRes) {
        byte[] buffer = new byte[0];
        try {
            InputStream in = context.getResources().openRawResource(rawRes);
            buffer = new byte[in.available()];
            int len = in.read(buffer);
            if (len < 1) {
                throw new IOException("Nothing is read.");
            }
        } catch (Exception ex) {
            Log.d("READ_JS_TAG", "Ex", ex);
        }
        return new String(buffer);
    }
    private String loadInitJs( ) {
        String s = loadFile(this, R.raw.trust_script);
        String replace = s.replace("__CHAINID__", CHAIN_ID + "");
       return replace.replace("__RPCURL__",RPC_URL);
    }
}