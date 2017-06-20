package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.dto.MeetingDTO;
import com.leadingsoft.liuw.base.ResultDTO;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import com.leadingsoft.liuw.model.WxUser;
import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.repository.MeetingRepository;
import com.leadingsoft.liuw.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;

/**
 * Created by liuw on 2017/5/31.
 */
@Slf4j
@RestController
@RequestMapping("/w/meetings")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    /**
     * 新建会议
     * @param dto
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResultDTO<Long> create(@RequestBody final MeetingDTO dto) {
        final Meeting meeting = new Meeting();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            meeting.setMeetingTime(dateFormat.parse(dto.getMeetingDate() + " " + dto.getMeetingTime()));
        } catch (Exception ex) {
            throw new CustomRuntimeException("404", "会议时间格式不正确");
        }
        meeting.setTitle(dto.getTitle());
        meeting.setContent(dto.getContent());
        meeting.setMeetingRoorm(dto.getMeetingTime());


        return ResultDTO.success(meeting.getId());
    }

    /**
     * 会议详细
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultDTO<MeetingDTO> get(@PathVariable final Long id){
        final Meeting meeting = this.meetingRepository.findOne(id);
        if(meeting == null) {
            throw new CustomRuntimeException("404", "会议不存在");
        }
        MeetingDTO dto = new MeetingDTO();
        dto.setMeetingDate(DateTimeUtil.formatDate(meeting.getMeetingTime(), "yyyy-MM-dd"));
        dto.setMeetingTime(DateTimeUtil.formatDate(meeting.getMeetingTime(), "HH:mm"));
        dto.setTitle(meeting.getTitle());
        dto.setContent(meeting.getContent());
        for(WxUser wxUser : meeting.getAttendees()){
            dto.getAttendees().add(wxUser.getName());
        }

        return ResultDTO.success(dto);
    }
}
