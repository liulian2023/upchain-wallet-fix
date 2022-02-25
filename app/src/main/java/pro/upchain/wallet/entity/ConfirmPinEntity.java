package pro.upchain.wallet.entity;

public class ConfirmPinEntity {
    String code="";
    boolean isDelete = false;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }
}
