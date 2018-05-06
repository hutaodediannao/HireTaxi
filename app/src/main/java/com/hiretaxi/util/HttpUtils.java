package com.hiretaxi.util;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.hiretaxi.config.HttpRequest;

import java.util.Map;

/**
 * Created by Administrator on 2017/4/28.
 */
public class HttpUtils {

    private static final String TAG = "HttpUtils";
    private static final int DEFACULT_TIMEOUT_MS = 1000 * 10;
    public static StringRequest stringRequest;

    /**
     * Get请求，获得返回数据 * * @param context 上下文 * @param url 发送请求的URL * @param tag TAG标签 * @param vif 请求回调的接口（请求成功或者失败）
     */
    public static void doGet(Context context, String url, String tag, VolleyInterface vif) {
        Log.i(TAG, url);

        //获取全局的请求队列并把基于Tag标签的请求全部取消，防止重复请求
        HttpRequest.getRequestQuenue().cancelAll(tag);
        //实例化StringRequest
        stringRequest = new StringRequest(Request.Method.GET, url, vif.loadingListener(), vif.errorListener());
        // 设置标签
        stringRequest.setTag(tag);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFACULT_TIMEOUT_MS,//默认超时
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 将请求添加至队列里面
        HttpRequest.getRequestQuenue().add(stringRequest);
        // 启动
//        HttpRequest.getRequestQuenue().start(); //我去，妹的纠结了很久的"com.android.volley.NoConnectionError: java.io.InterruptedIOException"原来就是因为增加了这句话
    }

    /**
     * 向指定 URL 发送POST方法的请求 * * @param context 上下文 * @param url 发送请求的URL * @param tag TAG标签 * @param params 请求参数，请求参数应该是Hashmap类型 * @param vif 请求回调的接口（请求成功或者失败）
     */
    public static void doPost(Context context, String url, String tag, final Map<String, String> params,
                              VolleyInterface vif) {
        Log.e("发送Get请求的URL:", url);
        //获取全局的请求队列并把基于Tag标签的请求全部取消，防止重复请求
        HttpRequest.getRequestQuenue().cancelAll(tag);
        stringRequest = new StringRequest(url, vif.loadingListener(), vif.errorListener()) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        // 设置标签
        stringRequest.setTag(tag);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                DEFACULT_TIMEOUT_MS,//默认超时
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,//默认最大尝试次数
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        // 加入队列
        HttpRequest.getRequestQuenue().add(stringRequest);
        // 启动
//        HttpRequest.getRequestQuenue().start(); //我去，妹的纠结了很久的"com.android.volley.NoConnectionError: java.io.InterruptedIOException"原来就是因为增加了这句话
    }
}
