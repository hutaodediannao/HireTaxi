package com.hiretaxi.requestHelper;

import android.content.Context;
import android.util.Log;

import com.hiretaxi.activity.ClientCaseActivity;
import com.hiretaxi.config.Constant;
import com.hiretaxi.model.Other;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.ToastUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2017/7/21.
 */

public class RequestNetDataHelper {

    private static final String TAG = "RequestNetDataHelper";
    
    public static void requestNetForLianXiData(final Context context) {
        if (!NetUtils.isConnected(context)) {
            ToastUtil.showToast(context,Constant.NO_NET_CONNECT);
            return;
        }
        BmobQuery<Other> bmobQuery = new BmobQuery<Other>();
        //判断是否有缓存，该方法必须放在查询条件（如果有的话）都设置完之后再来调用才有效，就像这里一样。
        boolean isCache = bmobQuery.hasCachedResult(Other.class);
        if(isCache){//--此为举个例子，并不一定按这种方式来设置缓存策略
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        }else{
            bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        bmobQuery.setMaxCacheAge(TimeUnit.DAYS.toMillis(1));//此表示缓存一天

        bmobQuery.findObjects(new FindListener<Other>() {
            @Override
            public void done(List<Other> list, BmobException e) {
                if (e == null && list != null && !list.isEmpty()) {
                    ClientCaseActivity.qq = list.get(0).getQq()==null?"":list.get(0).getQq();
                    ClientCaseActivity.weiXin = list.get(0).getWeiXin()==null?"":list.get(0).getWeiXin();
                    ClientCaseActivity.phoneNumber = list.get(0).getPhoneNumber()==null?"":list.get(0).getPhoneNumber();
                    Constant.HELP_URL = list.get(0).getHelpUrl() == null ? Constant.DEFACULT_SRC_URL : list.get(0).getHelpUrl();
                    Log.i(TAG, "done: ok");
                } else {
//                    ToastUtil.showToast(context, "请求失败");
                    Log.i(TAG, "done: errorCode ====> " + e.getErrorCode());
                    
                }
            }
        });
    }
}
