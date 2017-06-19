package com.leadingsoft.liuw.dto.wx;

/**
 * 用户信息
 * <p>
 * {@link http://mp.weixin.qq.com/wiki/1/8a5ce6257f1d3b2afb20f83e72b72ce9.html}
 * </p>
 * 
 * @author user
 */
public class RespUserInfo {
	/* 否订阅该公众号 */
	private int subscribe;
	/* 用户的标识 */
	private String openid;
	private String nickname;
	private int sex;
	private String city;
	private String country;
	private String province;
	private String language;
	private String headimgurl;
	private long subscribe_time;
	private String unionid;
	private String remark;
	private long groupid;

	public int getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public long getSubscribe_time() {
		return subscribe_time;
	}

	public void setSubscribe_time(long subscribe_time) {
		this.subscribe_time = subscribe_time;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public long getGroupid() {
		return groupid;
	}

	public void setGroupid(long groupid) {
		this.groupid = groupid;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("subscribe" + ": " + this.subscribe + "|");
		sb.append("openid" + ": " + this.openid + "|");
		sb.append("nickname" + ": " + this.nickname + "|");
		sb.append("sex" + ": " + this.sex + "|");
		sb.append("city" + ": " + this.city + "|");
		sb.append("country" + ": " + this.country + "|");
		sb.append("province" + ": " + this.province + "|");
		sb.append("language" + ": " + this.language + "|");
		sb.append("headimgurl" + ": " + this.headimgurl + "|");
		sb.append("subscribe_time" + ": " + this.subscribe_time + "|");
		sb.append("unionid" + ": " + this.getUnionid() + "|");
		sb.append("remark" + ": " + this.remark + "|");
		sb.append("groupid" + ": " + this.groupid + "|");
		return sb.toString();
	}

}
