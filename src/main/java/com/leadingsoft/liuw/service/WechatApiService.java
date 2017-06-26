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


}
