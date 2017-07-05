package com.leadingsoft.liuw.job;

import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.service.MeetingService;
import com.leadingsoft.liuw.service.WxUserService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 自动解除黑名单（超过30天）
 */
@Slf4j
@Service
@Transactional
public class MeetingNotifyJob {


    // 提前几分钟通知
    private int notifyMiniutes = 10;
    @Autowired
    private MeetingService meetingService;
    @Autowired
    private MeetingRepository meetingRepository;


    @Scheduled(fixedRate = 60000)
    public void sendNotify() {
//        if (MeetingNotifyJob.log.isInfoEnabled()) {
//            MeetingNotifyJob.log.info("会议提醒Job开始...");
//        }

        final Date compareDate = DateTimeUtil.addMinutes(new Date(), 10);
        final List<Meeting> meetings = this.meetingRepository.findByMeetingTimeBeforeAndNotifiedFalse(compareDate);
        MeetingNotifyJob.log.info("待通知会议数量：" + meetings.size());
        for (Meeting meeting: meetings) {
            this.meetingService.sendMessage(meeting);
        }

//        if (MeetingNotifyJob.log.isInfoEnabled()) {
//            MeetingNotifyJob.log.info("会议提醒Job完成...");
//        }

    }
}
