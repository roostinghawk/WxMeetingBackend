package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.dto.MeetingDTO;
import com.leadingsoft.liuw.base.ResultDTO;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.repository.WxUserRepository;
import com.leadingsoft.liuw.service.MeetingService;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import com.leadingsoft.liuw.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private WxUserRepository wxUserRepository;
    @Autowired
    private MeetingService meetingService;


    /**
     * 获取会议一览
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public List<MeetingDTO> list(){
        String openId = SecurityUtils.getCurrentUserLogin();
        WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        List<Meeting> meetings = this.meetingRepository.findByAttendeesContainsOrderByMeetingTimeDesc(wxUser);

        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for(Meeting meeting: meetings) {
            MeetingDTO dto = new MeetingDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
            dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
            dto.setTitle(meeting.getTitle());
            dto.setContent(meeting.getContent());
            for(WxUser user : meeting.getAttendees()){
                dto.getAttendees().add(user.getNickName());
            }
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
        WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        Date today = new Date();
        List<Meeting> meetings = this.meetingRepository.findByAttendeesContainsAndMeetingTimeBetweenOrderByMeetingTimeDesc(
                wxUser, DateTimeUtil.toZeroTime(today), DateTimeUtil.toNextDayZeroTime(today));

        List<MeetingDTO> meetingDTOs = new ArrayList<>();
        for(Meeting meeting: meetings) {
            MeetingDTO dto = new MeetingDTO();
            dto.setId(meeting.getId());
            dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
            dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
            dto.setTitle(meeting.getTitle());
            dto.setContent(meeting.getContent());
            for(WxUser user : meeting.getAttendees()){
                dto.getAttendees().add(user.getNickName());
            }
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
        final Meeting meeting = new Meeting();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            meeting.setMeetingTime(dateFormat.parse(dto.getMeetingDate() + " " + dto.getMeetingTime()));
        } catch (Exception ex) {
            throw new CustomRuntimeException("404", "会议时间格式不正确");
        }
        meeting.setTitle(dto.getTitle());
        meeting.setContent(dto.getContent());
        meeting.setMeetingRoom(dto.getMeetingRoom());
        // 创建者默认参加会议
        String openId = SecurityUtils.getCurrentUserLogin();
        final WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        if(wxUser == null) {
            log.error("创建会议者不存在：" + openId);
            throw new CustomRuntimeException("404", "创建会议者不存在");
        }else {
            // TODO：现在用的是mongo，所以如此存储，如果是关系型数据库，是不行的
            wxUser.setFormId(dto.getFormId());
        }
        meeting.getAttendees().add(wxUser);

        this.meetingRepository.save(meeting);

        // 发送消息
        this.meetingService.sendMessage(meeting.getId());

        return ResultDTO.success(meeting.getId());
    }

    /**
     * 更新会议
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResultDTO<String> update(@RequestBody final MeetingDTO dto) {
        final Meeting meeting = this.meetingRepository.findOne(dto.getId());
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            meeting.setMeetingTime(dateFormat.parse(dto.getMeetingDate() + " " + dto.getMeetingTime()));
        } catch (Exception ex) {
            throw new CustomRuntimeException("404", "会议时间格式不正确");
        }
        meeting.setTitle(dto.getTitle());
        meeting.setContent(dto.getContent());
        meeting.setMeetingRoom(dto.getMeetingTime());

        this.meetingRepository.save(meeting);

        return ResultDTO.success(meeting.getId());
    }


    /**
     * 加入会议
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/join", method = RequestMethod.PUT)
    public ResultDTO<String> join(@PathVariable final String id, @RequestParam final String formId) {
        final Meeting meeting = this.meetingRepository.findOne(id);
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        String openId = SecurityUtils.getCurrentUserLogin();
        final WxUser wxUser = this.wxUserRepository.findOneByOpenIdFromApp(openId);
        if(wxUser == null) {
            log.error("参加会议者不存在：" + openId);
        } else {
            // TODO：现在用的是mongo，所以如此存储，如果是关系型数据库，是不行的
            wxUser.setFormId(formId);
        }
        meeting.getAttendees().add(wxUser);

        this.meetingRepository.save(meeting);

        return ResultDTO.success(meeting.getId());
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
        dto.setTitle(meeting.getTitle());
        dto.setContent(meeting.getContent());
        dto.setMeetingRoom(meeting.getMeetingRoom());
        String openId = SecurityUtils.getCurrentUserLogin();
        for(WxUser wxUser : meeting.getAttendees()){
            dto.getAttendees().add(wxUser.getNickName());
            if(openId.equals(wxUser.getOpenIdFromApp())){
                dto.setJoined(true);
            }
        }

        return ResultDTO.success(dto);
    }
}
