package com.hiretaxi.dao;

import android.content.Context;
import android.util.Log;

import com.hiretaxi.model.Post;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/23.
 */
public class PostUtil {

    private static final String TAG = "postUtil";
    private DaoManager mManager;
    private static PostUtil postUtilInstance;

    public static PostUtil getPostUtilInstance(Context context) {
        if (postUtilInstance == null) {
            synchronized (PostUtil.class) {
                if (postUtilInstance == null) {
                    postUtilInstance = new PostUtil(context);
                }
            }
        }
        return postUtilInstance;
    }


    private PostUtil(Context context) {
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成post记录的插入，如果表未创建，先创建Post表
     * @param post
     * @return
     */
    public boolean insertpost(Post post){
        boolean flag = false;
        flag = mManager.getDaoSession().getPostDao().insert(post) == -1 ? false : true;
        Log.i(TAG, "insert Post :" + flag + "-->" + post.toString());
        return flag;
    }

    /**
     * 插入多条数据，在子线程操作
     * @return
     */
    public boolean insertMultpost(final List<Post> postList) {
        boolean flag = false;
        try {
            mManager.getDaoSession().runInTx(new Runnable() {
                @Override
                public void run() {
                    for (Post post : postList) {
                        mManager.getDaoSession().insertOrReplace(post);
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
     * @param post
     * @return
     */
    public boolean updatepost(Post post){
        boolean flag = false;
        try {
            mManager.getDaoSession().update(post);
            flag = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * 删除单条记录
     * @param post
     * @return
     */
    public boolean deletePost(Post post){
        boolean flag = false;
        try {
            //按照id删除
            mManager.getDaoSession().delete(post);
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
            mManager.getDaoSession().deleteAll(Post.class);
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
    public List<Post> queryAllPost(){
        return mManager.getDaoSession().loadAll(Post.class);
    }

    /**
     * 根据主键id查询记录
     * @param key
     * @return
     */
    public Post queryPostById(long key){
        return mManager.getDaoSession().load(Post.class, key);
    }

    /**
     * 使用native sql进行查询操作
     */
    public List<Post> queryPostByNativeSql(String sql, String[] conditions){
        return mManager.getDaoSession().queryRaw(Post.class, sql, conditions);
    }

    /**
     * 使用queryBuilder进行查询
     * @return
     */
    public List<Post> queryPostByQueryBuilder(long id){
        QueryBuilder<Post> queryBuilder = mManager.getDaoSession().queryBuilder(Post.class);
        return queryBuilder.where(PostDao.Properties._id.eq(id)).list();
    }

}
