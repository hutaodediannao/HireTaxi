package com.hiretaxi.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.hiretaxi.activity.SearchActivity;
import com.hiretaxi.activity.UserInfoActivity;
import com.hiretaxi.activity.WebActivity;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.adapter.PostAdapter;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.config.Constant;
import com.hiretaxi.dao.ImageBannerUtil;
import com.hiretaxi.dao.PostUtil;
import com.hiretaxi.dialog.DialogFactory;
import com.hiretaxi.fragment.base.BaseFragment;
import com.hiretaxi.helper.BmobPostDataCallbackListener;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.model.ImageBanner;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.util.StringUtil;
import com.hiretaxi.util.UserPermissionCheck;
import com.hiretaxi.view.recyclerViewConfig.RefreashRecyclerView;
import com.stx.xhb.xbanner.XBanner;
import com.stx.xhb.xbanner.transformers.Transformer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;

import static cn.bmob.v3.BmobUser.getCurrentUser;

public class HomeFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    private static final String TAG = "HomeFragment";
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
    private XBanner banner;
    private View searchView;

    private ArrayList<String> imgList, titleList;
    private List<ImageBanner> bannerList;
    private ImageView ivGif;

    @Override
    protected int getChildFragmentLayout() {
        return R.layout.fragment_home;
    }

    @Override
    protected void handViewEvent(View fragmentRootview) {
        user = getCurrentUser(CarUser.class);
        initView();
        initList();
        initAdapter();
        setListener();
        onRefresh();
        setBannerDataForDb();
        initDialog();
    }


    private void initDialog() {
        receiveNewMsgDialog = DialogFactory.getTwoBtnDialog(this.getContext(), "新消息提醒", "收到新的订单消息请查看",
                "下次吧", "我看看", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        receiveNewMsgDialog.dismiss();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        receiveNewMsgDialog.dismiss();
                        //刷新数据列表哦
                        onRefresh();
                    }
                });
    }

    //此处订阅接收后端增加对应车型的数据的时候，会发送一条消息提醒
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handReceiveNewMsg(BmobPostDataCallbackListener.PostNewMsg postNewMsg) {
        if (this != null && !this.getActivity().isFinishing()) {
            if (postNewMsg != null) {
                receiveNewMsgDialog.setTitleText(postNewMsg.getMsgContent() == null ? "" : postNewMsg.getMsgContent() + " 新消息提醒");
            }
            if (!receiveNewMsgDialog.isShowing()) {
                receiveNewMsgDialog.show();
            }
        }
    }

    private SweetAlertDialog receiveNewMsgDialog;

    private void setBannerDataForDb() {
        bannerList = ImageBannerUtil.getImageBannerUtilInstance(getContext()).queryAllImageBanner();
        if (bannerList != null && !bannerList.isEmpty()) {
            getBannerDataForDb(bannerList);
            Msg msg = new Msg(titleList, imgList, bannerList);
            EventBus.getDefault().post(msg);
        }
    }

    public void requestBannerData() {
        BmobQuery<ImageBanner> query = new BmobQuery<ImageBanner>();
        query.findObjects(new FindListener<ImageBanner>() {
            @Override
            public void done(List<ImageBanner> list, BmobException e) {
                if (e == null && list != null) {
                    imgList.clear();
                    titleList.clear();
                    getBannerDataForDb(list);
                    Msg msg = new Msg(titleList, imgList, list);
                    EventBus.getDefault().post(msg);
                    bannerList = list;
                } else {
//                    ToastUtil.showToast(getContext(), "请求失败");
                }
            }
        });

    }

    private void getBannerDataForDb(List<ImageBanner> list) {
        for (ImageBanner ib : list) {
            if (ib.getImgCacheUrl() != null) {
                imgList.add(ib.getImgCacheUrl());
            } else if (ib.getImgFile() != null) {
                imgList.add(ib.getImgFile().getFileUrl());
            } else {
                imgList.add(Constant.DEFACULT_IMG_URL);
            }
            titleList.add(ib.getTitle());
        }
    }

    //初始化控件
    private void initView() {
        //移除通知栏
        BmobPostDataCallbackListener.closeNotifaction();
        banner = findView(R.id.banner);
        // 设置XBanner的页面切换特效
        banner.setPageTransformer(Transformer.Default);
        // 设置XBanner页面切换的时间，即动画时长
        banner.setPageChangeDuration(2000);
        searchView = findView(R.id.searchView);
        recyclerView = findView(R.id.swipe_target);
        layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        floatingActionButton = findView(R.id.fab);
        entryView = findView(R.id.entryView);
        swipeToLoadLayout = findView(R.id.swipeToLoadLayout);

        ivGif = findView(R.id.iv_gif);
        BitmapUtil.loadImageForLocalAsGif(ivGif, R.mipmap.title, this.getActivity());
    }

    private void initList() {
        imgList = new ArrayList<String>();
        titleList = new ArrayList<String>();
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

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).start(SearchActivity.class);
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
        requestBannerData();
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

    private SweetAlertDialog permissionDialog;

    private void showPermissionDialog(String title, String content, String btnContent, SweetAlertDialog.OnSweetClickListener onSweetClickListener) {
        if (permissionDialog == null) {
            permissionDialog = DialogFactory.getWaringDialog(getContext(), title)
                    .setConfirmText("确定")
                    .setConfirmClickListener(onSweetClickListener);
        }
        permissionDialog.setTitleText(title);
        permissionDialog.setContentText(content);
        permissionDialog.setConfirmText(btnContent);
        permissionDialog.setConfirmClickListener(onSweetClickListener);
        if (!permissionDialog.isShowing()) {
            permissionDialog.show();
        }
    }

    public void refreashData() {
        final CarUser carUser = BmobUser.getCurrentUser(CarUser.class);
        if (carUser == null) {
            showToast("请重新登录");
            return;
        }

        boolean checkPermissionForCurrentUser = UserPermissionCheck.checkUserContent(getContext(),
                BmobUser.getCurrentUser(CarUser.class));

        if (!checkPermissionForCurrentUser) {
            showPermissionDialog("重要提示", "初次使用，您需要完善用户资料信息", "去完善", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ((BaseActivity) getActivity()).start(UserInfoActivity.class);
                    permissionDialog.dismiss();
                }
            });
            closeSwipeToLoadLayoutLoading();
            return;
        } else if (StringUtil.isEmpty(carUser.getCarType())) {
            showToast("后台车型信息正在审核中");
            closeSwipeToLoadLayoutLoading();
            return;
        }  else if (checkPermissionForCurrentUser && !StringUtil.isEmpty(carUser.getCarType())) {
            if (permissionDialog != null) {
                permissionDialog.dismiss();
            }
        }

        if (!NetUtils.isConnected(getContext())) {
            showToast(com.hiretaxi.config.Constant.NO_NET_CONNECT);
            closeSwipeToLoadLayoutLoading();
            return;
        }
        if (user == null) {
            showToast(Constant.NO_LOGIN_DISCONNECT);
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
                    HomeFragment.this.list.clear();
                    isShowEntryViweLayout(queryList);
                    //添加请求的数据
                    HomeFragment.this.list.addAll(queryList);
                    adapter.updateList(HomeFragment.this.list);
                } else {
                    Log.i(TAG, "done: errorCode===> " + e.getErrorCode() + " " + e.getMessage());
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
        bmobQuery.order("-updatedAt");
        bmobQuery.addWhereDoesNotExists(Post.INDENT_USER);//未订单
        bmobQuery.addWhereNotEqualTo(Post.STATE, true);//是否接单的状态
        String carType = BmobUser.getCurrentUser(CarUser.class).getCarType();

        if (carType != null && !carType.isEmpty()) {
            if (carType.equals(Constant.CAR_TYPE_ARRAY[0])) {//经济型
                bmobQuery.addWhereEqualTo(Post.CAR_TYPE, Constant.CAR_TYPE_ARRAY[0]);
            } else if (carType.equals(Constant.CAR_TYPE_ARRAY[1])) {//舒适型
                String[] names = {Constant.CAR_TYPE_ARRAY[0], Constant.CAR_TYPE_ARRAY[1]};
                bmobQuery.addWhereContainedIn(Post.CAR_TYPE, Arrays.asList(names));
            } else if (carType.equals(Constant.CAR_TYPE_ARRAY[2])) {//商务型
                String[] names = {Constant.CAR_TYPE_ARRAY[0], Constant.CAR_TYPE_ARRAY[1], Constant.CAR_TYPE_ARRAY[2]};
                bmobQuery.addWhereContainedIn(Post.CAR_TYPE, Arrays.asList(names));
            } else if (carType.equals(Constant.CAR_TYPE_ARRAY[3])) {//豪华型
                String[] names = {Constant.CAR_TYPE_ARRAY[0], Constant.CAR_TYPE_ARRAY[1], Constant.CAR_TYPE_ARRAY[3]};
                bmobQuery.addWhereContainedIn(Post.CAR_TYPE, Arrays.asList(names));
            } else if (carType.equals(Constant.CAR_TYPE_ARRAY[4])) {
                bmobQuery.addWhereEqualTo(Post.CAR_TYPE, Constant.CAR_TYPE_ARRAY[4]);
            }
        }
    }

    //设置适配器
    private void initAdapter() {
        this.list = new ArrayList<Post>();
        readDbData();//读取数据库缓存
        adapter = new PostAdapter(this, list, R.layout.home_item);
        recyclerView.setAdapter(adapter);
        isShowEntryViweLayout(list);
    }

    @Nullable
    private void readDbData() {
        List<Post> queryList = PostUtil.getPostUtilInstance(getContext()).queryAllPost();
        if (queryList == null || queryList.isEmpty()) {
            return;
        }
        for (Post post : queryList) {
            if (post != null && (post.getSaveDbObjectId() == null || post.getSaveDbObjectId().equals("null")) &&
                    (post.getState() == null || !post.getState().booleanValue())) {
                this.list.add(post);
            }
        }
    }

    /**
     * 为了更好的体验效果建议在下面两个生命周期中调用下面的方法
     **/
    @Override
    public void onResume() {
        super.onResume();
        if (banner != null) {
            banner.startAutoPlay();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        banner.stopAutoPlay();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroy() {
        if (list != null) {
            PostUtil.getPostUtilInstance(getContext()).deleteAll();
            for (int i = 0; i < this.list.size(); i++) {
                if (this.list.get(i) != null) {
                    this.list.get(i).setSaveUpdateTime(this.list.get(i).getUpdatedAt());
                    if (list.get(i).getCarImgFile() != null) {
                        this.list.get(i).setImgUrlCache(this.list.get(i).getCarImgFile().getFileUrl());
                    }
                }

            }
            PostUtil.getPostUtilInstance(this.getContext()).insertMultpost(this.list);
        }

        if (bannerList != null) {
            ImageBannerUtil.getImageBannerUtilInstance(getContext()).deleteAll();
            for (int i = 0; i < bannerList.size(); i++) {
                if (bannerList.get(i) != null && bannerList.get(i).getImgFile() != null) {
                    bannerList.get(i).setImgCacheUrl(bannerList.get(i).getImgFile().getFileUrl());
                }
            }
            ImageBannerUtil.getImageBannerUtilInstance(getContext()).insertMultImageBanner(bannerList);
        }

        EventBus.getDefault().unregister(this);

        Glide.clear(ivGif);
        super.onDestroy();
    }

    private class Msg {
        private ArrayList<String> titleList, imgList;
        private List<ImageBanner> bannerArrayList;

        public Msg(ArrayList<String> titleList, ArrayList<String> imgList, List<ImageBanner> bannerList) {
            this.titleList = titleList;
            this.imgList = imgList;
            this.bannerArrayList = bannerList;
        }

        public List<ImageBanner> getBannerArrayList() {
            return bannerArrayList;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlerBannerData(Msg msg) {
        setBanner(msg);
    }

    private void setBanner(final Msg msg) {
        // 为XBanner绑定数据
        int count = banner.getChildCount();
        for (int i = 0; i < count; i++) {
            View childView = banner.getChildAt(i);
            if (childView instanceof ImageView) {
                banner.removeView(childView);
            }
            continue;
        }
        banner.invalidate();
        try {
            banner.setData(imgList, titleList);
        } catch (Exception e) {
            Log.i(TAG, "setBanner: e===> " + e.getMessage());
        }
        // XBanner适配数据
        banner.setmAdapter(new XBanner.XBannerAdapter() {
            @Override
            public void loadBanner(XBanner banner, View view, int position) {
                loadImg(imgList.get(position), (ImageView) view);
            }
        });

        // XBanner中某一项的点击事件
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                WebActivity.startActivity(getContext(), titleList.get(position), msg.getBannerArrayList().get(position).getSrcUrl());
            }
        });
    }

    private void loadImg(String imgUrl, ImageView iv) {
        if (imgUrl != null && imgUrl.toLowerCase().endsWith("gif")) {
            BitmapUtil.loadImage2(iv, imgUrl, this.getContext());
        } else {
            BitmapUtil.loadImageBanner(iv, imgUrl, getContext());
        }
    }

    public static int REFREASH_INDEX = -1;//标记是否需要刷新界面
    public static boolean NEED_REFRESH = false;//是否修改用户车型自动刷新功能

    @Override
    public void onStart() {
        super.onStart();
        if (REFREASH_INDEX != -1) {
            list.remove(REFREASH_INDEX);
            adapter.updateList(list);
            adapter.notifyDataSetChanged();
            REFREASH_INDEX = -1;
            isShowEntryViweLayout(list);
        }

        if (NEED_REFRESH) {
            refreashData();
            NEED_REFRESH = false;
        }

    }
}
