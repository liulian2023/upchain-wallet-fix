package pro.upchain.wallet.entity;

public class WebViewLoadFinishEvenEntity {
    boolean canGoBack;
    boolean canNextPage;
    String url;
    String title;

    public WebViewLoadFinishEvenEntity(boolean canGoBack, boolean canNextPage, String url,String title) {
        this.canGoBack = canGoBack;
        this.canNextPage = canNextPage;
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String geeUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isCanGoBack() {
        return canGoBack;
    }

    public void setCanGoBack(boolean canGoBack) {
        this.canGoBack = canGoBack;
    }

    public boolean isCanNextPage() {
        return canNextPage;
    }

    public void setCanNextPage(boolean canNextPage) {
        this.canNextPage = canNextPage;
    }
}
