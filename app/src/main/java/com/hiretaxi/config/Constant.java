package com.hiretaxi.config;

/**
 * Created by Administrator on 2017/7/4.
 */

public class Constant {

    public static final String DEFACULT_SRC_URL = "https://www.baidu.com";//缺省的web界面连接

    public static String HELP_URL = DEFACULT_SRC_URL;//帮助页面的URL地址

    public static final String APP_ID = "2b330eec2101fb205d1e53bba562e3bc";

    public static final String DEFACULT_IMG_URL = "http://img0.imgtn.bdimg.com/it/u=1689583646,2560929748&fm=26&gp=0.jpg";

    //网络string
    public static final String NO_NET_CONNECT = "网络已断开，请检查网络连接!";

    //提示登录
    public static final String NO_LOGIN_DISCONNECT = "您还未登录，请先登录";

    //微信开放平台数据
    public static final String WE_CHAT_APPID = "wxadd916c6b9df7ab0";
    public static final String WE_CHAT_APPSECRET = "0fd69ad1723d0c48dbdab9420c65b95a";

    //微信登录接口
    public static final String getWeChatLoginUrl(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + Constant.WE_CHAT_APPID
                + "&secret="
                + Constant.WE_CHAT_APPSECRET
                + "&code="
                + code
                + "&grant_type=authorization_code";
    }

    //获取微信用户信息接口
    public static final String getWeChatInfo(String access_token, String openId) {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" + access_token + "&openid=" + openId;
    }

    //微信登录注册参数
    public static final String SNS_TYPE = "snsType";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String EXPIRESIN = "expiresIn";
    public static final String USER_ID = "userId";
    //微信验证字段
    public static final String WX_ACCESS_TOKEN = "access_token";
    public static final String WX_EXPIRES = "expires_in";
    public static final String WX_REFRESH_TOKEN = "refresh_token";
    public static final String WX_OPENID = "openid";
    public static final String WX_SCOPE = "scope";
    public static final String WX_UNIONID = "unionid";
    public static final String WEI_XIN = "weixin";
    //微信信息
    public static final String NICK_NAME = "nickname";
    public static final String SEX = "sex";
    public static final String AVATAR = "headimgurl";

    public static String DEFACULT_HEADRE_IMG_URL;
    public static String DEFACULT_SEX = "男";
    public static String DEFACULT_NICK_NAME;

    //缺省为出租车
    public static final String[] CAR_TYPE_ARRAY = {"经济型", "舒适型", "商务型", "豪华型", "出租车"};

}
