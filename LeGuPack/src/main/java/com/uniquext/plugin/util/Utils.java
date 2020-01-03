package com.uniquext.plugin.util;

import com.uniquext.plugin.AutoUnpack;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Locale;

public class Utils {

    private static final String SIGNATURE_CMD = "jarsigner -digestalg SHA1 -sigalg MD5withRSA -verbose -keystore %s -storepass %s -keypass %s -signedjar %s %s %s";

    public static File createFolder(File parent, String name) {
        File folder = new File(parent, name);
        if (folder.exists() || folder.mkdirs()) {
            return folder;
        } else {
            return null;
        }
    }

    private static void command(String cmd) {
        try {
            System.out.println(cmd);
            Process process = Runtime.getRuntime().exec(cmd);  //该实例可用来控制进程并获得相关信息
            String line = null;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void signature(String signedApkPath, String unSignedApkPath) {
        command(
                String.format(Locale.CHINA, SIGNATURE_CMD,
                        AutoUnpack.mApkConfigs.signConfigs.storeFile.getAbsolutePath(),
                        AutoUnpack.mApkConfigs.signConfigs.storePassword,
                        AutoUnpack.mApkConfigs.signConfigs.keyPassword,
                        signedApkPath,
                        unSignedApkPath,
                        AutoUnpack.mApkConfigs.signConfigs.keyAlias
                )
        );
    }


}
