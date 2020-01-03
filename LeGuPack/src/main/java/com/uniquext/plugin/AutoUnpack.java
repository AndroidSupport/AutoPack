package com.uniquext.plugin;


import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.uniquext.plugin.extension.ApkConfigs;
import com.uniquext.plugin.http.HttpHelper;
import com.uniquext.plugin.http.exception.DownloadException;
import com.uniquext.plugin.http.exception.UploadException;
import com.uniquext.plugin.reinforce.ReinforceResult;
import com.uniquext.plugin.reinforce.Reinforcer;
import com.uniquext.plugin.util.Utils;

import java.io.File;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class AutoUnpack {

    private static final String TAG = "LeGuPack";

    public static ApkConfigs mApkConfigs = null;

    /**
     * 初始化乐固配置
     * 过滤非APK文件
     * 转换对象
     * 上传
     * 加固
     * 下载
     * 重签名
     */
    public static void start(ApkConfigs apkConfigs) {
        mApkConfigs = apkConfigs;
        if (apkConfigs.apkFolder == null || !apkConfigs.apkFolder.exists() || !apkConfigs.apkFolder.isDirectory()) {
            System.out.println(String.format(Locale.CHINA, "[%s] apkConfigs.apkFolder does not exists or is not a folder.", TAG));
        } else {
            Observable
                    .fromArray(Objects.requireNonNull(mApkConfigs.apkFolder.listFiles()))
                    .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".apk"))
                    .map(AutoUnpack::transform)
                    .doOnNext(AutoUnpack::upload)
                    .flatMap((Function<ItemApkInfo, ObservableSource<ItemApkInfo>>) AutoUnpack::reinforce)
                    .doOnNext(AutoUnpack::download)
                    .doOnNext(AutoUnpack::signature)
                    .onErrorResumeNext((Function<Throwable, ObservableSource<ItemApkInfo>>) throwable -> {
                        if (throwable instanceof UploadException) {
                            System.out.println(String.format(Locale.CHINA, "[%s] The apk upload failed.", AutoUnpack.TAG));
                        } else if (throwable instanceof TencentCloudSDKException) {
                            System.out.println(String.format(Locale.CHINA, "[%s] The apk reinforce failed.", AutoUnpack.TAG));
                        }
                        throwable.printStackTrace();
                        return Observable.empty();
                    })
                    .subscribe(new Observer<ItemApkInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ItemApkInfo apkInfo) {
                            System.out.println(String.format(Locale.CHINA, "[%s] %s processing completed.", AutoUnpack.TAG, apkInfo.getSourceName()));
                        }

                        @Override
                        public void onError(Throwable e) {
                            System.out.println(String.format(Locale.CHINA, "[%s] has error.", AutoUnpack.TAG));
                        }

                        @Override
                        public void onComplete() {
                            System.out.println(String.format(Locale.CHINA, "[%s] is Finished.", AutoUnpack.TAG));
                        }
                    });
        }
    }

    /**
     * 转换
     */
    private static ItemApkInfo transform(File file) {
        ItemApkInfo apkInfo = new ItemApkInfo(file.getName());
        apkInfo.setReinforceName(String.format(Locale.CHINA, "%s-%s", mApkConfigs.reinforcePrefix, file.getName()));
        apkInfo.setSignName(String.format(Locale.CHINA, "%s-%s", mApkConfigs.signPrefix, file.getName()));
        return apkInfo;
    }

    /**
     * 上传
     */
    private static void upload(ItemApkInfo apkInfo) throws UploadException {
        System.out.println("########## Upload  " + apkInfo.toString());
        System.out.println(String.format(Locale.CHINA, "[%s] %s is being uploaded. Please waiting...", AutoUnpack.TAG, apkInfo.getSourceName()));
        HttpHelper.getInstance().syncUpload(apkInfo);
        System.out.println(String.format(Locale.CHINA, "[%s] %s upload completed.", TAG, apkInfo.getSourceName()));
    }

    /**
     * 加固
     */
    private static ObservableSource<ItemApkInfo> reinforce(ItemApkInfo apkInfo) throws TencentCloudSDKException {
        System.out.println("########## Reinforce  " + apkInfo.toString());
        System.out.println(String.format(Locale.CHINA, "[%s] the apk %s starts to reinforce with LeGu.", AutoUnpack.TAG, apkInfo.getSourceName()));
        Reinforcer.getInstance().createShieldInstance(apkInfo);
        return Observable.merge(Observable.just(1L), Observable.interval(3000L, TimeUnit.MILLISECONDS, Schedulers.trampoline()))
                .flatMap((Function<Long, ObservableSource<ReinforceResult>>) aLong -> Observable.just(Reinforcer.getInstance().describeShieldResult(apkInfo.getItemId())))
                .filter(reinforceResult -> {
                    System.out.println(String.format(Locale.CHINA, "[%s] the apk %s current status is %d -> %s", AutoUnpack.TAG, apkInfo.getSourceName(), reinforceResult.status, reinforceResult.statusDesc));
                    return reinforceResult.status != 2;
                })
                .takeUntil(reinforceResult -> {
                    System.out.println(String.format(Locale.CHINA, "[%s] the apk %s reinforce completed", AutoUnpack.TAG, apkInfo.getSourceName()));
                    return reinforceResult.status != 2;
                })
                .map(reinforceResult -> {
                    apkInfo.setDownloadUrl(reinforceResult.url);
                    return apkInfo;
                });
    }

    /**
     * 下载
     */
    private static void download(ItemApkInfo apkInfo) throws DownloadException {
        System.out.println("########## Download  " + apkInfo.toString());
        System.out.println(String.format(Locale.CHINA, "[%s] %s is downloading. Please waiting...", AutoUnpack.TAG, apkInfo.getReinforceName()));
        HttpHelper.getInstance().syncDownload(apkInfo);
        System.out.println(String.format(Locale.CHINA, "[%s] %s download completed.", TAG, apkInfo.getReinforceName()));
    }

    /**
     * 重签名
     */
    private static void signature(ItemApkInfo apkInfo) throws NullPointerException{
        System.out.println("########## Signing  " + apkInfo.toString());
        System.out.println(String.format(Locale.CHINA, "[%s] %s is signing. Please waiting...", AutoUnpack.TAG, apkInfo.getReinforceName()));
        File folder = Utils.createFolder(mApkConfigs.apkFolder, "sign");
        Utils.signature(String.format(Locale.CHINA, "%s/%s", Objects.requireNonNull(folder).getAbsolutePath(), apkInfo.getSignName()), apkInfo.getReinforceApkPath());
        System.out.println(String.format(Locale.CHINA, "[%s] %s signed and renamed %s.", TAG, apkInfo.getReinforceName(), apkInfo.getSignName()));
    }

}
