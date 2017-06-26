package com.leadingsoft.liuw.service.impl;

import com.leadingsoft.liuw.dto.wx.MeetingMessage;
import com.leadingsoft.liuw.dto.wx.MeetingMessageData;
import com.leadingsoft.liuw.dto.wx.MeetingMessageDataDetail;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.service.MeetingService;
import com.leadingsoft.liuw.service.WechatApiService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by liuw on 2017/6/26.
 */
@Slf4j
@Service
public class MeetingServiceImpl implements MeetingService {

    @Value("${wxApp.config.templateId}")
    private String templateId;

    @Autowired
    private WechatApiService wechatApiService;
    @Autowired
    private MeetingRepository meetingRepository;

    @Override
    public void sendMessage(String meetingId) {
        final Meeting meeting = this.meetingRepository.findOne(meetingId);

        log.error(String.format("会议[%s]不存在", meetingId));
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }

        final MeetingMessage meetingMessage = this.initMeetingMessage(meeting);
        for(WxUser wxUser: meeting.getAttendees()) {
            meetingMessage.setTouser(wxUser.getOpenIdFromApp());
            meetingMessage.setForm_id(wxUser.getFormId());
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
