package pro.upchain.wallet.utils.dapp2;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;

public class WebAppInterface {
    private Context context;
    private WebView webView;
    private String dappUrl;

    public WebAppInterface(Context context, WebView webView, String dappUrl) {
        this.context = context;
        this.webView = webView;
        this.dappUrl = dappUrl;
    }
    @JavascriptInterface
    public void postMessage(String json){
        JSONObject jsonObject = JSONObject.parseObject(json);
    }
}
