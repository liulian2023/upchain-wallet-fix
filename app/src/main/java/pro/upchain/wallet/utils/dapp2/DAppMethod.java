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
    public String  fromValue(DAppMethod dAppMethod) {
        String methodName = null;
        switch (dAppMethod) {
            case SIGNTRANSACTION:
                methodName =  "signTransaction";
            break;
            default:
                break;
        }
        return  methodName;
    }
}
