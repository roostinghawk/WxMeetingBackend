package com.leadingsoft.liuw.service.impl;
import com.leadingsoft.liuw.dto.wx.RespUserInfo;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.AESUtil;
import com.leadingsoft.liuw.utils.JsonUtils;
import org.springframework.stereotype.Service;

@Service
public class WechatApiServiceImpl implements WechatApiService {
    @Override
    public RespUserInfo getUserInfoFromApp(
            final String encryptedData,
            final String sessionKey,
            final String iv) {

        // 解密数据
        String result = AESUtil.aesCbcDecode(encryptedData, sessionKey, iv);
        RespUserInfo respUserInfo = JsonUtils.jsonToPojo(result, RespUserInfo.class);

        return respUserInfo;
    }
}