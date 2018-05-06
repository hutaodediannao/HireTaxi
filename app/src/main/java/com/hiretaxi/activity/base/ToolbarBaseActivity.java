package com.hiretaxi.activity.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hiretaxi.R;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.ScreenUtils;
import com.hiretaxi.view.BaseToolbar;

/**
 * Created by Administrator on 2017/7/11.
 */

public abstract class ToolbarBaseActivity extends BaseActivity {

    private LinearLayout mRootView;
    private FrameLayout contentRootView;
    private Toolbar mToolbar;
    public View toolbarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_toolbase, null);
        contentRootView = (FrameLayout) mRootView.findViewById(R.id.contentView);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View childView = LayoutInflater.from(this).inflate(layoutResID, null);
        contentRootView.addView(childView);
        this.setContentView(mRootView);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initToolbar();
    }

    private void initToolbar() {
        mToolbar = findView(R.id.toolbar);
         toolbarView = getToolbarView();
        if (toolbarView == null) {
            toolbarView = new BaseToolbar(this);
        }
        Toolbar.LayoutParams toolbarLayoutParmas = new Toolbar.LayoutParams(
                ScreenUtils.getScreenWidth(this),
                DensityUtils.dp2px(getApplicationContext(), 50)
        );
        mToolbar.addView(toolbarView, toolbarLayoutParmas);
        setSupportActionBar(mToolbar);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        this.setContentView(view);
    }

    public void setDefacultTitle(String title) {
        if (getToolbarView() == null) {
            if (toolbarView != null) {
                ((BaseToolbar)toolbarView).setTvTitle(title);
            }
        }
    }

    protected abstract View getToolbarView();


}
