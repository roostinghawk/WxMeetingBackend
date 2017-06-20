package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.model.WxAppToken;
import org.springframework.data.repository.Repository;

/**
 * WxAppTokenRepository
 */
public interface WxAppTokenRepository extends Repository<WxAppToken, Long> {


    WxAppToken findOne(final Long id);

    WxAppToken save(final WxAppToken model);

    WxAppToken findOneByOpenId(final String openId);

    WxAppToken findByValue(final String token);

}
