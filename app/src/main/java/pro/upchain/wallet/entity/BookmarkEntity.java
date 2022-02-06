package pro.upchain.wallet.entity;

public class BookmarkEntity {
    String address;
    String title;
    boolean showDelete = false;

    public BookmarkEntity(String address, String title) {
        this.address = address;
        this.title = title;
    }

    public BookmarkEntity() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isShowDelete() {
        return showDelete;
    }

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
