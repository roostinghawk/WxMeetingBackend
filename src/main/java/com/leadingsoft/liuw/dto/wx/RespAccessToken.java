package com.leadingsoft.liuw.dto.wx;

import java.util.Date;

public class RespAccessToken {
    private String access_token;
    private int expires_in;

    /* 获取时间，配合expires_in计算过期时间 */
    private long get_in = new Date().getTime() / 1000;

    public String getAccess_token() {
        return this.access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return this.expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public long getGet_in() {
        return this.get_in;
    }

    public void setGet_in(long get_in) {
        this.get_in = get_in;
    }

    public boolean isExpire() {
        final long inteval = ((new Date().getTime() / 1000) - this.get_in);
        System.out.println("access_token expired after " + (this.expires_in - inteval) + " seconds later.");
        return inteval > this.expires_in;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("access_token" + ": " + this.getAccess_token() + "|");
        sb.append("expires_in" + ": " + this.getExpires_in() + "|");
        return sb.toString();
    }

}
