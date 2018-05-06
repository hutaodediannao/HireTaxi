package com.hiretaxi;

import android.app.Application;

import com.hiretaxi.config.AppInit;


/**
 * Created by Administrator on 2017/7/3.
 */

public class CarApplication extends Application {

    private static CarApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this);
        instance = this;
    }

    public static CarApplication getInstance() {
        return instance;
    }
}
