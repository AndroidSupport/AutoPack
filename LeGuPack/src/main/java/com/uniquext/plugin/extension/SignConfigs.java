package com.uniquext.plugin.extension;

import java.io.File;

public class SignConfigs {

    public File storeFile;
    public String storePassword;
    public String keyAlias;
    public String keyPassword;

    public void storeFile(File storeFile) {
        this.storeFile = storeFile;
    }

    public void storePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    public void keyAlias(String keyAlias) {
        this.keyAlias = keyAlias;
    }

    public void keyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }
}
