package com.uniquext.plugin

import com.uniquext.plugin.extension.ApkConfigs
import org.gradle.api.Plugin
import org.gradle.api.Project

class AutoPackPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def extension = project.extensions.create('apkConfigs', ApkConfigs)
        project.task('reinforce') {
            doLast {
                AutoUnpack.start(extension)
            }
        }
    }
}
