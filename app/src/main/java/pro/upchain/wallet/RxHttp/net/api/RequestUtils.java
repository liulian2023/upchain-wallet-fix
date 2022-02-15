package pro.upchain.wallet.RxHttp.net.api;

public class    RequestUtils {
    /**
     * 注册
     */
    public static String REGISTER = "mobile/userInfo/register";
    /**
     * 登录
     */
    public static String LOGIN = "mobile/userInfo/login";
    /**
     * 交易记录
     */
    public static String transfer_history = "mobile/transferInfo/pageList";

    /**
     * 添加钱包地址
     *
     * address
     * blockchainType
     * privateKey
     */
    public static String ADD_ADDRESS = "mobile/WalletAddress/addAddress";

    /**
     * 合约列表
     */
    public static String CONTRACT_LIST = "mobile/systemContract/list";

    //
    /**
     * 合约列表
     */
    public static String ABOUT_US = "mobile/aboutUs/getInfo";

    /**
     * 获取用户合约列表
     */

    public static String COIN_LIST = "mobile/userContract/getList";

    /**
     * 系统参数
     */
    public static String SYSTEM_SYSTEM ="mobile/home/systemInfo";

    /**
     * 版本更新
     */

    public static String VERSION_UPDATE ="mobile/versionManage/getInfo";

    /**
     * 线路测试
     */

    public static String URL_TEST ="mobile/home/test";
}
