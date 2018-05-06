package com.hiretaxi.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.hiretaxi.R;
import com.hiretaxi.activity.DetailActivity;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.adapter.PostAdapter;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.config.Constant;
import com.hiretaxi.dao.PostUtil;
import com.hiretaxi.fragment.base.BaseFragment;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.view.recyclerViewConfig.RefreashRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class OkIndentFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    public SwipeToLoadLayout swipeToLoadLayout;
    public int mPagerIndex = 0;//分页查询页数的下标
    public static final int mLimit = 10; //分页中每页显示的数据条数
    public ArrayList<Post> list;
    public RefreashRecyclerView recyclerView;
    public View entryView;
    public RecyclerView.LayoutManager layoutManager;
    public CircleImageView floatingActionButton;
    private PostAdapter adapter;
    private CarUser user;
    private ImageView ivGif;

    @Override
    protected int getChildFragmentLayout() {
        return R.layout.fragment_receive;
    }

    @Override
    protected void handViewEvent(View fragmentRootview) {
        user = BmobUser.getCurrentUser(CarUser.class);
        if (user == null) {
            showToast(Constant.NO_LOGIN_DISCONNECT);
            return;
        }
        initView();
        initAdapter();
        setListener();
        onRefresh();
    }

    //首先读取本地数据
    private void readPostDataFormDb() {
        List<Post> postList = PostUtil.getPostUtilInstance(getContext()).queryAllPost();
        if (postList == null || postList.isEmpty()) {
            return;
        }
        for (Post post : postList) {
            if (post != null && (post.getSaveDbObjectId() != null && post.getSaveDbObjectId().equals(user.getObjectId())) &&
                    (post.getState() != null && post.getState().booleanValue()) ) {
                this.list.add(post);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (this.list != null) {
            //这里不需要删除数据，已经在主页删除过一次就够了
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i) != null) {
                    this.list.get(i).setSaveDbObjectId(user.getObjectId());
                    this.list.get(i).setSavePostObjectId(this.list.get(i).getObjectId());
                    this.list.get(i).setSaveUpdateTime(this.list.get(i).getUpdatedAt());
                    if (list.get(i).getCarImgFile() != null) {
                        this.list.get(i).setImgUrlCache(this.list.get(i).getCarImgFile().getFileUrl());
                    }
                }
            }
            PostUtil.getPostUtilInstance(this.getContext()).insertMultpost(this.list);
        }
        Glide.clear(ivGif);
        super.onDestroy();
    }

    //初始化控件
    private void initView() {
        recyclerView = findView(R.id.swipe_target);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        floatingActionButton = findView(R.id.fab);
        entryView = findView(R.id.entryView);
        swipeToLoadLayout = findView(R.id.swipeToLoadLayout);

        ivGif = findView(R.id.iv_gif);
        BitmapUtil.loadImageForLocalAsGif(ivGif, R.mipmap.title, this.getActivity());
    }

    //设置监听事件
    private void setListener() {
        adapter.setClickItemListener(new BaseAdapter.ClickItemListener<Post>() {
            @Override
            public void clickItem(Post post, int position) {
                ((BaseActivity) getActivity()).start(DetailActivity.class, post, position);
            }
        });

        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int firstVisivityItemIndex = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                if (firstVisivityItemIndex > 0) {
                    out();
                } else {
                    in();
                    if (recyclerView.getChildAt(0) != null) {
                        int top = recyclerView.getChildAt(0).getTop();
                        int bannerViewHeight = recyclerView.getChildAt(0).getMeasuredHeight();
                        float alphaValue = top * 1f / bannerViewHeight;
                    }
                }
            }
        });

        //一键置顶
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

    }

    //true表示在外面，false表示在里面
    public boolean isOut = true;

    public void out() {
        if (isOut) {
            ObjectAnimator onaim = ObjectAnimator.ofFloat(floatingActionButton, "translationX", 0f,
                    -DensityUtils.dp2px(getContext(), 90)).setDuration(500);
            onaim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isOut = false;
                }
            });
            onaim.start();
        }
    }

    public void in() {
        if (!isOut) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(floatingActionButton, "translationX",
                    -DensityUtils.dp2px(getContext(), 90), 0f).setDuration(500);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    isOut = true;
                }
            });
            anim.start();
        }
    }

    @Override
    public void onLoadMore() {
        loadMoreData();
    }

    @Override
    public void onRefresh() {
        refreashData();
    }

    public void loadMoreData() {
        if (!NetUtils.isConnected(this.getContext())) {
            showToast(Constant.NO_NET_CONNECT);
            closeSwipeToLoadLayoutLoading();
            return;
        }
        mPagerIndex++;
        BmobQuery<Post> query = new BmobQuery<Post>();
        addQueryParmas(query, mPagerIndex * mLimit);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> queryList, BmobException e) {
                if (e == null) {
                    if (null == queryList || queryList.isEmpty()) {
                        mPagerIndex--;
                        showToast("没有更多数据了");
                    }
                    loadDataComplete(queryList);
                } else {
                    mPagerIndex--;
//                    showToast("加载失败:" + e.getMessage());
                    closeSwipeToLoadLayoutLoading();
                }
            }
        });
    }


    public void refreashData() {
        if (!NetUtils.isConnected(getContext())) {
            showToast(Constant.NO_NET_CONNECT);
            closeSwipeToLoadLayoutLoading();
            return;
        }
        swipeToLoadLayout.setRefreshing(true);
        BmobQuery<Post> bmobQuery = new BmobQuery<Post>();
        mPagerIndex = 0;
        addQueryParmas(bmobQuery, mPagerIndex);
        bmobQuery.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> queryList, BmobException e) {
                if (e == null) {
                    OkIndentFragment.this.list.clear();
                    //添加请求的数据
                    OkIndentFragment.this.list.addAll(queryList);
                    adapter.updateList(OkIndentFragment.this.list);
                    isShowEntryViweLayout(list);
                } else {
//                    showToast("刷新失败！" + e.getMessage() + "," + e.getErrorCode());
                }
                closeSwipeToLoadLayoutLoading();
            }
        });
    }

    /**
     * 判断是否显示空View提示
     *
     * @param postList
     */
    public void isShowEntryViweLayout(List postList) {
        if (null == postList) {
            entryView.setVisibility(View.VISIBLE);
            return;
        } else if (postList.isEmpty()) {
            entryView.setVisibility(View.VISIBLE);
        } else {
            entryView.setVisibility(View.GONE);
        }
    }

    public void closeSwipeToLoadLayoutLoading() {
        swipeToLoadLayout.setLoadingMore(false);
        swipeToLoadLayout.setRefreshing(false);
    }

    /**
     * 数据加载完成
     *
     * @param queryList
     */
    public void loadDataComplete(List<Post> queryList) {
        list.addAll(queryList);
        adapter.updateList(list);
        closeSwipeToLoadLayoutLoading();
    }


    private void addQueryParmas(BmobQuery<Post> bmobQuery, int skip) {
        bmobQuery.setLimit(mLimit);
        bmobQuery.setSkip(skip);
        bmobQuery.order("-createdAt");
        bmobQuery.addWhereEqualTo(Post.INDENT_USER, user);
        bmobQuery.addWhereEqualTo(Post.STATE, true);
        bmobQuery.order("-updatedAt");
    }

    //设置适配器
    private void initAdapter() {
        this.list = new ArrayList<Post>();
        readPostDataFormDb();//读取本地缓存数据
        adapter = new PostAdapter(this, list, R.layout.home_item);
        recyclerView.setAdapter(adapter);
        isShowEntryViweLayout(list);
    }

    public static boolean isNeedRefreash = false;//标记是否需要刷新界面

    @Override
    public void onStart() {
        Log.i("test", "onStart: MyIndentFragment onstart");
        super.onStart();
        if (isNeedRefreash) {
            refreashData();
            isNeedRefreash = false;
        }
    }

}
