package pro.upchain.wallet.ui.activity;

import android.graphics.Bitmap;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.CommonToolBarUtils;

public class AboutUsActivity extends BaseActivity {

    @BindView(R.id.about_us_webview)
    WebView about_us_webview;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    public void initToolBar() {
        CommonToolBarUtils.initToolbar(this,R.string.about_us);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        WebSettings settings = about_us_webview.getSettings();
        settings.setJavaScriptEnabled(true);
        about_us_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webview, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

        });

        HashMap<String, Object> data = new HashMap<String, Object>();
        HttpApiUtils.wwwNormalRequest(this, null, RequestUtils.ABOUT_US, data, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                JSONObject jsonObject = JSONObject.parseObject(result);
                if(jsonObject == null || !jsonObject.containsKey("content")){
                    about_us_webview.loadDataWithBaseURL(null,"<style> img{ max-width:100%; height:auto;} </style>"+getResources().getString(R.string.about_us_content),"text/html", "utf-8",null);
                }else {
                    String content = jsonObject.getString("content");
                    about_us_webview.loadDataWithBaseURL(null,"<style> img{ max-width:100%; height:auto;} </style>"+content,"text/html", "utf-8",null);
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }
}