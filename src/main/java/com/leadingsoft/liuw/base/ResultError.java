package com.leadingsoft.liuw.base;


import java.io.Serializable;

public class ResultError implements Serializable {
    private static final long serialVersionUID = 8042733799291115991L;
    private String field;
    private String errmsg;
    private String errcode;

    public ResultError() {
    }

    public ResultError(String errmsg, String field) {
        this.field = field;
        this.errmsg = errmsg;
    }

    public ResultError(String errcode, String errmsg, String field) {
        this.field = field;
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    public String getField() {
        return this.field;
    }

    public String getErrmsg() {
        return this.errmsg;
    }

    public String getErrcode() {
        return this.errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public void setField(String field) {
        this.field = field;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String toString() {
        return String.format("{errcode:%s, errmsg:%s, field:%s}", new Object[]{this.errcode, this.errmsg, this.field});
    }
}
