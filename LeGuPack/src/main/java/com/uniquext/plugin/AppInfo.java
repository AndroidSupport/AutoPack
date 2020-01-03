package com.uniquext.plugin;

public class AppInfo {

    public String name;
    public String localPath = "/Users/uniquext/DESKTOP/";
    public String sourceName;
    public String reinforceName;
    public String signName;
    public String md5;
    public String remoteUrl;
    public String itemId;
    public String downloadUrl;

    public AppInfo(String name) {
        this.sourceName = name;
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
