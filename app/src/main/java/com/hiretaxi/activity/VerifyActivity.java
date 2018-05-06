package com.hiretaxi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.model.Authentication;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.ScreenUtils;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class VerifyActivity extends ToolbarBaseActivity implements View.OnClickListener {

    private static final String TAG = "VerifyActivity";
    private ImageView ivAdd0, ivAdd1, ivAdd2, ivAdd3, ivAdd4, ivAdd5;
    private ImageView ivPhoto0, ivPhoto1, ivPhoto2, ivPhoto3, ivPhoto4, ivPhoto5;
    private ProgressBar dowloadProgressBar;
    private TextView tvProgress;
    private View submitBtn, tvAttention;
    private View progressLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        setDefacultTitle("验证");

        updateUI();
        setListener();
    }

    private void setListener() {
        ivAdd0.setOnClickListener(this);
        ivAdd1.setOnClickListener(this);
        ivAdd2.setOnClickListener(this);
        ivAdd3.setOnClickListener(this);
        ivAdd4.setOnClickListener(this);
        ivAdd5.setOnClickListener(this);
    }

    private void updateUI() {
        View view0 = findView(R.id.item0);
        View view1 = findView(R.id.item1);
        View view2 = findView(R.id.item2);
        View view3 = findView(R.id.item3);
        View view4 = findView(R.id.item4);
        View view5 = findView(R.id.item5);
        tvAttention = findView(R.id.tv_attention);
        progressLay = findView(R.id.pregressbarLay);
        ivPhoto0 = (ImageView) view0.findViewById(R.id.iv_photo);
        ivPhoto1 = (ImageView) view1.findViewById(R.id.iv_photo);
        ivPhoto2 = (ImageView) view2.findViewById(R.id.iv_photo);
        ivPhoto3 = (ImageView) view3.findViewById(R.id.iv_photo);
        ivPhoto4 = (ImageView) view4.findViewById(R.id.iv_photo);
        ivPhoto5 = (ImageView) view5.findViewById(R.id.iv_photo);
        ivAdd0 = (ImageView) view0.findViewById(R.id.iv_add);
        ivAdd1 = (ImageView) view1.findViewById(R.id.iv_add);
        ivAdd2 = (ImageView) view2.findViewById(R.id.iv_add);
        ivAdd3 = (ImageView) view3.findViewById(R.id.iv_add);
        ivAdd4 = (ImageView) view4.findViewById(R.id.iv_add);
        ivAdd5 = (ImageView) view5.findViewById(R.id.iv_add);
        ivPhoto0.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.one, this));
        ivPhoto1.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.two, this));
        ivPhoto2.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.three, this));
        ivPhoto3.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.four, this));
        ivPhoto4.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.five, this));
        ivPhoto5.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.six, this));

        dowloadProgressBar = findView(R.id.progressBar);
        tvProgress = findView(R.id.tv_dowload_progress);
        progressLay.setVisibility(View.GONE);
        submitBtn = findView(R.id.btn_submit);

        CarUser user = BmobUser.getCurrentUser(CarUser.class);
        if (user != null && user.getValidateOk() != null && user.getValidateOk().booleanValue()) {
            tvAttention.setVisibility(View.VISIBLE);
            submitBtn.setVisibility(View.GONE);
            View v = findView(R.id.gridLayout);
           v.setVisibility(View.GONE);
        } else {
            showToast("如果在两小时之内你上传过图片，请等待审核结果，否则，您可能未通过审核，请重新上传资料");
            tvAttention.setVisibility(View.GONE);
        }
    }

    @Override
    protected View getToolbarView() {
        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == ivAdd0) {
            startImgSelectActivity(REQUEST_CODE_INDEX[0]);
        } else if (v == ivAdd1) {
            startImgSelectActivity(REQUEST_CODE_INDEX[1]);
        } else if (v == ivAdd2) {
            startImgSelectActivity(REQUEST_CODE_INDEX[2]);
        } else if (v == ivAdd3) {
            startImgSelectActivity(REQUEST_CODE_INDEX[3]);
        } else if (v == ivAdd4) {
            startImgSelectActivity(REQUEST_CODE_INDEX[4]);
        } else if (v == ivAdd5) {
            startImgSelectActivity(REQUEST_CODE_INDEX[5]);
        }
    }


    private void startImgSelectActivity(int index) {
// 自由配置选项
        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
                // 是否多选, 默认true
                .multiSelect(false)
                // 是否记住上次选中记录, 仅当multiSelect为true的时候配置，默认为true
                .rememberSelected(false)
                // “确定”按钮背景色
                .btnBgColor(Color.GRAY)
                // “确定”按钮文字颜色
                .btnTextColor(Color.BLUE)
                // 使用沉浸式状态栏
                .statusBarColor(getResources().getColor(R.color.royalblue))
                // 返回图标ResId
                .backResId(R.drawable.ic_back)
                // 标题
                .title("选择图片")
                // 标题文字颜色
                .titleColor(getResources().getColor(R.color.white))
                // TitleBar背景色
                .titleBgColor(getResources().getColor(R.color.royalblue))
                // 裁剪大小。needCrop为true的时候配置
//                .cropSize(1, 1, DensityUtils.dp2px(this, 200), DensityUtils.dp2px(this, 200))
                .cropSize(1, 1, ScreenUtils.getScreenWidth(this), ScreenUtils.getScreenHeight(this))
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();

        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, index);
    }

    // 自定义图片加载器
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
            BitmapUtil.loadImage2(imageView, path, context, DensityUtils.dp2px(context, 100), DensityUtils.dp2px(context, 100));
        }
    };

    private static final int[] REQUEST_CODE_INDEX = {
            0x00000000, 0x00000001, 0x00000010, 0x00000011, 0x00000100, 0x00000101};

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (data == null) {
            return;
        }
        List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
        ImageView ivIndex = null;
        switch (requestCode) {
            case 0x00000000:
                ivIndex = ivPhoto0;
                if (pathList != null && !pathList.isEmpty()) imgArray[0] = pathList.get(0);
                break;
            case 0x00000001:
                ivIndex = ivPhoto1;
                if (pathList != null && !pathList.isEmpty()) imgArray[1] = pathList.get(0);
                break;
            case 0x00000010:
                ivIndex = ivPhoto2;
                if (pathList != null && !pathList.isEmpty()) imgArray[2] = pathList.get(0);
                break;
            case 0x00000011:
                ivIndex = ivPhoto3;
                if (pathList != null && !pathList.isEmpty()) imgArray[3] = pathList.get(0);
                break;
            case 0x00000100:
                ivIndex = ivPhoto4;
                if (pathList != null && !pathList.isEmpty()) imgArray[4] = pathList.get(0);
                break;
            case 0x00000101:
                ivIndex = ivPhoto5;
                if (pathList != null && !pathList.isEmpty()) imgArray[5] = pathList.get(0);
                break;
        }

        for (String path : pathList) {
            updateImg(path, ivIndex);
        }
    }

    private void updateImg(String path, ImageView iv) {
        BitmapUtil.loadImageForLocalPath(
                iv, path, this,
                DensityUtils.dp2px(this, 50),
                DensityUtils.dp2px(this, 50)
        );
    }

    private String[] imgArray = {null, null, null, null, null, null};

    //点击提交图片集
    public void uploadImgArray(View view) {
        CarUser user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            showToast("请登录");
            return;
        }
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            return;
        }

        int length = imgArray.length;
        for (int i = 0; i < length; i++) {
            if (imgArray[i] == null) {
                showToast("图片" + (1 + i) + "未选择");
                return;
            }
        }

        loadDialog.setTitleText("正在请求服务器");
        loadDialog.show();
        //1.先检查后台数据是否唯一（重点）
        BmobQuery<Authentication> query = new BmobQuery<Authentication>();
        query.addWhereEqualTo(Authentication.USER_KEY, user);
        query.count(Authentication.class, new CountListener() {
            @Override
            public void done(Integer count, BmobException e) {
                if (e == null) {
                    if (count != null) {
                        requestNetData(count);
                    } else {
                        loadDialog.dismiss();
                        showToast("请求失败");
                    }
                } else {
                    loadDialog.dismiss();
                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    /**
     * type表示上传类行0，表示需要保存，1，表示需要更新
     * 插入一条数据
     */
    private void requestNetData(final int type) {
        BmobFile.uploadBatch(imgArray, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list.size() == imgArray.length) {
                    switch (type) {
                        case 0:
                            progressLay.setVisibility(View.VISIBLE);
                            updateAuthentication(list);
                            break;
                        case 1:
                            progressLay.setVisibility(View.VISIBLE);
                            updateOneData(list);
                            break;
                    }
                }
            }

            @Override
            public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                dowloadProgressBar.setProgress(totalPercent);
                tvProgress.setText("上传进度为: " + totalPercent + "%");
            }

            @Override
            public void onError(int i, String s) {
                showToast("上传失败，请稍后重试");
                loadDialog.dismiss();
            }
        });
    }


    //更新一条数据
    private void updateOneData(final List<BmobFile> fileList) {
        final CarUser user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            loadDialog.dismiss();
            return;
        }
        BmobQuery<Authentication> query = new BmobQuery<Authentication>();
        query.addWhereEqualTo(Authentication.USER_KEY, user);
        query.findObjects(new FindListener<Authentication>() {
            @Override
            public void done(List<Authentication> list, BmobException e) {
                if (e == null) {
                    //保证数据唯一
                    if (list != null && list.size() == 1) {
                        update(list.get(0), fileList);
                    } else {
                        loadDialog.dismiss();
                        showToast("请求失败");
                    }
                } else {
                    loadDialog.dismiss();
                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    //开始更新一条数据
    private void update(Authentication authentication, List<BmobFile> list) {
        Authentication newAuthentication = new Authentication();
        newAuthentication.setIdCardImgFile(list.get(0));
        newAuthentication.setCarFontImgFile(list.get(1));
        newAuthentication.setCarBehindImgFile(list.get(2));
        newAuthentication.setDriverLicenceOne(list.get(3));
        newAuthentication.setDriverLicenceTwo(list.get(4));
        newAuthentication.setDocument(list.get(5));
        newAuthentication.update(authentication.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                progressLay.setVisibility(View.GONE);
                if (e == null) {
//                    showToast("更新图片成功");
                    updateUserData();
                } else {
                    loadDialog.dismiss();
                    showToast("上传失败" + e.getErrorCode());
                }
            }
        });
    }

    //上传了新的图片之后，开始更新用户的重新上传的字段
    private void updateUserData() {
        CarUser user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            showToast("请登录");
            return;
        }

        CarUser newUser = new CarUser();
        newUser.setNewUploadImg(new Boolean(true));
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    close();
                } else {
                    loadDialog.dismiss();
                    showToast("请求失败" + e.getErrorCode());
                }
            }
        });
    }

    private void close() {
        showToast("上传成功");
        loadDialog.dismiss();
        submitBtn.setEnabled(false);
        showToast("提交完成，2秒后将会自动关闭，请等待后台验证结果");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private static Handler handler = new Handler();

    //图片上传完成，开始对图片操作
    private void updateAuthentication(List<BmobFile> list) {
        CarUser user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            loadDialog.dismiss();
            return;
        }
        Authentication authentication = new Authentication();
        authentication.setUser(user);
        authentication.setIdCardImgFile(list.get(0));
        authentication.setCarFontImgFile(list.get(1));
        authentication.setCarBehindImgFile(list.get(2));
        authentication.setDriverLicenceOne(list.get(3));
        authentication.setDriverLicenceTwo(list.get(4));
        authentication.setDocument(list.get(5));

        //1.保存一条数据
        authentication.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                progressLay.setVisibility(View.GONE);
                if (e == null) {
//                    close();
                    updateUserData();
                } else {
                    loadDialog.dismiss();
                    showToast("上传失败");
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        Glide.clear(ivPhoto0);
        Glide.clear(ivPhoto1);
        Glide.clear(ivPhoto2);
        Glide.clear(ivPhoto3);
        Glide.clear(ivPhoto4);
        Glide.clear(ivPhoto5);
        super.onDestroy();
    }
}
