package com.leadingsoft.liuw.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liuw on 2017/6/16.
 */
@Getter
@Setter
public class EnrolleeDTO {

    private String id;

    /**
     * 微信的openId
     */
    private String openId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 微信昵称
     */
    private String nickName;
}
