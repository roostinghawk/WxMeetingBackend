package com.leadingsoft.liuw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Created by liuw on 2017/6/27.
 */
@Slf4j
@RestController
@RequestMapping("/w/messages")
public class MessageController {

    /**
     * 接收普通消息
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce 随机数
     * @param xmlData xml消息体
     */
    @RequestMapping(method = RequestMethod.POST)
    public String receiveMessage(@RequestParam final String signature,
                                 @RequestParam final String timestamp,
                                 @RequestParam final String nonce,
                                 @RequestBody final String xmlData) {

        if(log.isDebugEnabled()) {
            log.debug(String.format("微信推送消息\n%s", xmlData));
        }

//        if (!this.wechatApiService.checkSignature(signature, timestamp, nonce)) {
//            return "";
//        }
//
//        final AbstractMessage inMessage = this.wechatApiService.parseXml(xmlData);
//        WechatCallbackController.LOG.info(String.format("XML消息解析\n%s", inMessage));
//
//        final RespUserInfo respUserInfo = this.wechatApiService.getUserInfo(inMessage.getFromUserName());
//
//        final MsgType msgType = inMessage.getMsgType();
//        String reply = "";
//        switch (msgType) {
//            case text:
//                reply = this.wechatMessageService.handleText((TextMessage) inMessage, respUserInfo);
//                break;
//            case image:
//                //TODO
//                break;
//            case voice:
//                //TODO
//                break;
//            default:
//                break;
//        }

//        if(log.isDebugEnabled()) {
//            log.debug(String.format("回复微信消息\n%s", reply));
//        }
//
//        return reply;

        return "";
    }
}
