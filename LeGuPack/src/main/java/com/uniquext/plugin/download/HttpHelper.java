package com.uniquext.plugin.download;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.uniquext.plugin.AppInfo;
import com.uniquext.plugin.AutoUnpack;
import com.uniquext.plugin.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class HttpHelper {

    private static final String UPLOAD_URL = "http://web-admin-test.percentcompany.com/api/app-version/upload-apk";
    private OkHttpClient mOkHttpClient;


    public HttpHelper() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpHelper getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void syncUpload(final AppInfo appInfo) throws UploadException {
        File apk = new File(AutoUnpack.mApkConfigs.apkFolder, appInfo.sourceName);
        RequestBody body = RequestBody.create(MediaType.parse("*/*"), apk);
        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", appInfo.sourceName, body)
                .build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject().getAsJsonObject("data");
            appInfo.remoteUrl = jsonObject.get("appUrl").getAsString();
            appInfo.md5 = jsonObject.get("md5").getAsString();
        } catch (IOException e) {
            throw new UploadException();
        }

    }

    private File createFile(String localPath, String name) {
        String folderPath = String.format(Locale.CHINA, "%s/%s", localPath, "reinforce").replace("//", "/");
        String fileName = String.format(Locale.CHINA, "%s-%s", "LeGu", name);
        System.out.println(String.format(Locale.CHINA, "Create APK file -> %s/%s", folderPath, fileName));
        return FileUtils.createFile(folderPath, fileName);
    }

    public void download(final AppInfo appInfo) {
        final Request request = new Request.Builder()
                .get()
                .url(appInfo.downloadUrl)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                System.out.println(String.format(Locale.CHINA, "#[HttpHelper:%s] Failed)", appInfo.name));
                e.printStackTrace();
            }

            @Override
            public void onResponse(Response response) throws IOException {

                System.out.println("### " + response.toString());
                File apkFile = createFile(appInfo.localPath, appInfo.name);

                if (apkFile != null) {
                    int len = 0;

                    long total = response.body().contentLength();
                    System.out.println(String.format(Locale.CHINA, "#[HttpHelper:%s] download... -> %d)", appInfo.name, total));

                    InputStream is = response.body().byteStream();
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    byte[] buf = new byte[(int) total];

                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    //关闭流
                    fos.close();
                    is.close();
                    System.out.println(String.format(Locale.CHINA, "#[HttpHelper:%s] Success)", appInfo.name));
                } else {
                    System.out.println("### create file error");
                }

            }

        });
    }

    private static class SingleHolder {
        private static final HttpHelper INSTANCE = new HttpHelper();
    }

}
