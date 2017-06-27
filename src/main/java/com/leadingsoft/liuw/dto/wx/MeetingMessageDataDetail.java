package com.leadingsoft.liuw.dto.wx;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by liuw on 2017/6/26.
 */
@Getter
@Setter
public class MeetingMessageDataDetail implements Serializable {
    private static final long serialVersionUID = -5886256600903646506L;
    private String color;
    private String value;

    public MeetingMessageDataDetail(String color, String value) {
        this.color = color;
        this.value = value;
    }
}
