package com.hiretaxi.config;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/**
 * Created by Administrator on 2017/4/28.
 */
public class HttpRequest {

    private static RequestQueue quenue;
    private static Context mCntext;

    public static void init(Context context) {
        mCntext = context;
    }

    public static RequestQueue getRequestQuenue() {
        if (quenue == null) {
            synchronized (HttpRequest.class) {
                if (quenue == null) {
                    quenue = Volley.newRequestQueue(mCntext);
                }
            }
        }
        return quenue;
    }


}
