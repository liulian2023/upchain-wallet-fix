package pro.upchain.wallet.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.util.ArrayList;
import java.util.List;

public class TransferHistoryEntity {


    private String startRow;
    private List<Integer> navigatepageNums;
    private String prePage;
    private boolean hasNextPage;
    private String nextPage;
    private String pageSize;
    private String endRow;
    private List<ListBean> list = new ArrayList<>();
    private String pageNum;
    private String navigatePages;
    private String total;
    private String navigateFirstPage;
    private String pages;
    private String size;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private String navigateLastPage;
    private boolean isFirstPage;

    public String getStartRow() {
        return startRow;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public String getPrePage() {
        return prePage;
    }

    public void setPrePage(String prePage) {
        this.prePage = prePage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getEndRow() {
        return endRow;
    }

    public void setEndRow(String endRow) {
        this.endRow = endRow;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(String navigatePages) {
        this.navigatePages = navigatePages;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(String navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public String getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(String navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public static class ListBean implements MultiItemEntity {
        private String toUid;
        private String offset;
        private String successfulTime;
        private String blockchainType;
        private String contract;
        private String pageSize;
        private String pageNum;
        private String toAddress;
        private String money;
        private String fromUid;
        private String createTime;
        private String fromAddress;
        private String id;
        private String state;
        private String hash;
        private int itemType= 0;
        private String titleName;

        public String getToUid() {
            return toUid;
        }

        public void setToUid(String toUid) {
            this.toUid = toUid;
        }

        public String getOffset() {
            return offset;
        }

        public void setOffset(String offset) {
            this.offset = offset;
        }

        public String getSuccessfulTime() {
            return successfulTime;
        }

        public void setSuccessfulTime(String successfulTime) {
            this.successfulTime = successfulTime;
        }

        public String getBlockchainType() {
            return blockchainType;
        }

        public void setBlockchainType(String blockchainType) {
            this.blockchainType = blockchainType;
        }

        public String getTitleName() {
            return titleName;
        }

        public void setTitleName(String titleName) {
            this.titleName = titleName;
        }

        public String getContract() {
            return contract;
        }

        public void setContract(String contract) {
            this.contract = contract;
        }

        public String getPageSize() {
            return pageSize;
        }

        public void setPageSize(String pageSize) {
            this.pageSize = pageSize;
        }

        public String getPageNum() {
            return pageNum;
        }

        public void setPageNum(String pageNum) {
            this.pageNum = pageNum;
        }

        public String getToAddress() {
            return toAddress;
        }

        public void setToAddress(String toAddress) {
            this.toAddress = toAddress;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getFromUid() {
            return fromUid;
        }

        public void setFromUid(String fromUid) {
            this.fromUid = fromUid;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getFromAddress() {
            return fromAddress;
        }

        public void setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        @Override
        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }
    }
}
