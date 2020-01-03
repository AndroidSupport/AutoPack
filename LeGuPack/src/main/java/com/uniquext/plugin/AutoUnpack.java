package com.uniquext.plugin;


import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.uniquext.plugin.download.HttpHelper;
import com.uniquext.plugin.download.UploadException;
import com.uniquext.plugin.extension.ApkConfigs;
import com.uniquext.plugin.reinforce.ReinforceResult;
import com.uniquext.plugin.reinforce.Reinforcer;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class AutoUnpack {

    public static final String TAG = "LeGuPack";

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
            Reinforcer.getInstance().init(mApkConfigs.secretConfigs.secretId, mApkConfigs.secretConfigs.secretKey);
            Observable
                    .fromArray(Objects.requireNonNull(mApkConfigs.apkFolder.listFiles()))
                    .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".apk"))
                    .map(file -> new AppInfo(file.getName()))
                    .doOnNext(AutoUnpack::upload)
//                    .flatMap((Function<AppInfo, ObservableSource<AppInfo>>) AutoUnpack::reinforce)
                    .onErrorResumeNext((Function<Throwable, ObservableSource<AppInfo>>) throwable -> {
                        if (throwable instanceof UploadException) {
                            System.out.println(String.format(Locale.CHINA, "[%s] The apk upload failed.", AutoUnpack.TAG));
                        } else if (throwable instanceof TencentCloudSDKException) {
                            System.out.println(String.format(Locale.CHINA, "[%s] The apk reinforce failed.", AutoUnpack.TAG));
                        }
                        throwable.printStackTrace();
                        return Observable.empty();
                    })

                    .subscribe(new Observer<AppInfo>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(AppInfo appInfo) {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    }

    private static void upload(AppInfo appInfo) throws UploadException {
        System.out.println("########## Upload");
        System.out.println(String.format(Locale.CHINA, "[%s] %s is being uploaded. Please waiting...", AutoUnpack.TAG, appInfo.sourceName));
        HttpHelper.getInstance().syncUpload(appInfo);
        System.out.println(String.format(Locale.CHINA, "[%s] %s upload completed.", TAG, appInfo.sourceName));
    }

    private static ObservableSource<AppInfo> reinforce(AppInfo appInfo) throws TencentCloudSDKException {
        System.out.println("########## Reinforce");
        System.out.println(String.format(Locale.CHINA, "[%s] the apk %s starts to reinforce with LeGu.", AutoUnpack.TAG, appInfo.sourceName));
        Reinforcer.getInstance().createShieldInstance(appInfo);
        return Observable.merge(Observable.just(1L), Observable.interval(3000L, TimeUnit.MILLISECONDS, Schedulers.trampoline()))
                .flatMap((Function<Long, ObservableSource<ReinforceResult>>) aLong -> Observable.just(Reinforcer.getInstance().describeShieldResult(appInfo.itemId)))
                .filter(reinforceResult -> {
                    System.out.println(String.format(Locale.CHINA, "[%s] the apk %s current status is %d -> %s", AutoUnpack.TAG, appInfo.sourceName, reinforceResult.status, reinforceResult.statusDesc));
                    return reinforceResult.status != 2;
                })
                .takeUntil(new Predicate<ReinforceResult>() {
                    @Override
                    public boolean test(ReinforceResult reinforceResult) throws Exception {
                        System.out.println(String.format(Locale.CHINA, "[%s] the apk %s reinforce completed", AutoUnpack.TAG, appInfo.sourceName));
                        return reinforceResult.status != 2;
                    }
                })
                .map(reinforceResult -> appInfo);
    }


    public static void t2(final AppInfo appInfo) {
//        final String apkName = appInfo.name;
//        Observable.just(appInfo)
//                .flatMap(new Function<AppInfo, ObservableSource<String>>() {
//                    @Override
//                    public ObservableSource<String> apply(AppInfo appInfo) throws Exception {
//                        System.out.println(String.format(Locale.CHINA, "#[AutoUnpack:%s] AutoUnpack starts to reinforce the apk with LeGu.", apkName));
//                        return Observable.just(Reinforcer.getInstance().createShieldInstance(appInfo)).retry(3);
//                    }
//                })
//                .flatMap(new Function<String, ObservableSource<ReinforceResult>>() {
//                    @Override
//                    public ObservableSource<ReinforceResult> apply(String s) throws Exception {
//                        final String itemId = s;
//                        System.out.println(String.format(Locale.CHINA, "#[AutoUnpack:%s] The itemId -> %s", apkName, itemId));
//                        return Observable.merge(Observable.just(1L), Observable.interval(3000L, TimeUnit.MILLISECONDS, Schedulers.trampoline()))
//                                .flatMap(new Function<Long, ObservableSource<ReinforceResult>>() {
//                                    @Override
//                                    public ObservableSource<ReinforceResult> apply(Long aLong) throws Exception {
//                                        return Observable.just(Reinforcer.getInstance().describeShieldResult(itemId));
//                                    }
//                                })
//                                .filter(new Predicate<ReinforceResult>() {
//                                    @Override
//                                    public boolean test(ReinforceResult reinforceResult) throws Exception {
//                                        System.out.println(String.format(Locale.CHINA, "#[AutoUnpack:%s] Current status -> %s", apkName, reinforceResult.status));
//                                        return reinforceResult.status != 2;
//                                    }
//                                })
//                                .takeUntil(new Predicate<ReinforceResult>() {
//                                    @Override
//                                    public boolean test(ReinforceResult reinforceResult) throws Exception {
//                                        return reinforceResult.status != 2;
//                                    }
//                                });
//
//                    }
//                })
//                .flatMap(new Function<ReinforceResult, ObservableSource<ReinforceResult>>() {
//                    @Override
//                    public ObservableSource<ReinforceResult> apply(ReinforceResult reinforceResult) throws Exception {
//                        if (reinforceResult.status == 1) {
//                            return Observable.just(reinforceResult);
//                        } else {
//                            System.out.println(String.format(Locale.CHINA, "#[AutoUnpack:%s] Reinforce exception. Error -> %s", apkName, reinforceResult.statusDesc));
//                            return Observable.error(new Throwable());
//                        }
//                    }
//                })
//                .retry(3)
//                .map(new Function<ReinforceResult, String>() {
//                    @Override
//                    public String apply(ReinforceResult reinforceResult) throws Exception {
//                        System.out.println(String.format(Locale.CHINA, "#[AutoUnpack:%s] Reinforce finish. DownloadUrl -> %s", apkName, reinforceResult.url));
//                        return reinforceResult.url;
//                    }
//                })
//                .subscribe(new Observer<String>() {
//                    @Override
//                    public void onSubscribe(Disposable disposable) {
//
//                    }
//
//                    @Override
//                    public void onNext(String s) {
//                        appInfo.downloadUrl = s;
//                        Downloader.getInstance().download(appInfo);
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//                        throwable.printStackTrace();
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
    }
}
