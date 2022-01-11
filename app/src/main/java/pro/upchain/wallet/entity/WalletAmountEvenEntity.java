package pro.upchain.wallet.entity;

import java.math.BigDecimal;

public class WalletAmountEvenEntity {
    BigDecimal sum;

    public WalletAmountEvenEntity(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
