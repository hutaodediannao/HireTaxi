package com.hiretaxi.activity;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.hiretaxi.R;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.model.CarUser;
import com.hiretaxi.util.BitmapUtil;
import com.hiretaxi.util.DensityUtils;
import com.hiretaxi.util.SPUtils;
import com.hiretaxi.view.SplashView;

import java.util.LinkedList;

import cn.bmob.v3.BmobUser;

import static com.hiretaxi.activity.StartActivity.IS_FIRST_GO_APP;

public class SplashActivity extends BaseActivity {

    private SplashView splashView;
    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private LinkedList<ImageView> imageViews;
    private View startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        initAdapter();
        setListener();

    }

    private boolean isEndPager = false;

    private void setListener() {
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                splashView.setCheckIndexUpdate(position);
                if (position == imageViews.size() - 1) {
                    isShowAnimation(true);
                    isEndPager = true;
                } else {
                    if (isEndPager) {
                        isShowAnimation(false);
                        isEndPager = false;
                    }
                }
            }
        });
    }

    public void isShowAnimation(boolean isShow) {
        if (isShow) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(startBtn, "translationY", 0, -DensityUtils.dp2px(this, 115));
            anim.setDuration(500);
            anim.start();
        } else {
            ObjectAnimator anim = ObjectAnimator.ofFloat(startBtn, "translationY", -DensityUtils.dp2px(this, 115), 0);
            anim.setDuration(500);
            anim.start();
        }
    }

    private void initAdapter() {
        imageViews = new LinkedList<ImageView>();
        ImageView iv0 = new ImageView(this);
        ImageView iv1 = new ImageView(this);
        ImageView iv2 = new ImageView(this);
        ImageView iv3 = new ImageView(this);

        iv0.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        iv3.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        iv0.setImageBitmap(BitmapUtil.getBitmap3(R.mipmap.a, this));
//        iv1.setImageBitmap(BitmapUtil.getBitmap3(R.mipmap.title, this));
//        iv2.setImageBitmap(BitmapUtil.getBitmap3(R.mipmap.b, this));
//        iv3.setImageBitmap(BitmapUtil.getBitmap3(R.mipmap.c, this));

        BitmapUtil.loadImageForLocal(iv0, R.mipmap.a, this);
        BitmapUtil.loadImageForLocal(iv1, R.mipmap.b, this);
        BitmapUtil.loadImageForLocal(iv2, R.mipmap.c, this);
        BitmapUtil.loadImageForLocalAsGif(iv3, R.mipmap.d, this);

        imageViews.add(iv0);
        imageViews.add(iv1);
        imageViews.add(iv2);
        imageViews.add(iv3);

        adapter = new MyPagerAdapter();
        viewPager.setAdapter(adapter);
    }

    private void initView() {
        viewPager = findView(R.id.viewPager);
        splashView = findView(R.id.splashView);
        startBtn = findView(R.id.startBtn);
    }

    public void startGon(View view) {
        CarUser trUser = BmobUser.getCurrentUser(CarUser.class);
        if (trUser == null) {
            start(LoginActivity.class);
        } else {
            start(MainActivity.class);
        }
        SPUtils.put(this, IS_FIRST_GO_APP, false);
        finish();
    }

    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ((ViewPager) container).addView(imageViews.get(position));
            return imageViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(imageViews.get(position));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < imageViews.size(); i++) {
            Glide.clear(imageViews.get(i));
        }
    }
}
