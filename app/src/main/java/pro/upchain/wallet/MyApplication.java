package pro.upchain.wallet;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.ArrayMap;

import androidx.multidex.MultiDexApplication;

import pro.upchain.wallet.RxHttp.net.common.ApiConfig;
import pro.upchain.wallet.domain.DaoMaster;
import pro.upchain.wallet.domain.DaoSession;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.repository.SharedPreferenceRepository;
import pro.upchain.wallet.utils.AppFilePath;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.LocalManageUtil;
import pro.upchain.wallet.utils.LogInterceptor;
import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.model.HttpHeaders;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.TimeZone;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.utils.SharePreferencesUtil;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class MyApplication extends MultiDexApplication {

    private static MyApplication sInstance;

    private RefWatcher refWatcher;

    private DaoSession daoSession;
    private static OkHttpClient httpClient;
    public static RepositoryFactory repositoryFactory;
    public static SharedPreferenceRepository sp;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();
        initGlobalTimeZone();
//        EventBus.getDefault().register(this);
        Realm.init(this);

        refWatcher = LeakCanary.install(this);

        AppFilePath.init(this);

       initEasyHttp();
       initRxhttp();
    }
    /**
     * 设置app内全局时区
     */
    public void initGlobalTimeZone() {
        TimeZone chinaTimeZone = TimeZone.getTimeZone("GMT+8");
        TimeZone.setDefault(chinaTimeZone);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocalManageUtil.setLocal(base));
    }

    private void initRxhttp(){
        ArrayMap<String, String> headMap = new ArrayMap<String, String>();
        //retrofit2 工厂类初始化
        ApiConfig build = new ApiConfig.Builder()
                .setBaseUrl1(CommonStr.API_HOST1)//BaseUrl，这个地方加入后项目中默认使用该url
                .setSucceedCode(200)//成功返回码  200
                .setDefaultTimeout(1000*30)//响应时间，可以不设置，默认为2000毫秒
                .setOpenHttps(true)
                //        .setHeads(headMap)
                .build();
        build.init(this);
    }
/*    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tokenRefresh(WebSocketEvent event) {
        if (event != null) {
            Bundle bundle = event.getmBundle();
            byte[] array = bundle.getByteArray("bundle");
            parseWebSocketMassage(array);
        }
    }*/
    private void initEasyHttp() {
        EasyHttp.init(this);//默认初始化;
        String token = SharePreferencesUtil.getString(CommonStr.USER_TOKEN, "");
        HttpHeaders authorization = new HttpHeaders("authorization",token);
        HttpHeaders languageType = new HttpHeaders("languageType", "2");
        EasyHttp.getInstance()
                .setBaseUrl("http://api.tjvqsqfdgpczu.com")
                .debug("EasyHttp", true)
                .setCertificates()
                .addCommonHeaders(authorization)
                .addCommonHeaders(languageType);
    }


    public static MyApplication getsInstance() {
        return sInstance;
    }

    protected void init() {

        sp = SharedPreferenceRepository.init(getApplicationContext());

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .build();

        Gson gson = new Gson();

        repositoryFactory = RepositoryFactory.init(sp, httpClient, gson);


        //创建数据库表
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();


    }


    public static OkHttpClient okHttpClient() {
        return httpClient;
    }

    public static RepositoryFactory repositoryFactory() {
        return  repositoryFactory;
    }


}
