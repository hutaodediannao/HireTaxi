package com.hiretaxi.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.hiretaxi.helper.BmobPostDataCallbackListener;


public class NotifactionPostService extends Service {

    private static final String TAG = "NotifactionPostService";
    public static final String NEW_MSG = "receiveNewMsg";

    public NotifactionPostService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand: ");

        //启动消息提醒机制
        if (intent != null && intent.getAction() != null && intent.getAction().equals(NEW_MSG)) {
            BmobPostDataCallbackListener.startListener();
        }
        return super.onStartCommand(intent, flags, startId);
    }

}
