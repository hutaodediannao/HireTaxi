package com.hiretaxi.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.hiretaxi.model.ImageBanner;
import com.hiretaxi.model.Notifaction;
import com.hiretaxi.model.Post;

import com.hiretaxi.dao.ImageBannerDao;
import com.hiretaxi.dao.NotifactionDao;
import com.hiretaxi.dao.PostDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig imageBannerDaoConfig;
    private final DaoConfig notifactionDaoConfig;
    private final DaoConfig postDaoConfig;

    private final ImageBannerDao imageBannerDao;
    private final NotifactionDao notifactionDao;
    private final PostDao postDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        imageBannerDaoConfig = daoConfigMap.get(ImageBannerDao.class).clone();
        imageBannerDaoConfig.initIdentityScope(type);

        notifactionDaoConfig = daoConfigMap.get(NotifactionDao.class).clone();
        notifactionDaoConfig.initIdentityScope(type);

        postDaoConfig = daoConfigMap.get(PostDao.class).clone();
        postDaoConfig.initIdentityScope(type);

        imageBannerDao = new ImageBannerDao(imageBannerDaoConfig, this);
        notifactionDao = new NotifactionDao(notifactionDaoConfig, this);
        postDao = new PostDao(postDaoConfig, this);

        registerDao(ImageBanner.class, imageBannerDao);
        registerDao(Notifaction.class, notifactionDao);
        registerDao(Post.class, postDao);
    }
    
    public void clear() {
        imageBannerDaoConfig.clearIdentityScope();
        notifactionDaoConfig.clearIdentityScope();
        postDaoConfig.clearIdentityScope();
    }

    public ImageBannerDao getImageBannerDao() {
        return imageBannerDao;
    }

    public NotifactionDao getNotifactionDao() {
        return notifactionDao;
    }

    public PostDao getPostDao() {
        return postDao;
    }

}