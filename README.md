# AutoPack
基于腾讯乐固的自动加固重签名插件

>目前乐固SDK仅支持云打包，因此需要先上传原始APK。<br>
***此插件上传空间为本人公司所在测试环境，强烈建议使用者在源码处替换到属于自己的云空间***

该插件将执行一下步骤（）
* 初始化乐固配置
* 过滤指定文件夹下非APK文件
* 上传原始apk
* 云加固
* 下载加固后的APK
* APK重签名
     
```
apply plugin: 'legu-pack'

buildscript {
    repositories {
        maven {
            url uri('../publish')
        }
        google()
        jcenter()
    }
    dependencies {
        classpath 'com.uniquext.plugin:LeGuPack:1.1.3'
    }
}

apkConfigs {
    //  apk所在文件夹，会处理该文件夹下所有apk
    apkFolder file(project.rootDir.absolutePath)
    //  加固后的APK前缀
    reinforcePrefix "reinforce"
    //  重签名后的APK前缀
    signPrefix "sign"

    signature {
        storeFile file("app_key_store") // as默认的keystore
        storePassword "yi3634132" // keystore 默认的打开密码
        keyAlias "key0" // 默认的别名
        keyPassword "yi3634132" // 默认的别名密码
    }

}

secretConfig {
    //  腾讯云 secretId 及 secretKey
    secretId "AKIDLxTsBMBNExGSXVI6jGDMZhibO4e7umRU"
    secretKey "AD9ziP7oOrOpBR2XvCMRNCvCNROE4qV3"
}
```