package com.hiretaxi.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.ToolbarBaseActivity;
import com.hiretaxi.adapter.MsgAdapter;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.config.Constant;
import com.hiretaxi.dao.NotifactionUtil;
import com.hiretaxi.dialog.DialogFactory;
import com.hiretaxi.model.Notifaction;
import com.hiretaxi.util.NetUtils;
import com.hiretaxi.view.BaseToolbar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MessageActivity extends ToolbarBaseActivity {

    private SweetAlertDialog deleteDialog, longDeleteDialog;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MsgAdapter adapter;
    private List<Notifaction> list;
    private View entryView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setDefacultTitle("消息");
        ((BaseToolbar) toolbarView).setViewVisibity(true, true, true);
        ((BaseToolbar) toolbarView).setIvRight(R.drawable.ic_delete);

        initView();
        initList();
        setAdapter();
        setListener();

//        requestNetData();
        readNoficationMsg();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveNewMsg(Boolean isSaveOk) {
        if (this != null && !this.isFinishing() && isSaveOk) {
            readNoficationMsg();
        }
    }

    private void readNoficationMsg() {
        list = NotifactionUtil.getNotifactionUtilInstance(this).queryAllNotifaction();
        if (list == null) {
            return;
        }

        Collections.sort(list,new Comparator<Notifaction>() {
            @Override
            public int compare(Notifaction o1, Notifaction o2) {
                return (int) (o2.get_id()-o1.get_id());
            }
        });

        adapter.updateList(list);
        adapter.notifyDataSetChanged();
        if (recyclerView.getChildCount() > 0) {
            recyclerView.scrollToPosition(0);
        }
        isShowRefreshing(false);
        if (list.isEmpty()) {
            isShowEntryView(true);
        } else {
            isShowEntryView(false);
        }
    }

    private void setAdapter() {
        adapter = new MsgAdapter(this, list, R.layout.msg_item_lay);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayout.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void initList() {
        list = new ArrayList<Notifaction>();
    }

    private void setListener() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                requestNetData();
                readNoficationMsg();
            }
        });

        ((BaseToolbar) toolbarView).setToolbarClickListener(new BaseToolbar.ToolbarClickListener() {
            @Override
            public void clickLeft() {
                finish();
            }

            @Override
            public void clickRight() {
                deleteDialog.show();
            }
        });

        adapter.setLongClickListener(new BaseAdapter.LongClickListener() {
            @Override
            public void longClickItem(Object o) {
                final Notifaction notifaction = (Notifaction) o;
                longDeleteDialog = DialogFactory.getTwoBtnDialog(MessageActivity.this, "确定清除吗?", "确定将会删除该条数据", "取消", "确定",
                        new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                longDeleteDialog.dismiss();
                            }
                        }, new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                if (NotifactionUtil.getNotifactionUtilInstance(MessageActivity.this).deletePost(notifaction)) {
                                    list = NotifactionUtil.getNotifactionUtilInstance(MessageActivity.this).queryAllNotifaction();
                                    adapter.updateList(list);
                                    adapter.notifyDataSetChanged();
                                    showToast("清除成功！");
                                    longDeleteDialog.dismiss();
                                } else {
                                    showToast("清除失败！");
                                    longDeleteDialog.dismiss();
                                }
                                if (list.isEmpty()) {
                                    isShowEntryView(true);
                                } else {
                                    isShowEntryView(false);
                                }
                            }
                        });
                longDeleteDialog.show();
            }
        });
    }

    private void requestNetData() {
        if (!NetUtils.isConnected(this)) {
            showToast(Constant.NO_NET_CONNECT);
            return;
        }
        isShowRefreshing(true);
        BmobQuery<Notifaction> query = new BmobQuery<Notifaction>();
        query.findObjects(new FindListener<Notifaction>() {
            @Override
            public void done(List<Notifaction> list, BmobException e) {
                if (e == null) {
                    if (list != null && !list.isEmpty()) {
                        MessageActivity.this.list = list;
                        adapter.updateList(list);
                    }
                }
                isShowRefreshing(false);
            }
        });
    }

    private void initView() {
        recyclerView = findView(R.id.recyclerView);
        entryView = findView(R.id.entryView);
        swipeRefreshLayout = findView(R.id.msgRefreashLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.red,
                R.color.blue,
                R.color.yellow,
                R.color.green,
                R.color.black
        );
        deleteDialog = DialogFactory.getTwoBtnDialog(this, "确定清除吗?", "确定将会删除所有内容", "取消", "确定",
                new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteDialog.dismiss();
                    }
                }, new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        if (NotifactionUtil.getNotifactionUtilInstance(MessageActivity.this).deleteAll()) {
                            list = NotifactionUtil.getNotifactionUtilInstance(MessageActivity.this).queryAllNotifaction();
                            adapter.updateList(list);
                            adapter.notifyDataSetChanged();
                            showToast("清除成功！");
                            deleteDialog.dismiss();
                        } else {
                            showToast("清除失败！");
                            deleteDialog.dismiss();
                        }

                        if (list.isEmpty()) {
                            isShowEntryView(true);
                        } else {
                            isShowEntryView(false);
                        }
                    }
                });
    }

    @Override
    protected View getToolbarView() {
        return null;
    }

    private void isShowRefreshing(boolean isRefresh) {
        if (isRefresh) {
            if (!swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(true);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void isShowEntryView(boolean isShow) {
        if (isShow) {
            entryView.setVisibility(View.VISIBLE);
        } else {
            entryView.setVisibility(View.GONE);
        }
    }
}
