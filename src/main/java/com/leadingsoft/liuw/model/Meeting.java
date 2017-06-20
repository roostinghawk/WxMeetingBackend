package com.leadingsoft.liuw.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by liuw on 2017/6/16.
 */
@Getter
@Setter
public class Meeting  implements Persistable<Long> {

    @Id
    private Long id;

    /**
     * 开始会议时间
     */
    private Date meetingTime;

    /**
     * 会议室
     */
    private String meetingRoorm;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建者
     */
    private WxUser createdBy;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 参会者
     */
    private List<WxUser> attendees = new ArrayList<>();

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return null == this.getId();
    }
}
