package com.cavalry.androidlib.mvp.presenter;

import android.content.Context;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class BasePresenter<V> implements IPresenter<V> {
    protected Context mContext;
    protected V mView;

    public BasePresenter(Context context, V view){
        mContext = context;
        attachView(view);
    }

    @Override
    public void attachView(V view) {
        mView = view;
    }

    @Override
    public void detachView() {
        if(mView!=null)
            mView = null;
    }


}
