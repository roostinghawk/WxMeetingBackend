package com.leadingsoft.liuw.service.impl;

import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.external.sms.service.SmsService;
import com.leadingsoft.ntxf.bean.weixin.EventBean;
import com.leadingsoft.ntxf.bean.weixin.PersonnelBean;
import com.leadingsoft.ntxf.constant.WxUrl;
import com.leadingsoft.ntxf.dto.external.wechat.JssdkConfig;
import com.leadingsoft.ntxf.dto.external.wechat.TemplateMessageDTO;
import com.leadingsoft.ntxf.dto.external.wechat.message.AbstractMessage;
import com.leadingsoft.ntxf.dto.external.wechat.message.EventMessage;
import com.leadingsoft.ntxf.dto.external.wechat.message.TextMessage;
import com.leadingsoft.ntxf.dto.external.wechat.response.*;
import com.leadingsoft.ntxf.enums.EventApproveResultEnum;
import com.leadingsoft.ntxf.enums.wechat.MsgType;
import com.leadingsoft.ntxf.model.WxUser;
import com.leadingsoft.ntxf.repository.EventRepository;
import com.leadingsoft.ntxf.repository.WxUserRepository;
import com.leadingsoft.ntxf.service.external.wechat.WechatApiService;
import com.leadingsoft.ntxf.utils.AESUtil;
import com.leadingsoft.ntxf.utils.DateTimeUtil;
import com.leadingsoft.ntxf.utils.WechatUtil;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WechatApiServiceImpl implements WechatApiService {

    @Value("${weixin.config.host}")
    private String host;

    private final static Logger LOG = LoggerFactory.getLogger(WechatApiServiceImpl.class);

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private WxUserRepository wxUserRepository;
    @Autowired
    private RestTemplate restTemplate;

    // 微信公众号账号信息
    @Value("${weixin.config.appId}")
    private String appId;
    @Value("${weixin.config.token}")
    private String token;
    @Value("${weixin.config.encodingAESKey}")
    private String encodingAESKey;
    @Value("${weixin.config.secret}")
    private String appSecret;
    @Value("${weixin.template.templateAssignTask}")
    private String templateAssignTask;
    @Value("${weixin.template.templateMisInformation}")
    private String templateMisInformation;
    @Value("${weixin.template.templateEffectiveInformation}")
    private String templateEffectiveInformation;

    private RespAccessToken respAccessToken;
    private RespJsapiTicket respJsapiTicket;

    @Autowired
    private WechatApiService wechatApiService;
    @Autowired
    private SmsService smsService;

    @Value("${wxApp.config.appId}")
    private String appIdForApp;
    @Value("${wxApp.config.secret}")
    private String appSecretForApp;
    
    private RespAccessToken respAccessTokenForApp;

    /**
     * 获取access_token
     *
     * @return
     */
    private String getAccessToken() {
        if ((null != this.respAccessToken) && !this.respAccessToken.isExpire()) {
            WechatApiServiceImpl.LOG.info("================HttpSession token=====================");
            WechatApiServiceImpl.LOG
                    .info("===============" + this.respAccessToken.getAccess_token() + "===================");
            return this.respAccessToken.getAccess_token();
        }

        WechatApiServiceImpl.LOG.info("================appId=====================");
        WechatApiServiceImpl.LOG.info("===============" + this.appId + "===================");
        WechatApiServiceImpl.LOG.info("================appSecret=====================");
        WechatApiServiceImpl.LOG.info("===============" + this.appSecret + "===================");

        final String reqUrl =
                String.format(WxUrl.URL_ACCESS_TOKEN, "client_credential", this.appId, this.appSecret);

        final ResponseEntity<RespAccessToken> accessToken =
                this.restTemplate.exchange(reqUrl, HttpMethod.GET, null,
                        RespAccessToken.class);

        this.respAccessToken = accessToken.getBody();

        WechatApiServiceImpl.LOG.info("================request token=====================");
        WechatApiServiceImpl.LOG
                .info("===============" + this.respAccessToken.getAccess_token() + "===================");
        return this.respAccessToken.getAccess_token();
    }
    
    /**
     * 获取access_token
     *
     * @return
     */
    private String getAccessTokenForApp() {
        if ((null != this.respAccessTokenForApp) && !this.respAccessTokenForApp.isExpire()) {
            WechatApiServiceImpl.LOG.info("================HttpSession token=====================");
            WechatApiServiceImpl.LOG
                    .info("===============" + this.respAccessTokenForApp.getAccess_token() + "===================");
            return this.respAccessTokenForApp.getAccess_token();
        }

        WechatApiServiceImpl.LOG.info("================appId=====================");
        WechatApiServiceImpl.LOG.info("===============" + this.appIdForApp + "===================");
        WechatApiServiceImpl.LOG.info("================appSecret=====================");
        WechatApiServiceImpl.LOG.info("===============" + this.appSecretForApp + "===================");

        final String reqUrl =
                String.format(WxUrl.URL_ACCESS_TOKEN, "client_credential", this.appIdForApp, this.appSecretForApp);

        final ResponseEntity<RespAccessToken> accessToken =
                this.restTemplate.exchange(reqUrl, HttpMethod.GET, null,
                        RespAccessToken.class);

        this.respAccessTokenForApp = accessToken.getBody();

        WechatApiServiceImpl.LOG.info("================request token=====================");
        WechatApiServiceImpl.LOG
                .info("===============" + this.respAccessTokenForApp.getAccess_token() + "===================");
        return this.respAccessTokenForApp.getAccess_token();
    }

    @Override
    public String verifyUrl(final String signature, final String timestamp, final String nonce, final String echostr) {
        String result = "";

        final String mSignature = WechatUtil.generateSignature(this.token, timestamp, nonce);

        if (signature.equals(mSignature)) {
            result = echostr;
        }

        return result;
    }

    @Override
    public boolean checkSignature(final String signature, final String timestamp, final String nonce) {
        boolean result = false;

        final String mSignature = WechatUtil.generateSignature(this.token, timestamp, nonce);

        if (signature.equals(mSignature)) {
            result = true;
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AbstractMessage parseXml(final String xmlData) {
        // 将解析结果存储在HashMap中
        final Map<String, String> map = new HashMap<String, String>();

        // 从request中取得输入流
        InputStream inputStream = null;
        try {
            inputStream = new ByteArrayInputStream(xmlData.getBytes("UTF-8"));
            // 读取输入流
            final SAXReader reader = new SAXReader();
            Document document;
            try {
                document = reader.read(inputStream);
            } catch (final DocumentException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            // 得到xml根元素
            final Element root = document.getRootElement();
            // 得到根元素的所有子节点
            final List<Element> elementList = root.elements();

            // 遍历所有子节点
            for (final Element e : elementList) {
                map.put(e.getName(), e.getText());
            }

            AbstractMessage message = null;
            Class<?> clazz = null;
            final MsgType msgType = MsgType.valueOf(map.get("MsgType"));
            switch (msgType) {
            case text:
                clazz = TextMessage.class;
                break;
            case event:
                clazz = EventMessage.class;
                break;
            default:
                break;
            }

            try {
                message = (AbstractMessage) clazz.newInstance();
                message.parse(msgType, map);
            } catch (final InstantiationException e) {
                e.printStackTrace();
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }

            return message;
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            // 释放资源
            if (inputStream != null) {
                try {
                    inputStream.close();
                    inputStream = null;
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String createQrcodeTicket(final String scene_id) {
        final String url = String.format(WxUrl.URL_QR_LIMIT_SCENE, this.getAccessToken());

        String req = "{\"action_name\":\"QR_LIMIT_STR_SCENE\",\"action_info\":{\"scene\":{\"scene_str\":\"%s\"}}}";
        req = String.format(req, scene_id);

        final HttpEntity<String> httpEntity = new HttpEntity<String>(req);

        final ResponseEntity<RespQRLimitTicket> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                RespQRLimitTicket.class);

        final String ticket = resp.getBody().getTicket();

        // return String.format(WxUrl.URL_QR_SCENE, ticket);
        return String.format(ticket);
    }

    @Override
    public RespCommon publishMenu(final String menuJsonStr) {
        final String url = String.format(WxUrl.URL_PUBLISH_MENU, this.getAccessToken());

        final HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json; charset=utf-8");
        final HttpEntity<String> httpEntity = new HttpEntity<String>(menuJsonStr, header);

        final ResponseEntity<RespCommon> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                RespCommon.class);

        return resp.getBody();
    }

    @Override
    public String getJsapiTicket() {

        if ((null != this.respJsapiTicket) && !this.respJsapiTicket.isExpire()) {
            return this.respJsapiTicket.getTicket();
        }

        final String url = String.format(WxUrl.URL_GET_JSAPI_TICKET, this.getAccessToken());

        final ResponseEntity<RespJsapiTicket> resp = this.restTemplate.exchange(url, HttpMethod.POST, null,
                RespJsapiTicket.class);

        final RespJsapiTicket respJsapiTicket = new RespJsapiTicket();
        respJsapiTicket.setTicket(resp.getBody().getTicket());
        respJsapiTicket.setExpires_in(3600);
        this.respJsapiTicket = respJsapiTicket;

        System.out.println(resp.getBody());

        return resp.getBody().getTicket();
    }

    @Override
    public JssdkConfig sign(final String jsapiTicket, final String url) {

        final String timestamp = Long.toString(System.currentTimeMillis() / 1000);
        final String nonceStr = UUID.randomUUID().toString();

        String signature = "";

        // 注意这里参数名必须全部小写，且必须有序
        final String string1 = "jsapi_ticket=" + jsapiTicket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp
                + "&url=" + url;
        System.out.println(string1);

        try {
            final MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = this.byteToHex(crypt.digest());
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final JssdkConfig jssdkConfig = new JssdkConfig();
        jssdkConfig.setAppId(this.appId);
        jssdkConfig.setTimestamp(timestamp);
        jssdkConfig.setNonceStr(nonceStr);
        jssdkConfig.setSignature(signature);

        return jssdkConfig;
    }

    private String byteToHex(final byte[] hash) {
        final Formatter formatter = new Formatter();
        for (final byte b : hash) {
            formatter.format("%02x", b);
        }
        final String result = formatter.toString();
        formatter.close();
        return result;
    }

    @Override
    public String wrapOauthUrlForBase(final String oriUrl) {
        String preUrl = oriUrl;
        String url = "";
        try {
            final int index = this.host.indexOf("https://");
            if (index >= 0) {
                preUrl = oriUrl.replace("http", "https");
            }
            url = String.format(WxUrl.URL_WRAP_OAUTH_BASE, this.appId, URLEncoder.encode(preUrl, "utf-8"));
            WechatApiServiceImpl.LOG.info(String.format("网页授权获取code请求地址(scope=snsapi_base)：%s\n", url));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    @Override
    public String wrapOauthUrlForUserInfo(final String oriUrl) {
        String url = "";
        try {
            url = String.format(WxUrl.URL_WRAP_OAUTH_USERINFO, this.appId, URLEncoder.encode(oriUrl, "utf-8"));
            WechatApiServiceImpl.LOG.info(String.format("网页授权获取code请求地址(snsapi_userinfo)：%s\n", url));
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return url;
    }

    @Override
    public RespOauthAccessToken getOauthAccessToken(final String code) {
        final String url = String.format(WxUrl.URL_GET_OPEN_ID_BY_OAUTH, this.appId, this.appSecret, code);
        WechatApiServiceImpl.LOG.info(String.format("使用code换取openid请求地址：%s\n", url));

        final ResponseEntity<String> resp = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        final RespOauthAccessToken respOauthAccessToken = JsonUtils.jsonToPojo(resp.getBody(),
                RespOauthAccessToken.class);

        WechatApiServiceImpl.LOG.info(String.format("使用code换取openid结果：%s\n", respOauthAccessToken));

        return respOauthAccessToken;
    }

    @Override
    public void getUserInfoByOauth(final String access_token, final String openId) {
        final String url = String.format(WxUrl.URL_GET_USER_INFO_BY_OAUTH, access_token, openId);
        WechatApiServiceImpl.LOG.info(String.format("获取用户基本信息请求地址(拉取用户信息,scope为 snsapi_userinfo)：%s\n", url));
        final ResponseEntity<String> resp = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        final RespUserInfoByOauth respOauthAccessToken = JsonUtils.jsonToPojo(resp.getBody(),
                RespUserInfoByOauth.class);
        WechatApiServiceImpl.LOG.info(String.format("获取用户基本信息请求地址(拉取用户信息,scope为 snsapi_userinfo)结果：%s\n",
                respOauthAccessToken));
    }

    @Override
    public RespUserInfo getUserInfo(final String openId) {
        final String url = String.format(WxUrl.URL_USER_INFO, this.getAccessToken(), openId);
        WechatApiServiceImpl.LOG.info(String.format("获取用户基本信息请求地址(UnionID机制)：%s\n", url));
        final ResponseEntity<String> resp = this.restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        RespUserInfo respUserInfo = null;
        try {
            respUserInfo = JsonUtils.jsonToPojo(new String(resp.getBody().getBytes("ISO-8859-1"), "UTF-8"),
                    RespUserInfo.class);
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WechatApiServiceImpl.LOG.info(String.format("获取用户基本信息请求地址(UnionID机制)结果：%s\n", respUserInfo));

        return respUserInfo;
    }

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

    @Override
    public void sendTemplateMsg(final String jsonData) {
        final String url = String.format(WxUrl.URL_SEND_TEMPLATE_MSG, this.getAccessToken());
        WechatApiServiceImpl.LOG.info(String.format("发送模板消息地址：%s\n", url));

        final HttpHeaders headerWechat = new HttpHeaders();
        headerWechat.add("Content-Type", "application/json; charset=utf-8");
        final HttpEntity<String> httpEntityWechat = new HttpEntity<String>(jsonData, headerWechat);

        final ResponseEntity<RespCommon> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntityWechat,
                RespCommon.class);

        WechatApiServiceImpl.LOG.info(String.format("发送模板消息响应：%s\n", resp.getBody()));
    }

    @Override
    public String uploadMedia(final File tmpFile) {
        final RestTemplate restTemplate = new RestTemplate();
        final URI url = URI.create(String.format(WxUrl.URL_UPLOAD_MEDIA, this.getAccessToken(), "image"));

        final FileSystemResource file = new FileSystemResource(tmpFile);
        final LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", file);
        map.add("fileName", tmpFile.getName());
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final RequestEntity<LinkedMultiValueMap<String, Object>> reqEntity =
                new RequestEntity<LinkedMultiValueMap<String, Object>>(
                        map, headers, HttpMethod.POST, url);
        final ResponseEntity<String> resp = restTemplate.exchange(reqEntity, String.class);
        WechatApiServiceImpl.LOG.info(String.format("发送模板消息响应：%s\n", resp.getBody()));

        final RespUploadMedia respUploadMedia = JsonUtils.jsonToPojo(resp.getBody(), RespUploadMedia.class);
        return null != respUploadMedia ? respUploadMedia.getMedia_id() : "";
    }

    @Override
    public String getMediaUrl(final String serverId) {
        return String.format(WxUrl.URL_GET_MEDIA, this.getAccessToken(), serverId);
    }

    /**
     * 文本的客服消息
     */
    @Override
    public void sendCustomMsg(final String touser, final String msgtype, final String content) {
        final String url = String.format(WxUrl.URL_CUSTOM_SEND, this.getAccessToken());

        String req = "{\"touser\":\"%s\",\"msgtype\":\"%s\",\"%s\":{\"content\":\"%s\"}}";
        req = String.format(req, touser, msgtype, msgtype, content);
        WechatApiServiceImpl.LOG.info("==文本消息==" + req + "====");
        final HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json; charset=utf-8");

        final HttpEntity<String> httpEntity = new HttpEntity<String>(req, header);

        final ResponseEntity<String> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                String.class);

        WechatApiServiceImpl.LOG.info(resp.getBody());
    }

    /**
     * 图片的客服消息
     */
    @Override
    public void sendCustomImageMsg(final String touser, final String mediaId) {
        final String url = String.format(WxUrl.URL_CUSTOM_SEND, this.getAccessToken());

        String req = "{\"touser\":\"%s\",\"msgtype\":\"image\",\"image\":{\"media_id\":\"%s\"}}";
        req = String.format(req, touser, mediaId);

        final HttpHeaders header = new HttpHeaders();
        header.add("Content-Type", "application/json; charset=utf-8");

        final HttpEntity<String> httpEntity = new HttpEntity<String>(req, header);

        final ResponseEntity<String> resp = this.restTemplate.exchange(url, HttpMethod.POST, httpEntity,
                String.class);

        WechatApiServiceImpl.LOG.info(resp.getBody());
    }

    /**
     * java根据url获取json对象
     *
     * @author openks
     * @since 2013-7-16 需要添加java-json.jar才能运行
     */
    private String readAll(final Reader rd) throws IOException {
        final StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public JSONObject readJsonFromUrl(final String url) throws IOException, JSONException {
        final InputStream is = new URL(url).openStream();
        try {
            final BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            final String jsonText = this.readAll(rd);
            final JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
            // System.out.println("同时 从这里也能看出 即便return了，仍然会执行finally的！");
        }
    }

    @Override
    public String getPlaceInfo(final String name, final String region) throws JSONException, IOException {
        final JSONObject json =
                this.readJsonFromUrl("http://apis.map.qq.com/ws/place/v1/search?boundary=region(" + region + ",1)"
                        + "&keyword=" + name
                        + "&page_size=20&page_index=1&orderby=_distance&key=JVVBZ-WRD3K-2P4JA-AL2AX-ORVCF-SKB6J");
        System.out.println(json.toString());
        return json.toString();
    }

    @Override
    public String getLocation(final String lat, final String lng) throws JSONException, IOException {
        final JSONObject json =
                this.readJsonFromUrl("http://apis.map.qq.com/ws/geocoder/v1/?location=" + lat + "," + lng + ""
                        + "&key=JVVBZ-WRD3K-2P4JA-AL2AX-ORVCF-SKB6J&get_poi=1");
        System.out.println(json.toString());
        return json.toString();
    }

    /**
     * 微信分派任务消息模板
     *
     * @category 一个上报人，发送给多个处理者。
     * @param
     */
    @Override
    public void sendMessageTemplateMsg(final EventBean event) {
        final List<PersonnelBean> list = event.getPersonel();
        for (final PersonnelBean personnel : list) {
            final String openId = personnel.getOpenId();
            final Date time = event.getCreatedDate();
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            final Map<String, String> map = new HashMap<>();

            map.put("first", "您好，" + personnel.getName() + "，" + "您有一项待反馈任务需要处理");
            map.put("keyword1", event.getEventType());
            map.put("keyword2", event.getBasePartName() + "（" + event.getBasePartAddress() + "）");
            map.put("keyword3", formatter.format(time));
            map.put("remark", "请尽快处理！");

            final TemplateMessageDTO dto = this.constructTemplateMessage(openId,
                    this.templateAssignTask, map);

            final String jsonStr = JsonUtils.pojoToJson(dto);

            WechatApiServiceImpl.LOG.info(String.format("发送模板数据：%s\n", jsonStr));

            this.wechatApiService.sendTemplateMsg(jsonStr);

            final StringBuffer sb = new StringBuffer();
            sb.append("您好，").append(personnel.getName()).append("，您有一项待反馈任务需要处理");
            sb.append("\n待办内容：").append(event.getEventType());
            sb.append("\n基本信息：").append(event.getBasePartName());
            sb.append("（").append(event.getBasePartAddress()).append("）");
            sb.append("\n时间：").append(formatter.format(time));
            sb.append("\n请尽快处理！");
            // 发送短信
            this.sendMsg(personnel.getTel(), sb.toString());
        }
    }

    /**
     * 微信误报消息模板
     *
     * @param
     */
    @Override
    public void sendErrorMessageTemplate(final EventBean event) {
        final String openId = event.getOpenId();
        final String name = event.getUserName();
        final String eventCode = event.getEventType();
        final String eventBasePartName = event.getBasePartName();
        String remark = "";
        final WxUser reportUser = this.wxUserRepository.findOneByOpenId(openId);
        final Date todayDate = new Date();
        final Date oldDate = DateTimeUtil.addDays(todayDate, -30);
        if (reportUser.isBlack()) {
            remark = "您已经被加入黑名单！";
        } else {
            final Long count =
                    this.eventRepository
                            .countByReportInfoReportUserAndCreatedDateBetweenAndApproveInfoApproveResultEnum(
                                    reportUser,
                                    oldDate, todayDate, EventApproveResultEnum.FAKE);

            if (count < 3) {
                remark = "近30天，您已经有" + (count + 1) + "次隐患误报。误报3次将被限制上报隐患";
            }
        }

        final Map<String, String> map = new HashMap<>();

        String title = "您好！";
        if (StringUtils.isNotEmpty(name)) {
            title = name + "，" + title;
        }
        map.put("first", title);
        map.put("keyword1", "上报的【" + eventBasePartName + "】【" + eventCode + "】内容为【误报】");
        map.put("keyword2", "取消处理");
        map.put("remark", remark);

        final TemplateMessageDTO dto = this.constructTemplateMessage(openId,
                this.templateMisInformation, map);

        final String jsonStr = JsonUtils.pojoToJson(dto);

        WechatApiServiceImpl.LOG.info(String.format("发送模板数据：%s\n", jsonStr));

        this.wechatApiService.sendTemplateMsg(jsonStr);

        // this.sendMsg("01123", "sss");
    }

    /**
     * 微信通过审查消息模板
     *
     * @param event
     */
    @Override
    public void sendSuccessMessageTemplate(final EventBean event) {
        final String openId = event.getOpenId();

        final String eventCode = event.getEventType();

        final String title = "您所举报的隐患已通过审查！";
        final String remark = "获得有奖积分5分";

        final Map<String, String> map = new HashMap<>();

        map.put("first", title);
        map.put("keyword1", eventCode);
        map.put("keyword2", "通过审查");
        map.put("remark", remark);

        final TemplateMessageDTO dto = this.constructTemplateMessage(openId,
                this.templateEffectiveInformation, map);

        final String jsonStr = JsonUtils.pojoToJson(dto);

        WechatApiServiceImpl.LOG.info(String.format("发送模板数据：%s\n", jsonStr));

        this.wechatApiService.sendTemplateMsg(jsonStr);

        // this.sendMsg("01123", "sss");

        final Map<String, String> personnelMap = new HashMap<>();

        personnelMap.put("first", "您所处理的隐患已通过审查！");
        personnelMap.put("keyword1", eventCode);
        personnelMap.put("keyword2", "通过审查");
        personnelMap.put("remark", "获得有奖积分10分");

        final TemplateMessageDTO personnelDto = this.constructTemplateMessage(
                event.getPersonel().get(0).getOpenId(),
                this.templateEffectiveInformation, personnelMap);

        final String personnelJsonStrs = JsonUtils.pojoToJson(personnelDto);

        WechatApiServiceImpl.LOG.info(String.format("发送模板数据：%s\n", personnelJsonStrs));

        this.wechatApiService.sendTemplateMsg(personnelJsonStrs);

        // this.sendMsg("01123", "sss");
    }

    /**
     * 微信误报消息模板
     *
     * @param
     */
    @Override
    public void sendDealMessageTemplate(final EventBean event) {
        final String openId = event.getOpenId();
        final String name = event.getUserName();
        final String time = event.getTime();
        final String eventCode = event.getEventType();
        final String remark = time;

        final Map<String, String> map = new HashMap<>();

        String title = "您好！";
        if (StringUtils.isNotEmpty(name)) {
            title = name + "，" + title;
        }
        map.put("first", title);
        map.put("keyword1", "上报的【" + eventCode + "】内容马上过期！");
        map.put("keyword2", "请尽快处理！");
        map.put("remark", remark);

        final TemplateMessageDTO dto = this.constructTemplateMessage(openId,
                this.templateMisInformation, map);

        final String jsonStr = JsonUtils.pojoToJson(dto);

        WechatApiServiceImpl.LOG.info(String.format("发送模板数据：%s\n", jsonStr));

        this.wechatApiService.sendTemplateMsg(jsonStr);

        final String msg = title + "上报的【" + eventCode + "】内容马上过期！请尽快处理！";

        // this.sendMsg(event.getTel(), "sss");
    }

    private TemplateMessageDTO constructTemplateMessage(final String toUser, final String templateId,
            final Map<String, String> map) {
        final TemplateMessageDTO dto = new TemplateMessageDTO();
        dto.setTouser(toUser);
        dto.setTemplateId(templateId);

        final Map<String, TemplateMessageDTO.TemplateItemDTO> itemMap = new HashMap<>();

        for (final Map.Entry<String, String> entry : map.entrySet()) {
            final String key = entry.getKey();
            final String value = entry.getValue();

            final TemplateMessageDTO.TemplateItemDTO titleItem = new TemplateMessageDTO().new TemplateItemDTO();
            titleItem.setValue(value);
            titleItem.setColor("#173177");
            itemMap.put(key, titleItem);
        }

        dto.setData(itemMap);

        return dto;
    }

    private void sendMsg(final String mobile, final String text) {
        this.smsService.send(mobile, text);
    }
}
