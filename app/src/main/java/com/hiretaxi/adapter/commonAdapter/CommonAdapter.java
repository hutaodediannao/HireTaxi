package com.hiretaxi.adapter.commonAdapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hiretaxi.adapter.commonViewHolder.CommonViewHolder;


/**
 * Created by Administrator on 2017/6/13.
 */
public class CommonAdapter extends RecyclerView.Adapter<CommonViewHolder> {

    public Context mContext;
    public Fragment mFragment;

    public CommonAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public CommonAdapter(Context context, Fragment fragment) {
        this.mContext = context;
        this.mFragment = fragment;
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


}
