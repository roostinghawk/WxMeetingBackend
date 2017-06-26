package com.leadingsoft.liuw.dto.wx;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by liuw on 2017/6/26.
 */
@Getter
@Setter
public class MeetingMessageData {
    // 会议主题
    private MeetingMessageDataDetail keyword1;
    // 时间
    private MeetingMessageDataDetail keyword2;
    // 地点
    private MeetingMessageDataDetail keyword3;
    // 参加人员
    private MeetingMessageDataDetail keyword4;

}
