package com.hiretaxi.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.adapter.PostAdapter;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.config.Constant;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.KeyBoardUtils;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.view.SearchToolbarView;
import com.hiretaxi.view.recyclerViewConfig.RefreashRecyclerView;

import java.util.LinkedList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchActivity extends ToolbarBaseActivity implements OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "SearchActivity";
    private SearchToolbarView searchToolbarView;
    private String mSearchKey;

    public static final int TYPE_LINEAR = 0;
    public static final int TYPE_GRID = 1;
    public static final int TYPE_STAGGERED_GRID = 2;
    public SwipeToLoadLayout swipeToLoadLayout;
    public int mType;

    public int mPagerIndex = 0;//分页查询页数的下标
    public int mLimit = 10; //分页中每页显示的数据条数

    public LinkedList list;
    public PostAdapter adapter;
    public RefreashRecyclerView recyclerView;
    public View entryView;
    public RecyclerView.LayoutManager layoutManager;
    public CircleImageView floatingActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        initList();
        initAdapter();
        setListener();
    }

    @Override
    protected View getToolbarView() {
        searchToolbarView = new SearchToolbarView(this);
        return searchToolbarView;
    }

    private void setListener() {
        searchToolbarView.setHeaderLsitener(new SearchToolbarView.HeaderLsitener() {
            @Override
            public void clickLeftListener() {
                finish();
            }

            @Override
            public void clickRightListener(String searchKey) {
                mSearchKey = searchKey;
                refreashData();
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

        adapter.setClickItemListener(new BaseAdapter.ClickItemListener() {
            @Override
            public void clickItem(Object o, int position) {
                start(DetailActivity.class, o, position);
            }
        });
    }

    //true表示在外面，false表示在里面
    public boolean isOut = true;

    public void out() {
        if (isOut) {
            ObjectAnimator onaim = ObjectAnimator.ofFloat(floatingActionButton, "translationX", 0f,
                    -DensityUtils.dp2px(this, 90)).setDuration(500);
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
                    -DensityUtils.dp2px(this, 90), 0f).setDuration(500);
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

    private void initView() {
        KeyBoardUtils.openKeybord(searchToolbarView.getEtSearch(), this);
        entryView = findView(R.id.entryView);
        swipeToLoadLayout = findView(R.id.swipeToLoadLayout);
        recyclerView = findView(R.id.swipe_target);
        floatingActionButton = findView(R.id.fab);
        layoutManager = null;
        mType = TYPE_LINEAR;
        if (mType == TYPE_LINEAR) {
            layoutManager = new LinearLayoutManager(this);
        } else if (mType == TYPE_GRID) {
            layoutManager = new GridLayoutManager(this, 2);
        } else if (mType == TYPE_STAGGERED_GRID) {
            layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        }
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void finish() {
        KeyBoardUtils.closeKeybord(searchToolbarView.getEtSearch(), this);
        super.finish();
    }

    //请求网络数据列表
    public void initList() {
        list = new LinkedList();
    }

    public void initAdapter() {
        adapter = new PostAdapter(this, list, R.layout.home_item);
        recyclerView.setAdapter(adapter);
    }

    public void refreashData() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            closeSwipeToLoadLayoutLoading();
            return;
        }
        if (mSearchKey == null || mSearchKey.isEmpty()) {
            showToast("搜索内容不能为空！");
            closeSwipeToLoadLayoutLoading();
            return;
        }

        mPagerIndex = 0;
        final BmobQuery<Post> query = getPostBmobQuery(mSearchKey);

        swipeToLoadLayout.setRefreshing(true);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> queryList, BmobException e) {
                KeyBoardUtils.closeKeybord(searchToolbarView.getEtSearch(), SearchActivity.this);
                if (e == null) {
                    list.clear();
                    loadDataComplete(queryList);
                    isShowEntryViweLayout(list);
                } else {
                    closeSwipeToLoadLayoutLoading();
                    showToast("刷新失败");
                }
            }
        });
    }

    @NonNull
    private BmobQuery<Post> getPostBmobQuery(String mSearchKey) {
        final BmobQuery<Post> query = new BmobQuery<Post>();
        query.setLimit(mLimit);
        query.setSkip(mPagerIndex);
//        query.order("-createdAt");
        query.order("-updatedAt");
        query.addWhereDoesNotExists(Post.INDENT_USER);
        query.addWhereNotEqualTo(Post.STATE, true);
        query.addWhereContains(Post.TITLE, mSearchKey);

        String carType = BmobUser.getCurrentUser(CarUser.class).getCarType();
        if (carType != null && !carType.isEmpty()) {
            query.addWhereEqualTo(Post.CAR_TYPE, carType);
        }
        return query;
    }

    public void loadMoreData() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            closeSwipeToLoadLayoutLoading();
            return;
        }
        if (mSearchKey == null || mSearchKey.isEmpty()) {
            showToast("搜索内容不能为空！");
            closeSwipeToLoadLayoutLoading();
            return;
        }
        mPagerIndex++;
        BmobQuery<Post> query = getPostBmobQuery2();
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> queryList, BmobException e) {
                if (e == null) {
                    if (null == queryList) {
                        mPagerIndex--;
                    } else if (queryList.isEmpty()) {
                        showToast("没有更多数据了");
                    }
                    loadDataComplete(queryList);
                } else {
                    showToast("加载失败");
                    closeSwipeToLoadLayoutLoading();
                }
            }
        });
    }

    @NonNull
    private BmobQuery<Post> getPostBmobQuery2() {
        BmobQuery<Post> query = new BmobQuery<Post>();
        query.setLimit(mLimit);
        query.setSkip(mPagerIndex * mLimit);
//        query.order("-createdAt");
        query.order("-updatedAt");
        query.addWhereDoesNotExists(Post.INDENT_USER);
        query.addWhereNotEqualTo(Post.STATE, true);
        query.addWhereContains(Post.TITLE, mSearchKey);
        return query;
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

    /**
     * 判断是否显示空View提示
     * @param postList
     */
    public void isShowEntryViweLayout(List<Post> postList) {
        if (null == postList) {
            entryView.setVisibility(View.VISIBLE);
        } else if (postList.isEmpty()) {
            entryView.setVisibility(View.VISIBLE);
        } else {
            entryView.setVisibility(View.GONE);
        }
    }

}

