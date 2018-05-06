package com.hiretaxi.dao;

import android.content.Context;
import android.util.Log;

import com.hiretaxi.model.Notifaction;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */
public class NotifactionUtil {

    private static final String TAG = "NotifactionUtil";
    private DaoManager mManager;
    private static NotifactionUtil postUtilInstance;

    public static NotifactionUtil getNotifactionUtilInstance(Context context) {
        if (postUtilInstance == null) {
            synchronized (NotifactionUtil.class) {
                if (postUtilInstance == null) {
                    postUtilInstance = new NotifactionUtil(context);
                }
            }
        }
        return postUtilInstance;
    }


    private NotifactionUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成post记录的插入，如果表未创建，先创建Post表
     * @param notifaction
     * @return
     */
    public boolean insertNotifaction(Notifaction notifaction){
        boolean flag = false;
        flag = mManager.getDaoSession().getNotifactionDao().insert(notifaction) == -1 ? false : true;
        Log.i(TAG, "insert Post :" + flag + "-->" + notifaction.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @return
     */
    public boolean insertMultNotifaction(final List<Notifaction> notifactionList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Notifaction notifaction : notifactionList) {
                        mManager.getDaoSession().insertOrReplace(notifaction);
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
     * @param notifaction
     * @return
     */
    public boolean updateNotifaction(Notifaction notifaction){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(notifaction);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param notifaction
     * @return
     */
    public boolean deletePost(Notifaction notifaction){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(notifaction);
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
            mManager.getDaoSession().deleteAll(Notifaction.class);
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
    public List<Notifaction> queryAllNotifaction(){
        return mManager.getDaoSession().loadAll(Notifaction.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Notifaction queryPostById(long key){
        return mManager.getDaoSession().load(Notifaction.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Notifaction> queryNotifactionByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Notifaction.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Notifaction> queryNotifactionByQueryBuilder(long id){
        QueryBuilder<Notifaction> queryBuilder = mManager.getDaoSession().queryBuilder(Notifaction.class);
        return queryBuilder.where(NotifactionDao.Properties._id.eq(id)).list();
    }

}
