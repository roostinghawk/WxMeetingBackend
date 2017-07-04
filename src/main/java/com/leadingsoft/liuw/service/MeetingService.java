package com.leadingsoft.liuw.service;

import com.leadingsoft.liuw.model.Meeting;

/**
 * Created by liuw on 2017/6/26.
 */
public interface MeetingService {

    void sendMessage(final Meeting meeting);

    Meeting create(final Meeting meeting, String formId, String creatorName);

    Meeting update(final Meeting meeting, String creatorName);

    void join(final Meeting meeting, String openId, String formId);
}
