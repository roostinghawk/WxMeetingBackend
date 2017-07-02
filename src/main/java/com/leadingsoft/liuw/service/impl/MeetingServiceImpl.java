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
    public void sendMessage(Meeting meeting) {
        try {
            log.info(String.format("发送会议提醒开始[%s]", meeting.getId()));
            final MeetingMessage meetingMessage = this.initMeetingMessage(meeting);
            for (String openId : meeting.getAttendees()) {

                final AttendeeInfo attendeeInfo = this.attendeeInfoRepository.findOneByOpenIdAndMeetingId(openId, meeting.getId());
                if (attendeeInfo != null) {
                    meetingMessage.setTouser(openId);
                    meetingMessage.setForm_id(attendeeInfo.getFormId());
                    meetingMessage.setPage("pages/detail/detail?id=" + meeting.getId());

                    final String json = JsonUtil.pojoToJson(meetingMessage);
                    if (log.isDebugEnabled()) {
                        log.info(json);
                    }

                    log.info(String.format("发送会议[%s]提醒给用户[%s]开始", meeting.getId(), openId));
                    this.wechatApiService.sendTemplateMsg(json);
                    log.info(String.format("发送会议[%s]提醒给用户[%s]完成", meeting.getId(), openId));
                }
            }

            // 标记为已通知
            meeting.setNotified(true);
            this.meetingRepository.save(meeting);

            log.info(String.format("发送会议提醒完成[%s]", meeting.getId()));
        } catch (Exception ex) {
            log.error("发送提醒异常", ex);
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
        this.sendMessage(meeting);

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
