package pro.upchain.wallet.RxHttp.net.token;


import pro.upchain.wallet.RxHttp.net.model.BaseEntity;

/**
 * Created by zhpan on 2018/3/27.
 */

public class RefreshTokenResponse extends BaseEntity {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
