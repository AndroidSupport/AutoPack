package com.uniquext.plugin;

public class AppInfo {

    public String name;
    public String localPath = "/Users/uniquext/DESKTOP/";




    public AppInfo(String name) {
        this.sourceName = name;
    }

    public AppInfo(String url, String md5) {
        this.remoteUrl = url;
        this.md5 = md5;
    }



    public String sourceName;
    public String reinforceName;
    public String signName;

    public String md5;
    public String remoteUrl;

    public String itemId;
    public String downloadUrl;
}
