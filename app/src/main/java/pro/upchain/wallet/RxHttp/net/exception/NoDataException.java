package pro.upchain.wallet.RxHttp.net.exception;

/**
 * created  by ganzhe on 2019/11/12.
 */
public class NoDataException extends RuntimeException{
    public NoDataException() {
        super("服务器没有返回对应的Data数据", new Throwable("Server error"));
    }
}
