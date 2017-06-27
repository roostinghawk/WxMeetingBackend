package com.leadingsoft.liuw.dto.wx;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 模板消息
 * Created by liuw on 2017/6/26.
 */
@Getter
@Setter
public abstract class AbstractMessage implements Serializable {
    private String touser;

    private String template_id;

    // 可选， 跳转page
    private String page;

    private String form_id;

    private Object data;

    private String emphasis_keyword;

}
