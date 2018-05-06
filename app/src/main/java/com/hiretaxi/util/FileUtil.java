package com.hiretaxi.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017/7/25.
 */

public class FileUtil {

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

}
