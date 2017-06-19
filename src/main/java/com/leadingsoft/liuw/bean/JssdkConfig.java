package com.leadingsoft.liuw.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 通过config接口注入权限验证配置
 */
@Getter
@Setter
public class JssdkConfig {
    /* 公众号的唯一标识 */
    private String appId;
    /* 生成签名的时间戳 */
    private String timestamp;
    /* 生成签名的随机串 */
    private String nonceStr;
    /* 签名 */
    private String signature;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("appId" + ": " + this.getAppId() + "|");
        sb.append("timestamp" + ": " + this.getTimestamp() + "|");
        sb.append("nonceStr" + ": " + this.getNonceStr() + "|");
        sb.append("signature" + ": " + this.getSignature() + "|");
        return sb.toString();
    }

}
