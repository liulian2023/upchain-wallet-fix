package pro.upchain.wallet.RxHttp.net.common;





import io.reactivex.Observable;
import pro.upchain.wallet.RxHttp.net.token.RefreshTokenResponse;
import retrofit2.http.GET;

/**
 * Created by dell on 2017/4/1.
 */

public interface CommonService {
    @GET("refresh_token")
    Observable<RefreshTokenResponse> refreshToken();
}
