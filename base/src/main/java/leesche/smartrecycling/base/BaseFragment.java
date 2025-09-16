package leesche.smartrecycling.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;

public abstract class BaseFragment extends Fragment implements OnTouchListener {

    protected View mRoot;
    protected boolean mIsFirstInitData = true;

    private int MIN_DELAY_TIME= 500;  // 两次点击间隔不能少于1000ms
    private long lastClickTime;
    Unbinder unbinder;
    public Activity activity;

    public  HomeFragmentListener homeFragmentListener;

    public void setMIN_DELAY_TIME(int MIN_DELAY_TIME) {
        this.MIN_DELAY_TIME = MIN_DELAY_TIME;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initArgs(getArguments());
        homeFragmentListener = (HomeFragmentListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        initInstanceState(savedInstanceState);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BasicMessageEvent event) {

    }
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , @Nullable Bundle savedInstanceState) {
        if (mRoot == null) {
            int layId = getContentLayoutId();
            View root = inflater.inflate(layId, container, false);
            mRoot = root;
        } else {
            if (mRoot.getParent() != null) {
                ((ViewGroup) mRoot.getParent()).removeView(mRoot);
            }
        }
        //绑定试图
        unbinder = ButterKnife.bind(this, mRoot);
        initView(mRoot);
        mRoot.setOnTouchListener(this);
        return mRoot;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mIsFirstInitData) {
            mIsFirstInitData = false;
            onFirstInit();
        }
        initData();
    }

    /**
     * 初始化相关参数
     *
     * @param bundle 参数Bundle
     * @return 如果参数初始化正确返回true, 错误返回false
     */
    protected void initArgs(Bundle bundle) {
    }

    /**
     * 得到当前界面的资源文件Id
     *
     * @return 资源文件Id
     */
    protected abstract int getContentLayoutId();

    protected abstract void initView(View mRoot);
    protected abstract void initInstanceState(Bundle savedInstanceState);

    /**
     * 初始化首次数据
     */
    protected void onFirstInit() {

    }

    /**
     * 初始化数据
     */
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    /**
     * 返回按键触发时调用
     */
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= MIN_DELAY_TIME) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}
