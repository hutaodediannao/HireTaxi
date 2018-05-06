package com.hiretaxi.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by Administrator on 2017/7/12.
 */

public class Authentication extends BmobObject {

    public static final String USER_KEY = "user";
    private CarUser user;
    private BmobFile idCardImgFile;
    private BmobFile driverLicenceOne;
    private BmobFile driverLicenceTwo;
    private BmobFile document;
    private BmobFile carBehindImgFile;
    private BmobFile carFontImgFile;

    public Authentication() {}

    public CarUser getUser() {
        return user;
    }

    public void setUser(CarUser user) {
        this.user = user;
    }

    public BmobFile getIdCardImgFile() {
        return idCardImgFile;
    }

    public void setIdCardImgFile(BmobFile idCardImgFile) {
        this.idCardImgFile = idCardImgFile;
    }

    public BmobFile getDriverLicenceOne() {
        return driverLicenceOne;
    }

    public void setDriverLicenceOne(BmobFile driverLicenceOne) {
        this.driverLicenceOne = driverLicenceOne;
    }

    public BmobFile getDriverLicenceTwo() {
        return driverLicenceTwo;
    }

    public void setDriverLicenceTwo(BmobFile driverLicenceTwo) {
        this.driverLicenceTwo = driverLicenceTwo;
    }

    public BmobFile getDocument() {
        return document;
    }

    public void setDocument(BmobFile document) {
        this.document = document;
    }

    public BmobFile getCarBehindImgFile() {
        return carBehindImgFile;
    }

    public void setCarBehindImgFile(BmobFile carBehindImgFile) {
        this.carBehindImgFile = carBehindImgFile;
    }

    public BmobFile getCarFontImgFile() {
        return carFontImgFile;
    }

    public void setCarFontImgFile(BmobFile carFontImgFile) {
        this.carFontImgFile = carFontImgFile;
    }
}
