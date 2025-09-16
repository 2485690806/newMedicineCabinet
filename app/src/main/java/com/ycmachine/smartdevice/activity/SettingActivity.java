package com.ycmachine.smartdevice.activity;

import android.os.Bundle;
import android.view.View;

import com.ycmachine.smartdevice.R;

import leesche.smartrecycling.base.BaseActivity;
import leesche.smartrecycling.base.HomeFragmentListener;

public class SettingActivity  extends BaseActivity implements HomeFragmentListener {

    @Override
    public int initLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    @Override
    public void initData() {

    }

    @Override
    public void skipToFragment(int position) {

    }

    public void backToActivity(View view) {
        finish();
    }

}
