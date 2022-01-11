package pro.upchain.wallet.RxHttp.net.interceptor;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {
    private static final String TAG = "TokenInterceptor";
    private String url = "xxx";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        Log.d(TAG, "response.code=" + response.code());

        //判断token是否过期，与后台小伙伴协商，或者通过判断新旧token是否一致
        if (isTokenExpired(response)) {
            Log.d(TAG, "自动刷新Token,然后重新请求数据");
            //同步请求方式，获取最新的Token
            String newToken = getNewToken();
            Log.e(TAG, "intercept:新的请求头 "+newToken );
            //使用新的Token，创建新的请求
            Request newRequest = chain.request()
                    .newBuilder()
                    .header("Authorization", newToken)
                    .build();
            //重新请求
            return chain.proceed(newRequest);
        }
        return response;
    }

    //判断Token是否过期,我这里是因为Token过期会返回403,可以通过判断新旧token是否一致
    private boolean isTokenExpired(Response response) {
        if (response.code() == 403) {
            return true;
        }
        return false;
    }

    //用同步方法获取新的Token
    private String getNewToken() throws IOException {
     /*   // 通过获取token的接口，同步请求接口
        String newToken = "";
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("xxx", xxx);
        map.put("xxx", xxx);
        // GsonUtil只是有个把Map变成String的工具类
        String params = GsonUtil.mapToJson(map);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), params);
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(10, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(MyDatas.path + url)
                .post(requestBody).build();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            //坑人点，response.body().string()只能调用一次，否则会报java.lang.IllegalStateException: closed
            //第一次调用.body().string(),OkHttp就会默默地释放资源,再次调用就会抛出异常,有兴趣的可以去看源码
            String relut = response.body().string();
            Log.e(TAG, "getNewToken: " +relut);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return "newToken";
    }

}
