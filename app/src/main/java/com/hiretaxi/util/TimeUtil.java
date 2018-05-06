package com.hiretaxi.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 统一时间工具类
 * Created by relaysoul on 2016/3/17.
 */
public class TimeUtil {
    public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;

    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }

    /**
     * 将日期信息转换成今天、明天、后天、星期
     *
     * @param date
     * @return
     */
    public static String getDateDetail(String date) {
        Calendar today = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        Date dateNow = new Date();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            today.setTime(df.parse(df.format(dateNow)));
            today.set(Calendar.HOUR, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            target.setTime(df.parse(date));
            target.set(Calendar.HOUR, 0);
            target.set(Calendar.MINUTE, 0);
            target.set(Calendar.SECOND, 0);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        long intervalMilli = target.getTimeInMillis() - today.getTimeInMillis();
        int xcts = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        return showDateDetail(xcts, target);

    }

    /**
     * 将日期差显示为日期或者星期
     *
     * @param xcts
     * @param target
     * @return
     */
    private static String showDateDetail(int xcts, Calendar target) {
        switch (xcts) {
            case 0:
                return TConstant.TODAY;
            case 1:
                return TConstant.TOMORROW;
            case 2:
                return TConstant.AFTER_TOMORROW;
            case -1:
                return TConstant.YESTERDAY;
            case -2:
                return TConstant.BEFORE_YESTERDAY;
            default:
                int dayForWeek = 0;
                dayForWeek = target.get(Calendar.DAY_OF_WEEK);
                switch (dayForWeek) {
                    case 1:
                        return TConstant.SUNDAY;
                    case 2:
                        return TConstant.MONDAY;
                    case 3:
                        return TConstant.TUESDAY;
                    case 4:
                        return TConstant.WEDNESDAY;
                    case 5:
                        return TConstant.THURSDAY;
                    case 6:
                        return TConstant.FRIDAY;
                    case 7:
                        return TConstant.SATURDAY;
                    default:
                        return null;
                }

        }
    }

    /**
     * 把timestamp各式化成yyyy/MM/dd kk:mm:ss
     *
     * @param dateTaken
     * @return
     */
    public static String timeString(long dateTaken) {
        return android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成yyyy/MM/dd
     *
     * @param dateTaken
     * @return
     */
    public static String dateString(long dateTaken) {
        return android.text.format.DateFormat.format("yyyy-MM-dd", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成MM-dd
     *
     * @param dateTaken
     * @return
     */
    public static String dateString2(long dateTaken) {
        return android.text.format.DateFormat.format("MM-dd HH:mm", dateTaken).toString();
    }

    public static String dateString3(long dateTaken) {
        return android.text.format.DateFormat.format("MM-dd", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成hh:mm
     *
     * @param dateTaken
     * @return
     */
    public static String msString(long dateTaken) {
        return android.text.format.DateFormat.format("HH:mm", dateTaken).toString();
    }

    /**
     * 把timestamp各式化成dd天kk小时mm分ss秒
     *
     * @param dateTaken
     * @return
     */
    public static String dateInterval(long dateTaken) {
        if (dateTaken < 0) {
            return "00:00:00";
        }
        long s = dateTaken % 60;
        long m = dateTaken / 60 % 60;
        long h = dateTaken / (60 * 60) % 24;
        DecimalFormat format = new DecimalFormat("00");
        return format.format(h) + ":" + format.format(m) + ":" + format.format(s);
    }

    public static String liveStartTime(long startTime) {
        return android.text.format.DateFormat.format("MM-dd HH:mm", startTime).toString();
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     *
     * @param timeStamp
     * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        long time = (System.currentTimeMillis() - timeStamp) / 1000;
        //LogHelper.title("convertTimeToFormat--->",String.valueOf(time)+"  传进来的时间为："+String.valueOf(timeStamp));
        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time <= 3600 * 24 * 7) {
            return time / (3600 * 24) + "天前";
        } else {
            return timeString(timeStamp);
        }
    }

    /**
     * 获得服务器时间 s
     *
     * @return
     */
//    public static long getServerTime() {
////        return System.currentTimeMillis() / 1000 - Constants.TIME_DIFF;
//        return 0;
//    }

    /**
     * 获得服务器时间
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
//    public static String getFormateServerTime() {
//        System.setProperty("user.timezone", "Asia/Shanghai");
//        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
//        TimeZone.setDefault(tz);
//        long serverTime = System.currentTimeMillis() / 1000 - Constants.TIME_DIFF;
//        String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(serverTime * 1000));
//        return date;
//    }

    public interface TConstant {
        /**
         * 日期
         */
        String TODAY = "今天";
        String YESTERDAY = "昨天";
        String TOMORROW = "明天";
        String BEFORE_YESTERDAY = "前天";
        String AFTER_TOMORROW = "后天";
        String SUNDAY = "星期日";
        String MONDAY = "星期一";
        String TUESDAY = "星期二";
        String WEDNESDAY = "星期三";
        String THURSDAY = "星期四";
        String FRIDAY = "星期五";
        String SATURDAY = "星期六";
    }

    public static String parseTiemContent(String timeContent) {
        if (timeContent == null || timeContent.length() < 11) {
            return "";
        }
        return timeContent.substring(0, 11);
    }

}
