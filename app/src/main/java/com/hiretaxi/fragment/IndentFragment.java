package com.hiretaxi.fragment;

import android.support.annotation.IdRes;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hiretaxi.R;
import com.hiretaxi.adapter.FragmentAdapter;
import com.hiretaxi.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class IndentFragment extends BaseFragment {

    private ViewPager viewPager;
    private RadioGroup mRadioGroup;
    private List<BaseFragment> fragmentList;
    private FragmentAdapter fragmentAdapter;

    @Override
    protected int getChildFragmentLayout() {
        return R.layout.fragment_server;
    }

    @Override
    protected void handViewEvent(View fragmentRootview) {
        initView();
        setAdapter();
        setListener();
    }

    private void initView() {
        viewPager = findView(R.id.int_vp);
        mRadioGroup = findView(R.id.int_radioGroup);
        RadioButton radioButton = (RadioButton) mRadioGroup.getChildAt(0);
        radioButton.setChecked(true);
    }

    private void setAdapter() {
        fragmentList = new ArrayList<BaseFragment>();
        fragmentList.add(BaseFragment.newInstance(MyIndentFragment.class));
        fragmentList.add(BaseFragment.newInstance(OkIndentFragment.class));
        fragmentAdapter = new FragmentAdapter(getFragmentManager(), fragmentList);
        viewPager.setAdapter(fragmentAdapter);
//            viewPager.setOffscreenPageLimit(fragmentList.size());
    }


    private void setListener() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.int_rd0:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.int_rd1:
                        viewPager.setCurrentItem(1);
                        break;
                }
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        ((RadioButton) findView(R.id.int_rd0)).setChecked(true);
                        break;
                    case 1:
                        ((RadioButton) findView(R.id.int_rd1)).setChecked(true);
                        break;
                }
            }
        });
    }

    private static final String TAG = "IndentFragment";
    public static int NEET_REFREASH_INDEX = -1;//标记是否需要刷新界面
    @Override
    public void onStart() {
        super.onStart();
        switch (NEET_REFREASH_INDEX) {
            case 0:
                viewPager.setCurrentItem(0);
                break;
            case 1:
                viewPager.setCurrentItem(1);
                break;
        }
        NEET_REFREASH_INDEX = -1;
    }
}
