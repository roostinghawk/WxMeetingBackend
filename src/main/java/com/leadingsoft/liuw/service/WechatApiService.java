package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.dto.wx.RespUserInfo;
import com.leadingsoft.liuw.model.Meeting;

/**
 * @author user
 */
public interface WechatApiService {
    
    /**
     * 获取从小程序登录的用户基本信息。
     *
     */
    public RespUserInfo getUserInfoFromApp(
            final String encryptedData,
            final String sessionKey,
            final String iv);

    void sendTemplateMsg(final String jsonData);

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
     * @param timestamp
     * @param nonce
     * @return
     */
    public boolean checkSignature(final String signature, final String timestamp, final String nonce);


}
