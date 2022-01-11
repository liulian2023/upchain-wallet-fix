package pro.upchain.wallet.RxHttp.net.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryUtils {

        /*
         * 默认信任所有的证书
         * todo 最好加上证书认证，主流App都有自己的证书
         * */
        public static SSLSocketFactory createSSLSocketFactory() {
            SSLSocketFactory sslSocketFactory = null;
            try {
                SSLContext sslContext = SSLContext.getInstance("TLS");
//                sslContext.init(null, new TrustManager[]{createTrustAllManager()}, new SecureRandom());
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (Exception e) {

            }
            return sslSocketFactory;
        }


        public static X509TrustManager createTrustAllManager() {
            X509TrustManager tm = null;
            try {
                tm = new X509TrustManager() {

                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
            } catch (Exception e) {

            }
            return tm;
        }

        public static class TrustAllHostnameVerifier implements HostnameVerifier {


            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        }
}
