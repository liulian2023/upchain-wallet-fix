package pro.upchain.wallet.entity;

public class LoginEntity {

    private String inviteCode;
    private String userState;
    private String token;
    private String uid;
    private String isBindEthAccreditInfo;

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getUserState() {
        return userState;
    }

    public void setUserState(String userState) {
        this.userState = userState;
    }

    public String getToken() {
        return token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIsBindEthAccreditInfo() {
        return isBindEthAccreditInfo;
    }

    public void setIsBindEthAccreditInfo(String isBindEthAccreditInfo) {
        this.isBindEthAccreditInfo = isBindEthAccreditInfo;
    }
}
