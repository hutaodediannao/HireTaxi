package com.hiretaxi.util;

import com.hiretaxi.config.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 简单的json解析器
 * Created by Administrator on 2017/5/2.
 */
public class SampleJsonParseUtil {

    //判断是否存在该key
    public static boolean hasKey(String jsonContent, String key) {
        try {
            JSONObject jsonObject = new JSONObject(jsonContent);
            if (key == null) {
                return false;
            }
            if (jsonObject.has(key)) {
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    //获取key对应的value
    public static String getValue(String jsonContnet, String key) {
        if (hasKey(jsonContnet, key)) {
            try {
                return new JSONObject(jsonContnet).getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //获取jsonObject对象
    public static JSONObject getJsonObject(String jsonObject, String key) {
        if (hasKey(jsonObject, key)) {
            try {
                return new JSONObject(jsonObject).getJSONObject(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //微信个人信息解析
    public static String getWeChatInfo(String jsonStr, String key) {
        JSONObject jsonObject = getJsonObject(jsonStr, Constant.WEI_XIN);
        if (jsonObject == null) {
            return null;
        }
        if (key == null || jsonStr == null) {
            return null;
        }
        if (jsonObject.has(key)) {
            try {
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

}
