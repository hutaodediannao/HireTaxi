package com.hiretaxi.helper;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;

/**
 * 提醒声音播放管理类
 * Created by Administrator on 2017/7/25.
 */

public class SoundPlayer {

    public SoundPool mSoundPool;
    private static SoundPlayer instance;

    public static SoundPlayer getInstance() {
        if (instance == null) {
            synchronized (SoundPlayer.class) {
                if (instance == null) {
                    instance = new SoundPlayer();
                }
            }
        }
        return instance;
    }

    private SoundPlayer() {
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    }

    /**
     * 开始播放音乐
     *
     * @param context
     * @param soundRes
     */
    public void playerMessage(Context context, int soundRes) {
        mSoundPool.load(context, soundRes, 1);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSoundPool.play(1, 1, 1, 0, 0, 1);
            }
        }, 500);
    }

    private Handler handler = new Handler();

}
