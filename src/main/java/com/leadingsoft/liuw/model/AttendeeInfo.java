package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * Created by liuw on 2017/6/28.
 */
@Getter
@Setter
public class AttendeeInfo {

    @Id
    private String id;

    private String meetingId;

    private String openId;

    private String formId;
}
