package com.hiretaxi.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.hiretaxi.R;
import com.hiretaxi.adapter.commonAdapter.BaseAdapter;
import com.hiretaxi.adapter.commonViewHolder.CommonViewHolder;
import com.hiretaxi.model.Post;
import com.hiretaxi.util.DateUtils;
import com.hiretaxi.util.DensityUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class PostAdapter extends BaseAdapter<Post> {

    public PostAdapter(Context mContext, List<Post> list, int itemLayout) {
        super(mContext, list, itemLayout);
    }

    public PostAdapter(Fragment fragment, List<Post> list, int itemLayout) {
        super(fragment, list, itemLayout);
    }

    @Override
    public void bindViewHolder(CommonViewHolder holder, int position, Post post) {
        if (post == null) {
            return;
        }
        if (post.getImgUrlCache() != null) {
            holder.setImageView(R.id.ivHeader, post.getImgUrlCache(),
                    DensityUtils.dp2px(mContext, 90), DensityUtils.dp2px(mContext, 75));
        } else if (post.getCarImgFile() != null) {
            holder.setImageView(R.id.ivHeader, post.getCarImgFile().getFileUrl(),
                    DensityUtils.dp2px(mContext, 90), DensityUtils.dp2px(mContext, 75));
        } else {
            setImageViewForType(holder, post.getCarType());
        }

        String time = post.getSaveUpdateTime() == null ? post.getUpdatedAt() : post.getSaveUpdateTime();
        String resultTime = "";
        if (time != null && null != DateUtils.string2date(time)) {
            Date date = DateUtils.string2date(time);
            resultTime = DateUtils.getFriendlyTime(date);
        }

        String startLocation = post.getStartLocation() == null ? "" : post.getStartLocation();
        String endLocation = post.getEndLocation() == null ? "" : post.getEndLocation();
        holder.setTextView(R.id.tvTitle, post.getTitle() == null ? "" : post.getTitle())
//                setTextView(R.id.tvTitle, startLocation +" --> "+ endLocation)
                .setTextView(R.id.tvPrice, post.getPrice() == null ? "未知" : post.getPrice())
                .setTextView(R.id.tvTime, resultTime == null ? "" : resultTime);
    }

    private void setImageViewForType(CommonViewHolder holder, String carType) {
        if (carType == null || carType.trim().isEmpty()) {
            holder.setImageView(R.id.ivHeader, R.mipmap.jing_ji);
        } else if (carType.equals("经济型")) {
            holder.setImageView(R.id.ivHeader, R.mipmap.jing_ji);
        } else if (carType.equals("舒适型")) {
            holder.setImageView(R.id.ivHeader, R.mipmap.shu_shi);
        } else if (carType.equals("商务型")) {
            holder.setImageView(R.id.ivHeader, R.mipmap.shang_wu);
        } else if (carType.equals("豪华型")) {
            holder.setImageView(R.id.ivHeader, R.mipmap.hao_hua);
        } else if (carType.equals("出租车")) {
            holder.setImageView(R.id.ivHeader, R.mipmap.taxi);
        } else {
            holder.setImageView(R.id.ivHeader, R.mipmap.jing_ji);
        }

    }
}
