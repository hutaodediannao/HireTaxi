package com.hiretaxi.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hiretaxi.fragment.base.BaseFragment;

import java.util.List;

/**
 * Created by Administrator on 2017/7/4.
 */

public class FragmentAdapter extends FragmentPagerAdapter {

    private List<BaseFragment> fragmentList;

    public FragmentAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        this.fragmentList = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
