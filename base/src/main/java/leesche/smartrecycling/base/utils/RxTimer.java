package leesche.smartrecycling.base.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.Getter;


public class RxTimer {

    private TextView mTimeTv;
    private long playRecordTime;
    private long answerTime = 10 * 60;
    @Getter
    private Disposable mDDisposable;
    private String flag;
    private static RxTimer rxTimer;
    private boolean answerTimeHalf = false;
    private long remainTime = 0;
    private boolean isRest = false;
    static Object object = new Object();
    OnTimeCounterListener onTimeCounterListener;

    public interface OnTimeCounterListener {
        void onTimeEnd();
    }

    public static RxTimer getInstance() {
        synchronized (object) {
            if (rxTimer == null) {
                rxTimer = new RxTimer();
            }
        }
        return rxTimer;
    }

    public boolean isAnswerTimeHalf() {
        return answerTimeHalf;
    }

    public void start(TextView mTimeTv, long time, String flag, OnTimeCounterListener onTimeCounterListener) {
        //避免重复开启倒计时
        stop();
        if (mDDisposable != null && !mDDisposable.isDisposed()) {
            return;
        }
        this.mTimeTv = mTimeTv;
        this.answerTime = time;
        this.flag = flag;
        this.mTimeTv.setText(answerTime + "s");
        this.mTimeTv.setVisibility(View.VISIBLE);
        this.onTimeCounterListener = onTimeCounterListener;
        Observable.intervalRange(playRecordTime, answerTime + 1, 0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDDisposable = d;
//                        Logger.d("[系统]倒计时 onSubscribe：" + flag);
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onNext(Long value) {
                        //记录当前计时数
                        playRecordTime = value;
                        remainTime = answerTime - value;
                        if ("Deposit".equals(flag)) {
                            mTimeTv.setText(("( " + remainTime) + "s )");
                        } else {
                            mTimeTv.setText((remainTime) + "s");
                        }
                        if (remainTime < answerTime / 2) answerTimeHalf = true;
//                        Logger.d("[系统]倒计时 onNext：" + "" + value + " - " + flag);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        Logger.d("[系统]倒计时 onError：" + " - " + flag);
                    }

                    @Override
                    public void onComplete() {
//                        Logger.d("[系统]倒计时 onComplete：" + flag);
                        if (onTimeCounterListener != null) onTimeCounterListener.onTimeEnd();
                    }
                });
    }

    public void resetCurrentTimer(boolean isHalf) {
        if (mDDisposable == null) return;
        if (!isHalf || answerTimeHalf) {
            isRest = true;
            RxTimer.getInstance().start(mTimeTv, answerTime, flag, onTimeCounterListener);
        }
    }

    public void pause() {
        if (mDDisposable != null) {
            mDDisposable.dispose();
        }
    }

    public void stop() {
        if (mDDisposable != null) {
            if (!isRest) onTimeCounterListener = null;
            mTimeTv.setText("");
            this.mTimeTv.setVisibility(View.INVISIBLE);
            playRecordTime = 0;
            answerTimeHalf = false;
            mDDisposable.dispose();
            mDDisposable = null;
            isRest = false;
        }
    }
}
