package pro.upchain.wallet.RxHttp.net.interceptor;
import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EncodedInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        if(request.body() instanceof FormBody){
            FormBody.Builder newFormBody = new FormBody.Builder();
            FormBody oldFormBody =(FormBody) request.body();
          /*  for (int i = 0; i < oldFormBody.size(); i++) {
*//*                String name = URLDecoder.decode(oldFormBody.encodedName(i), "utf-8");
                String value = URLDecoder.decode(oldFormBody.encodedValue(i), "utf-8");
                newFormBody.addEncoded(name, value.replace(" ","+"));*//*
                String name = oldFormBody.encodedName(i);
                String value = oldFormBody.encodedValue(i).replace("%2f", "/").replace("%2F", "/");
                newFormBody.add(name, value);

            }*/
//            requestBuilder.method(request.method(),newFormBody.build());
            MediaType contentType = MediaType.get("application/x-www-form-urlencoded");
            FormBody formBody =new  FormBody.Builder().build();
            RequestBody requestBody = formBody.create(contentType, "username=mobile:ll11111&password=vKQhdSA%2Fu%2F2FTRx%2BkZGMLQ%3D%3D");


            requestBuilder.method(request.method(),requestBody);
        }
        Request build = requestBuilder.build();
        return chain.proceed(build);
    }


}
