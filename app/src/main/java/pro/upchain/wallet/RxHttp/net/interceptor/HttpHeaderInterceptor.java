package pro.upchain.wallet.RxHttp.net.interceptor;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.annotations.EverythingIsNonNull;
import pro.upchain.wallet.RxHttp.net.utils.StringMyUtil;
import pro.upchain.wallet.MyApplication;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.DeviceUtils;
import pro.upchain.wallet.utils.SharePreferencesUtil;

public class HttpHeaderInterceptor implements Interceptor {

    @Override
    @EverythingIsNonNull
    public Response intercept(Chain chain) throws IOException {
//        String  token=  mSharedPreferenceHelperImpl.getToken();
        String  token= SharePreferencesUtil.getString( CommonStr.USER_TOKEN,"");
        Request.Builder builder = chain.request().newBuilder();
        Request request=null;
        builder.addHeader("languageType","2");
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
