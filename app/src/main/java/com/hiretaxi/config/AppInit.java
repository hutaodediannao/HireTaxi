package com.hiretaxi.config;

import android.content.Context;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobInstallation;

/**
 * Created by Administrator on 2017/7/4.
 */

public class AppInit {

    public static void init(Context context) {
        initBmob(context);
        HttpRequest.init(context);
        initPush(context);
    }

    private static void initBmob(Context context) {
        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config = new BmobConfig.Builder(context)
                ////设置appkey
                .setApplicationId(Constant.APP_ID)
                ////请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                ////文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 100)
                ////文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);
    }

    private static void initPush(Context context) {
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(context);
    }

}
