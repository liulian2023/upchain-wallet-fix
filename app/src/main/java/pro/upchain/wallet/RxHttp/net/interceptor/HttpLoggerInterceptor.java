package pro.upchain.wallet.RxHttp.net.interceptor;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * created  by ganzhe on 2019/9/12.
 */
public class HttpLoggerInterceptor{
    public static HttpLoggingInterceptor getLoggerInterceptor() {
        //日志拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> {
            Log.e("HttpLogger-->", message.toString());
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

}
