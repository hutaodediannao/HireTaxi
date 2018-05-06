package com.hiretaxi.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.ScreenUtils;

/**
 * Created by Administrator on 2017/7/13.
 */

public class CustomImageView extends ImageView {


    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(
                (ScreenUtils.getScreenWidth(getContext()) - DensityUtils.dp2px(getContext(), 40))/ 3 ,
                (ScreenUtils.getScreenWidth(getContext()) - DensityUtils.dp2px(getContext(), 40))/ 3
        );
    }

}
