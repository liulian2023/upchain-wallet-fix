package pro.upchain.wallet.RxHttp.net.interceptor;


import static pro.upchain.wallet.utils.CommonStr.CHAIN_TYPE;
import static pro.upchain.wallet.utils.CommonStr.LOCAL_LANGUAGE;

import android.provider.Settings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.DeviceUtils;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.SharePreferencesUtil;

public class HttpHeaderInterceptor implements Interceptor {

    @Override
    @EverythingIsNonNull
    public Response intercept(Chain chain) throws IOException {
//        String  token=  mSharedPreferenceHelperImpl.getToken();
        String  token= SharePreferencesUtil.getString( CommonStr.USER_TOKEN,"");
        Request.Builder builder = chain.request().newBuilder();
        Request request=null;
        String languageType = "";
        String localLanguage = SharePreferencesUtil.getString(LOCAL_LANGUAGE, "en");
        String blockchainType = SharePreferencesUtil.getString(CHAIN_TYPE, "2");
        if(localLanguage.equals("en")){
            languageType = "3";
        }else if(localLanguage.equals("zh")){
            languageType = "2";
        }else if(localLanguage.equals("vi")){
            languageType = "5";
        }else {
            languageType = "3";
        }
        builder.addHeader("languageType",languageType);
        builder.addHeader("blockchainType",blockchainType);
        long currentTimeMillis = System.currentTimeMillis();
        builder.addHeader("timestamp", String.valueOf(currentTimeMillis));
        builder.addHeader("timestampSign", Md5Utils.md5(currentTimeMillis +"DW425WhDrHslzD8sI8Fi5Y5u4xVjgchz"));
        builder.addHeader("deviceNumber", DeviceUtils.getUniqueId(MyApplication.getsInstance()));

        if(StringMyUtil.isNotEmpty(token)){
            builder.addHeader("authorization", token);
        }else {
            builder.addHeader("authorization", "");
        }
        request = builder.build();
        Response response = chain.proceed(request);
        return response;
    }
}
