package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.service.WxUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuw on 2017/6/21.
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private WxUserService wxUserService;

    @RequestMapping(method = RequestMethod.GET)
    public void get(){
        this.wxUserService.initForApp("xxxxx");
    }
}
