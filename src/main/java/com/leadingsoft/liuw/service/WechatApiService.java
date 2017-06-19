package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.bean.EventBean;
import com.leadingsoft.liuw.dto.wx.RespUserInfo;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;

/**
 * @author user
 */
public interface WechatApiService {

    /**
     * 验证RUL有效性
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    public String verifyUrl(final String signature, final String timestamp, final String nonce, final String echostr);

    /**
     * 校验签名
     *
     * @param token
     * @param timestamp
     * @param nonce
     * @return
     */
    public boolean checkSignature(final String signature, final String timestamp, final String nonce);

    /**
     * 解析XML数据
     *
     * @param xmlData
     * @return
     */
    public AbstractMessage parseXml(final String xmlData);

    /**
     * 创建永久二维码ticket
     *
     * @param scene_id
     */
    public String createQrcodeTicket(final String scene_id);

    /**
     * 计算签名
     *
     * @param url
     * @return
     */
    public JssdkConfig sign(final String jsapiTicket, final String url);

    /**
     * 静默获取code
     *
     * @param url
     * @return
     */
    public String wrapOauthUrlForBase(final String url);

    /**
     * 经用户同意，获取code
     *
     * @param url
     * @return
     */
    public String wrapOauthUrlForUserInfo(final String url);

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    public RespOauthAccessToken getOauthAccessToken(final String code);

    /**
     * 获取用户信息
     *
     * @return
     */
    public void getUserInfoByOauth(final String access_token, final String openId);

    /**
     * 获取用户基本信息。 {@link
     * ://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html}
     *
     * @param openId
     * @return 该用户是否关注公众号
     */
    public RespUserInfo getUserInfo(final String openId);
    
    /**
     * 获取从小程序登录的用户基本信息。
     *
     */
    public RespUserInfo getUserInfoFromApp(
            final String encryptedData,
            final String sessionKey,
            final String iv);

    /**
     * 发送模板消息
     *
     * @param jsonData
     */
    public void sendTemplateMsg(final String jsonData);

    public String uploadMedia(final File file);

    public String getMediaUrl(final String serverId);

    /**
     * 发送客服消息
     *
     * @param touser
     * @param msgtype
     * @param content
     */
    public void sendCustomMsg(final String touser, final String msgtype, final String content);

    public void sendCustomImageMsg(final String touser, final String mediaId);

    public String getPlaceInfo(final String name, final String region) throws JSONException, IOException;

    public String getLocation(final String lat, final String lng) throws JSONException, IOException;

    /**
     * 微信消息模板
     *
     * @category 一个上报人，发送给多个处理者。
     */
    public void sendMessageTemplateMsg(EventBean dto);

    /**
     * 微信误报消息模板
     *
     * @category param Event.java
     */
    public void sendErrorMessageTemplate(EventBean dto);

    /**
     * 微信通过审查消息模板
     *
     * @category param Event.java
     */
    public void sendSuccessMessageTemplate(EventBean dto);

    /**
     * 微信处理消息模板（48小时即将到期）
     *
     * @category param Event.java
     */
    public void sendDealMessageTemplate(EventBean dto);
}
