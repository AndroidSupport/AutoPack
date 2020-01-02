package com.uniquext.plugin.extension;

class SignConfigs {
    File storeFile
    String storePassword
    String keyAlias
    String keyPassword

    void storeFile(File storeFile) {
        this.storeFile = storeFile
    }
    void storePassword(String storePassword) {
        this.storePassword = storePassword
    }
    void keyAlias(String keyAlias) {
        this.keyAlias = keyAlias
    }
    void keyPassword(String keyPassword) {
        this.keyPassword = keyPassword
    }
}
