package com.hiretaxi.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/7/3.
 */
public class Other extends BmobObject {

    private String qq, weiXin, phoneNumber, helpUrl;

    public Other(String qq, String weiXin, String phoneNumber) {
        this.qq = qq;
        this.weiXin = weiXin;
        this.phoneNumber = phoneNumber;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeiXin() {
        return weiXin;
    }

    public void setWeiXin(String weiXin) {
        this.weiXin = weiXin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getHelpUrl() {
        return helpUrl;
    }

    public void setHelpUrl(String helpUrl) {
        this.helpUrl = helpUrl;
    }
}
