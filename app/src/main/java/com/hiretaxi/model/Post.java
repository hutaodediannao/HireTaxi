package com.hiretaxi.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/7/12.
 */

/**
 * 订单
 */
@Entity
public class Post extends BmobObject {

    @Transient
    public static final String INDENT_USER = "indentUser";
    @Transient
    public static final String STATE = "state";
    @Transient
    public static final String TITLE = "title";
    @Transient
    public static final String CAR_TYPE = "carType";

    @Id(autoincrement = true)
    private Long _id;
    private String saveDbObjectId;//保存在数据库中标记被那个司机报名的userObjectID
    private String savePostObjectId;//保存自己的objectID便于在detail页面查询
    private String saveUpdateTime;//保存自己的updateTime

    private String title;//标题
    private String startLocation;//开始位置
    private String endLocation;//结束位置
    private String price;//价格
    @Transient
    private CarUser indentUser;//接单人
    private String person;//乘客
    private String carType;//汽车类型
    @Transient
    private BmobFile carImgFile;//汽车图片
    private Boolean state;//订单详情（如：已结单，已完成。等等）
    private String carIdCard;//车牌号
    private String tellNumber;//电话号码

    private String imgUrlCache;//本地缓存数据图片

    public Post() {}

    @Generated(hash = 1135712236)
    public Post(Long _id, String saveDbObjectId, String savePostObjectId,
            String saveUpdateTime, String title, String startLocation,
            String endLocation, String price, String person, String carType,
            Boolean state, String carIdCard, String tellNumber,
            String imgUrlCache) {
        this._id = _id;
        this.saveDbObjectId = saveDbObjectId;
        this.savePostObjectId = savePostObjectId;
        this.saveUpdateTime = saveUpdateTime;
        this.title = title;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.price = price;
        this.person = person;
        this.carType = carType;
        this.state = state;
        this.carIdCard = carIdCard;
        this.tellNumber = tellNumber;
        this.imgUrlCache = imgUrlCache;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTellNumber() {
        return tellNumber;
    }

    public void setTellNumber(String tellNumber) {
        this.tellNumber = tellNumber;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public CarUser getIndentUser() {
        return indentUser;
    }

    public void setIndentUser(CarUser indentUser) {
        this.indentUser = indentUser;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public BmobFile getCarImgFile() {
        return carImgFile;
    }

    public void setCarImgFile(BmobFile carImgFile) {
        this.carImgFile = carImgFile;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getCarIdCard() {
        return carIdCard;
    }

    public void setCarIdCard(String carIdCard) {
        this.carIdCard = carIdCard;
    }

    public String getSaveDbObjectId() {
        return saveDbObjectId;
    }

    public void setSaveDbObjectId(String saveDbObjectId) {
        this.saveDbObjectId = saveDbObjectId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "saveDbObjectId='" + saveDbObjectId + '\'' +
                ", title='" + title + '\'' +
                ", carType='" + carType + '\'' +
                ", state=" + state +
                '}';
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getSavePostObjectId() {
        return savePostObjectId;
    }

    public void setSavePostObjectId(String savePostObjectId) {
        this.savePostObjectId = savePostObjectId;
    }

    public String getSaveUpdateTime() {
        return saveUpdateTime;
    }

    public void setSaveUpdateTime(String saveUpdateTime) {
        this.saveUpdateTime = saveUpdateTime;
    }

    public String getImgUrlCache() {
        return imgUrlCache;
    }

    public void setImgUrlCache(String imgUrlCache) {
        this.imgUrlCache = imgUrlCache;
    }
}
