package com.leadingsoft.liuw.job;

//import com.leadingsoft.bizfuse.quartz.core.annotition.Job;
//import com.leadingsoft.bizfuse.quartz.core.annotition.JobMapping;
//import com.leadingsoft.bizfuse.quartz.core.annotition.SimpleTrigger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 自动解除黑名单（超过30天）
 */
@Slf4j
//@Job
@Service
@Transactional
public class MeetingNotifyJob {

//    @Autowired
//    private WxUserRepository wxUserRepository;
//    @Autowired
//    private WxUserService wxUserService;
//
//    @JobMapping(id = "AutoBlacklistJob.removeBlack")
//    // TODO：上线时改为每小时执行一次
//    @SimpleTrigger(repeatInterval = 3600 * 1000)
////    @SimpleTrigger(repeatInterval = 60 * 1000 * 10)
//    public void removeBlack() {
//        if (MeetingNotifyJob.log.isInfoEnabled()) {
//            MeetingNotifyJob.log.info("自动解除黑名单开始...");
//        }
//        Date now = new Date();
//
//        // 取得所有超过30天的黑名单用户
//        List<WxUser> blackUsers = this.wxUserRepository.findAllByBlackAndBlackTimeLessThan(true, DateUtils.addDays(now, -30));
//
//        //调用解除方法解除黑名单
//        for (WxUser blackUser:blackUsers) {
//            this.wxUserService.removeFromBlack(blackUser.getId());
//        }
//
//
//        if (MeetingNotifyJob.log.isInfoEnabled()) {
//            MeetingNotifyJob.log.info("自动解除黑名单结束...");
//        }
//
//    }
}
