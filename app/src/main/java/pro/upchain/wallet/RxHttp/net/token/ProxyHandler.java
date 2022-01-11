package pro.upchain.wallet.RxHttp.net.token;


import static pro.upchain.wallet.RxHttp.net.common.RxLibConstants.SP_TOKEN;

import android.text.TextUtils;


import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import pro.upchain.wallet.RxHttp.net.common.BaseEntityObserver;
import pro.upchain.wallet.RxHttp.net.common.CommonService;
import pro.upchain.wallet.RxHttp.net.common.RetrofitFactory;
import pro.upchain.wallet.RxHttp.net.exception.RefreshTokenExpiredException;
import pro.upchain.wallet.RxHttp.net.exception.TokenExpiredException;
import pro.upchain.wallet.RxHttp.net.utils.LogUtils;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * created  by ganzhe on 2019/11/13.
 */
public class ProxyHandler implements InvocationHandler {

    private final static int REFRESH_TOKEN_VALID_TIME = 30;
    private static long tokenChangedTime = 0;
    private Throwable mRefreshTokenError = null;
    private boolean mIsTokenNeedRefresh;

    private Object mProxyObject;
    private IGlobalManager mGlobalManager;
    private String mBaseUrl;

    public ProxyHandler(Object proxyObject, IGlobalManager globalManager, String baseUrl) {
        mBaseUrl = baseUrl;
        mProxyObject = proxyObject;
        mGlobalManager = globalManager;
    }

    private Observable<?> refreshTokenWhenTokenExpired() {
        synchronized (ProxyHandler.class) {
            // Have refreshed the token successfully in the valid time.
            if (new Date().getTime() - tokenChangedTime < REFRESH_TOKEN_VALID_TIME) {
                mIsTokenNeedRefresh = true;
                return Observable.just(true);
            } else {

                RetrofitFactory
                        .getInstance()
                        .create(mBaseUrl, CommonService.class)
                        .refreshToken()
                        .subscribe(new BaseEntityObserver<RefreshTokenResponse>() {
                            @Override
                            public void onSuccess(RefreshTokenResponse response) {
                                if (response != null) {
                                    mGlobalManager.tokenRefresh(response);
                                    mIsTokenNeedRefresh = true;
                                    tokenChangedTime = new Date().getTime();
                                }
                            }
                            @Override
                            public void onFail(String msg) {
                                LogUtils.e(msg);
                            }
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                mRefreshTokenError = e;
                            }
                        });
                if (mRefreshTokenError != null) {
                    Observable<Object> error = Observable.error(mRefreshTokenError);
                    mRefreshTokenError = null;
                    return error;
                } else {
                    return Observable.just(true);
                }
            }
        }
    }

    /**
     * Update the token of the args in the method.
     * <p>
     * PS： 因为这里使用的是 GET 请求，所以这里就需要对 Query 的参数名称为 token 的方法。
     * 若是 POST 请求，或者使用 Body ，自行替换。因为 参数数组已经知道，进行遍历找到相应的值，进行替换即可（更新为新的 token 值）。
     */
    @SuppressWarnings("unchecked")
    private void updateMethodToken(Method method, Object[] args) {
        String token ="";
        if (mIsTokenNeedRefresh && !TextUtils.isEmpty(token)) {
            Annotation[][] annotationsArray = method.getParameterAnnotations();
            Annotation[] annotations;
            if (annotationsArray != null && annotationsArray.length > 0) {
                for (int i = 0; i < annotationsArray.length; i++) {
                    annotations = annotationsArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof FieldMap || annotation instanceof QueryMap) {
                            if (args[i] instanceof Map)
                                ((Map<String, Object>) args[i]).put(SP_TOKEN, token);
                        } else if (annotation instanceof Query) {
                            if (SP_TOKEN.equals(((Query) annotation).value()))
                                args[i] = token;
                        } else if (annotation instanceof Field) {
                            if (SP_TOKEN.equals(((Field) annotation).value()))
                                args[i] = token;
                        } else if (annotation instanceof Part) {
                            if (SP_TOKEN.equals(((Part) annotation).value())) {
                                RequestBody tokenBody = RequestBody.create(MediaType.parse("multipart/form-data"), token);
                                args[i] = tokenBody;
                            }
                        } else if (annotation instanceof Body) {
                           /* if (args[i] instanceof BaseRequest) {
                                BaseRequest requestData = (BaseRequest) args[i];
                                requestData.setToken(token);
                                args[i] = requestData;
                            }*/
                        }
                    }
                }
            }
            mIsTokenNeedRefresh = false;
        }
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        return Observable.just(true).flatMap(new Function<Object, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Object o) throws Exception {
                try {
                    try {
                        if (mIsTokenNeedRefresh) {
                            updateMethodToken(method, args);
                        }
                        return (Observable<?>) method.invoke(mProxyObject, args);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }).retryWhen(new Function<Observable<Throwable>, ObservableSource<?>>() {
            @Override
            public ObservableSource<?> apply(Observable<Throwable> observable) throws Exception {
                return observable.flatMap(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        if (throwable instanceof TokenExpiredException) {
                            return refreshTokenWhenTokenExpired();
                        } else if (throwable instanceof RefreshTokenExpiredException) {
                            // Token 不存在，执行退出登录的操作。（为了防止多个请求，都出现 Token 不存在的问题，
                            // 这里需要取消当前所有的网络请求）
                            mGlobalManager.logout();
                            return Observable.error(throwable);
                        }
                        return Observable.error(throwable);
                    }
                });
            }
        });
    }
}
