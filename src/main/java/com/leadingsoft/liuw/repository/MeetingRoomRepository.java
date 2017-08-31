package com.leadingsoft.liuw.repository;

import com.leadingsoft.liuw.model.Meeting;
import com.leadingsoft.liuw.model.MeetingRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MeetingRoomRepository extends MongoRepository<MeetingRoom, String> {

    List<MeetingRoom> findAll();
}
