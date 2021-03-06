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

    private String endTime;

    private String meetingRoom;

    private String title;

    private String content;

    private String creatorName;

    private String createdBy;

    private List<String> attendees = new ArrayList<String>();

    private boolean creator = false;

    private boolean joined = false;

    private String formId;
}
