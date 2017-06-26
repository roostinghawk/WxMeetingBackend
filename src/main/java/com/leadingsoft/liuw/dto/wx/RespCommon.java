package com.leadingsoft.liuw.dto.wx;

public class RespCommon {
    private String errcode;
    private String errmsg;

    public String getErrcode() {
        return this.errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append("errcode" + ": " + this.getErrcode() + "|");
        sb.append("errmsg" + ": " + this.getErrmsg() + "|");
        return sb.toString();
    }

}
