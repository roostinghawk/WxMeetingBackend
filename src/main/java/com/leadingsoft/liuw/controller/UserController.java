package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.service.WxUserService;
import com.leadingsoft.liuw.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuw on 2017/7/3.
 */
@Slf4j
@RestController
@RequestMapping("/w/users")
public class UserController {

    @Autowired
    private WxUserService wxUserService;

    @RequestMapping(value = "/name", method = RequestMethod.GET)
    public String getName(){
        final String openId = SecurityUtils.getCurrentUserLogin();
        return this.wxUserService.getName(openId);
    }
}
