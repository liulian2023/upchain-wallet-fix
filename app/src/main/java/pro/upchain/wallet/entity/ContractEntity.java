package pro.upchain.wallet.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class ContractEntity implements MultiItemEntity {


    private String tokenSymbol;
    private String offset;
    private String blockchainType;
    private String contract;
    private String tokenName;
    private String icon;
    private String pageSize;
    private String sort;
    private String pageNum;
    private String isEnable;
    private String createTime;
    private int decimals;
    private int isSuggested;//1：是，2：否
    private String id;
    String titleName;
    private int itemType= 1;
    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getBlockchainType() {
        return blockchainType;
    }

    public int getIsSuggested() {
        return isSuggested;
    }

    public void setIsSuggested(int isSuggested) {
        this.isSuggested = isSuggested;
    }

    public void setBlockchainType(String blockchainType) {
        this.blockchainType = blockchainType;
    }

    public String getContract() {
        return contract;
    }

    public void setContract(String contract) {
        this.contract = contract;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getIsEnable() {
        return isEnable;
    }

    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int getItemType() {
        return itemType;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
