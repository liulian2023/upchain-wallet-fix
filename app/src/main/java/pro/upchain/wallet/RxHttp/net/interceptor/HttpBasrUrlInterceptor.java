package pro.upchain.wallet.RxHttp.net.interceptor;

import android.text.TextUtils;


import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpBasrUrlInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        //获取request
        Request request = chain.request();
        RequestBody body = request.body();
        //获取request的创建者builder
        Request.Builder requestBuilder = request.newBuilder();
        //从request中获取原有的HttpUrl实例oldHttpUrl
        HttpUrl oldHttpUrl = request.url();
//        SharedPreferenceHelperImpl sp = new SharedPreferenceHelperImpl();
        HttpUrl newBaseUrl = null;
//        String newBaseUrl1 = sp.getNewBaseUrl();
        String newBaseUrl1 = "";
        //有切换过路线
        if(!TextUtils.isEmpty(newBaseUrl1)){
            //获取头信息的集合
            List<String> urlnameList = request.headers("urlname");
            if(urlnameList!=null&&urlnameList.size()>0){//头部集合不为空
                //删除原有配置中的值,就是namesAndValues集合里的值
                requestBuilder.removeHeader("urlname");
                //获取头信息中配置的value  此处为cp
                String urlname = urlnameList.get(0);
                if("cp".equals(urlname)){//cp接口不替换
                    newBaseUrl = oldHttpUrl;
                }else {//其它接口替换
                    newBaseUrl = HttpUrl.parse(newBaseUrl1);
                }
            }else {//没有添加头部的替换
                newBaseUrl = HttpUrl.parse(newBaseUrl1);
            }
        }else {
            newBaseUrl = oldHttpUrl;
        }
            //重建新的HttpUrl，修改需要修改的url部分
            HttpUrl newFullUrl = oldHttpUrl
                    .newBuilder()
                    .scheme(newBaseUrl.scheme())
                    .host(newBaseUrl.host())
                    .port(newBaseUrl.port())
                    .build();
            //重建这个request，通过builder.url(newFullUrl).build()；

        Request newRequest = requestBuilder.url(newFullUrl)/*.post(body)*/.build();
        return chain.proceed(newRequest);
    }
}
