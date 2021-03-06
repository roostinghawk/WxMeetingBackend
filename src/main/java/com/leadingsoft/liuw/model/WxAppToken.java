package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

/**
 * 微信小程序用Token
 * 
 *
 */
@Setter
@Getter
public class WxAppToken{

    @Id
    private String id;

    /**
     * 用户OpenId
     */
    private String openId;
    
    /**
     * sessionKey
     */
    private String sessionKey;

    /**
     * Token值
     *
     */
    @NotBlank
    @Length(max = 40)
    private String value;

}
