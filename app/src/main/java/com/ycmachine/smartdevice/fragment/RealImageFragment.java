package com.ycmachine.smartdevice.fragment;

import android.os.Bundle;
import android.view.View;

import com.ycmachine.smartdevice.R;

import leesche.smartrecycling.base.BaseFragment;
import leesche.smartrecycling.base.utils.RxTimer;


public class RealImageFragment extends BaseFragment implements RxTimer.OnTimeCounterListener{


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_machine_image;
    }

    @Override
    protected void initView(View mRoot) {

    }

    @Override
    protected void initInstanceState(Bundle savedInstanceState) {


    }

    @Override
    public void onTimeEnd() {

    }
}