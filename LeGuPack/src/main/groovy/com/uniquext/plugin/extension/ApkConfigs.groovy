package com.uniquext.plugin.extension

import org.gradle.api.Action;

class ApkConfigs {

    File apkFolder
    String reinforcePrefix
    String signPrefix

    SignConfigs signConfigs = new SignConfigs()

    void signature(Action<SignConfigs> action) {
        action.execute(signConfigs)
    }
}
