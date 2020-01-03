package com.uniquext.plugin;

public class ItemApkInfo {

    private String sourceName;
    private String reinforceName;
    private String reinforceApkPath;
    private String signName;
    private String md5;
    private String remoteUrl;
    private String itemId;
    private String downloadUrl;

    public ItemApkInfo(String name) {
        this.sourceName = name;
    }

    public String getReinforceApkPath() {
        return reinforceApkPath;
    }

    public void setReinforceApkPath(String reinforceApkPath) {
        this.reinforceApkPath = reinforceApkPath;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getReinforceName() {
        return reinforceName;
    }

    public void setReinforceName(String reinforceName) {
        this.reinforceName = reinforceName;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "sourceName='" + sourceName + '\'' +
                ", reinforceName='" + reinforceName + '\'' +
                ", signName='" + signName + '\'' +
                ", md5='" + md5 + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", itemId='" + itemId + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
