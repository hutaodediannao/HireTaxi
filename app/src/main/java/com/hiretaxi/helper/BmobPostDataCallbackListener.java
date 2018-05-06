package com.hiretaxi.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.hiretaxi.CarApplication;
import com.hiretaxi.R;
import com.hiretaxi.activity.MainActivity;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.StringUtil;
import com.hiretaxi.util.UserPermissionCheck;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ValueEventListener;

/**
 * Created by Administrator on 2017/7/28.
 */

public class BmobPostDataCallbackListener {

    private static BmobRealTimeData rtd;

    public static void startListener() {
        rtd = new BmobRealTimeData();
        // 监听表更新
        rtd.start(new ValueEventListener() {
            @Override
            public void onDataChange(JSONObject data) {
                handlerData(data);
            }

            @Override
            public void onConnectCompleted(Exception ex) {
                Log.i("bmob", "连接成功:" + rtd.isConnected());
                if (rtd.isConnected()) {
                    rtd.subTableUpdate("Post");
                }
            }
        });
    }

    private static void handlerData(JSONObject data) {
        Log.i("bmob", "(" + data.optString("action") + ")" + "数据：" + data);
        if (data.optString("action") != null && data.optString("action").equals("updateTable")) {
            try {
                JSONObject jsonObject = data.getJSONObject("data");
                if (jsonObject == null || jsonObject.toString().isEmpty()) {
                    return;
                }
                if (
//                        jsonObject.has("createdAt")&&
//                        jsonObject.has("updatedAt")&&
//                        !jsonObject.getString("createdAt").equals(jsonObject.getString("updatedAt"))&&
                        !jsonObject.has("indentUser") &&
                                jsonObject.has(Post.CAR_TYPE) &&
                                BmobUser.getCurrentUser(CarUser.class) != null
                        ) {
                    String carType = jsonObject.getString(Post.CAR_TYPE);
                    String myCarType = BmobUser.getCurrentUser(CarUser.class).getCarType();
                    String createTime = jsonObject.getString("createdAt");
                    if (StringUtil.isNotEmpty(carType) &&
                            StringUtil.isNotEmpty(myCarType) &&
                            UserPermissionCheck.checkCarType(carType)) {
                        if (createTime.length() >= 10 && createTime.contains(" ")) {
                            createTime = createTime.substring(createTime.indexOf(" "));
                        }
                        final String finalCreateTime = createTime;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handlerUI(finalCreateTime == null ? "" : finalCreateTime);
                            }
                        }, (long) (Math.random() * 1000 * 10));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static Handler handler = new Handler();

    /**
     * 收到后端数据变化的新车型
     */
    private static void handlerUI(String createTime) {
        //开始播放通知铃声
        showNitifaction();
        SoundPlayer.getInstance().playerMessage(CarApplication.getInstance(), R.raw.post_sound);
        EventBus.getDefault().post(new PostNewMsg(createTime));
    }

    private static final int NOTIFICATION_FLAG = 1;
    private static NotificationManager manager;
    private static Notification notify;

    private static void showNitifaction() {
        PendingIntent pendingIntent = PendingIntent.getActivity(CarApplication.getInstance(), 0, new Intent(CarApplication.getInstance(), MainActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level
        // API16之后才支持
        notify = getNotifaction();
        notify.number += 1;
        manager.notify(NOTIFICATION_FLAG, notify);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示
    }

    private static Notification getNotifaction() {
        if (notify == null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(CarApplication.getInstance(), 0, new Intent(CarApplication.getInstance(), MainActivity.class), 0);
            // 通过Notification.Builder来创建通知，注意API Level
            // API16之后才支持
            notify =
                    new Notification.Builder(CarApplication.getInstance())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker("么么哒")
                            .setContentTitle("有新单了，快抢哦")
                            .setContentText("新单来袭，速抢哦，手慢了就没了哦")
                            .setContentIntent(pendingIntent).setNumber(10).build(); // 需要注意build()是在API
            // level16及之后增加的，API11可以使用getNotificatin()来替代
            notify.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            // 在Android进行通知处理，首先需要重系统哪里获得通知管理器NotificationManager，它是一个系统Service。
            manager = (NotificationManager) CarApplication.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return notify;
    }


    public static void closeNotifaction() {
        if (manager != null) {
            manager.cancelAll();
        }
    }

    /**
     * 新消息收到的内容包装
     */
    public static class PostNewMsg {
        String msgContent;

        public PostNewMsg(String msgContent) {
            this.msgContent = msgContent;
        }

        public String getMsgContent() {
            return msgContent;
        }
    }

}
