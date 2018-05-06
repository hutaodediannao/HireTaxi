package com.hiretaxi.util;

import android.content.Context;

import com.hiretaxi.config.Constant;
import com.hiretaxi.model.CarUser;

import cn.bmob.v3.BmobUser;

/**
 * 检索当前用户
 * Created by Administrator on 2017/7/31.
 */

public class UserPermissionCheck {

    public static boolean checkUserContent(Context context, CarUser carUser) {
        if (carUser == null) {
            ToastUtil.showToast(context, "请登录");
            return false;
        }
        String nickName = carUser.getNickName();
        String sex = carUser.getSex();
        String mobileNumber = carUser.getMobilePhoneNumber();
        String sosMobileNumber = carUser.getSosPhoneNumber();
        String userIdCardNumber = carUser.getIdCardNumber();
        String driverLicense = carUser.getDriverLicense();
        String carIdNumber = carUser.getCarIdCardNumber();
        String carInsurance = carUser.getCarInsurance();
        String carInfo = carUser.getCarStatu();

        boolean state = true;
        if (StringUtil.isEmpty(nickName)) {
            state = false;
        } else if (StringUtil.isEmpty(sex)) {
            state = false;
        } else if (StringUtil.isEmpty(mobileNumber)) {
            state = false;
        } else if (StringUtil.isEmpty(sosMobileNumber)) {
            state = false;
        } else if (StringUtil.isEmpty(userIdCardNumber)) {
            state = false;
        } else if (StringUtil.isEmpty(driverLicense)) {
            state = false;
        } else if (StringUtil.isEmpty(carIdNumber)) {
            state = false;
        } else if (StringUtil.isEmpty(carInsurance)) {
            state = false;
        } else if (StringUtil.isEmpty(carInfo)) {
            state = false;
        }
        return state;
    }

    public static boolean checkUserContent2(Context context, CarUser carUser) {
        if (carUser == null) {
            ToastUtil.showToast(context, "请登录");
            return false;
        }
        String nickName = carUser.getNickName();
        String sex = carUser.getSex();
        String mobileNumber = carUser.getMobilePhoneNumber();
        String sosMobileNumber = carUser.getSosPhoneNumber();
        String userIdCardNumber = carUser.getIdCardNumber();
        String driverLicense = carUser.getDriverLicense();
        String carIdNumber = carUser.getCarIdCardNumber();
        String carInsurance = carUser.getCarInsurance();
        String carInfo = carUser.getCarStatu();

        boolean state = true;
        if (StringUtil.isEmpty(nickName)) {
            ToastUtil.showToast(context, "昵称不能为空");
            state = false;
        } else if (StringUtil.isEmpty(sex)) {
            ToastUtil.showToast(context, "性别不能为空");
            state = false;
        } else if (StringUtil.isEmpty(mobileNumber)) {
            ToastUtil.showToast(context, "电话号码不能为空");
            state = false;
        } else if (StringUtil.isEmpty(sosMobileNumber)) {
            ToastUtil.showToast(context, "紧急电话号码不能为空");
            state = false;
        } else if (StringUtil.isEmpty(userIdCardNumber)) {
            ToastUtil.showToast(context, "身份证号不能为空");
            state = false;
        } else if (StringUtil.isEmpty(driverLicense)) {
            ToastUtil.showToast(context, "驾照信息不能为空");
            state = false;
        } else if (StringUtil.isEmpty(carIdNumber)) {
            ToastUtil.showToast(context, "车牌号不能为空");
            state = false;
        } else if (StringUtil.isEmpty(carInsurance)) {
            ToastUtil.showToast(context, "保险信息不能为空");
            state = false;
        } else if (StringUtil.isEmpty(carInfo)) {
            ToastUtil.showToast(context, "车况信息不能为空");
            state = false;
        }
        return state;
    }

    /**
     * 检索匹配的车型能够接受到那些车型相关的信息
     */
    public static boolean checkCarType(String carType) {
        if (StringUtil.isEmpty(carType)) {
            return false;
        }

        CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        String myCarType = carUser.getCarType();
        if (StringUtil.isEmpty(myCarType)) {
            return false;
        }

        boolean checkPermission = false;
        if (myCarType.equals(Constant.CAR_TYPE_ARRAY[0])) {
            if (carType.equals(myCarType)) {
                checkPermission = true;
            }
        } else if (myCarType.equals(Constant.CAR_TYPE_ARRAY[1])) {
            if (carType.equals(Constant.CAR_TYPE_ARRAY[0]) ||
                    carType.equals(Constant.CAR_TYPE_ARRAY[1])) {
                checkPermission = true;
            }
        } else if (myCarType.equals(Constant.CAR_TYPE_ARRAY[2])) {
            if (carType.equals(Constant.CAR_TYPE_ARRAY[0]) ||
                    carType.equals(Constant.CAR_TYPE_ARRAY[1]) ||
                    carType.equals(Constant.CAR_TYPE_ARRAY[2])) {
                checkPermission = true;
            }
        } else if (myCarType.equals(Constant.CAR_TYPE_ARRAY[3])) {
            if (carType.equals(Constant.CAR_TYPE_ARRAY[0]) ||
                    carType.equals(Constant.CAR_TYPE_ARRAY[1]) ||
                    carType.equals(Constant.CAR_TYPE_ARRAY[3])) {
                checkPermission = true;
            }
        } else if (myCarType.equals(Constant.CAR_TYPE_ARRAY[4])) {
            if (carType.equals(myCarType)) {
                checkPermission = true;
            }
        }

        return checkPermission;
    }

}
