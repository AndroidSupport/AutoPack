package com.uniquext.plugin.extension;

import org.gradle.api.Action;

import java.io.File;

public class ApkConfigs {

    public File apkFolder;
    public String reinforcePrefix;
    public String signPrefix;
    public SignConfigs signConfigs = new SignConfigs();

    public void apkFolder(File apkFolder) {
        this.apkFolder = apkFolder;
    }

    public void reinforcePrefix(String reinforcePrefix) {
        this.reinforcePrefix = reinforcePrefix;
    }

    public void signPrefix(String signPrefix) {
        this.signPrefix = signPrefix;
    }

    public void signature(Action<SignConfigs> action) {
        action.execute(signConfigs);
    }

}