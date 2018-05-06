package com.hiretaxi.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.dialog.DialogFactory;
import com.hiretaxi.requestHelper.RequestNetDataHelper;
import com.hiretaxi.util.ToastUtil;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ClientCaseActivity extends ToolbarBaseActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 200;
    private View weixinView, qqView, callView;
    public static String qq = "", weiXin = "", phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_case);
        setDefacultTitle("联系我们");

        initView();
        showAnim();
        requestNetData();
    }

    @Override
    protected View getToolbarView() {
        return null;
    }

    private void requestNetData() {
        if (qq.isEmpty() && weiXin.isEmpty() && phoneNumber.isEmpty()) {
            RequestNetDataHelper.requestNetForLianXiData(this);
        }
    }

    private void showAnim() {
        showScaleAnim(weixinView);
        showScaleAnim(qqView);
        showScaleAnim(callView);
    }

    private void initView() {
        weixinView = findView(R.id.weiXin);
        qqView = findView(R.id.qq);
        callView = findView(R.id.call);
    }

    //以view中心缩方的动画
    public void showScaleAnim(View view) {
        PropertyValuesHolder scXHolder = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
        PropertyValuesHolder scYHolder = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
        PropertyValuesHolder alHolder = PropertyValuesHolder.ofFloat("alpha", 0f, 1f);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(view, scXHolder, scYHolder, alHolder);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    public void callMe(View view) {
        if (null == qq || qq.isEmpty()) {
            showToast("暂未提供QQ联系方式");
            return;
        }
        ClipboardManager copy = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ToastUtil.showToast(this, "QQ号复制成功");
        copy.setText(qq);
    }

    public final static String WEIXIN_CHATTING_MIMETYPE = "vnd.android.cursor.item/vnd.com.tencent.mm.chatting.profile";//微信聊天

    public void weiXin(View view) {
        if (null == weiXin || weiXin.isEmpty()) {
            showToast("暂未提供微信联系方式");
            return;
        }
        ClipboardManager copy = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        ToastUtil.showToast(this, "微信号复制成功");
        copy.setText(weiXin);
    }

    public void call(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            callPhone();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CALL_PHONE},
                MY_PERMISSIONS_REQUEST_CALL_PHONE);
    }

    private void callPhone() {
        if (null == phoneNumber || phoneNumber.isEmpty()) {
            showToast("暂未提供电话联系方式");
            return;
        }
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simSer = tm.getSimSerialNumber();
            if (simSer == null || simSer.equals("")) {
                //Toast.makeText(this, "插入SIM卡才能开启此应用", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.call_phone);
                builder.setTitle("插入SIM卡才能开启此应用");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                Uri uri = Uri.parse("tel:" + phoneNumber);   //拨打电话号码的URI格式
                Intent it = new Intent();   //实例化Intent
                it.setAction(Intent.ACTION_DIAL);   //指定Action
                it.setData(uri);   //设置数据
                this.startActivity(it);//启动Acitivity
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                showDialog();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private SweetAlertDialog sweetAlertDialog;
    private void showDialog() {
        if (sweetAlertDialog == null) {
            sweetAlertDialog = DialogFactory.getTwoBtnDialog(this, "权限申请", "需要申请电话拨打的权限",
                    "取消", "同意", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    }, new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            requestPermission();
                        }
                    });
        }
        sweetAlertDialog.show();
    }

}
