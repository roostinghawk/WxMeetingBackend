package com.leadingsoft.liuw.service.impl;

import com.leadingsoft.liuw.dto.wx.MeetingMessage;
import com.leadingsoft.liuw.dto.wx.MeetingMessageData;
import com.leadingsoft.liuw.dto.wx.MeetingMessageDataDetail;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.AttendeeInfo;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.repository.AttendeeInfoRepository;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.service.MeetingService;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by liuw on 2017/6/26.
 */
@Slf4j
@Service
@Transactional
public class MeetingServiceImpl implements MeetingService {

    @Value("${wxApp.config.templateId}")
    private String templateId;

    @Autowired
    private WechatApiService wechatApiService;
    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private AttendeeInfoRepository attendeeInfoRepository;

    @Override
    public void sendMessage(String meetingId) {
        final Meeting meeting = this.meetingRepository.findOne(meetingId);
        if(meeting == null) {
            log.error(String.format("会议[%s]不存在", meetingId));
            throw new CustomRuntimeException("404", "会议不存在");
        }

        final MeetingMessage meetingMessage = this.initMeetingMessage(meeting);
        for(String openId: meeting.getAttendees()) {

            final AttendeeInfo attendeeInfo = this.attendeeInfoRepository.findOneByOpenIdAndMeetingId(openId, meetingId);
            if(attendeeInfo != null) {
                meetingMessage.setTouser(openId);
                meetingMessage.setForm_id(attendeeInfo.getFormId());
                meetingMessage.setPage("detail?id=" + meeting.getId());

                final String json = JsonUtil.pojoToJson(meetingMessage);
                log.info(json);
                this.wechatApiService.sendTemplateMsg(json);
            }
        }

    }

    @Override
    public Meeting create(final Meeting meeting, final String formId) {
        this.meetingRepository.save(meeting);
        final AttendeeInfo attendeeInfo = new AttendeeInfo();
        attendeeInfo.setOpenId(meeting.getAttendees().get(0));
        attendeeInfo.setFormId(formId);
        attendeeInfo.setMeetingId(meeting.getId());
        this.attendeeInfoRepository.save(attendeeInfo);

        // TODO: 为了测试：发送消息
        this.sendMessage(meeting.getId());

        return meeting;
    }

    @Override
    public void join(Meeting meeting, String openId, String formId) {
        // check是否已加入
        boolean exist = false;
        for(String attendeeOpenId: meeting.getAttendees()){
            if(attendeeOpenId.equals(openId)) {
                return;
            }
        }

        meeting.getAttendees().add(openId);
        this.meetingRepository.save(meeting);

        final AttendeeInfo attendeeInfo = new AttendeeInfo();
        attendeeInfo.setOpenId(openId);
        attendeeInfo.setFormId(formId);
        attendeeInfo.setMeetingId(meeting.getId());
        this.attendeeInfoRepository.save(attendeeInfo);
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
