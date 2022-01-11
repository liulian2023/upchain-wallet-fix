package pro.upchain.wallet.RxHttp.net.https;

import android.annotation.SuppressLint;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * created  by ganzhe on 2019/9/11.
 */
public class UnSafeHostnameVerify implements HostnameVerifier {
    @SuppressLint("BadHostnameVerifier")
    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}

