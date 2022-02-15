package pro.upchain.wallet.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.dialog.NumberProgressBar;
import com.azhon.appupdate.listener.OnButtonClickListener;
import com.azhon.appupdate.listener.OnDownloadListenerAdapter;
import com.azhon.appupdate.manager.DownloadManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import pro.upchain.wallet.BuildConfig;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.RxHttp.net.utils.SystemUtil;
import pro.upchain.wallet.domain.ETHWallet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.entity.AppUpdateEntity;
import pro.upchain.wallet.entity.LoginEntity;
import pro.upchain.wallet.entity.SystemParamsEntity;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;



public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        downLoadTxt();
        requestSystemParams();
        login();
    }


    private void requestSystemParams() {
            HttpApiUtils.wwwNormalRequest(this, null, RequestUtils.SYSTEM_SYSTEM, new HashMap<String, Object>(), new HttpApiUtils.OnRequestLintener() {
                @Override
                public void onSuccess(String result) {
                    SystemParamsEntity systemParamsEntity = JSONObject.parseObject(result, SystemParamsEntity.class);
                    String onlineService = systemParamsEntity.getOnlineService();
                    SharePreferencesUtil.putString(CommonStr.ONLINE_SERVICE,onlineService);
                }

                @Override
                public void onFail(String msg) {

                }
            });
    }

    private void login() {
        String token = SharePreferencesUtil.getString(CommonStr.USER_TOKEN,"");
        HashMap<String, Object> data = new HashMap<>();
        if(StringMyUtil.isNotEmpty(token)){
            String username = SharePreferencesUtil.getString(CommonStr.USER_NAME,"");
            String password = SharePreferencesUtil.getString(CommonStr.USER_PASSWORD,"");
            data.put("userName",username);
            data.put("password",password);
        }else {
            data.put("userName", Md5Utils.md5(Utils.randomPsw(20)+System.currentTimeMillis()));
            data.put("password", Utils.randomPsw(8));
        }

        HttpApiUtils.wwwNormalRequest(this, null, RequestUtils.LOGIN, data, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                LoginEntity loginEntity = JSONObject.parseObject(result, LoginEntity.class);
                SharePreferencesUtil.putString( CommonStr.USER_TOKEN,loginEntity.getToken());
                SharePreferencesUtil.putString( CommonStr.USER_NAME, (String) data.get("userName"));
                SharePreferencesUtil.putString( CommonStr.USER_PASSWORD, (String) data.get("password"));

                new FetchWalletInteract().fetch().observeOn(AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).subscribe(
                        SplashActivity.this::onWalltes,SplashActivity. this::onError

                );
            }

            @Override
            public void onFail(String msg) {
                new FetchWalletInteract().fetch().observeOn(AndroidSchedulers.mainThread()).delay(2, TimeUnit.SECONDS).subscribe(
                        SplashActivity. this::onWalltes, SplashActivity.this::onError

                );
            }
        });
    }

    public void onWalltes(List<ETHWallet> ethWallets) {
        if (ethWallets.size() == 0) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        }
    }

    public void onError(Throwable throwable) {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SplashActivity.this.startActivity(intent);
    }

    private void downLoadTxt(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                accessTxt(new AccessTxtListener() {
                    @Override
                    public void success(String content) {
                        if(StringMyUtil.isNotEmpty(content)){
                            SharePreferencesUtil.putString(CommonStr.URL_LIST,content);
                            List<String> parseArray = JSONArray.parseArray(content, String.class);
                            for (int i = 0; i < parseArray.size(); i++) {
                                String url = parseArray.get(i);
                                HashMap<String, Object> data = new HashMap<>();
                                HttpApiUtils.normalRequest(SplashActivity.this, null, RequestUtils.URL_TEST, data, new HttpApiUtils.OnRequestLintener() {
                                    @Override
                                    public void onSuccess(String result) {
                                    SharePreferencesUtil.putString(CommonStr.NEW_BASE_URL,url);

                                    }

                                    @Override
                                    public void onFail(String msg) {

                                    }
                                });
                            }

                        }


                    }
                    @Override
                    public void fail(int code) {
                    }
                });
            }
        }).start();

    }
    /**
     * 读取文本内容
     */
    public  void accessTxt(AccessTxtListener accessTxtListener) {
        String  url= "https://cpimg.yisan5.com/appDomains.txt";
        OkHttpClient.Builder clientBuilder= new OkHttpClient().newBuilder();
        clientBuilder.readTimeout(2, TimeUnit.SECONDS);
        clientBuilder.connectTimeout(2, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(2, TimeUnit.SECONDS);
        OkHttpClient okClient = clientBuilder.build();
        if(StringMyUtil.isNotEmpty(url)){
            Request request = new Request.Builder().url(url).build();
            try {
                okhttp3.Response response = okClient.newCall(request).execute();
                if (response.isSuccessful()) {
                    String msg = response.body().string();
                    if(accessTxtListener!=null){
                        accessTxtListener.success(msg);
                    }
                } else {
                    accessTxtListener.fail(response.code());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public  interface AccessTxtListener{
        void success(String content);
        void fail(int code);

    }
    AccessTxtListener accessTxtListener;

    public void setAccessTxtListener(AccessTxtListener accessTxtListener) {
        this.accessTxtListener = accessTxtListener;
    }
}
