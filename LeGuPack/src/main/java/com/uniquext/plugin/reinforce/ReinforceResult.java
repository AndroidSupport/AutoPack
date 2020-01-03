package com.uniquext.plugin.reinforce;

public class ReinforceResult {

    public String url;
    //  1-已完成,2-处理中,3-处理出错,4-处理超时
    public int status;
    public String statusDesc;

    public ReinforceResult(String url, int status, String statusDesc) {
        this.url = url;
        this.status = status;
        this.statusDesc = statusDesc;
    }

}
