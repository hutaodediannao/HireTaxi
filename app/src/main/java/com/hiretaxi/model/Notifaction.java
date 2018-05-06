package com.hiretaxi.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import cn.bmob.v3.BmobObject;

/**
 * Created by Administrator on 2017/7/18.
 */

@Entity
public class Notifaction extends BmobObject {

    @Id(autoincrement = true)
    private Long _id;
    private String content;

    public Notifaction() {}

    @Generated(hash = 1100269590)
    public Notifaction(Long _id, String content) {
        this._id = _id;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }
}
