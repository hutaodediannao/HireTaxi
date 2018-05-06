package com.hiretaxi.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.hiretaxi.R;


/**
 * Created by Administrator on 2017/6/16.
 */
public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (context == null) {
            return;
        }
        TextView tv = (TextView) LayoutInflater.from(context).inflate(R.layout.toast_fly, null);
        tv.setText(content == null ? "" : content);
        if (toast == null) {
            toast = new Toast(context);
        }

        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, -DensityUtils.dp2px(context, -150));
        toast.setView(tv);
        toast.show();
    }


}
