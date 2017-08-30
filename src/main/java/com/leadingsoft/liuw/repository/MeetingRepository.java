package com.leadingsoft.liuw.repository;

import com.leadingsoft.liuw.model.AttendeeInfo;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.model.WxUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by liuw on 2017/5/9.
 */
public interface MeetingRepository extends MongoRepository<Meeting, String> {

    List<Meeting> findAll();

    Meeting findOne(String id);

    List<Meeting> findByAttendeesContainsOrderByMeetingTimeDesc(String openId);

    List<Meeting> findByAttendeesContainsAndMeetingTimeBetweenOrderByMeetingTimeDesc(String openId, Date startTime, Date endTime);

    List<Meeting> findByMeetingTimeAfterOrderByMeetingTimeDesc(Date startTime);

    List<Meeting> findByMeetingRoomAndMeetingTimeAfterOrderByMeetingTimeDesc(String meetingRoom, Date startTime);

    List<Meeting> findByMeetingTimeBeforeAndNotifiedFalse(Date startTime);

    /**
     * 查找重复日期的会议个数
     * @param endTime
     * @param startTime
     * @return
     */
    Long countByMeetingTimeLessThanEqualAndEndTimeGreaterThanEqualAndMeetingRoom(Date endTime, Date startTime, String meetingRoom);
}
