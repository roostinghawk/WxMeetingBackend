package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.model.WxUser;

/**
 * Created by liuw on 2017/6/20.
 */
public interface WxUserService {

    /**
     * 初始化微信小程序用户
     *
     */
    WxUser initForApp(final String openId);

    /**
     * 初始化微信小程序用户
     *
     */
    WxUser initForApp(final String openId, final String unionId);
}
