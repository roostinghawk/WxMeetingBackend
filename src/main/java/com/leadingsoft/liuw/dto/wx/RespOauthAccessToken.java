package com.leadingsoft.liuw.dto.wx;

public class RespOauthAccessToken {
    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

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

    public String getRefresh_token() {
        return this.refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return this.scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return this.unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("access_token" + ": " + this.getAccess_token() + "|");
        sb.append("expires_in" + ": " + this.getExpires_in() + "|");
        sb.append("refresh_token" + ": " + this.getRefresh_token() + "|");
        sb.append("openid" + ": " + this.getOpenid() + "|");
        sb.append("scope" + ": " + this.getScope() + "|");
        sb.append("unionid" + ": " + this.getUnionid() + "|");
        return sb.toString();
    }

}
