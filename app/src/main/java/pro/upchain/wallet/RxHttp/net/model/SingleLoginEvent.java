package pro.upchain.wallet.RxHttp.net.model;

public class SingleLoginEvent {
    private boolean singleLogin;
    private int flag;

    public boolean isSingleLogin() {
        return singleLogin;
    }

    public void setSingleLogin(boolean singleLogin) {
        this.singleLogin = singleLogin;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public SingleLoginEvent(boolean singleLogin, int flag) {
        this.singleLogin = singleLogin;
        this.flag = flag;
    }
}
