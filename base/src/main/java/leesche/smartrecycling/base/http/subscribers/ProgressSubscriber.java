package leesche.smartrecycling.base.http.subscribers;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.JsonSyntaxException;
import com.leesche.logger.Logger;

import org.greenrobot.eventbus.EventBus;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.cert.CertPathValidatorException;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import leesche.smartrecycling.base.R;
import leesche.smartrecycling.base.common.EventType;
import leesche.smartrecycling.base.eventbus.BasicMessageEvent;
import leesche.smartrecycling.base.http.HttpUrls;
import leesche.smartrecycling.base.http.progress.ProgressCancelListener;
import leesche.smartrecycling.base.http.progress.ProgressDialogHandler;

/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog
 * 在Http请求结束是，关闭ProgressDialog
 * 调用者自己对请求数据进行处理
 * Created by liukun on 16/3/10.
 */
public class ProgressSubscriber<T> implements ProgressCancelListener, Observer<T> {

    private SubscriberOnNextListener mSubscriberOnNextListener;
    private SubscriberOnNextListener2 mSubscriberOnNextListener2;
    private ProgressDialogHandler mProgressDialogHandler;

    private Context context;
    private String lastHintMsg = "";

    Disposable mDisposable;

    public ProgressSubscriber(SubscriberOnNextListener mSubscriberOnNextListener, Context context) {
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    public ProgressSubscriber(SubscriberOnNextListener2 mSubscriberOnNextListener2, Context context) {
        this.mSubscriberOnNextListener2 = mSubscriberOnNextListener2;
        this.context = context;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }
    public ProgressSubscriber(SubscriberOnNextListener2 mSubscriberOnNextListener2) {
        this.mSubscriberOnNextListener2 = mSubscriberOnNextListener2;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, true);
    }

    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }

    private String lastHttpErrorMsg = "";

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onError(Throwable e) {
        if (mSubscriberOnNextListener2 != null) {
            mSubscriberOnNextListener2.onError(e.getMessage());
        }
        if (e.getMessage().equals(HttpUrls.lastErrorMsg)) return;
        HttpUrls.lastErrorMsg = e.getMessage();
        if (e instanceof SocketTimeoutException) {
            Logger.i("[系统] 网络连接超时：" + e.getMessage());
            HttpUrls.HTTP_ERROR_CODE = 100001;
        } else if (e instanceof ConnectException) {
            Logger.i("[系统] 连接异常：" + e.getMessage());
            HttpUrls.HTTP_ERROR_CODE = 100002;
        } else if (e instanceof UnknownHostException) {
            Logger.i("[系统] 未知服务：" + e.getMessage());
            HttpUrls.HTTP_ERROR_CODE = 100003;
        } else if (e instanceof JsonSyntaxException) {
            Logger.i("[系统] 数据解析错误：" + e.getMessage());
            HttpUrls.HTTP_ERROR_CODE = 100004;
        } else if (e instanceof CertPathValidatorException) {
            Logger.i("[系统] 证书路径验证器异常：" + e.getMessage());
            HttpUrls.HTTP_ERROR_CODE = 100005;
        } else {
            HttpUrls.HTTP_ERROR_CODE = 100006;
            if ("token失效".equalsIgnoreCase(e.getMessage())) {
                EventBus.getDefault().post(new BasicMessageEvent(EventType.BasicEvent.GET_DEVICE_INFO));
            }
        }
    }

    @Override
    public void onComplete() {
//        dismissProgressDialog();
    }

    @Override
    public void onSubscribe(Disposable d) {
//        showProgressDialog();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            mSubscriberOnNextListener.onNext(t);
        }
        if (mSubscriberOnNextListener2 != null) {
            mSubscriberOnNextListener2.onNext(t);
        }
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    @Override
    public void onCancelProgress() {
        if (mDisposable != null) {
            if (!mDisposable.isDisposed()) {
                mDisposable.dispose();
            }
        }
    }
}