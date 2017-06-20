package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.dto.wx.RespUserInfo;

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


}
