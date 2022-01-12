package pro.upchain.wallet.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class PendingHistoryEntity {
    @Id(autoincrement = true)
    private Long id;
    private String toUid;
    private String toAddress;
    private String money;
    private String createTime;
    private String hash;
    private int itemType= 0;
    private String titleName;
    private String mineAddress;

    @Generated(hash = 1035534276)
    public PendingHistoryEntity(Long id, String toUid, String toAddress,
            String money, String createTime, String hash, int itemType,
            String titleName, String mineAddress) {
        this.id = id;
        this.toUid = toUid;
        this.toAddress = toAddress;
        this.money = money;
        this.createTime = createTime;
        this.hash = hash;
        this.itemType = itemType;
        this.titleName = titleName;
        this.mineAddress = mineAddress;
    }
    @Generated(hash = 187854479)
    public PendingHistoryEntity() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getToUid() {
        return this.toUid;
    }
    public void setToUid(String toUid) {
        this.toUid = toUid;
    }
    public String getToAddress() {
        return this.toAddress;
    }
    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
    public String getMoney() {
        return this.money;
    }
    public void setMoney(String money) {
        this.money = money;
    }
    public String getCreateTime() {
        return this.createTime;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getHash() {
        return this.hash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }
    public int getItemType() {
        return this.itemType;
    }
    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
    public String getTitleName() {
        return this.titleName;
    }
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }
    public String getMineAddress() {
        return this.mineAddress;
    }
    public void setMineAddress(String mineAddress) {
        this.mineAddress = mineAddress;
    }



}
