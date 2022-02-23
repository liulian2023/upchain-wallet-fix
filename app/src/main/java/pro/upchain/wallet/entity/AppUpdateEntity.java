package pro.upchain.wallet.entity;

public class AppUpdateEntity {

    private String StringernetDownloadUrl="";
    private String downloadUrl="";
    private String mustUpdate="";
    private String id="";
    private String versionContent ="";
    private String type="";
    private String versionName = "";
    private int versionCode =0;

    public String getInternetDownloadUrl() {
        return StringernetDownloadUrl;
    }

    public void setInternetDownloadUrl(String StringernetDownloadUrl) {
        this.StringernetDownloadUrl = StringernetDownloadUrl;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getStringernetDownloadUrl() {
        return StringernetDownloadUrl;
    }

    public void setStringernetDownloadUrl(String stringernetDownloadUrl) {
        StringernetDownloadUrl = stringernetDownloadUrl;
    }

    public String getVersionContent() {
        return versionContent;
    }

    public void setVersionContent(String versionContent) {
        this.versionContent = versionContent;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(String mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        try {
            this.versionCode = versionCode;
        } catch (Exception e) {
            versionCode =0 ;
            e.printStackTrace();
        }
    }
}
