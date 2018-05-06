package com.hiretaxi.config;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/3.
 */

public class MyActivityManager {

    private static MyActivityManager instance;
    public static LinkedList<Activity> activityList;

    private static MyActivityManager getInstance() {
        if (instance == null) {
            synchronized (MyActivityManager.class) {
                if (instance == null) {
                    instance = new MyActivityManager();
                }
            }
        }
        return instance;
    }

    private MyActivityManager() {
        if (activityList == null) {
            activityList = new LinkedList<Activity>();
        }
    }

    public static void addActivityToList(Activity activity) {
        getInstance();
        if (null != activity && !activity.isFinishing() && !activityList.contains(activity)) {
            activityList.addLast(activity);
        }
    }

    public static void removeActivityFormList(Activity activity) {
        getInstance();
        if (null != activity && !activity.isFinishing() && activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    //移除不是activity的其他所有activity
    public static void removeOtherActivity(Activity activity) {
        getInstance();
        if (null != activity && !activity.isFinishing() && activityList.contains(activity)) {
            for (Activity a : activityList) {
                if (!a.getClass().getSimpleName().equals(activity.getClass().getSimpleName()) && !a.isFinishing()) {
                    a.finish();
                }
            }
            activityList.clear();
            activityList.add(activity);
        }
    }

    //移除所有activity
    public static void removeAllActivity() {
        getInstance();
        for (Activity a : activityList) {
            if (a != null && !a.isFinishing()) {
                a.finish();
            }
        }
        activityList.clear();
    }

    /* 判断某个界面是否在前台
     * @param activity 要判断的Activity
     * @return 是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        return isForeground(activity, activity.getClass().getName());
    }

    /**
     * 判断某个界面是否在前台
     * @param context   Context
     * @param className 界面的类名
     * @return 是否在前台显示
     */
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }
}
