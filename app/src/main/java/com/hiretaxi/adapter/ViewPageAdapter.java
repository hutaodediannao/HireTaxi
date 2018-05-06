package com.hiretaxi.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.hiretaxi.R;

import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片浏览ViewPageAdapter
 * Created by Jelly on 2016/3/10.
 */
public class ViewPageAdapter extends PagerAdapter {
    private Context context;
    private List<String> images;
    private SparseArray<View> cacheView;
    private ViewGroup containerTemp;

    public ViewPageAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
        cacheView = new SparseArray<>(images.size());
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        if(containerTemp == null) containerTemp = container;

        View view = cacheView.get(position);

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.vp_item_image,container,false);
            view.setTag(position);
            final ImageView image = (ImageView) view.findViewById(R.id.image);
            final PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(image);

            Glide.with(context).load(images.get(position))
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(new GlideDrawableImageViewTarget(image) {

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    // 开始加载图片
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    // 图片加载完成
                    photoViewAttacher.update();
                }
            });

            photoViewAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    Activity activity = (Activity) context;
                    activity.finish();
                }
            });
            cacheView.put(position,view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

}
