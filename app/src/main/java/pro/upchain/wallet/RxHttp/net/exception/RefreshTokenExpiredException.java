package pro.upchain.wallet.RxHttp.net.exception;


import pro.upchain.wallet.RxHttp.net.common.ErrorCode;

/**
 * created  by ganzhe on 2019/11/13.
 */
public class RefreshTokenExpiredException extends RuntimeException {

    private int errorCode;

    public RefreshTokenExpiredException(int errorCode,String cause) {
        super(ErrorCode.getErrorMessage(errorCode, cause), new Throwable(cause));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
