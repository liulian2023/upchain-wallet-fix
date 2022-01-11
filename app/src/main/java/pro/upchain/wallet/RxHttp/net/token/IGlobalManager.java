package pro.upchain.wallet.RxHttp.net.token;



public interface IGlobalManager {
    /**
     * Exit the login state.
     */
    void logout();

    void tokenRefresh(RefreshTokenResponse response);
}
