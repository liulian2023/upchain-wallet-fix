package pro.upchain.wallet.RxHttp.net.common;


import java.lang.reflect.Proxy;

import pro.upchain.wallet.RxHttp.net.token.IGlobalManager;
import pro.upchain.wallet.RxHttp.net.token.ProxyHandler;


/**
 * created  by ganzhe on 2019/11/13.
 */
public class IdeaApiProxy {

    @SuppressWarnings("unchecked")
    public <T> T getApiService(Class<T> tClass, String baseUrl, IGlobalManager manager) {
        T t = RetrofitFactory.getInstance().create(baseUrl,tClass);

        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class<?>[] { tClass }, new ProxyHandler(t, manager,baseUrl));
    }
}
