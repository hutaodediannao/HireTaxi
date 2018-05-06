package com.hiretaxi.adapter.commonViewHolder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiretaxi.util.BitmapUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.stx.xhb.xbanner.XBanner;

/**
 * Created by Administrator on 2017/6/13.
 */
public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> viewSparseArray;
    private View mItemView;
    private Context mContext;
    private XBanner banner;
    private Fragment mFragment;

    public static CommonViewHolder commonViewHolderNewInstance(View itemView, Context context) {
        CommonViewHolder commonViewHolder = null;
        if (itemView.getTag() == null) {
            commonViewHolder = new CommonViewHolder(itemView, context);
        } else {
            commonViewHolder = (CommonViewHolder) itemView.getTag();
        }
        return commonViewHolder;
    }

    public static CommonViewHolder commonViewHolderNewInstance2(View itemView, Context context, Fragment fragment) {
        CommonViewHolder commonViewHolder = null;
        if (itemView.getTag() == null) {
            commonViewHolder = new CommonViewHolder(itemView, context, fragment);
        } else {
            commonViewHolder = (CommonViewHolder) itemView.getTag();
        }
        return commonViewHolder;
    }

    public CommonViewHolder(View itemView, Context context) {
        super(itemView);
        this.viewSparseArray = new SparseArray<View>();
        this.mItemView = itemView;
        this.mContext = context;
        itemView.setTag(this);
    }

    public CommonViewHolder(View itemView, Context context, Fragment fragment) {
        super(itemView);
        this.viewSparseArray = new SparseArray<View>();
        this.mItemView = itemView;
        this.mContext = context;
        this.mFragment = fragment;
        itemView.setTag(this);
    }

    private <T extends View> T getView(int viewID) {
        View view = viewSparseArray.get(viewID);
        if (view == null) {
            view = mItemView.findViewById(viewID);
            viewSparseArray.put(viewID, view);
        }
        return (T) view;
    }

    public CommonViewHolder setTextView(int viewId, String conent) {
        TextView tv = getView(viewId);
        tv.setText(conent == null ? "" : conent);
        return this;
    }

    public CommonViewHolder setImageView(int imageViewId, String imgUrl) {
        ImageView iv = getView(imageViewId);
        BitmapUtil.loadImage3(iv, imgUrl, mContext);
        return this;
    }

    public CommonViewHolder setImageView(int imageViewId, String imgUrl, int width, int height) {
        RoundedImageView iv = getView(imageViewId);
        if (null == mFragment) {
            BitmapUtil.loadImage2(iv, imgUrl, mContext, width, height);
        } else {
            BitmapUtil.loadImage2ForFragment(iv, imgUrl, mFragment, width, height);
        }
        return this;
    }

    public CommonViewHolder setImageView(int imageViewId, int resourceId) {
        ImageView iv = getView(imageViewId);
        iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        iv.setImageBitmap(BitmapUtil.getBitmap(resourceId, mContext));
        return this;
    }

    public View getItemView() {
        return mItemView;
    }

}
