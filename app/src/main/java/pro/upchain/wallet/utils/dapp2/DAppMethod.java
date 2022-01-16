package pro.upchain.wallet.utils.dapp2;

public enum DAppMethod {
    SIGNTRANSACTION,
    SIGNPERSONALMESSAGE,
    SIGNMESSAGE,
    SIGNTYPEDMESSAGE,
    ECRECOVER,
    REQUESTACCOUNTS,
    WATCHASSET,
    ADDETHEREUMCHAIN,
    UNKNOWN;
    public static DAppMethod  fromValue(String value) {
        DAppMethod methodName = null;
        switch (value) {
            case "requestAccounts":
                methodName =  REQUESTACCOUNTS;
            break;
            case "signMessage":
                methodName =  SIGNMESSAGE;
                break;
            case "signPersonalMessage":
                methodName =  SIGNPERSONALMESSAGE;
                break;
            case "signTypedMessage":
                methodName =  SIGNTYPEDMESSAGE;
                break;
            case "signTransaction":
                methodName =  SIGNTRANSACTION;
                break;
            default:
                break;
        }
        return  methodName;
    }
}
