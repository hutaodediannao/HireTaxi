package com.hiretaxi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.hiretaxi.R;
import com.hiretaxi.activity.base.BaseActivity;
import com.hiretaxi.adapter.ViewPageAdapter;
import com.hiretaxi.view.ViewPagerFixed;

import java.util.ArrayList;


/**
 * 图片滑动查看器界面
 */
public class ImageBrowseActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private ArrayList<String> images;
    private int position;
    private ViewPageAdapter adapter;
    private ViewPagerFixed vp;
    private TextView hint;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browse);

        initView();
        getData();
    }

    private void initView() {
        hint = findView(R.id.hint);
        vp = findView(R.id.viewPager);
    }

    private void getData() {
        //获取data
        Intent intent = getIntent();
        images = intent.getStringArrayListExtra("images");
        position = intent.getIntExtra("position", 0);
        imageUrl = images.get(position);
        //设置ViewPager
        adapter = new ViewPageAdapter(this, images);
        vp.setAdapter(adapter);
        vp.setCurrentItem(position);
        vp.addOnPageChangeListener(this);
        hint.setText(position + 1 + "/" + images.size());
    }

//    @Override
//    public String setHeaderLayoutTitle() {
//        return "图片查看";
//    }

    public static void startActivity(Context context, ArrayList<String> images, int position) {
        Intent intent = new Intent(context, ImageBrowseActivity.class);
        intent.putStringArrayListExtra("images", images);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        hint.setText(position + 1 + "/" + images.size());
        imageUrl = images.get(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void save(View view) {
//        Picasso.with(this).load(imageUrl).into(new Target() {
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                // 创建目录
//                File appDir = new File(Environment.getExternalStorageDirectory(), Constant.ALJZIMG);
//                if (!appDir.exists()) {
//                    appDir.mkdir();
//                }
//
//                String imageType = getImageType(imageUrl); //获取图片类型
//                String fileName = System.currentTimeMillis() + "." + imageType;
//                File file = new File(appDir, fileName);
//                //保存图片
//                try {
//                    FileOutputStream fos = new FileOutputStream(file);
//                    if(TextUtils.equals(imageType,"jpg")) imageType = "jpeg";
//                    imageType = imageType.toUpperCase();
//                    bitmap.compress(Bitmap.CompressFormat.valueOf(imageType), 100, fos);
//                    fos.flush();
//                    fos.close();
//                    showToast("保存成功");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                // 其次把文件插入到系统图库
//                try {
//                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), fileName, null);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//                // 最后通知图库更新
//                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        });
    }

    private String getImageType(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf(".")+1);
    }

    public void close(View view) {
        finish();
    }
}
