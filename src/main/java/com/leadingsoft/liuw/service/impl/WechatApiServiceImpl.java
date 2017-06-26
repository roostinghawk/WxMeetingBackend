package com.leadingsoft.liuw.service.impl;
import com.leadingsoft.liuw.constant.WxUrl;
import com.leadingsoft.liuw.dto.wx.*;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.AESUtil;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class WechatApiServiceImpl implements WechatApiService {

    @Value("${wxApp.config.appId}")
    private String appIdForApp;
    @Value("${wxApp.config.secret}")
    private String appSecretForApp;

    @Autowired
    private RestTemplate restTemplate;
    private RespAccessToken respAccessTokenForApp;

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
        RespUserInfo respUserInfo = JsonUtil.jsonToPojo(result, RespUserInfo.class);

        return respUserInfo;
    }

    @Override
    public void sendTemplateMsg(final String jsonData) {
        final String url = String.format(WxUrl.URL_SEND_TEMPLATE_MSG, this.getAccessTokenForApp());
        log.info(String.format("发送模板消息地址：%s\n", url));

        final HttpHeaders headerWechat = new HttpHeaders();
        headerWechat.add("Content-Type", "application/json; charset=utf-8");
        final HttpEntity<String> httpEntityWechat = new HttpEntity<String>(jsonData, headerWechat);

        final ResponseEntity<RespCommon> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntityWechat,
                RespCommon.class);

        log.info(String.format("发送模板消息响应：%s\n", resp.getBody()));
    }


    /**
     * 获取access_token
     *
     * @return
     */
    private String getAccessTokenForApp() {
        if ((null != this.respAccessTokenForApp) && !this.respAccessTokenForApp.isExpire()) {
            log.info("================HttpSession token=====================");
            log
                    .info("===============" + this.respAccessTokenForApp.getAccess_token() + "===================");
            return this.respAccessTokenForApp.getAccess_token();
        }

        log.info("================appId=====================");
        log.info("===============" + this.appIdForApp + "===================");
        log.info("================appSecret=====================");
        log.info("===============" + this.appSecretForApp + "===================");

        final String reqUrl =
                String.format(WxUrl.URL_ACCESS_TOKEN, "client_credential", this.appIdForApp, this.appSecretForApp);

        final ResponseEntity<RespAccessToken> accessToken =
                this.restTemplate.exchange(reqUrl, HttpMethod.GET, null,
                        RespAccessToken.class);

        this.respAccessTokenForApp = accessToken.getBody();

        log.info("================request token=====================");
        log
                .info("===============" + this.respAccessTokenForApp.getAccess_token() + "===================");
        return this.respAccessTokenForApp.getAccess_token();
    }
}