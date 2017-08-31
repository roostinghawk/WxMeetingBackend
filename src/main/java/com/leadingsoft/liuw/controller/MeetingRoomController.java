package com.leadingsoft.liuw.controller;

import com.leadingsoft.liuw.dto.MeetingRoomDTO;
import com.leadingsoft.liuw.model.MeetingRoom;
import com.leadingsoft.liuw.repository.MeetingRoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/w/meetingRooms")
public class MeetingRoomController {
    @Autowired
    private MeetingRoomRepository meetingRoomRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<MeetingRoomDTO> getAll(){
        final List<MeetingRoom> meetingRooms = this.meetingRoomRepository.findAll();
        final List<MeetingRoomDTO> dtoList = new ArrayList<>();

        for(MeetingRoom meetingRoom : meetingRooms) {
            final MeetingRoomDTO dto = new MeetingRoomDTO();
            dto.setId(meetingRoom.getId());
            dto.setName(meetingRoom.getName());

            dtoList.add(dto);
        }

        return dtoList;
    }
}
