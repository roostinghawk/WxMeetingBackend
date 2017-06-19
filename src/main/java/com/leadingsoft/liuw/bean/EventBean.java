package com.leadingsoft.liuw.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/4/11.
 */
@Getter
@Setter
public class EventBean {

    //事件编号
    private String no;

    //事件状态
    private String status;

    //隐患类型
    private String eventType;

    //提交时间
    private Date createdDate;

    //wx openid
    private String openId;

    //上报人姓名
    private String userName;

    //上报时间
    private String time;

    //联系电话
    private String tel;

    //事件主体名称
    private String basePartName;

    //事件主体名称
    private String basePartAddress;

    /**
     * @param personel.name
     */
    private List<PersonnelBean> personel = new ArrayList<>();;
}
