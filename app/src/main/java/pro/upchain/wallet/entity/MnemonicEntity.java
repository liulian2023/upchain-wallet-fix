package pro.upchain.wallet.entity;

public class MnemonicEntity {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MnemonicEntity(String name) {
        this.name = name;
    }
}
