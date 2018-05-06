package com.hiretaxi.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.SPUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class StartActivity extends BaseActivity {

    public static final String IS_FIRST_GO_APP = "IS_FIRST_GO_APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CarUser trUser = BmobUser.getCurrentUser(CarUser.class);
                boolean isFirstGoApp = (boolean) SPUtils.get(StartActivity.this, IS_FIRST_GO_APP, true);

                if (isFirstGoApp) {
                    start(SplashActivity.class);
                } else {
                    if (trUser == null) {
                        start(LoginActivity.class);
                    } else {
                        start(MainActivity.class);
//                        autoLogin();
                    }
                }
                        finish();
            }
        }, 1000);
    }

    private void autoLogin() {
        CarUser trUser = BmobUser.getCurrentUser(CarUser.class);
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            return;
        }

        CarUser loginUser = new CarUser();
        loginUser.setUsername(trUser.getUsername());
        loginUser.setPassword(trUser.getUsername());
        loginUser.setObjectId(trUser.getObjectId());
        loginUser.login(new SaveListener<BmobUser>() {
            @Override
            public void done(BmobUser bmobUser, BmobException e) {
                if (e == null) {
//                    showToast("登录成功");
//                    start(MainActivity.class);
                    finish();
                } else {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            autoLogin();
                        }
                    }, 1000 * 2);
                }
            }
        });
    }

    private Handler handler = new Handler();
}
