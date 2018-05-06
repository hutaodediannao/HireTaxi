package com.hiretaxi.adapter.commonAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hiretaxi.adapter.commonViewHolder.CommonViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */
public abstract class BaseAdapter<T> extends CommonAdapter {

    public List<T> mList;
    public int itemLayout;

    public BaseAdapter(Context mContext, List<T> list, int itemLayout) {
        super(mContext);
        this.mList = list;
        this.itemLayout = itemLayout;
    }

    public BaseAdapter(Fragment mContext, List<T> list, int itemLayout) {
        super(mContext.getContext(), mContext);
        this.mList = list;
        this.itemLayout = itemLayout;
    }

    public void updateList(List<T> list) {
        this.mList = list;
        this.notifyDataSetChanged();
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(itemLayout, null);
        CommonViewHolder commonViewHolder = null;
        if (mFragment == null) {
            commonViewHolder = CommonViewHolder.commonViewHolderNewInstance(itemView, mContext);
        } else {
            commonViewHolder = CommonViewHolder.commonViewHolderNewInstance2(itemView, mContext, mFragment);
        }
        return commonViewHolder;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        bindViewHolder(holder, position, mList.get(position));
        holder.getItemView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickItemListener != null) {
                    clickItemListener.clickItem(mList.get(position), position);
                }
            }
        });

        holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (longClickListener != null) {
                    longClickListener.longClickItem(mList.get(position));
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public abstract void bindViewHolder(CommonViewHolder holder, int position, T t);

    public interface ClickItemListener<T> {
        void clickItem(T t, int position);
    }

    private ClickItemListener clickItemListener;

    public void setClickItemListener(ClickItemListener clickItemListener) {
        this.clickItemListener = clickItemListener;
    }

    public interface LongClickListener<T> {
        void longClickItem(T t);
    }

    private LongClickListener longClickListener;

    public void setLongClickListener(LongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}
