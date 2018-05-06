package com.hiretaxi.wxapi;

import android.os.Bundle;
import android.util.Log;

import com.hiretaxi.activity.LoginActivity;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.config.Constant;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Created by Administrator on 2017/4/28.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private final String TAG = this.getClass().getSimpleName();
    //    public static final String APP_ID = "请自己填写";
//    public static final String APP_SECRET = "请自己填写";
    private IWXAPI mApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApi = WXAPIFactory.createWXAPI(this, Constant.WE_CHAT_APPID, true);
        mApi.handleIntent(this.getIntent(), this);
    }

    //微信发送的请求将回调到onReq方法
    @Override
    public void onReq(BaseReq baseReq) {
    }

    //发送到微信请求的响应结果
    @Override
    public void onResp(BaseResp resp) {

        Log.i(TAG, "onResp: resp.errCode=====> " + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                //发送成功
                LoginActivity.status = BaseResp.ErrCode.ERR_OK;
                String code = ((SendAuth.Resp) resp).code; //即为所需的code
                LoginActivity.msgCode = code;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                //发送取消
                LoginActivity.status = BaseResp.ErrCode.ERR_USER_CANCEL;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //发送被拒绝
                LoginActivity.status = BaseResp.ErrCode.ERR_AUTH_DENIED;
                break;
            default:
                showToast("error");
                //发送返回
                break;
        }
        finish();
    }


}
