package com.uniquext.plugin.reinforce;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ms.v20180408.MsClient;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceRequest;
import com.tencentcloudapi.ms.v20180408.models.CreateShieldInstanceResponse;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultRequest;
import com.tencentcloudapi.ms.v20180408.models.DescribeShieldResultResponse;
import com.uniquext.plugin.ItemApkInfo;

import java.util.Locale;

public class Reinforcer {

    private static final String END_POINT = "ms.tencentcloudapi.com";
    private static final String APP_INFO_FORMAT = "{\"AppInfo\": {\"AppUrl\": \"%s\", \"AppMd5\": \"%s\", \"FileName\": \"%s\"}, \"ServiceInfo\": {\"CallbackUrl\": \"null\"}}";
    private static final String ITEM_ID_FORMAT = "{\"ItemId\":\"%s\"}";
    private MsClient msClient = null;

    private Reinforcer() {

    }

    public static Reinforcer getInstance() {
        return SingleHolder.INSTANCE;
    }

    public void init(String secretId, String secretKey) {
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(END_POINT);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        Credential credential = new Credential(secretId, secretKey);
        msClient = new MsClient(credential, "", clientProfile);
    }

    public void createShieldInstance(ItemApkInfo apkInfo) throws TencentCloudSDKException {
        System.out.println(getAppInfoParams(apkInfo));
        CreateShieldInstanceRequest req = CreateShieldInstanceRequest.fromJsonString(getAppInfoParams(apkInfo), CreateShieldInstanceRequest.class);
        CreateShieldInstanceResponse resp = msClient.CreateShieldInstance(req);
        apkInfo.setItemId(resp.getItemId());
    }

    public ReinforceResult describeShieldResult(String itemId) throws TencentCloudSDKException {
        DescribeShieldResultRequest req = DescribeShieldResultRequest.fromJsonString(getItemIdParams(itemId), DescribeShieldResultRequest.class);
        DescribeShieldResultResponse resp = msClient.DescribeShieldResult(req);
        return new ReinforceResult(
                resp.getShieldInfo() == null ? null : resp.getShieldInfo().getAppUrl(),
                resp.getTaskStatus(),
                String.format(Locale.CHINA, "[%s]%s", resp.getStatusDesc(), resp.getStatusRef())
        );
    }

    private String getAppInfoParams(ItemApkInfo apkInfo) {
        return String.format(Locale.CHINA, APP_INFO_FORMAT, apkInfo.getRemoteUrl(), apkInfo.getMd5(), apkInfo.getSourceName());
    }

    private String getItemIdParams(String itemId) {
        return String.format(Locale.CHINA, ITEM_ID_FORMAT, itemId);
    }

    private static class SingleHolder {
        private static final Reinforcer INSTANCE = new Reinforcer();
    }

}
