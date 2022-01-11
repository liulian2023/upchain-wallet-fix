package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;

import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.R;
import pro.upchain.wallet.RxHttp.net.api.HttpApiUtils;
import pro.upchain.wallet.RxHttp.net.api.RequestUtils;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.RxHttp.net.utils.SystemUtil;
import pro.upchain.wallet.domain.ETHWallet;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.entity.LoginEntity;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.Utils;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class SplashActivity extends AppCompatActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        login();
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
}
