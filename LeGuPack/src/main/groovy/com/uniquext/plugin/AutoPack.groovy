package com.uniquext.plugin


import com.uniquext.plugin.extension.ApkConfigs
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoPack implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def extension = project.extensions.create('apkConfigs', ApkConfigs)

        project.task('reinforce') {
            doLast {
                System.out.println("ApkConfigs apkFolder " + extension.apkFolder.getAbsolutePath())
                System.out.println("ApkConfigs reinforcePrefix " + extension.reinforcePrefix)
                System.out.println("ApkConfigs signPrefix " + extension.signPrefix)

                System.out.println("SignConfigs storeFile " + extension.signConfigs.storeFile.getAbsolutePath())
                System.out.println("SignConfigs storePassword " + extension.signConfigs.storePassword)
                System.out.println("SignConfigs keyAlias " + extension.signConfigs.keyAlias)
                System.out.println("SignConfigs keyPassword " + extension.signConfigs.keyPassword)

            }
        }
    }
}
