package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.dto.MeetingDTO;
import com.leadingsoft.liuw.base.ResultDTO;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.repository.AttendeeInfoRepository;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.repository.WxUserRepository;
import com.leadingsoft.liuw.service.MeetingService;
import com.leadingsoft.liuw.service.WxUserService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by liuw on 2017/5/31.
 */
@Slf4j
@RestController
@RequestMapping("/w/meetings")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private WxUserService wxUserService;
    @Autowired
    private MeetingService meetingService;


    /**
     * 获取会议一览
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<MeetingDTO> list(){
        String openId = SecurityUtils.getCurrentUserLogin();
        List<Meeting> meetings = this.meetingRepository.findByAttendeesContainsOrderByMeetingTimeDesc(openId);

        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for(Meeting meeting: meetings) {
            MeetingDTO dto = new MeetingDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
            dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
            dto.setEndTime(DateTimeUtil.formatDate(meeting.getEndTime(), "HH:mm"));
            dto.setTitle(meeting.getTitle());
            dto.setContent(meeting.getContent());
            dto.setMeetingRoom(meeting.getMeetingRoom());
            dto.setCreatedBy(this.wxUserService.getName(meeting.getCreatedBy()));
            meetingDTOs.add(dto);
        }

        return meetingDTOs;

    }

    /**
     * 获取今天及将来的会议一览
     * @return
     */
    @RequestMapping(value = "/future", method = RequestMethod.GET)
    public List<MeetingDTO> listForFuture(){
        String openId = SecurityUtils.getCurrentUserLogin();
        List<Meeting> meetings = this.meetingRepository.findByAttendeesContainsAndMeetingTimeAfterOrderByMeetingTimeDesc(
                openId, DateTimeUtil.toZeroTime(new Date()));

        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for(Meeting meeting: meetings) {
            MeetingDTO dto = new MeetingDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
            dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
            dto.setEndTime(DateTimeUtil.formatDate(meeting.getEndTime(), "HH:mm"));
            dto.setTitle(meeting.getTitle());
            dto.setContent(meeting.getContent());
            dto.setMeetingRoom(meeting.getMeetingRoom());
            dto.setCreatedBy(this.wxUserService.getName(meeting.getCreatedBy()));
            meetingDTOs.add(dto);
        }

        return meetingDTOs;

    }

    /**
     * 获取今天会议一览
     * @return
     */
    @RequestMapping(value= "/today", method = RequestMethod.GET)
    public List<MeetingDTO> todayList(){
        String openId = SecurityUtils.getCurrentUserLogin();
        Date today = new Date();
        List<Meeting> meetings = this.meetingRepository.findByAttendeesContainsAndMeetingTimeBetweenOrderByMeetingTimeDesc(
                openId, DateTimeUtil.toZeroTime(today), DateTimeUtil.toNextDayZeroTime(today));

        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for(Meeting meeting: meetings) {
            MeetingDTO dto = new MeetingDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
            dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
            dto.setEndTime(DateTimeUtil.formatDate(meeting.getEndTime(), "HH:mm"));
            dto.setTitle(meeting.getTitle());
            dto.setContent(meeting.getContent());
            dto.setCreatedBy(this.wxUserService.getName(meeting.getCreatedBy()));
            meetingDTOs.add(dto);
        }

        return meetingDTOs;

    }

    /**
     * 新建会议
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultDTO<String> create(@RequestBody final MeetingDTO dto) {
        final Meeting meeting = this.checkAndInitMeeting(dto, true);

        // 创建者默认参加会议
        String openId = SecurityUtils.getCurrentUserLogin();
        meeting.setCreatedBy(openId);
        meeting.getAttendees().add(openId);
        this.meetingService.create(meeting, dto.getFormId(), dto.getCreatorName());

        return ResultDTO.success(meeting.getId());
    }

    /**
     * 更新会议
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResultDTO<String> update(@RequestBody final MeetingDTO dto) {
        final Meeting meeting = this.checkAndInitMeeting(dto, false);

        // 只有会议的创建者能够更新
        String openId = SecurityUtils.getCurrentUserLogin();
        if(!openId.equals(meeting.getCreatedBy())){
            throw new CustomRuntimeException("500", "只有创建者能够更新会议");
        }

        this.meetingService.update(meeting, dto.getCreatorName());

        return ResultDTO.success(meeting.getId());
    }

    /**
     * 删除会议
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResultDTO<Void> delete(@PathVariable final String id){
        final Meeting meeting = this.meetingRepository.findOne(id);
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        // 只有会议的创建者能够删除
        String openId = SecurityUtils.getCurrentUserLogin();
        if(!openId.equals(meeting.getCreatedBy())){
            throw new CustomRuntimeException("500", "只有创建者能够删除会议");
        }

        this.meetingRepository.delete(id);

        return ResultDTO.success();
    }


    /**
     * 加入会议
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/join", method = RequestMethod.PUT)
    public ResultDTO<Void> join(@PathVariable final String id, @RequestParam final String formId) {
        final Meeting meeting = this.meetingRepository.findOne(id);
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        String openId = SecurityUtils.getCurrentUserLogin();
        this.meetingService.join(meeting, openId, formId);
        return ResultDTO.success();
    }

    /**
     * 会议详细
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultDTO<MeetingDTO> get(@PathVariable final String id){
        final Meeting meeting = this.meetingRepository.findOne(id);
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        MeetingDTO dto = new MeetingDTO();
        dto.setId(meeting.getId());
        dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
        dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
        dto.setEndTime(DateTimeUtil.formatDate(meeting.getEndTime(), "HH:mm"));
        dto.setTitle(meeting.getTitle());
        dto.setContent(meeting.getContent());
        dto.setMeetingRoom(meeting.getMeetingRoom());
        // 发起者
        dto.setCreatedBy(this.wxUserService.getName(meeting.getCreatedBy()));
        // 是否是创建者
        String openId = SecurityUtils.getCurrentUserLogin();
        if(openId.equals(meeting.getCreatedBy())) {
            dto.setCreator(true);
        }
        for(String attendeeOpenId : meeting.getAttendees()){
            dto.getAttendees().add(this.wxUserService.getName(attendeeOpenId));
            if (openId.equals(attendeeOpenId)) {
                dto.setJoined(true);
            }
        }

        return ResultDTO.success(dto);
    }

    /**
     * check输入是否合法
     * @param dto
     */
    private Meeting checkAndInitMeeting(MeetingDTO dto, boolean isCreate) {
        // 必须check
        if(StringUtils.isEmpty(dto.getMeetingDate())
                || StringUtils.isEmpty(dto.getMeetingTime())
                || StringUtils.isEmpty(dto.getEndTime())
                || StringUtils.isEmpty(dto.getTitle())
                || StringUtils.isEmpty(dto.getMeetingRoom())
                || StringUtils.isEmpty(dto.getContent())
                || StringUtils.isEmpty(dto.getCreatorName())) {
            throw new CustomRuntimeException("param.empty", "请输入必填项目");
        }

        // 会议时间check
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date meetingTime;
        Date endTime;
        try {
            meetingTime = dateFormat.parse(dto.getMeetingDate() + "T" + dto.getMeetingTime());
            endTime = dateFormat.parse(dto.getMeetingDate() + "T" + dto.getEndTime());
        } catch (ParseException e) {
            throw new CustomRuntimeException("time.error", "时间格式不正确");
        }

        if (meetingTime.compareTo(new Date()) <= 0) {
            throw new CustomRuntimeException("time.error", "会议开始时间必须晚于当前时间");
        }

        if (meetingTime.compareTo(endTime) >= 0) {
            throw new CustomRuntimeException("time.error", "会议结束时间必须晚于开始时间");
        }

        final Long conflictCount = this.meetingRepository.countByMeetingTimeLessThanEqualAndEndTimeGreaterThanEqualAndMeetingRoom(
                endTime, meetingTime, dto.getMeetingRoom());
        if ((isCreate && conflictCount > 0) || (!isCreate && conflictCount > 1)) {
            throw new CustomRuntimeException("time.conflict", "会议时间范围与其它预订存在冲突");
        }

        Meeting meeting;
        if (isCreate) {
            meeting = new Meeting();
        } else {
            meeting = this.meetingRepository.findOne(dto.getId());
            if(meeting == null) {
                throw new CustomRuntimeException("404", "会议不存在");
            }
        }

        meeting.setMeetingTime(meetingTime);
        meeting.setEndTime(endTime);
        meeting.setMeetingRoom(dto.getMeetingRoom());
        meeting.setTitle(dto.getTitle());
        meeting.setContent(dto.getContent());

        return meeting;
    }
}
