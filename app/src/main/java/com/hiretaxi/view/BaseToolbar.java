package com.hiretaxi.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hiretaxi.R;

/**
 * Created by Administrator on 2017/7/12.
 */

public class BaseToolbar extends FrameLayout {

    private View headerView;
    private ImageView ivLeft, ivRight;
    private TextView tvTitle;

    public BaseToolbar(@NonNull Context context) {
        this(context, null);
    }

    public BaseToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseToolbar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setViewVisibity(true, true, false);
        setConfig(attrs);
        setListener();
    }

    private void setConfig(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.HeaderView);
        int ivLeftResId = typedArray.getResourceId(R.styleable.HeaderView_ivLeftSrc, R.drawable.ic_keyboard_arrow_left_black_24dp);
        int ivRightResId = typedArray.getResourceId(R.styleable.HeaderView_ivRightSrc, R.drawable.ic_chevron_right_black_24dp);
        String title = typedArray.getString(R.styleable.HeaderView_tvCenterContent);
        int colorRes = typedArray.getResourceId(R.styleable.HeaderView_headerbackground, R.color.royalblue);
        boolean leftVisibity = typedArray.getBoolean(R.styleable.HeaderView_left_visibity, true);
        boolean rightVisibity = typedArray.getBoolean(R.styleable.HeaderView_right_visibity, true);

        setIvLeft(ivLeftResId);
        setTvTitle(title);
        setIvRight(ivRightResId);
        headerView.setBackgroundResource(colorRes);
        if (leftVisibity) {
            ivLeft.setVisibility(View.VISIBLE);
        } else {
            ivLeft.setVisibility(View.GONE);
        }
        if (rightVisibity) {
            ivRight.setVisibility(View.VISIBLE);
        } else {
            ivRight.setVisibility(View.GONE);
        }

        typedArray.recycle();
    }

    public void setIvLeft(int ivLeftResId) {ivLeft.setImageResource(ivLeftResId);}

    public void setIvRight(int ivRightResId) {
        ivRight.setImageResource(ivRightResId);
    }


    private void setListener() {
        ivLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarClickListener != null) {
                    toolbarClickListener.clickLeft();
                } else {
                    if (getContext() instanceof Activity && !((Activity)getContext()).isFinishing()) {
                        ((Activity)getContext()).finish();
                    }
                }
            }
        });

        ivRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarClickListener != null) {
                    toolbarClickListener.clickRight();
                }
            }
        });
    }

    private void initView() {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.toolbar_lay, null);
        ivLeft = (ImageView) headerView.findViewById(R.id.iv_left);
        ivRight = (ImageView) headerView.findViewById(R.id.iv_right);
        tvTitle = (TextView) headerView.findViewById(R.id.tv_title);
        this.addView(headerView);
    }

    public void setTvTitle(String contentTitle) {
        tvTitle.setText(contentTitle == null ? "" : contentTitle);
    }

    public void setViewVisibity(boolean leftVisibity, boolean tvTitleVisibity, boolean rightVisibity) {
        if (leftVisibity) {
            ivLeft.setVisibility(View.VISIBLE);
        } else {
            ivLeft.setVisibility(View.GONE);
        }

        if (tvTitleVisibity) {
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }

        if (rightVisibity) {
            ivRight.setVisibility(View.VISIBLE);
        } else {
            ivRight.setVisibility(View.GONE);
        }
    }

    private ToolbarClickListener toolbarClickListener;

    public void setToolbarClickListener(ToolbarClickListener toolbarClickListener) {
        this.toolbarClickListener = toolbarClickListener;
    }

    public interface ToolbarClickListener {
        void clickLeft();
        void clickRight();
    }

}
