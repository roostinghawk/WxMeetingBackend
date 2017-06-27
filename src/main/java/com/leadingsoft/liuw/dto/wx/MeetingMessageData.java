package com.leadingsoft.liuw.dto.wx;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by liuw on 2017/6/26.
 */
@Getter
@Setter
public class MeetingMessageData implements Serializable {
    private static final long serialVersionUID = -660644602890440345L;
    // 会议主题
    private MeetingMessageDataDetail keyword1;
    // 时间
    private MeetingMessageDataDetail keyword2;
    // 地点
    private MeetingMessageDataDetail keyword3;
    // 参加人员
    private MeetingMessageDataDetail keyword4;

}
