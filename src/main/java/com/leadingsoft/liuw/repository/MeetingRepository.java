package com.leadingsoft.liuw.repository;

import com.leadingsoft.liuw.model.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by liuw on 2017/5/9.
 */
public interface MeetingRepository extends MongoRepository<Meeting, Long> {

    List<Meeting> findAll();

    Meeting findOne(Long id);
}
