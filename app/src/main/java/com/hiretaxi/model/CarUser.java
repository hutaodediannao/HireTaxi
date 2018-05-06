package com.hiretaxi.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2017/7/12.
 */

public class CarUser extends BmobUser {

    private String nickName;//昵称
    private String sex;//性别
    private String avatarImgUrl;//图标
    private Authentication authentication;//用户上传的
    private Boolean validateOk;//是否严重通过
    private Boolean isNewUploadImg;//true，表示该用户上传了新的验证图片，否则表示未提交验证
    private String carInsurance;//车险保单
    private String carType;//车辆类型（轿车，SUV，拖拉机。。。等等
    private String driverLicense;//驾驶证
    private String idCardNumber;//身份证
    private String sosPhoneNumber;//紧急电话
    private String carStatu;//车况
    private String carIdCardNumber;//车牌号

    public CarUser() {
    }

    public CarUser(String userName) {
        this.setUsername(userName);
        this.setPassword(userName);
    }

    public CarUser(String nickName, String sex,String carInsurance,
                   String driverLicense, String carIdCardNumber , String sosPhoneNumber,
                   String carStatu,String idCardNumber) {
        this.nickName = nickName;
        this.sex = sex;
        this.carInsurance = carInsurance;
        this.driverLicense = driverLicense;
        this.idCardNumber = idCardNumber;
        this.sosPhoneNumber = sosPhoneNumber;
        this.carStatu = carStatu;
        this.carIdCardNumber = carIdCardNumber;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public Boolean getValidateOk() {
        return validateOk;
    }

    public void setValidateOk(Boolean validateOk) {
        this.validateOk = validateOk;
    }

    public Boolean getNewUploadImg() {
        return isNewUploadImg;
    }

    public void setNewUploadImg(Boolean newUploadImg) {
        isNewUploadImg = newUploadImg;
    }

    public String getCarInsurance() {
        return carInsurance;
    }

    public void setCarInsurance(String carInsurance) {
        this.carInsurance = carInsurance;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

//    public String getDriverAge() {
//        return driverAge;
//    }
//
//    public void setDriverAge(String driverAge) {
//        this.driverAge = driverAge;
//    }

    public String getDriverLicense() {
        return driverLicense;
    }

    public void setDriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    public String getIdCardNumber() {
        return idCardNumber;
    }

    public void setIdCardNumber(String idCardNumber) {
        this.idCardNumber = idCardNumber;
    }

    public String getAvatarImgUrl() {
        return avatarImgUrl;
    }

    public void setAvatarImgUrl(String avatarImgUrl) {
        this.avatarImgUrl = avatarImgUrl;
    }

    public String getSosPhoneNumber() {
        return sosPhoneNumber;
    }

    public void setSosPhoneNumber(String sosPhoneNumber) {
        this.sosPhoneNumber = sosPhoneNumber;
    }

    public String getCarStatu() {
        return carStatu;
    }

    public void setCarStatu(String carStatu) {
        this.carStatu = carStatu;
    }

//    public String getCarAge() {
//        return carAge;
//    }
//
//    public void setCarAge(String carAge) {
//        this.carAge = carAge;
//    }

    public String getCarIdCardNumber() {
        return carIdCardNumber;
    }

    public void setCarIdCardNumber(String carIdCardNumber) {
        this.carIdCardNumber = carIdCardNumber;
    }

    @Override
    public String toString() {
        return "CarUser{" +
                "nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                ", authentication=" + authentication +
                ", validateOk=" + validateOk +
                ", isNewUploadImg=" + isNewUploadImg +
                ", carInsurance='" + carInsurance + '\'' +
                ", carType='" + carType + '\'' +
                ", driverLicense='" + driverLicense + '\'' +
                ", idCardNumber='" + idCardNumber + '\'' +
                '}';
    }
}
