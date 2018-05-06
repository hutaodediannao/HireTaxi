package com.hiretaxi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.hiretaxi.R;

/**
 * Created by Administrator on 2017/7/21.
 */

public class SplashView extends View {

    private Paint blodPaint, circlePaint;

    private int color;
    private int circleRadiusSize;
    private int cirlceDisplay;

    public SplashView(Context context) {
        this(context, null);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SplashView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MySplashView);
        color = typedArray.getResourceId(R.styleable.MySplashView_circleSplashColor, R.color.white);
        circleRadiusSize = typedArray.getInt(R.styleable.MySplashView_circleSplashRadius, R.dimen._10dp);
        cirlceDisplay = typedArray.getInt(R.styleable.MySplashView_circleSplashDisplay, R.dimen._20dp);
        typedArray.recycle();
        initPaint();
    }

    private void initPaint() {
        blodPaint = new Paint();
        blodPaint.setAntiAlias(true);
        blodPaint.setStyle(Paint.Style.STROKE);
        blodPaint.setColor(color);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(color);
    }

    private int mCheckIndex = 0;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setCheckForIndex(mCheckIndex, canvas);
    }

    private void setCheckForIndex(int checkForIndex, Canvas canvas) {
        switch (checkForIndex) {
            case 0:
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, blodPaint);
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                break;
            case 1:
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, blodPaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                break;
            case 2:
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, blodPaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                break;
            case 3:
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 - cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay / 2, getMeasuredHeight() / 2, circleRadiusSize, circlePaint);
                canvas.drawCircle(getMeasuredWidth() / 2 + cirlceDisplay * 3 / 2, getMeasuredHeight() / 2, circleRadiusSize, blodPaint);
                break;
        }
    }

//    刷新界面
    public void setCheckIndexUpdate(int checkIndexUpdate) {
        this.mCheckIndex = checkIndexUpdate;
        this.invalidate();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = 0, height = 0;
//        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) {
//            width = MeasureSpec.getSize(widthMeasureSpec);
//        } else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
//            width = Math.min(MeasureSpec.getSize(widthMeasureSpec), ScreenUtils.getScreenWidth(getContext()));
//        }
//
//        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
//            height = MeasureSpec.getSize(heightMeasureSpec);
//        } else if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
//            height = Math.min(MeasureSpec.getSize(heightMeasureSpec), ScreenUtils.getScreenHeight(getContext()));
//        }
//
//        setMeasuredDimension(width, height);
//    }
}
