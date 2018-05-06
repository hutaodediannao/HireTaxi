package com.hiretaxi.view.recyclerViewConfig;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/3/22.
 */
public class RefreashRecyclerView extends RecyclerView {

    private boolean isAbleTouchScroll = true;//能否使用手滑动列表

    public void setAbleTouchScroll(boolean ableTouchScroll) {
        isAbleTouchScroll = ableTouchScroll;
    }

    public RefreashRecyclerView(Context context) {
        super(context);
    }

    public RefreashRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreashRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private boolean isUpFinger = false;//手指是否离开该控件

    public boolean isUpFinger() {
        return isUpFinger;
    }

    private void setUpFinger(boolean upFinger) {
        isUpFinger = upFinger;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        setUpFinger(false);
        if (isAbleTouchScroll) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_UP:
                    setUpFinger(true);
                    break;
            }
            return super.onTouchEvent(e);
        } else {
            return false;
        }
    }

}
