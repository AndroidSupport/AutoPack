package com.uniquext.plugin

import com.uniquext.plugin.extension.ApkConfigs
import com.uniquext.plugin.extension.SecretConfigs
import com.uniquext.plugin.reinforce.Reinforcer
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoPackPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def apkConfigs = project.extensions.create('apkConfigs', ApkConfigs)
        def secretConfig = project.extensions.create('secretConfig', SecretConfigs)
        project.task('reinforce') {
            doLast {
                Reinforcer.getInstance().init(secretConfig.secretId, secretConfig.secretKey);
                AutoUnpack.start(apkConfigs)
            }
        }
    }
}
