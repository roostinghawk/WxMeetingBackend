package com.leadingsoft.liuw.filter;

import com.leadingsoft.liuw.base.DefaultAuthenticationSuccessHandler;
import com.leadingsoft.liuw.base.DefaultAuthenticationToken;
import com.leadingsoft.liuw.dto.WxAppAuthenticationDTO;
import com.leadingsoft.liuw.dto.wx.RespUserInfo;
import com.leadingsoft.liuw.model.WxAppToken;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.repository.WxUserRepository;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.service.WxAppTokenRepository;
import com.leadingsoft.liuw.service.WxUserService;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class WxAppAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    @Autowired
    private WxUserRepository wxUserRepository;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WxAppTokenRepository wxAppTokenRepository;
    @Autowired
    private WechatApiService wechatApiService;

    @Value("${wxApp.config.appId}")
    private String appId;
    @Value("${wxApp.config.secret}")
    private String appSecret;

    private String AUTH_URL_PATTERN = "https://api.weixin.qq.com/sns/jscode2session?appid={APPID}&secret={SECRET}&grant_type=authorization_code&js_code={CODE}";

    public WxAppAuthenticationFilter() {
        super("/wxApp/login");
        super.setAuthenticationSuccessHandler(new DefaultAuthenticationSuccessHandler());
        super.setAuthenticationFailureHandler(new WxAuthenticationFailureFilter());
    }

    @SuppressWarnings({"rawtypes", "unchecked" })
    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        WxAppAuthenticationDTO authDTO;
        try {
            final InputStreamReader reader = new InputStreamReader(request.getInputStream());
            authDTO = JsonUtil.jsonToPojo(reader, WxAppAuthenticationDTO.class);
            // debug
            if(authDTO != null) {
                log.debug(authDTO.getCode());
                log.debug(authDTO.getEncryptedData());
                log.debug(authDTO.getIv());
            }
        } catch (final IOException e) {
            log.error("解析登录请求参数失败", e);
            return null;
        }

        // 取得code
//        String code = request.getParameter("code");
//        if(StringUtils.isEmpty(code)) {
//            log.error("Code为空");
//            return null;
//        }

        String url = AUTH_URL_PATTERN.
                replace("{APPID}", appId).
                replace("{SECRET}", appSecret).
                replace("{CODE}", authDTO.getCode());
        final String str = this.restTemplate.getForObject(url, String.class);
        log.info("登录返回内容" + str);
        final Map resultMap = JsonUtil.jsonToMap(str);
        if(!resultMap.containsKey("openid")) {
            return null;
        }

        String openId = (String)resultMap.get("openid");
        String sessionKey = (String)resultMap.get("session_key");
        
        log.info("小程序Open ID：" + openId);
        /**
         * 第一次登陆时，创建小程序用户
         * 通过微信登录过时，添加小程序的APP ID
         */
        if(wxUserRepository.countByOpenId(openId) == 0) {
            RespUserInfo userInfo = wechatApiService.getUserInfoFromApp(
                    authDTO.getEncryptedData(),
                    sessionKey,
                    authDTO.getIv());
            if(userInfo != null && !StringUtils.isEmpty(userInfo.getUnionid())) {
                WxUser wxUser = wxUserRepository.findByUnionId(userInfo.getUnionid());
                if(wxUser == null) {
                    wxUserService.initForApp(openId, userInfo.getUnionid(), userInfo.getNickName());
                } else {
                    wxUser.setOpenIdFromApp(openId);
                    wxUser.setNickName(userInfo.getNickName());
                    wxUserRepository.save(wxUser);
                }
            } else {
                wxUserService.initForApp(openId, userInfo.getUnionid(), userInfo.getNickName());
            }
        }

//        WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
//        if(wxUser == null) {
//            this.wxUserService.initForApp(openId);
//        } else {
//            wxUser.setOpenIdFromApp(openId);
//            this.wxUserRepository.save(wxUser);
//        }

        // 小程序Token生成
        WxAppToken wxAppToken = wxAppTokenRepository.findOneByOpenId(openId);
        if(wxAppToken == null) {
            wxAppToken = new WxAppToken();
        }
        wxAppToken.setOpenId(openId);
//        wxAppToken.setSessionKey();
        wxAppToken.setValue(UUID.randomUUID().toString());
        wxAppTokenRepository.save(wxAppToken);

        final DefaultAuthenticationToken auth = new DefaultAuthenticationToken();
        auth.setPrincipal(openId);
        auth.setAuthenticated(true);
        final Map map = new HashMap();
        map.put("token", wxAppToken.getValue());
        auth.setDetails(map);
        
        return auth;
    }
}
