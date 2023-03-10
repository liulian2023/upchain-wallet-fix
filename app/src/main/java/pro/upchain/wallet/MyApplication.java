package pro.upchain.wallet;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.ArrayMap;

import androidx.multidex.MultiDexApplication;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;
import me.jessyan.autosize.utils.AutoSizeLog;
import me.jessyan.autosize.utils.ScreenUtils;
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
import com.umeng.commonsdk.UMConfigure;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.model.HttpHeaders;

import java.util.Locale;
import java.util.TimeZone;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.utils.OwnUncaughtExceptionHandler;
import pro.upchain.wallet.utils.SharePreferencesUtil;



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
        initUm();
        initGlobalTimeZone();
//        EventBus.getDefault().register(this);
        Realm.init(this);

        refWatcher = LeakCanary.install(this);

        AppFilePath.init(this);

       initEasyHttp();
       initRxhttp();
        // ????????????????????????????????????  ???????????????????????????
        Thread.setDefaultUncaughtExceptionHandler(new OwnUncaughtExceptionHandler());
        fitScreen();
    }

    private void initUm() {
        //??????LOG??????????????????false
        if(BuildConfig.DEBUG){
            UMConfigure.setLogEnabled(true);
        }

        UMConfigure.init(this,"622877a3317aa877608ca83d","allChannel",UMConfigure.DEVICE_TYPE_PHONE, "");
    }

    private void fitScreen() {
        AutoSizeConfig.getInstance()
                .setCustomFragment(true)
                .setExcludeFontScale(true)
                //?????????????????????
                .setOnAdaptListener(new onAdaptListener() {
                    @Override
                    public void onAdaptBefore(Object target, Activity activity) {
                        AutoSizeConfig.getInstance().setScreenWidth(ScreenUtils.getScreenSize(activity)[0]);
                        AutoSizeConfig.getInstance().setScreenHeight(ScreenUtils.getScreenSize(activity)[1]);
                        AutoSizeLog.d(String.format(Locale.ENGLISH, "%s onAdaptBefore!", target.getClass().getName()));
                    }

                    @Override
                    public void onAdaptAfter(Object target, Activity activity) {
                        AutoSizeLog.d(String.format(Locale.ENGLISH, "%s onAdaptAfter!", target.getClass().getName()));
                    }
                })
                .setLog(false);

    }
    /**
     * ??????app???????????????
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
        //retrofit2 ??????????????????
        ApiConfig build = new ApiConfig.Builder()
                .setBaseUrl1(CommonStr.API_HOST1)//BaseUrl????????????????????????????????????????????????url
                .setSucceedCode(200)//???????????????  200
                .setDefaultTimeout(1000*30)//??????????????????????????????????????????2000??????
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
        EasyHttp.init(this);//???????????????;
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


        //??????????????????
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
