package com.hiretaxi.dao;

import android.content.Context;
import android.util.Log;

import com.hiretaxi.model.ImageBanner;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */
public class ImageBannerUtil {

    private static final String TAG = "ImageBannerUtil";
    private DaoManager mManager;
    private static ImageBannerUtil postUtilInstance;

    public static ImageBannerUtil getImageBannerUtilInstance(Context context) {
        if (postUtilInstance == null) {
            synchronized (ImageBannerUtil.class) {
                if (postUtilInstance == null) {
                    postUtilInstance = new ImageBannerUtil(context);
                }
            }
        }
        return postUtilInstance;
    }


    private ImageBannerUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成post记录的插入，如果表未创建，先创建Post表
     * @param imageBanner
     * @return
     */
    public boolean insertImageBanner(ImageBanner imageBanner){
        boolean flag = false;
        flag = mManager.getDaoSession().getImageBannerDao().insert(imageBanner) == -1 ? false : true;
        Log.i(TAG, "insert Post :" + flag + "-->" + imageBanner.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @return
     */
    public boolean insertMultImageBanner(final List<ImageBanner> imageBannerList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (ImageBanner imageBanner : imageBannerList) {
                        mManager.getDaoSession().insertOrReplace(imageBanner);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 修改一条数据
     * @param imageBanner
     * @return
     */
    public boolean updateImageBanner(ImageBanner imageBanner){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(imageBanner);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param imageBanner
     * @return
     */
    public boolean deletePost(ImageBanner imageBanner){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(imageBanner);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除所有记录
     * @return
     */
    public boolean deleteAll(){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().deleteAll(ImageBanner.class);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<ImageBanner> queryAllImageBanner(){
        return mManager.getDaoSession().loadAll(ImageBanner.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public ImageBanner queryPostById(long key){
        return mManager.getDaoSession().load(ImageBanner.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<ImageBanner> queryImageBannerByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(ImageBanner.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<ImageBanner> queryImageBannerByQueryBuilder(long id){
        QueryBuilder<ImageBanner> queryBuilder = mManager.getDaoSession().queryBuilder(ImageBanner.class);
        return queryBuilder.where(ImageBannerDao.Properties._id.eq(id)).list();
    }

}
