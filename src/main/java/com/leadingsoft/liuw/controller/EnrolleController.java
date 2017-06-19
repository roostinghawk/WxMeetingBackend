package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.model.WxUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuw on 2017/5/9.
 */
@Slf4j
@RestController
@RequestMapping("/w/enrollees")
public class EnrolleController {

//    @Autowired
//    private EnrolleeRepository enrolleeRepository;

    @RequestMapping("/test")
    public void test(){
        WxUser enrollee = new WxUser();
        enrollee.setName("Liuw");
        //enrollee.setWxId("xxx");

//        this.enrolleeRepository.save(enrollee);
    }
}
