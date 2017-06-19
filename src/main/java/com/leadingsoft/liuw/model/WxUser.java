package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import javax.persistence.Column;
//import org.springframework.data.jpa.domain.AbstractPersistable;

//import javax.persistence.Entity;


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
    @Length(max = 30)
    @Column(name = "xcxwxh", length = 30, unique = true, updatable = false, columnDefinition = "varchar(30) COMMENT '小程序App Id'")
    private String openId;

    /**
     * Union ID
     */
    @Length(max = 30)
    @Column(length = 30, unique = true, updatable = false, columnDefinition = "varchar(30) COMMENT 'union id'")
    private String unionId;

    /**
     * 姓名
     */
    @Length(max = 50)
    @Column(name = "xm", length = 50, columnDefinition = "varchar(50) COMMENT '姓名'")
    private String name;

    /**
     * 电话
     */
    @Length(max = 11)
    @Column(name = "dh", length = 11, columnDefinition = "varchar(11) COMMENT '电话'")
    private String mobile;

}
