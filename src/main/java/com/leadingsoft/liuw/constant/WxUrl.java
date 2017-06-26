package com.leadingsoft.liuw.constant;

/**
 * 微信开发相关的接口
 *
 * @author user
 */
public class WxUrl {


    /* ===================== 公众号接口 ==================== */
    /* 获取access_token。公众号调用各接口时都需使用access_token */
    public final static String URL_ACCESS_TOKEN =
            "https://api.weixin.qq.com/cgi-bin/token?grant_type=%s&appid=%s&secret=%s";

    public final static String URL_SEND_TEMPLATE_MSG =
            "https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token=%S";
}
