package com.hiretaxi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.adapter.FragmentAdapter;
import com.hiretaxi.config.MyActivityManager;
import com.hiretaxi.fragment.HomeFragment;
import com.hiretaxi.fragment.IndentFragment;
import com.hiretaxi.fragment.MeFragment;
import com.hiretaxi.fragment.base.BaseFragment;
import com.hiretaxi.helper.BmobPostDataCallbackListener;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.requestHelper.RequestNetDataHelper;
import com.hiretaxi.service.NotifactionPostService;
import com.hiretaxi.util.NetUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity implements BaseFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    public ViewPager viewPager;
    private RadioGroup mRadioGroup;
    private List<BaseFragment> fragmentList;
    private FragmentAdapter fragmentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoLogin();
        initView();
        setAdapter(savedInstanceState);
        setListener();
        requestCacheData();
        startPostNitifactionService();
    }

    //启动一个service后台服务
    private void startPostNitifactionService() {
        Intent service = new Intent(this, NotifactionPostService.class);
        service.setAction(NotifactionPostService.NEW_MSG);
        startService(service);
    }

    //此处订阅接收后端增加对应车型的数据的时候，会发送一条消息提醒
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handReceiveNewMsg(BmobPostDataCallbackListener.PostNewMsg postNewMsg) {
        if (this != null && !this.isFinishing()) {
            //移除通知栏
            BmobPostDataCallbackListener.closeNotifaction();
            viewPager.setCurrentItem(0);
        }
    }

    //加载缓存数据
    private void requestCacheData() {
        //联系人页面缓存
        RequestNetDataHelper.requestNetForLianXiData(this);
    }

    private Handler handler = new Handler();

    private void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rd0:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.rd1:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.rd2:
                        viewPager.setCurrentItem(2);
                        break;
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                    Log.i(TAG, "onPageSelected: postition===> " + position );
                switch (position) {
                    case 0:
                        ((RadioButton) findView(R.id.rd0)).setChecked(true);
                        break;
                    case 1:
                        ((RadioButton) findView(R.id.rd1)).setChecked(true);
                        break;
                    case 2:
                        ((RadioButton) findView(R.id.rd2)).setChecked(true);
                        break;
                }
            }
        });
    }

    private void initView() {
        viewPager = findView(R.id.vp);
        mRadioGroup = findView(R.id.radioGroup);
    }

    private void setAdapter(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            fragmentList = new ArrayList<BaseFragment>();
            fragmentList.add(BaseFragment.newInstance(HomeFragment.class));
            fragmentList.add(BaseFragment.newInstance(IndentFragment.class));
            fragmentList.add(BaseFragment.newInstance(MeFragment.class));
            fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
            viewPager.setAdapter(fragmentAdapter);
            viewPager.setOffscreenPageLimit(fragmentList.size());
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showCancleDialog();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void showCancleDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定要退出吗？")
                .setContentText("")
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                finish();
            }
        }).show();
    }

    public void showLogOutDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("确定要登出吗？")
                .setContentText("将会清除用户信息哦")
                .setCancelText("取消")
                .setConfirmText("确定")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                }).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.cancel();
                BmobUser.logOut();   //清除缓存用户对象
                MyActivityManager.removeAllActivity();
                start(LoginActivity.class);
            }
        }).show();
    }

    public static boolean isNeedRefreash = false;
    public static boolean UPDATE_USER_INFO_CAR_TYPE = false;//用户修改车型之后自动刷新数据


    @Override
    public void onStart() {
        super.onStart();
        if (isNeedRefreash) {
            viewPager.setCurrentItem(1);
            isNeedRefreash = false;
        }

        if (UPDATE_USER_INFO_CAR_TYPE) {
            viewPager.setCurrentItem(0);
            UPDATE_USER_INFO_CAR_TYPE = false;
        }
    }

    private void autoLogin() {
        CarUser trUser = BmobUser.getCurrentUser(CarUser.class);
        if (!NetUtils.isConnected(this)) {
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

}
