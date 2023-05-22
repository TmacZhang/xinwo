package com.cavalry.androidlib.mvp.subscriber;

import android.content.Context;

import com.cavalry.androidlib.mvp.subscriber.handler.ProgressDialogHandler;
import com.cavalry.androidlib.mvp.subscriber.listener.OnProgressCancelListener;


/**
 * Created by cavalry on 16-10-27.
 * <p>使用无参构造方法创建的对象不会显示loading, 有参构造方法创建的对象会显示loading</p>
 */

public abstract class LoadingSubscriber<T> extends BaseSubscriber<T> implements OnProgressCancelListener {
    private final static String TAG = "LoadingSubscriber";

    private boolean mShowLoading = true;
    private ProgressDialogHandler mProgressDialogHandler;
    private static boolean mDialogCancelable = true;//按返回键是否可以取消Diaglog


    public LoadingSubscriber(){
        mShowLoading = false;
    }

    public LoadingSubscriber(Context context) {
        this(context, mDialogCancelable);
    }

    public LoadingSubscriber(Context context, boolean dialogCancelable) {
        this(context,dialogCancelable,null);
    }

    public LoadingSubscriber(Context context, String msg) {
        this(context,mDialogCancelable,msg);
    }

    public LoadingSubscriber(Context context, boolean dialogCancelable, String msg) {
        mShowLoading = true;
        mDialogCancelable = dialogCancelable;
        mProgressDialogHandler = new ProgressDialogHandler(context, this, mDialogCancelable,msg);
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressDialog();
    }

    @Override
    public void onCompleted() {
        dismissProgressDialog();
    }

    @Override
    public void onError(Throwable e) {
        dismissProgressDialog();
    }


    @Override
    public void onNext(T t) {
    }


    @Override
    public void onProgressCancel() {
        if (this.isUnsubscribed()) {
            this.unsubscribe();
        }
    }

    private void showProgressDialog() {
        if (mShowLoading && mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    private void dismissProgressDialog() {
        if (mShowLoading && mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
        }
    }
}
