package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.dto.wx.*;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.AttendeeInfo;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuw on 2017/5/9.
 */
@Slf4j
@RestController
@RequestMapping("/w/tests")
public class TestController {

    @Value("${wxApp.config.templateId}")
    private String templateId;

    @Autowired
    private WechatApiService wechatApiService;
    @Autowired
    private MeetingRepository meetingRepository;

    @RequestMapping("/{meetingId}")
    public void test(@PathVariable final String meetingId){
        final Meeting meeting = this.meetingRepository.findOne(meetingId);

        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }

        final MeetingMessage meetingMessage = this.initMeetingMessage(meeting);
        for(AttendeeInfo attendeeInfo: meeting.getAttendees()) {
            meetingMessage.setTouser(attendeeInfo.getOpenId());
            meetingMessage.setForm_id(attendeeInfo.getFormId());
            meetingMessage.setPage("detail?id=" + meeting.getId());

            final String json = JsonUtil.pojoToJson(meetingMessage);
            log.info(json);
            this.wechatApiService.sendTemplateMsg(json);
        }


    }

    private MeetingMessage initMeetingMessage(final Meeting meeting) {
        final MeetingMessage meetingMessage = new MeetingMessage();
        meetingMessage.setTemplate_id(this.templateId);

        final MeetingMessageData data = new MeetingMessageData();
        data.setKeyword1(new MeetingMessageDataDetail("#173177", meeting.getTitle()));
        data.setKeyword2(new MeetingMessageDataDetail("#173177", DateTimeUtil.formatDate(
                meeting.getMeetingTime(), "yyyy-MM-dd HH:mm")));
        data.setKeyword3(new MeetingMessageDataDetail("#173177", meeting.getMeetingRoom()));
        data.setKeyword4(new MeetingMessageDataDetail("#173177", meeting.getTitle()));

        meetingMessage.setData(data);
        meetingMessage.setEmphasis_keyword("keyword1.DATA");

        return meetingMessage;
    }
}
