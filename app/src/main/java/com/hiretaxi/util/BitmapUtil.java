package com.hiretaxi.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hiretaxi.R;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;

/**
 * Created by Administrator on 2017/6/13.
 */
public class BitmapUtil {

    public static void loadImage(ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .placeholder(R.mipmap.no_picture)
                .error(R.mipmap.no_picture)
                .crossFade(1000)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadImage2(final ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .asGif()
//                .animate(R.anim.crop_image_fade_anim)
                .override(ScreenUtils.getScreenWidth(context), DensityUtils.dp2px(context, 200))
                .placeholder(R.mipmap.no_picture)
                .error(R.mipmap.no_picture)
                .centerCrop()
                .thumbnail(0.2f)//加载缩微图
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(iv);;
    }

    public static void loadImageBanner(final ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .placeholder(R.mipmap.no_picture)
                .error(R.mipmap.no_picture)
                .centerCrop()
                .override(ScreenUtils.getScreenWidth(context), DensityUtils.dp2px(context, 200))
                .skipMemoryCache(true)
                .thumbnail(0.2f)//加载缩微图
//                .transform(new GlideRoundTransform(context))
                .animate(R.anim.crop_image_fade_anim)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new BitmapImageViewTarget(iv){
                    @Override
                    protected void setResource(Bitmap resource) {
                        iv.setImageBitmap(resource);
                    }
                });
    }

    public static void loadImage3(ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .crossFade(1000)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadImage4(ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadBlurTransImage(ImageView iv, String imgUrl, Context context) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .crossFade(1000)
                .centerCrop()
                .bitmapTransform(new BlurTransformation(context, 14, 4))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadBlurTransImage2(ImageView iv, String imgUrl, Context context, int width, int height) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .centerCrop()
                .override(width, height)
                .bitmapTransform(new BlurTransformation(context, 14, 4))
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadImage2(final ImageView iv, String imgUrl, Context context, int width, int height) {
        if (imgUrl == null || iv == null || context == null) {
            iv.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.no_picture, context));
            return;
        }
        Glide.with(context)
                .load(imgUrl)
                .asBitmap()
                .placeholder(R.mipmap.no_picture)
                .error(R.mipmap.no_picture)
                .centerCrop()
                .override(width, height)
                .skipMemoryCache(false)
//                .thumbnail(0.f)//加载缩微图
//                .transform(new GlideRoundTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .into(iv)
//        如果通过Glide的into()方法直接传入该控件的引用就会出现加载图片到控件上时先显示直角图片，
//        再显示圆角图片。这个时候如果采用了into()方法里面传入BitmapImageViewTarget对象的话就能避免这个问题。
//        into方法使用的方式如下:
                .into(new BitmapImageViewTarget(iv){
                    @Override
                    protected void setResource(Bitmap resource) {
                        iv.setImageBitmap(resource);
                    }
                });
    }

    public static void loadImage2ForFragment(final ImageView iv, String imgUrl, Fragment fragment, final int width, int height) {
        if (imgUrl == null || iv == null) {
            iv.setImageBitmap(BitmapUtil.getBitmap(R.mipmap.no_picture, fragment.getContext()));
            return;
        }
        Glide.with(fragment)
                .load(imgUrl)
                .asBitmap()
                .placeholder(R.mipmap.no_picture)
                .error(R.mipmap.no_picture)
                .centerCrop()
                .override(width, height)
                .skipMemoryCache(false)
//                .thumbnail(0.f)//加载缩微图
//                .transform(new GlideRoundTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
//                .into(iv)
//        如果通过Glide的into()方法直接传入该控件的引用就会出现加载图片到控件上时先显示直角图片，
//        再显示圆角图片。这个时候如果采用了into()方法里面传入BitmapImageViewTarget对象的话就能避免这个问题。
//        into方法使用的方式如下:
                .into(new BitmapImageViewTarget(iv){
                    @Override
                    protected void setResource(Bitmap resource) {
                        iv.setImageBitmap(resource);
                    }
                });

    }

    public static void loadImageForLocal(ImageView iv, int imgResId, Context context) {
        if (imgResId == 0 || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgResId)
//                .crossFade(1000)
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    public static void loadImageForLocalAsGif(ImageView iv, int imgResId, Context context) {
        if (imgResId == 0 || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(imgResId)
                .asGif()
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(iv);
    }

    public static void loadImageForLocal(ImageView iv, String path, Context context, int width, int height) {
        if (path == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(new File(path))
                .fitCenter()
                .centerCrop()
                .crossFade(1000)
                .override(width, height)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new BlurTransformation(context, 20, 4))
                .into(iv);
    }

    public static void loadImageForLocalPath(ImageView iv, String path, Context context, int width, int height) {
        if (path == null || iv == null || context == null) {
            iv.setImageResource(R.mipmap.no_picture);
            return;
        }
        Glide.with(context)
                .load(new File(path))
                .centerCrop()
                .crossFade(1000)
                .override(width, height)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(iv);
    }

    /**
     * 获取压缩图片
     * 加载内存卡图片
     */
    public static Bitmap getBitmap(int resoucesId, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = null;
//        bitmap = BitmapFactory.decodeFile(url, options);
        bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        int be = (int) ((options.outHeight > options.outWidth ? options.outHeight / (DensityUtils.dp2px(context, 50))
                : options.outWidth / (DensityUtils.dp2px(context, 50))));
        if (be <= 0) // 判断200是否超过原始图片高度
            be = 1; // 如果超过，则不进行缩放
        options.inSampleSize = be;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            L.i("OutOfMemoryError");
        }
        return bitmap;
    }

    public static Bitmap getBitmap2(int resoucesId, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = null;
//        bitmap = BitmapFactory.decodeFile(url, options);
        bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        int be = (int) ((options.outHeight > options.outWidth ? options.outHeight / ScreenUtils.getScreenWidth(context)
                : options.outWidth / ScreenUtils.getScreenWidth(context)));
        if (be <= 0) // 判断200是否超过原始图片高度
            be = 1; // 如果超过，则不进行缩放
        options.inSampleSize = be;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            L.i("OutOfMemoryError");
        }
        return bitmap;
    }


    public static Bitmap getBitmap3(int resoucesId, Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
        Bitmap bitmap = null;
//        bitmap = BitmapFactory.decodeFile(url, options);
        bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        int be = (int) ((options.outHeight > options.outWidth ? options.outHeight / ScreenUtils.getScreenHeight(context)
                : options.outWidth / ScreenUtils.getScreenWidth(context)));
        if (be <= 0) // 判断200是否超过原始图片高度
            be = 1; // 如果超过，则不进行缩放
        options.inSampleSize = be;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = false;
        try {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resoucesId, options);
        } catch (OutOfMemoryError e) {
            System.gc();
            L.i("OutOfMemoryError");
        }
        return bitmap;
    }


}
