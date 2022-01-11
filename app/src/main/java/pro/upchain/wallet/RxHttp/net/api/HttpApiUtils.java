package pro.upchain.wallet.RxHttp.net.api;


import static android.widget.Toast.LENGTH_SHORT;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.upchain.wallet.RxHttp.net.common.BaseStringObserver;
import pro.upchain.wallet.RxHttp.net.utils.AppContextUtils;
import pro.upchain.wallet.RxHttp.net.utils.ErrorUtil;
import pro.upchain.wallet.RxHttp.net.utils.RxUtil;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.utils.AESParamUtil;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class HttpApiUtils {
    private static final String REQUEST_404 = "请求地址已失效";
    private static final String REQUEST_400 = "请求失败";
    /**
     * (显示加载中,没有errorLayout)
     * @param activity 上下文
     * @param url 接口后缀,用于跟httpApi中的注解url做对比,决定调用哪个方法
     * @param data 数据源
     * @param onRequestLintener  请求结果回调
     */

    public static void request(final Activity activity,Fragment fragment, final String url, HashMap<String, Object> data, final OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {
                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    ToastUtils.showToast(msg);
                    onRequestLintener.onFail(msg);
                }
            }

            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if((Activity)activity instanceof BaseActivity){
                        ((BaseActivity) activity).showDialog();

                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if((Activity)activity instanceof BaseActivity){
                    ((BaseActivity) activity).dismissDialog();
                }
            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody =null;
        HashMap<String ,Object>map =null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            requestBody=HttpUtil.postRequest(data);
        }
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {

            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  requestBody);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(null==fragment){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }

                            //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public static void wwwRequest(final Activity activity,Fragment fragment, final String url, HashMap<String, Object> data, final OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {

            @Override
            public void onSuccess(String result) {
                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }

            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    ToastUtils.showToast(msg);
                    onRequestLintener.onFail(msg);
                }
            }

            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if((Activity)activity instanceof BaseActivity){
                    ((BaseActivity) activity).showDialog();

                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if((Activity)activity instanceof BaseActivity){
                    ((BaseActivity) activity).dismissDialog();
                }
            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody =null;
        HashMap<String ,Object>map =null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            data=HttpUtil.wwwPostRequestBody(data);
        }
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {

            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  data);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(null==fragment){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }

                    //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public static void normalRequest(final Activity activity,Fragment fragment, final String url, HashMap<String, Object> data, final OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {

                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    ToastUtils.showToast(msg);
                    onRequestLintener.onFail(msg);
                }
            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody =null;
        HashMap<String ,Object>map =null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            requestBody=HttpUtil.postRequest(data);
        }
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {

            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  requestBody);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(fragment==null){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }
                    //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public static void wwwNormalRequest(final Activity activity,Fragment fragment, final String url, HashMap<String, Object> data, final OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    if(!url.contains("mobile/WalletAddress/addAddress")){
                        ToastUtils.showToast(msg);
                    }
                    onRequestLintener.onFail(msg);
                }
            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody =null;
        HashMap<String ,Object>map =null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            data=HttpUtil.wwwPostRequestBody(data);
        }
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {

            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  data);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(fragment==null){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }
                    //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    /**
     *  application/json 有头部的列表 (包含加载中, 上拉刷新 下拉加载 失败视图  空视图的请求,将各种状态的视图添加到尾部,这样视图出现的时候不会将头部也一起覆盖)
     * @param fragment 当前所在的fragment  (如果在activity中请求,传null)
     * @param url 接口路径
     * @param data 数据源
     * @param loadingLinear  加载中的视图
     * @param errorLinear 加载失败的视图
     * @param reloadTv   点击刷新的按钮(具体看错误页面的布局,如果没有刷新按钮可以不传)
     * @param view  加载中 加载失败  空视图需要隐藏的view (主要是rececleView或者refreshLayout)
     * @param isLoadmore  是否是加载更多时调用
     * @param isrefresh  是否是下拉刷新时调用
     * @param onRequestLintener  请求结果回调
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void showLoadRequest(Activity activity, Fragment fragment, String url, HashMap<String, Object> data, ConstraintLayout loadingLinear, LinearLayout errorLinear, TextView reloadTv, View view, boolean isLoadmore, boolean isrefresh, OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {
                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                ToastUtils.showToast(msg);
                if(null!=onRequestLintener){
                    onRequestLintener.onFail(msg);
                }
                if(null!=errorLinear){
                    if(null!=fragment){
                        ErrorUtil.showErrorLayout(fragment,view,errorLinear,reloadTv);
                    }else {
                        ErrorUtil.showErrorLayout((Activity)activity,view,errorLinear,reloadTv);
                    }
                }
            }
            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if(!isLoadmore&&!isrefresh){
                    if(null!=loadingLinear){
                        loadingLinear.setVisibility(View.VISIBLE);
                    }
                    if(null!=errorLinear){
                        ErrorUtil.hideErrorLayout(view,errorLinear);
                    }
                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if(null!=loadingLinear){
                    loadingLinear.setVisibility(View.GONE);
                }
            }
        };

        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody = null;
        HashMap<String,Object> map = null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            requestBody= HttpUtil.postRequest(data);
        }

        IHttpApi iHttpApiT = HttpApiImpl.getInstance().iHttpApiT;

        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {
            if(method.getName().equalsIgnoreCase(split[1])){
                try {

                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  requestBody);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(fragment==null){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     *  x-www-form-urlencoded  有头部的列表 (包含加载中, 上拉刷新 下拉加载 失败视图  空视图的请求,将各种状态的视图添加到尾部,这样视图出现的时候不会将头部也一起覆盖)
     * @param fragment 当前所在的fragment  (如果在activity中请求,传null)
     * @param url 接口路径
     * @param data 数据源
     * @param loadingLinear  加载中的视图
     * @param errorLinear 加载失败的视图
     * @param reloadTv   点击刷新的按钮(具体看错误页面的布局,如果没有刷新按钮可以不传)
     * @param view  加载中 加载失败  空视图需要隐藏的view (主要是rececleView或者refreshLayout)
     * @param isLoadmore  是否是加载更多时调用
     * @param isrefresh  是否是下拉刷新时调用
     * @param onRequestLintener  请求结果回调
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void wwwShowLoadRequest(Activity activity, Fragment fragment, String url, HashMap<String, Object> data, ConstraintLayout loadingLinear, LinearLayout errorLinear, TextView reloadTv, View view, boolean isLoadmore, boolean isrefresh, OnRequestLintener onRequestLintener) {
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {
                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                ToastUtils.showToast(msg);
                if(null!=onRequestLintener){
                    onRequestLintener.onFail(msg);
                }
                if(null!=errorLinear){
                    if(null!=fragment){
                        ErrorUtil.showErrorLayout(fragment,view,errorLinear,reloadTv);
                    }else {
                        ErrorUtil.showErrorLayout((Activity)activity,view,errorLinear,reloadTv);
                    }
                }
            }
            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if(!isLoadmore&&!isrefresh){
                    if(null!=loadingLinear){
                        loadingLinear.setVisibility(View.VISIBLE);
                    }
                    if(null!=errorLinear){
                        ErrorUtil.hideErrorLayout(view,errorLinear);
                    }
                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if(null!=loadingLinear){
                    loadingLinear.setVisibility(View.GONE);
                }
            }
        };

        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        RequestBody requestBody = null;
        HashMap<String,Object> map = null;
        String getOrPost = split[0];
        if(getOrPost.equalsIgnoreCase("get")){
            map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        }else {
            data=HttpUtil.wwwPostRequestBody(data);
        }

        IHttpApi iHttpApiT = HttpApiImpl.getInstance().iHttpApiT;

        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {
            if(method.getName().equalsIgnoreCase(split[1])){
                try {
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    if(getOrPost.equalsIgnoreCase("get")){
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  map);
                    }else {
//                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  requestBody);
                        //x-www-form-urlencoded不传body 直接用map
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  data);
                    }
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(fragment==null){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    /**
     * (显示加载中,没有errorLayout)
     * @param activity 上下文
     * @param url 接口后缀,用于跟httpApi中的注解url做对比,决定调用哪个方法
     * @param
     * @param onRequestLintener  请求结果回调
     */

    public static void pathRequest(final Activity activity, final  Fragment fragment,final String url, String pathParam, final OnRequestLintener onRequestLintener) {
        if(null==activity && fragment == null){
            return;
        }
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {

                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    ToastUtils.showToast(msg);
                    onRequestLintener.onFail(msg);
                }
            }

            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if((Activity)activity instanceof BaseActivity){
                    ((BaseActivity) activity).showDialog();

                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if((Activity)activity instanceof BaseActivity){
                    ((BaseActivity) activity).dismissDialog();
                }
            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {
            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                        invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  pathParam);
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(null==fragment){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }
                    //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public static void pathNormalRequest(final Activity activity, final Fragment fragment ,final String url, String pathParam, final OnRequestLintener onRequestLintener) {
        if(null==activity && fragment == null){
            return;
        }
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {

                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
            }
            }
            @Override
            public void onFail(String msg) {
                if(onRequestLintener!=null){
                    ToastUtils.showToast(msg);
                    onRequestLintener.onFail(msg);
                }
            }

            @Override
            protected void onRequestStart() {
                super.onRequestStart();

            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();

            }
        };
        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        String getOrPost = split[0];
        HttpApiImpl httpApi = HttpApiImpl.getInstance();
        IHttpApi iHttpApiT = httpApi.iHttpApiT;
        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {

            if(method.getName().equalsIgnoreCase(split[1])){
                try {
//                    int parameterCount = method.getParameterCount();
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                    invoke =parameterCount==0?method.invoke(iHttpApiT): method.invoke(iHttpApiT,  pathParam);
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    if(null==fragment){
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                                .subscribe(observer);
                    }else {
                        responseObservable
                                .compose(RxUtil.rxSchedulerHelper())
                                .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) fragment)))
                                .subscribe(observer);
                    }
                    //使用java8才可以调用subscribe(observer)方法;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public  static interface OnRequestLintener{
        void onSuccess(String result);
        void onFail(String msg);
    }

    /**
     * 通过url筛选IHttpApi中对应的方法
     * @param url  接口路径(url必须和IHttpApi注释中的路径一致)
     * @return
     */
    public static String getMethodName(String url) {
        Method[] declaredMethods1 = IHttpApi.class.getDeclaredMethods();
        for (Method method:declaredMethods1) {
            GET getAnnotation = method.getAnnotation(GET.class);
            POST postAnnotation = method.getAnnotation(POST.class);
            if(getAnnotation==null && postAnnotation==null){
                continue;
            }
            String value =getAnnotation!=null? getAnnotation.value():postAnnotation.value();
            if(url.equals(value)){
                return (getAnnotation!=null?"get":"post")+","+method.getName();
            }
        }
        return null;
    }
    /**
     *  get请求 @phth注解加@quary注解 有头部的列表 (包含加载中, 上拉刷新 下拉加载 失败视图  空视图的请求,将各种状态的视图添加到尾部,这样视图出现的时候不会将头部也一起覆盖)
     * @param fragment 当前所在的fragment  (如果在activity中请求,传null)
     * @param url 接口路径
     * @param data 数据源
     * @param loadingLinear  加载中的视图
     * @param errorLinear 加载失败的视图
     * @param reloadTv   点击刷新的按钮(具体看错误页面的布局,如果没有刷新按钮可以不传)
     * @param view  加载中 加载失败  空视图需要隐藏的view (主要是rececleView或者refreshLayout)
     * @param isLoadmore  是否是加载更多时调用
     * @param isrefresh  是否是下拉刷新时调用
     * @param onRequestLintener  请求结果回调
     */
    @TargetApi(Build.VERSION_CODES.O)
    public static void pathShowLoadRequest(Activity activity, Fragment fragment, String url, String pathParameter,HashMap<String, Object> data, ConstraintLayout loadingLinear, LinearLayout errorLinear, TextView reloadTv, View view, boolean isLoadmore, boolean isrefresh, OnRequestLintener onRequestLintener) {
        if(null==activity){
            return;
        }
        BaseStringObserver<ResponseBody> observer = new BaseStringObserver<ResponseBody>() {
            @Override
            public void onSuccess(String result) {
                
                    if(onRequestLintener!=null){
                        onRequestLintener.onSuccess(result);
                    }
            }
            @Override
            public void onFail(String msg) {
                ToastUtils.showToast(msg);
                if(null!=onRequestLintener){
                    onRequestLintener.onFail(msg);
                }
                if(null!=errorLinear){
                    if(null!=fragment){
                        ErrorUtil.showErrorLayout(fragment,view,errorLinear,reloadTv);
                    }else {
                        ErrorUtil.showErrorLayout((Activity)activity,view,errorLinear,reloadTv);
                    }
                }
            }
            @Override
            protected void onRequestStart() {
                super.onRequestStart();
                if(!isLoadmore&&!isrefresh){
                    if(null!=loadingLinear){
                        loadingLinear.setVisibility(View.VISIBLE);
                    }
                    if(null!=errorLinear){
                        ErrorUtil.hideErrorLayout(view,errorLinear);
                    }
                }
            }

            @Override
            protected void onRequestEnd() {
                super.onRequestEnd();
                if(null!=loadingLinear){
                    loadingLinear.setVisibility(View.GONE);
                }
            }
        };

        String methodName = getMethodName(url);
        String[] split = methodName.split(",");
        HashMap<String,Object> map = null;
        map= (HashMap<String, Object>) HttpUtil.getRequest(data);
        IHttpApi iHttpApiT = HttpApiImpl.getInstance().iHttpApiT;

        Method[] declaredMethods = iHttpApiT.getClass().getDeclaredMethods();
        for (Method method:declaredMethods) {
            if(method.getName().equalsIgnoreCase(split[1])){
                try {
                    int parameterCount = method.getParameterTypes().length;
                    Object invoke;
                        invoke =method.invoke(iHttpApiT, pathParameter, map);
                    Observable<Response<ResponseBody>> responseObservable = (Observable<Response<ResponseBody>>) invoke;
                    responseObservable
                            .compose(RxUtil.rxSchedulerHelper())
                            .as(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from((LifecycleOwner) activity)))
                            .subscribe(observer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
    public static void show( String text) {
        Toast toast = null;
        try {
                toast= Toast.makeText(AppContextUtils.getContext(), null, Toast.LENGTH_SHORT);
                toast.setText(text);
            toast.show();
        } catch (Exception e) {
            //解决在子线程中调用Toast的异常情况处理
            Looper.prepare();
            toast = Toast.makeText(AppContextUtils.getContext(), null, LENGTH_SHORT);
            toast.setText(text);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            Looper.loop();
        }
    }


    public static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        StringBuilder prestr = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr.append(key).append("=").append(value);
            } else {
                prestr.append(key).append("=").append(value).append("&");
            }
        }

        return prestr.toString();
    }

    public static interface FormRequestResult{
        void onSuccess(String result);
        void onFail(String failStr);
    }


    public static void requestETHUSDTRate(OnRequestLintener onRequestLintener) {
        EasyHttp.get("/api/v3/ticker/price")
                .baseUrl("https://api.binance.com")
                .params("symbol","ETHUSDT")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        if(onRequestLintener!=null){
                            onRequestLintener.onFail(e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if(onRequestLintener!=null){
                            onRequestLintener.onSuccess(s);
                        }
                    }
                });
    }

    public static void requestETHoTHERRate(String symbol,OnRequestLintener onRequestLintener) {
        EasyHttp.get("/api/v3/ticker/price")
                .baseUrl("https://api.binance.com")
                .params("symbol",symbol+"ETH")
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                        if(onRequestLintener!=null){
                            onRequestLintener.onFail(e.getMessage());
                        }
                    }

                    @Override
                    public void onSuccess(String s) {
                        if(onRequestLintener!=null){
                            onRequestLintener.onSuccess(s);
                        }
                    }
                });
    }



    public static void addAddress(Activity activity,Fragment fragment,ETHWallet ethWallet){
        HashMap<String, Object> data = new HashMap<>();
        data.put("address",ethWallet.getAddress());
        data.put("blockchainType",2);
        data.put("privateKey", AESParamUtil.encrypt(ETHWalletUtils.derivePrivateKey(ethWallet.getId(), ethWallet.getPassword())));
        HttpApiUtils.wwwNormalRequest(activity, fragment, RequestUtils.ADD_ADDRESS, data, new HttpApiUtils.OnRequestLintener() {
            @Override
            public void onSuccess(String result) {
                LogUtils.e("上传成功");
                List<ETHWallet> ethWalletList = WalletDaoUtils.loadAll();
                for (int i = 0; i < ethWalletList.size(); i++) {
                    String address = ethWalletList.get(i).getAddress();
                    if(ethWallet.getAddress().equals(address)){
                        ethWallet.setIsUpload(true);
                    }
                }
            }

            @Override
            public void onFail(String msg) {
                LogUtils.e("上传失败");
            }
        });
    }
}
