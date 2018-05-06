package com.hiretaxi.fragment.base;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hiretaxi.activity.base.BaseActivity;

public abstract class BaseFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;
    private OnFragmentInteractionListener mListener;
    public View fragmentRootview;

    public BaseFragment() {
    }

    public static BaseFragment newInstance(Class fragmentCla) {
        BaseFragment fragment = null;
        try {
            fragment = (BaseFragment) fragmentCla.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    public static BaseFragment newInstance1(String param1) {
        BaseFragment fragment = null;
        try {
            fragment = BaseFragment.class.newInstance();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentRootview = LayoutInflater.from(getContext()).inflate(getChildFragmentLayout(), null);
        return fragmentRootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handViewEvent(fragmentRootview);
    }

    protected abstract int getChildFragmentLayout();//获取布局

    protected abstract void handViewEvent(View fragmentRootview); //处理事件

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public void showToast(String content) {
        ((BaseActivity)getActivity()).showToast(content==null?"":content);
    }

    public <T> T findView(int viewId) {
        T t = (T) fragmentRootview.findViewById(viewId);
        return t;
    }
}
