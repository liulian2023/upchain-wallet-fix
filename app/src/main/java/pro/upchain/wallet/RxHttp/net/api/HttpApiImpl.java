/*
 * Copyright (c) 2019.  ganzhe
 */

package pro.upchain.wallet.RxHttp.net.api;


import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import pro.upchain.wallet.RxHttp.net.common.RetrofitFactory;
import pro.upchain.wallet.utils.AESUtil;
import pro.upchain.wallet.utils.CommonStr;
import pro.upchain.wallet.utils.SharePreferencesUtil;
import retrofit2.Response;


public class HttpApiImpl {

    public IHttpApi iHttpApiT;
//    public CacheProvider cacheProvider;


    private HttpApiImpl() {
        iHttpApiT = RetrofitFactory.getInstance().create(IHttpApi.class);
        //     cacheProvider = RetrofitFactory.getInstance().cacheProvider;
    }
    public HttpApiImpl(String base_url) {
        iHttpApiT = RetrofitFactory.getInstance().create(base_url,IHttpApi.class);
        //   cacheProvider = RetrofitFactory.getInstance().cacheProvider;
    }

    public static HttpApiImpl  getInstance() {
        return HttpApiImplHolder.S_INSTANCE;
    }


    private static class HttpApiImplHolder {
        private static final HttpApiImpl S_INSTANCE = new HttpApiImpl();
    }

    public static HttpApiImpl  getInstance1() {
        return HttpApiImplHolder1.S_INSTANCE1;
    }

    private static class HttpApiImplHolder1 {
        private static final HttpApiImpl S_INSTANCE1 = new HttpApiImpl(CommonStr.API_HOST1);

    }

    public static HttpApiImpl  getInstance2() {
        return HttpApiImplHolder2.S_INSTANCE2;
    }

    private static class HttpApiImplHolder2 {
//        private static final HttpApiImpl S_INSTANCE2 = new HttpApiImpl(BuildConfig.API_HOST2);
        private static final HttpApiImpl S_INSTANCE2 = new HttpApiImpl(SharePreferencesUtil.getString(CommonStr.CP_BASE_URL,CommonStr.API_HOST2));
    }
    public void addValidToken(Map<String,Object> data){
        data.put("validToken", AESUtil.encrypt(JSON.toJSONString(data)));
    }

/*    public Observable<Response<ResponseBody>> uploadFile(String imgPath) {
        File file = new File(imgPath);
        RequestBody body = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), body);
        return iHttpApiT.uploadFile(body,part);
    }

    public Observable<Response<ResponseBody>> pingTest(){
        Map<String, Object> dataMap = new HashMap<>();//上期开奖结果请求参数
        return iHttpApiT.pingTest(dataMap);
    }*/

    public Observable<Response<ResponseBody>> pingTest(){
        HashMap<String, Object> dataMap = new HashMap<>();
        return iHttpApiT.pingTest(dataMap);
    }
}
