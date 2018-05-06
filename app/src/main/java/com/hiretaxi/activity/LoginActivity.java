package com.hiretaxi.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.hiretaxi.R;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.config.MyActivityManager;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.util.HttpUtils;
import com.hiretaxi.util.SampleJsonParseUtil;
import com.hiretaxi.util.StringUtil;
import com.hiretaxi.util.VolleyInterface;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.trycatch.mysnackbar.Prompt;
import com.trycatch.mysnackbar.TSnackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.tencent.mm.opensdk.modelbase.BaseResp.ErrCode;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private View loginView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginView = findView(R.id.tv_login);
        status = -100;
        registerWeChatConfig();
        initLoadingTips();

        requestLocationPermission();
    }

    //注册微信开发平台的API
    private void registerWeChatConfig() {
        iwxapi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        iwxapi.registerApp(Constant.WE_CHAT_APPID);
    }

    private IWXAPI iwxapi;

    public void startLogin(View view) {
        loginByWeiXin(view);
    }

    private void loginByWeiXin(View view) {
        boolean isInstallWeChat = iwxapi.isWXAppInstalled();
        if (isInstallWeChat) {
            view.setEnabled(false);
            SendAuth.Req req = new SendAuth.Req();
            //授权读取用户信息
            req.scope = "snsapi_userinfo";
            //自定义信息
            req.state = "wechat_sdk_demo_test";
            //向微信发送请求
            iwxapi.sendReq(req);
            snackBar.setText("正在拉取微信...");
            snackBar.show();
        } else {
            showToast("未安装微信哦，请你先安装微信吧");
        }
    }

    public static int status = -100;
    public static String msgCode = null;

    @Override
    public void onResume() {
        super.onResume();
        switch (status) {
            case ErrCode.ERR_OK:
                snackBar.setText("正在登陆服务器...");
                snackBar.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doGet(msgCode);
                    }
                }, 1000);
                break;
            case ErrCode.ERR_USER_CANCEL:
//                showToast("您取消了");
                loginView.setEnabled(true);
                snackBar.dismiss();
                waringSnackBar.show();
                break;
            case ErrCode.ERR_AUTH_DENIED:
//                showToast("被拒绝了");
                loginView.setEnabled(true);
                snackBar.dismiss();
                waringSnackBar.show();
                break;
            default:
                loginView.setEnabled(true);
                snackBar.dismiss();
                break;
        }
    }

    private TSnackbar snackBar, waringSnackBar;

    private void initLoadingTips() {
        snackBar = TSnackbar.make(getWindow().getDecorView().getRootView(),
                "正在登陆服务器...", TSnackbar.LENGTH_INDEFINITE, TSnackbar.APPEAR_FROM_TOP_TO_DOWN);
        snackBar.setBackgroundColor(R.color.royalblue);
        snackBar.addIconProgressLoading(0, true, false);

        waringSnackBar = TSnackbar.make(getWindow().getDecorView().getRootView(),
                "登陆服务器失败，是否重试？", TSnackbar.LENGTH_INDEFINITE);
        waringSnackBar.setPromptThemBackground(Prompt.WARNING);
        waringSnackBar.setAction("重试", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginByWeiXin(findViewById(R.id.tv_login));
            }
        });
    }

    private void doGet(String code) {
        String url = Constant.getWeChatLoginUrl(code);
        String tag = "GET";
        HttpUtils.doGet(getApplicationContext(), url, tag,
                new VolleyInterface(getApplicationContext(),
                        VolleyInterface.mListener, VolleyInterface.mErrorListtener) {
                    @Override
                    public void onSuccess(String result) {
                        Log.i(TAG, "onSuccess: ======>" + result);
                        if (SampleJsonParseUtil.hasKey(result, Constant.WX_ACCESS_TOKEN) &&
                                SampleJsonParseUtil.hasKey(result, Constant.WX_EXPIRES) &&
                                SampleJsonParseUtil.hasKey(result, Constant.WX_OPENID)) {
                            String snsType = Constant.WEI_XIN;
                            String accessToken = SampleJsonParseUtil.getValue(result, Constant.WX_ACCESS_TOKEN);
                            String expiresIn = SampleJsonParseUtil.getValue(result, Constant.WX_EXPIRES);
                            String userId = SampleJsonParseUtil.getValue(result, Constant.WX_OPENID);

                            loginByWeiXin(snsType, accessToken, expiresIn, userId);
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        loadDialog.dismiss();
                        snackBar.dismiss();
                        waringSnackBar.show();
                    }
                });
    }

    private void loginByWeiXin(String snsType, String accessToken, String expiresIn, final String userId) {
        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType, accessToken, expiresIn, userId);
        BmobUser.loginWithAuthData(authInfo, new LogInListener<JSONObject>() {
            @Override
            public void done(JSONObject jsonObject, BmobException e) {
                if (e == null) {
                    //1.请求微信信息
                    Log.i(TAG, "onSuccess: jsonObject====> " + jsonObject.toString());
                    try {
                        String access_token = jsonObject.getJSONObject(Constant.WEI_XIN).getString(Constant.WX_ACCESS_TOKEN);
                        String openId = jsonObject.getJSONObject(Constant.WEI_XIN).getString(Constant.WX_OPENID);
                        requestWeChatInfo(access_token, openId);
                    } catch (JSONException ex) {
                        loadDialog.dismiss();
                        snackBar.dismiss();
                        waringSnackBar.show();
                        ex.printStackTrace();
                    }
                } else {
                    loadDialog.dismiss();
                    snackBar.dismiss();
                    waringSnackBar.show();
                }
            }
        });
    }

    //请求微信信息

    private void requestWeChatInfo(String access_token, final String openid) {
        String tag = "requestWeChatInfo";
        String url = Constant.getWeChatInfo(access_token, openid);
        HttpUtils.doGet(this, url, tag, new VolleyInterface(getApplicationContext(),
                VolleyInterface.mListener, VolleyInterface.mErrorListtener) {
            @Override
            public void onSuccess(String result) {
                Log.i(TAG, "onSuccess: result====> " + result);
                //2.更新用户信息
                if (SampleJsonParseUtil.hasKey(result, Constant.WX_OPENID)) {
                    String nickName = SampleJsonParseUtil.getValue(result, Constant.NICK_NAME);
                    String sex = SampleJsonParseUtil.getValue(result, Constant.SEX).equals("1") ? "男" : "女";
                    String avatar = SampleJsonParseUtil.getValue(result, Constant.AVATAR);

                    CarUser bmobUser = BmobUser.getCurrentUser(CarUser.class);
                    CarUser newUser = new CarUser(openid);

//                    if (bmobUser.getCarType() == null || bmobUser.getCarType().isEmpty()) {
//                        newUser.setCarType(Constant.DEFACULT_CAR_TYPE);
//                    }

                    if (bmobUser.getAvatarImgUrl() == null|| bmobUser.getAvatarImgUrl().isEmpty()) {
                        newUser.setAvatarImgUrl(avatar);
                    }
                    if (bmobUser.getSex() == null|| bmobUser.getSex().isEmpty()) {
                        newUser.setSex(sex);
                    }
                    if (bmobUser.getNickName() == null|| bmobUser.getNickName().isEmpty()) {
                        newUser.setNickName(new String(nickName.getBytes(Charset.forName("ISO-8859-1"))));
                    }
                    if (bmobUser.getNewUploadImg() == null) {
                        newUser.setNewUploadImg(false);
                    }
                    if (bmobUser.getValidateOk() == null) {
                        newUser.setValidateOk(false);
                    }
                    if (newUser.getCarStatu() == null) {
                        newUser.setCarStatu("车况良好");
                    }

                    Constant.DEFACULT_HEADRE_IMG_URL = avatar;
                    Constant.DEFACULT_NICK_NAME = StringUtil.isEmpty(nickName) ? Constant.DEFACULT_NICK_NAME : nickName;
                    Constant.DEFACULT_SEX = sex == null ? Constant.DEFACULT_SEX : sex;

                    updateUserInfo(newUser, bmobUser.getObjectId());
                } else {
                    loadDialog.dismiss();
                    snackBar.dismiss();
                    waringSnackBar.show();
                }
            }

            @Override
            public void onError(VolleyError error) {
                loadDialog.dismiss();
                snackBar.dismiss();
                waringSnackBar.show();
            }
        });
    }

    //微信登录成功后，修改登录用户信息，将服务器密码修改为账号相同的
    private void updateUserInfo(CarUser newUser, String objectId) {
        newUser.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    loginOk();
                } else {
                    waringSnackBar.show();
                    snackBar.dismiss();
                }
            }
        });
    }

    private void loginOk() {
        waringSnackBar.dismiss();
        snackBar.setPromptThemBackground(Prompt.SUCCESS);
        snackBar.setText("登陆服务器成功");
        snackBar.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                snackBar.dismiss();
                MyActivityManager.removeAllActivity();
                start(MainActivity.class);
            }
        }, 2000);
    }

    private Handler handler = new Handler();

    // 要申请的权限
    private String[] permissions = {Manifest.permission.CAMERA};
    private void requestLocationPermission() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 检查该权限是否已经获取
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission();
            }
        }
    }

    // 提示用户该请求权限的弹出框
    private void showDialogTipUserRequestPermission() {

        new AlertDialog.Builder(this)
                .setTitle("相机权限不可用")
                .setMessage("由于此app需要使用相机功能需要获取相机权限；\n取消，您将无法正常使用相机服务")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                        showToast("权限获取失败");
                    }
                }).setCancelable(false).show();
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting();
                    } else {
                        showToast("权限获取失败");
                    }
                } else {
                    showToast("权限获取成功");
                }
            }
        }
    }

    // 提示用户去应用设置界面手动开启权限
    private AlertDialog mDialog;
    private void showDialogTipUserGoToAppSettting() {

        mDialog = new AlertDialog.Builder(this)
                .setTitle("相机权限不可用")
                .setMessage("请在-应用设置-权限-中，允许爆米花订车APP使用相机权限来获取位置服务")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
                        showToast("相机权限获取失败");
                    }
                }).setCancelable(false).show();
    }

    // 跳转到当前应用的设置界面
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 123);
    }
}
