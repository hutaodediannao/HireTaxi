package com.hiretaxi.adapter;

import android.content.Context;

import com.hiretaxi.R;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.adapter.commonViewHolder.CommonViewHolder;
import com.hiretaxi.model.Notifaction;
import com.hiretaxi.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/18.
 */

public class MsgAdapter extends BaseAdapter<Notifaction> {

    public MsgAdapter(Context mContext, List<Notifaction> list, int itemLayout) {
        super(mContext, list, itemLayout);
    }

    @Override
    public void bindViewHolder(CommonViewHolder holder, int position, Notifaction notifaction) {

        String resultTime = "";
        long time = notifaction.get_id();
        Date date = new Date(time);
        resultTime = DateUtils.getFriendlyTime(date);

        holder.setTextView(R.id.tv_content, notifaction.getContent())
                .setTextView(R.id.tvTime, resultTime == null ? "" : resultTime);
    }
}
