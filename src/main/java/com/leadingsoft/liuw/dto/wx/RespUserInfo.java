package com.leadingsoft.liuw.dto.wx;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户信息
 * <p>
 * {@link http://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html}
 * </p>
 * 
 * @author user
 */
@Getter
@Setter
public class RespUserInfo {
	/* 否订阅该公众号 */
	private int subscribe;
	/* 用户的标识 */
	private String openId;
	private String nickName;
	private int gender;
	private String language;
	private String city;
	private String province;
	private String country;
	private String avatarUrl;
	private WaterMark watermark;

	private long subscribe_time;
	private String unionid;
	private String remark;
	private long groupid;


}
