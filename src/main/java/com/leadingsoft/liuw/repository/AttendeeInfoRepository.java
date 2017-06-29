package com.leadingsoft.liuw.repository;

import com.leadingsoft.liuw.model.AttendeeInfo;
import com.leadingsoft.liuw.model.WxUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * WxUserRepository
 */
public interface AttendeeInfoRepository extends MongoRepository<AttendeeInfo, String> {


    AttendeeInfo findOne(String id);

    AttendeeInfo save(AttendeeInfo model);

    AttendeeInfo findOneByOpenIdAndMeetingId(String openId, String meetingId);

}
