package com.uniquext.plugin.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.uniquext.plugin.ItemApkInfo;
import com.uniquext.plugin.AutoUnpack;
import com.uniquext.plugin.http.exception.DownloadException;
import com.uniquext.plugin.http.exception.UploadException;
import com.uniquext.plugin.util.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HttpHelper {

    private static final String UPLOAD_URL = "http://web-admin-test.percentcompany.com/api/app-version/upload-apk";
    private OkHttpClient mOkHttpClient;


    public HttpHelper() {
        mOkHttpClient = new OkHttpClient();
    }

    public static HttpHelper getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void syncUpload(final ItemApkInfo apkInfo) throws UploadException {
        File apk = new File(AutoUnpack.mApkConfigs.apkFolder, apkInfo.getSourceName());
        RequestBody body = RequestBody.create(MediaType.parse("*/*"), apk);
        RequestBody requestBody = new MultipartBuilder().type(MultipartBuilder.FORM)
                .addFormDataPart("file", apkInfo.getSourceName(), body)
                .build();
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .addHeader("Content-Type", "multipart/form-data")
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject().getAsJsonObject("data");
            apkInfo.setRemoteUrl(jsonObject.get("appUrl").getAsString());
            apkInfo.setMd5(jsonObject.get("md5").getAsString());
        } catch (IOException e) {
            throw new UploadException();
        }
    }


    public void syncDownload(final ItemApkInfo apkInfo) throws DownloadException {
        try {
            File folder = Utils.createFolder(AutoUnpack.mApkConfigs.apkFolder, "reinforce");
            File apkFile = new File(folder, apkInfo.getReinforceName());
            apkInfo.setReinforceApkPath(apkFile.getAbsolutePath());
            Request request = new Request.Builder()
                    .get()
                    .url(apkInfo.getDownloadUrl())
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            int len = 0;
            InputStream is = response.body().byteStream();
            FileOutputStream fos = new FileOutputStream(apkFile);
            byte[] buf = new byte[(int) response.body().contentLength()];
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException | NullPointerException e) {
            throw new DownloadException();
        }
    }

    private static class SingleHolder {
        private static final HttpHelper INSTANCE = new HttpHelper();
    }

}
