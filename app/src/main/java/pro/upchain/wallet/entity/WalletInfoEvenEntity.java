package pro.upchain.wallet.entity;


import pro.upchain.wallet.domain.ETHWallet;

public class WalletInfoEvenEntity {
    ETHWallet ethWallet;

    public WalletInfoEvenEntity(ETHWallet ethWallet) {
        this.ethWallet = ethWallet;
    }

    public ETHWallet getEthWallet() {
        return ethWallet;
    }

    public void setEthWallet(ETHWallet ethWallet) {
        this.ethWallet = ethWallet;
    }
}
