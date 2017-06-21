package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;


/**
 * Created by liuw on 2017/5/8.
 */
@Getter
@Setter
public class WxUser {

    @Id
    private String id;

    /**
     * 微信的openId
     */
    private String openId;

    /**
     * 小程序App ID
     */
    private String openIdFromApp;

    /**
     * Union ID
     */
    private String unionId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 是否是小程序用户
     */
    private boolean appUser;
}
