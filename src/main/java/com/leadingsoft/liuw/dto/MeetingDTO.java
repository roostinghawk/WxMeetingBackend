package com.leadingsoft.liuw.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuw on 2017/6/15.
 */
@Getter
@Setter
public class MeetingDTO {

    private String id;

    private String meetingDate;

    private String meetingTime;

    private String meetingRoorm;

    private String title;

    private String content;

    private List<String> attendees = new ArrayList<String>();
}
