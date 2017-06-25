package com.leadingsoft.liuw.service.impl;
import com.leadingsoft.liuw.dto.wx.RespUserInfo;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.AESUtil;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WechatApiServiceImpl implements WechatApiService {
    @Override
    public RespUserInfo getUserInfoFromApp(
            final String encryptedData,
            final String sessionKey,
            final String iv) {

        if(StringUtils.isEmpty(encryptedData)) {
            return null;
        }

        // 解密数据
        String result = AESUtil.aesCbcDecode(encryptedData, sessionKey, iv);
        log.info("WechatApiServiceImpl------------------");
        log.info("encryptedData: "+ encryptedData);
        log.info("sessionKey: " + sessionKey);
        log.info("iv: " + iv);
        log.info("result: " + result);
        log.info("WechatApiServiceImpl------------------");
        RespUserInfo respUserInfo = JsonUtil.jsonToPojo(result, RespUserInfo.class);

        return respUserInfo;
    }
}