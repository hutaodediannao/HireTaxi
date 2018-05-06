package com.hiretaxi.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/7/18.
 */

@Entity
public class ImageBanner extends BmobObject {

    @Id(autoincrement = true)
    private Long _id;
    private String title;
    @Transient
    private BmobFile imgFile;
    private String srcUrl;

    private String imgCacheUrl;//本地保存需要的缓存地址

    public ImageBanner() {
    }

    public ImageBanner(String title, BmobFile imgFile, String srcUrl) {
        this.title = title;
        this.imgFile = imgFile;
        this.srcUrl = srcUrl;
    }

    @Generated(hash = 1599053016)
    public ImageBanner(Long _id, String title, String srcUrl, String imgCacheUrl) {
        this._id = _id;
        this.title = title;
        this.srcUrl = srcUrl;
        this.imgCacheUrl = imgCacheUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BmobFile getImgFile() {
        return imgFile;
    }

    public void setImgFile(BmobFile imgFile) {
        this.imgFile = imgFile;
    }

    public String getSrcUrl() {
        return srcUrl;
    }

    public void setSrcUrl(String srcUrl) {
        this.srcUrl = srcUrl;
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getImgCacheUrl() {
        return imgCacheUrl;
    }

    public void setImgCacheUrl(String imgCacheUrl) {
        this.imgCacheUrl = imgCacheUrl;
    }
}

