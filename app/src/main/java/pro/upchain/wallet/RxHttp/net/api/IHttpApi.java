package pro.upchain.wallet.RxHttp.net.api;


import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface IHttpApi {
    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("mobile/userInfo/login")
    Observable<Response<ResponseBody>> login(@FieldMap HashMap<String,Object>data);
    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("mobile/userInfo/register")
    Observable<Response<ResponseBody>> register(@FieldMap HashMap<String,Object>data);

    /**
     * 交易记录
     */
    @FormUrlEncoded
    @POST("mobile/transferInfo/pageList")
    Observable<Response<ResponseBody>> transferHistory(@FieldMap HashMap<String,Object>data);

    /**
     * 添加地址
     */
    @FormUrlEncoded
    @POST("mobile/WalletAddress/addAddress")
    Observable<Response<ResponseBody>> addAddress(@FieldMap HashMap<String,Object>data);

    /**
     * 合约列表
     */

    @FormUrlEncoded
    @POST("mobile/systemContract/list")
    Observable<Response<ResponseBody>> addContract(@FieldMap HashMap<String,Object>data);

    /**
     * 关于我们
     * @param data
     * @return
     */

    @FormUrlEncoded
    @POST("mobile/aboutUs/getInfo")
    Observable<Response<ResponseBody>> aboutUs(@FieldMap HashMap<String,Object>data);
}