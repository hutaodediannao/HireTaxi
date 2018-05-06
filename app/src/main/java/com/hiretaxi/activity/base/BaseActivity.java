package com.hiretaxi.activity.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hiretaxi.config.MyActivityManager;
import com.hiretaxi.dialog.DialogFactory;
import com.hiretaxi.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/7/11.
 */

public class BaseActivity extends AppCompatActivity {

    public SweetAlertDialog loadDialog;
    public SweetAlertDialog logOutDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyActivityManager.addActivityToList(this);
        retisterEventBut(true);

        initLoadDialog();
        initLogoutDialog();
    }

    private void initLoadDialog() {
        if (loadDialog == null) {
            loadDialog = DialogFactory.getLoadingDialog(this, "加载中");
        }
    }

    private void initLogoutDialog() {
        logOutDialog = DialogFactory.getTwoBtnDialog(this, "确定退出吗?", "将会退出app哦", "取消", "确定", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                logOutDialog.dismiss();
            }
        }, new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                logOutDialog.dismiss();
                MyActivityManager.removeAllActivity();
            }
        });
        logOutDialog.setCanceledOnTouchOutside(false);
        logOutDialog.setCancelable(false);
    }

    public void showDialog(String content) {
        if (loadDialog == null) {
            loadDialog = DialogFactory.getLoadingDialog(this, content == null ? "加载中" : content);
        }
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    public void closeDialog() {
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    private void retisterEventBut(boolean regist) {
        if (regist) {
            if (!EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().register(this);
            }
        } else {
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }
        }
    }

    @Override
    protected void onDestroy() {
        MyActivityManager.removeActivityFormList(this);
        super.onDestroy();
        retisterEventBut(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onUserEvent(String str) {

    }

    public void start(Class activityCla) {
        this.startActivity(new Intent(this, activityCla));
    }

    public static final String KEY = "key";

    public void start(Activity a, String parmas) {
        Intent intent = new Intent(this, a.getClass());
        intent.putExtra(KEY, parmas);
        startActivity(intent);
    }

    public void start(Class act, Object o) {
        if (o instanceof Serializable) {
            Intent intent = new Intent(this, act);
            intent.putExtra(KEY, (Serializable) o);
            start(intent);
        }
    }

    public static final String POSITION = "position";
    public void start(Class act, Object o, int position) {
        if (o instanceof Serializable) {
            Intent intent = new Intent(this, act);
            intent.putExtra(KEY, (Serializable) o);
            intent.putExtra(POSITION, position);
            start(intent);
        }
    }

    public void start(Intent intent) {
        startActivity(intent);
    }

    public void showToast(String content) {
        ToastUtil.showToast(this, content);
    }

    public void setTextView(TextView textView, String content) {
        if (textView == null) {
            return;
        }
        textView.setText(content == null ? "" : content);
    }

    public void setAppendTextView(TextView textView, String content, String appendContent) {
        if (textView == null) {
            return;
        }
        textView.setText((appendContent == null ? "" : appendContent) + (content == null ? "" : content));
    }

    public void setTextView(int textViewId, String content) {
        TextView textView = findView(textViewId);
        textView.setText(content == null ? "" : content);
    }

    public <T> T findView(int viewId) {
        View view = findViewById(viewId);
        return (T) view;
    }
}
