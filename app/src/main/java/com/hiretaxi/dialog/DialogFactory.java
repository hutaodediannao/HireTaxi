package com.hiretaxi.dialog;

import android.app.Activity;
import android.content.Context;

import com.hiretaxi.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/6/15.
 */
public class DialogFactory {

    //显示一个加载进度的对话框
    public static SweetAlertDialog getLoadingDialog(Context context, String tips) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.chartreuse));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        return pDialog;
    }

    //显示一个提醒的对话框
    public static SweetAlertDialog getWaringDialog(final Activity activity, String tips) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(activity.getResources().getColor(R.color.chartreuse));
        pDialog.setTitleText(tips);
        pDialog.setCancelable(false);
        pDialog.setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        pDialog.dismiss();
                        activity.finish();
                    }
                });
        return pDialog;
    }

    //显示一个提醒的对话框
    public static SweetAlertDialog getWaringDialog(final Context context, String tips) {
        final SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        pDialog.getProgressHelper().setBarColor(context.getResources().getColor(R.color.chartreuse));
        pDialog.setTitleText(tips);
        pDialog.setCancelable(false);
        return pDialog;
    }

    //创建两个按钮，一个标题头家提示的的dialog
    public static SweetAlertDialog getTwoBtnDialog(Context context, String title, String msg,
                                                   String leftContent, String rightContent,
                                                   SweetAlertDialog.OnSweetClickListener onSweetLeftClickListener,
                                                   SweetAlertDialog.OnSweetClickListener onSweetRightClickListener
    ) {
        SweetAlertDialog dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(title)
                .setContentText(msg)
                .setCancelText(leftContent)
                .setConfirmText(rightContent)
                .setCancelClickListener(onSweetLeftClickListener)
                .setConfirmClickListener(onSweetRightClickListener);
        return dialog;
    }

}
