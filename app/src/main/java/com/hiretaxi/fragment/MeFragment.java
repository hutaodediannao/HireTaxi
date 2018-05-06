package com.hiretaxi.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ecloud.pulltozoomview.PullToZoomScrollViewEx;
import com.hiretaxi.R;
import com.hiretaxi.activity.ClientCaseActivity;
import com.hiretaxi.activity.MainActivity;
import com.hiretaxi.activity.MessageActivity;
import com.hiretaxi.activity.UserInfoActivity;
import com.hiretaxi.activity.VerifyActivity;
import com.hiretaxi.activity.WebActivity;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.config.MyActivityManager;
import com.hiretaxi.fragment.base.BaseFragment;
import com.hiretaxi.helper.SoundPlayer;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.Notifaction;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.SPUtils;
import com.hiretaxi.util.ScreenUtils;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

import cn.bmob.push.PushConstants;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.hiretaxi.config.Constant.DEFACULT_IMG_URL;
import static com.hiretaxi.dao.NotifactionUtil.getNotifactionUtilInstance;

public class MeFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MeFragment";
    private CircleImageView headerIv;
    private ImageView baIv;
    private TextView tvName, tvDowloadPregress;
    private View etView, tvAttention, tvCancle, tvMsg, newMsgView, tvHelp, tvLianXi;
    private CarUser user;
    private PullToZoomScrollViewEx scrollViewEx;
    public static final String HAS_NEW_NOTIFACTION_MSG = "hasNewMsg";

    @Override
    protected int getChildFragmentLayout() {
        return R.layout.fragment_me;
    }

    @Override
    protected void handViewEvent(View fragmentRootview) {
        initView();
        setListener();
        updateUserAvator();//更新用户信息显示

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initSnackbar();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateNickName();
        updateNewMsgViewBar();
    }

    private void updateNewMsgViewBar() {
        boolean isHasNewMsg = (boolean) SPUtils.get(this.getContext(), HAS_NEW_NOTIFACTION_MSG, false);
        if (isHasNewMsg) {
            newMsgView.setVisibility(View.VISIBLE);
        } else {
            newMsgView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Glide.clear(headerIv);
        Glide.clear(baIv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerReceiveNewMsg(Boolean isSaveOk) {
        if (this != null && !this.getActivity().isFinishing() && isSaveOk) {
            //该界面不在前台显示才通知
            if (!MyActivityManager.isForeground(getContext(), MessageActivity.class.getName())) {
                newMsgView.setVisibility(View.VISIBLE);
                showMsgBarTips();
            }
        }
    }

    private void showMsgBarTips() {
        snackBar.show();
    }

    private TSnackbar snackBar;

    @NonNull
    private TSnackbar initSnackbar() {
        snackBar = TSnackbar.make(fragmentRootview, "收到一条新消息", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setAction("查看", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMsgActivity();
            }
        });
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.setBackgroundColor(R.color.royalblue);
        snackBar.addIcon(R.mipmap.appicon, DensityUtils.dp2px(getContext(), 120), DensityUtils.dp2px(getContext(), 120));
        return snackBar;
    }

    public static class MyPushMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
                Log.i("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
                try {
                    Notifaction notifaction = new Notifaction();
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("msg"));
                    notifaction.setContent(jsonObject.getString("alert"));
                    Long _id = new Date().getTime();
                    notifaction.set_id(_id);
                    Log.i("bmob", "onReceive: isSaveOk===> " + 1);
                    //检查是否存在该条数据
                    boolean isHasThisData = checkNotifactionData(context, notifaction);
                    if (!isHasThisData) {
                        //将数据保存在数据库
                        boolean isSaveOk = getNotifactionUtilInstance(context).insertNotifaction(notifaction);
                        SPUtils.put(context, HAS_NEW_NOTIFACTION_MSG, true);
                        EventBus.getDefault().post(isSaveOk);
                        //开始播放通知铃声
                        SoundPlayer.getInstance().playerMessage(context, R.raw.msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private boolean checkNotifactionData(Context context, Notifaction notifaction) {
            List<Notifaction> list = getNotifactionUtilInstance(context).queryAllNotifaction();
            if (list == null || list.isEmpty()) {
                return false;
            }
            for (Notifaction notifactionIndex : list) {
                if (null != notifactionIndex && notifactionIndex.getContent() != null
                        && notifaction != null && notifaction.getContent() != null
                        && notifactionIndex.getContent().equals(notifaction.getContent())) {
                    return true;
                }
            }
            return false;
        }
    }

    //更新用户信息
    private void updateUserAvator() {
        user = BmobUser.getCurrentUser(CarUser.class);
        String imgPath = (String) SPUtils.get(getContext(), IMG_FILE_KEY, "");
        String cacheImgPath = Constant.DEFACULT_HEADRE_IMG_URL;

        if (user == null) {
            setHeader(imgPath);
        } else {
            if (user.getAvatarImgUrl() != null) {
                imgPath = user.getAvatarImgUrl();
                BitmapUtil.loadImage4(headerIv, imgPath, getContext());
                BitmapUtil.loadBlurTransImage2(baIv, imgPath, getContext(), ScreenUtils.getScreenWidth(getContext()), DensityUtils.dp2px(getContext(), 200));
                SPUtils.put(getContext(), IMG_FILE_KEY, imgPath);
            } else {
                if (cacheImgPath != null) {
                    SPUtils.put(getContext(), IMG_FILE_KEY, cacheImgPath);
                }
                setHeader(imgPath);
            }
        }
    }

    public static final String NICK_KEY = "nick_key";

    private void updateNickName() {
        user = BmobUser.getCurrentUser(CarUser.class);
        String nickName = Constant.DEFACULT_NICK_NAME;
        if (nickName != null) {
            SPUtils.put(getContext(), NICK_KEY, nickName);
        }

        if (user == null) {
            tvName.setText(nickName == null ? "" : nickName);
            return;
        }

        if (user.getNickName() != null) {
            nickName = user.getNickName();
        } else {
            nickName = SPUtils.get(getContext(), NICK_KEY, "").toString();
            if (nickName.isEmpty()) {
                nickName = "这个人很懒，没有写昵称";
            } else {
                nickName = new String(nickName.getBytes(Charset.forName("ISO-8859-1")));
            }
        }
        tvName.setText(nickName);
    }

    private void setHeader(String imgPath) {
        if (imgPath.isEmpty()) {
            BitmapUtil.loadImage(headerIv, DEFACULT_IMG_URL, getContext());
            BitmapUtil.loadBlurTransImage2(baIv, DEFACULT_IMG_URL, getContext(),
                    ScreenUtils.getScreenWidth(getContext()),
                    DensityUtils.dp2px(getContext(), 200));
        } else {
            updateImg(imgPath);
        }
    }

    private void initView() {
        scrollViewEx = findView(R.id.sc);
        ImageView zoomView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.zoom_zoom, null);
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.zoom_header, null);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.zoom_content, null);
        scrollViewEx.setHeaderView(headView);
        scrollViewEx.setScrollContentView(contentView);
        scrollViewEx.setZoomView(zoomView);

        headerIv = (CircleImageView) headView.findViewById(R.id.iv_header);
        tvName = (TextView) headView.findViewById(R.id.tv_name);
        baIv = (ImageView) zoomView.findViewById(R.id.iv_bg);

        tvDowloadPregress = findView(R.id.tv_dowload_progress);
        etView = findView(R.id.iv_edit);
        tvAttention = findView(R.id.tv0);
        tvCancle = findView(R.id.tv1);
        tvMsg = findView(R.id.tv2);
        newMsgView = findView(R.id.new_msg_view);
        tvHelp = findView(R.id.tv3);
        tvLianXi = findView(R.id.tv4);
    }

    private void setListener() {
        headerIv.setOnClickListener(this);
        tvName.setOnClickListener(this);
        etView.setOnClickListener(this);
        tvAttention.setOnClickListener(this);
        tvCancle.setOnClickListener(this);
        tvMsg.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvLianXi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                ((BaseActivity) getActivity()).start(UserInfoActivity.class);
                break;
            case R.id.iv_header:
                startImgSelectActivity();
                break;
            case R.id.tv_name:
                ((BaseActivity) getActivity()).start(UserInfoActivity.class);
                break;
            case R.id.tv0:
                ((BaseActivity) getActivity()).start(VerifyActivity.class);
                break;
            case R.id.tv1:
                ((MainActivity) getActivity()).showLogOutDialog();
                break;
            case R.id.tv2:
                goMsgActivity();
                break;
            case R.id.tv3:
                WebActivity.startActivity(getContext(), "帮助", Constant.HELP_URL);
                break;
            case R.id.tv4:
                ((BaseActivity) getActivity()).start(ClientCaseActivity.class);
                break;
        }
    }

    private void goMsgActivity() {
        if (this != null && !this.getActivity().isFinishing()) {
            newMsgView.setVisibility(View.GONE);
            if (snackBar != null && snackBar.isShown()) {
                snackBar.dismiss();
            }
            SPUtils.put(getContext(), HAS_NEW_NOTIFACTION_MSG, false);
            ((MainActivity) getActivity()).start(MessageActivity.class);
        }
    }

    private void startImgSelectActivity() {
// 自由配置选项
        ImgSelConfig config = new ImgSelConfig.Builder(getContext(), loader)
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
                .cropSize(1, 1, DensityUtils.dp2px(getContext(), 200), DensityUtils.dp2px(getContext(), 200))
                .needCrop(true)
                // 第一个是否显示相机，默认true
                .needCamera(true)
                // 最大选择图片数量，默认9
                .maxNum(1)
                .build();

        // 跳转到图片选择器
        ImgSelActivity.startActivity(this, config, REQUEST_CODE);
    }

    // 自定义图片加载器
    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            // TODO 在这边可以自定义图片加载库来加载ImageView，例如Glide、Picasso、ImageLoader等
            BitmapUtil.loadImage2(imageView, path, context, DensityUtils.dp2px(context, 100), DensityUtils.dp2px(context, 100));
        }
    };

    private static final int REQUEST_CODE = 0x00000101;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 图片选择结果回调
        if (requestCode == REQUEST_CODE && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
            for (String path : pathList) {
                uploadImg(path);
            }
        }
    }

    private void uploadImg(String path) {
        tvDowloadPregress.setVisibility(View.VISIBLE);
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {

            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
                    updateUserInfo(bmobFile.getFileUrl());
                    tvDowloadPregress.setVisibility(View.GONE);
                } else {
                    showToast("上传文件失败：" + e.getMessage());
                    tvDowloadPregress.setVisibility(View.VISIBLE);
                    tvDowloadPregress.setText("上传失败");
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            tvDowloadPregress.setVisibility(View.GONE);
                            tvDowloadPregress.setText("0%");
                        }
                    }, 3000);
                }
            }

            @Override
            public void onProgress(Integer value) {
                // 返回的上传进度（百分比）
                tvDowloadPregress.setText(value.toString() + "%");
            }
        });
    }

    public static Handler handler = new Handler();

    private void updateUserInfo(String fileUrl) {
        CarUser newUser = new CarUser();
        newUser.setAvatarImgUrl(fileUrl);
        CarUser user = BmobUser.getCurrentUser(CarUser.class);
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    updateUserAvator();
                } else {
                    showToast("上传失败" + e.getErrorCode());
                }
            }
        });
    }

    private void updateImg(String path) {
        BitmapUtil.loadImage2(
                headerIv, path, getContext(),
                DensityUtils.dp2px(getContext(), 50),
                DensityUtils.dp2px(getContext(), 50)
        );

        BitmapUtil.loadBlurTransImage2(
                baIv, path, getContext(),
                ScreenUtils.getScreenWidth(getContext()),
                DensityUtils.dp2px(getContext(), 200)
        );
    }

    public static final String IMG_FILE_KEY = "img_file_key";

    private void savePath(String path) {
        SPUtils.put(getContext(), IMG_FILE_KEY, path);
    }
}
