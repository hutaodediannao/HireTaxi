package com.hiretaxi.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.dialog.DialogFactory;
import com.hiretaxi.fragment.HomeFragment;
import com.hiretaxi.fragment.IndentFragment;
import com.hiretaxi.fragment.MyIndentFragment;
import com.hiretaxi.fragment.OkIndentFragment;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.ScreenUtils;
import com.hiretaxi.util.TimeUtil;
import com.hiretaxi.util.UserPermissionCheck;

import java.util.ArrayList;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class DetailActivity extends ToolbarBaseActivity {

    private TextView tvStartLocation, tvEndLocation,
            tvPrice, tvDate, tvCarIdCard, tvPerson, tvCarType;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView ivPhoto;

    private Post post;
    private int position;

    private ArrayList<String> pathList;
    private Button callBtn, submitBtn, overIndentBtn;
    private CarUser user;

//    private View identLay, ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setDefacultTitle("订单需求");

        user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            showToast("请登录");
            return;
        }
        post = (Post) getIntent().getSerializableExtra(KEY);
        position = getIntent().getIntExtra(POSITION, -1);

        initView();
        inintList();
        setListener();
        updateUI();
    }

    private void inintList() {
        pathList = new ArrayList<String>();
    }

    private void setListener() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.blue,
                R.color.yellow,
                R.color.green,
                R.color.black
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestNetData();
            }
        });

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pathList == null || pathList.isEmpty() && post.getCarImgFile() == null) {
//                    showToast("图片不存在");
                    return;
                }
                ImageBrowseActivity.startActivity(DetailActivity.this, pathList, 0);
            }
        });

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();//拨打电话
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckPostDialog();
            }
        });
    }

    private SweetAlertDialog waringCheckDialog, waringOverDialog, waringDialog;

    private SweetAlertDialog getWaringCheckDialog() {
        if (waringCheckDialog != null) {
            return waringCheckDialog;
        }
        waringCheckDialog = DialogFactory.getTwoBtnDialog(this, "确定接单吗？", "", "取消", "确定",
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        waringCheckDialog.dismiss();
                    }
                },
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        waringCheckDialog.dismiss();
                        checkPost();
                    }
                });
        return waringCheckDialog;
    }

    private SweetAlertDialog getWaringOverDialog() {
        if (waringOverDialog != null) {
            return waringOverDialog;
        }
        waringOverDialog = DialogFactory.getTwoBtnDialog(this, "确定完成此订单吗？", "", "取消", "确定",
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        waringOverDialog.dismiss();
                    }
                },
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        waringOverDialog.dismiss();
                        //提交数据之前一定要重新查询数据库，
                        // 防止多线程并发出现的问题（也就是可能导致多人同时更新数据库同一条数据的问题，
                        // 必须以最好一次为准）
                        if (!NetUtils.isConnected(DetailActivity.this)) {
                            showToast(Constant.NO_NET_CONNECT);
                            return;
                        }
                        final CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
                        if (carUser == null || post == null || post.getIndentUser() == null ||
                                !post.getIndentUser().getObjectId().equals(carUser.getObjectId())) {
                            showToast("此单已被他人抢现接手，请刷新数据");
                            finish();
                            return;
                        }

                        if (user.getCarType() == null || post.getCarType() == null ||
                                !UserPermissionCheck.checkCarType(post.getCarType())) {
                            showToast("数据异常");
                            finish();
                            return;
                        }

                        loadDialog.show();
                        BmobQuery<Post> query = new BmobQuery<Post>();
                        query.getObject(post.getObjectId(), new QueryListener<Post>() {
                            @Override
                            public void done(Post post, BmobException e) {
                                if (post != null && e == null && post.getIndentUser() != null &&
                                        post.getIndentUser().getObjectId().equals(carUser.getObjectId())) {
                                    //表示此单确实是本人所抢到的
                                    submitOver();
                                } else {
                                    showToast("请求失败，请刷新数据");
                                    loadDialog.dismiss();
                                    finish();
                                }
                            }
                        });
                    }
                });
        return waringOverDialog;
    }

    private void showCheckPostDialog() {
        getWaringCheckDialog().show();
    }

    private void call() {
        CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        if (carUser == null) {
            showToast("请先登录");
            return;
        }
        if (post.getIndentUser() == null) {
            showToast("接单成功才能拨打电话哦");
            return;
        }
        if (post.getIndentUser() != null && !post.getIndentUser().getObjectId().equals(carUser.getObjectId())) {
            showToast("此单已被他人接");
            return;
        }

        if (post.getIndentUser() != null && post.getIndentUser().getObjectId().equals(carUser.getObjectId())) {
            if (post.getTellNumber() == null || post.getTellNumber().isEmpty()) {
                showToast("未提供电话号码");
                return;
            }
            tellPhone();
        }
    }

    private void tellPhone() {
        try {
            TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String simSer = tm.getSimSerialNumber();
            if (simSer == null || simSer.equals("")) {
                //Toast.makeText(this, "插入SIM卡才能开启此应用", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
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
                Uri uri = Uri.parse("tel:" + post.getTellNumber());   //拨打电话号码的URI格式
                Intent it = new Intent();   //实例化Intent
                it.setAction(Intent.ACTION_DIAL);   //指定Action
                it.setData(uri);   //设置数据
                DetailActivity.this.startActivity(it);//启动Acitivity
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkPost() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            return;
        }

        if (post == null) {
            showToast("数据异常，请刷新数据");
            return;
        }

        if (user == null) {
            showToast("请登录");
            return;
        }

        if (user.getValidateOk() == null || !user.getValidateOk().booleanValue()) {
            //表示认证未通过
            showValidateDialog();
            return;
        }

        if (user.getCarType() == null || post.getCarType() == null || !UserPermissionCheck.checkCarType(post.getCarType())) {
            showToast("您的车型不匹配，请重新刷新列表");
            return;
        }

        if (post.getObjectId() == null) {
            showToast("请下拉刷新重试");
            return;
        }

        loadDialog.setTitleText("正在请求服务器");
        loadDialog.show();
        BmobQuery<Post> postBmobQuery = new BmobQuery<Post>();
        postBmobQuery.getObject(post.getObjectId(), new QueryListener<Post>() {
            @Override
            public void done(Post post, BmobException e) {
                if (e == null) {
                    if (post != null) {
                        if (post.getIndentUser() == null) {
                            //表示此单未被接
                            receive(post);
                        } else if (post.getIndentUser().getObjectId().equals(user.getObjectId())) {
                            loadDialog.dismiss();
                            showToast("您已经接了此单，不能重复接");
                        } else {
                            loadDialog.dismiss();
                            showWaringDialog();
                        }
                    } else {
                        loadDialog.dismiss();
                        showToast("数据不存在, 请刷新数据");
                        finish();
                    }
                } else {
                    loadDialog.dismiss();
//                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    private void showWaringDialog() {
        if (waringDialog == null) {
            waringDialog = DialogFactory.getWaringDialog(this, "对不起，此单已被他人接手");
        }
        waringDialog.show();
    }

    //显示一个认证的对话框
    private void showValidateDialog() {
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("您还未通过认证哦!")
                .setContentText("认证通过才可以接单哦")
                .setCancelText("下次吧")
                .setConfirmText("去认证")
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
                start(VerifyActivity.class);
                finish();
            }
        }).show();
    }

    private void receive(final Post post) {
        Post newPost = new Post();
        newPost.setIndentUser(user);
        newPost.update(post.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadDialog.dismiss();
                if (e == null) {
//                    showToast("恭喜，接单成功");
                    submitBtn.setEnabled(false);
                    MainActivity.isNeedRefreash = true;
                    MyIndentFragment.isNeedRefreash = true;
                    HomeFragment.REFREASH_INDEX = position;
                    IndentFragment.NEET_REFREASH_INDEX = 0;//滑动页面到已完成界面
                    DetailActivity.this.post.setIndentUser(BmobUser.getCurrentUser(CarUser.class));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!DetailActivity.this.isFinishing()) {
                                finish();
                            }
                        }
                    }, 500);
                } else {
//                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    private static Handler handler = new Handler();

    private void requestNetData() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            isShowRefreshing(false);
            return;
        }
        isShowRefreshing(true);
        BmobQuery<Post> query = new BmobQuery<Post>();
        //数据库保存这个savePostObjectID便于保存查询使用
        String objectId = post.getObjectId() == null ? post.getSavePostObjectId() : post.getObjectId();
        if (objectId == null) {
            showToast("数据异常");
            finish();
            return;
        }
        query.getObject(objectId, new QueryListener<Post>() {
            @Override
            public void done(Post post, BmobException e) {
                isShowRefreshing(false);
                if (e == null && post != null) {
                    DetailActivity.this.post = post;
                    updateUI();
                } else {
//                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    private void isShowRefreshing(boolean isRefresh) {
        if (isRefresh) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void updateUI() {
        setAppendTextView(tvStartLocation, post.getStartLocation(), "起点：");
        setAppendTextView(tvEndLocation, post.getEndLocation(), "终点：");
        setAppendTextView(tvPrice, post.getPrice(), "价格：");
        setAppendTextView(tvDate, TimeUtil.parseTiemContent(post.getCreatedAt()), "日期：");
        setAppendTextView(tvCarIdCard, post.getCarIdCard(), "车牌号：");
        setAppendTextView(tvPerson, post.getPerson(), "乘车人：");
        setAppendTextView(tvCarType, post.getCarType(), "车型：");

        if (post.getImgUrlCache() != null) {
            BitmapUtil.loadImage2(ivPhoto, post.getImgUrlCache(), this,
                    ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 150));
            pathList.clear();
            pathList.add(post.getImgUrlCache());
        } else if (post.getCarImgFile() != null) {
            BitmapUtil.loadImage2(ivPhoto, post.getCarImgFile().getFileUrl(), this,
                    ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 150));
            pathList.clear();
            pathList.add(post.getCarImgFile().getFileUrl());
        } else {
            setImageViewForType(post.getCarType());
        }

        if (post != null && post.getIndentUser() != null &&
                post.getIndentUser().getObjectId().equals(user.getObjectId())) {
            overIndentBtn.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
            callBtn.setVisibility(View.VISIBLE);
            if (post.getState() != null && post.getState().booleanValue()) {
                overIndentBtn.setEnabled(false);
            } else {
                overIndentBtn.setEnabled(true);
            }
        } else {
            overIndentBtn.setVisibility(View.GONE);
            callBtn.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.VISIBLE);
        }
    }

    private void setImageViewForType(String carType) {
        if (carType == null || carType.trim().isEmpty()) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.jing_ji, this));
        } else if (carType.equals("经济型")) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.jing_ji, this));
        } else if (carType.equals("舒适型")) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.shu_shi, this));
        } else if (carType.equals("商务型")) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.shang_wu, this));
        } else if (carType.equals("豪华型")) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.hao_hua, this));
        } else if (carType.equals("出租车")) {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.taxi, this));
        } else {
            ivPhoto.setImageBitmap(BitmapUtil.getBitmap2(R.mipmap.jing_ji, this));
        }
    }

    private void initView() {
        tvStartLocation = findView(R.id.tvStartLocation);
        tvEndLocation = findView(R.id.tvEndLocation);
        swipeRefreshLayout = findView(R.id.refreashLayout);
        tvPrice = findView(R.id.tvPrice);
        tvDate = findView(R.id.tvDate);
        tvCarIdCard = findView(R.id.tvCarIdCard);
        tvPerson = findView(R.id.tvPerson);
        tvCarType = findView(R.id.tvCarType);
        ivPhoto = findView(R.id.ivPhoto);
        callBtn = findView(R.id.btnCall);
        submitBtn = findView(R.id.btnReceive);
//        identLay = findView(R.id.ident_lay);
        overIndentBtn = findView(R.id.btn_over_ident);
    }

    @Override
    protected View getToolbarView() {
        return null;
    }

    public void overIndent(View view) {
        getWaringOverDialog().show();
    }

    private void submitOver() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            loadDialog.dismiss();
            return;
        }
        CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        if (carUser == null || post == null || post.getIndentUser() == null ||
                !post.getIndentUser().getObjectId().equals(carUser.getObjectId())) {
            showToast("数据错误，请重新刷新数据");
            loadDialog.dismiss();
            return;
        }
        Post newPost = new Post();
        newPost.setState(true);
        newPost.update(post.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                loadDialog.dismiss();
                if (e == null) {
                    showToast("提交完成");
                    MyIndentFragment.REFREASH_INDEX = position;
                    IndentFragment.NEET_REFREASH_INDEX = 1;//滑动页面到已完成界面
                    OkIndentFragment.isNeedRefreash = true;
//                    finish();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!DetailActivity.this.isFinishing()) {
                                finish();
                            }
                        }
                    }, 500);
                } else {
//                    showToast("请求失败");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        isShowRefreshing(false);
        Glide.clear(ivPhoto);
        super.onDestroy();
    }
}
