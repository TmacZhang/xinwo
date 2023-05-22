package com.cavalry.androidlib.mvp.presenter;

/**
 * Created by Cavalry on 16-4-1.
 * Function:
 * Description:
 * Attention:
 */
public interface IPresenter<V> {

    void attachView(V view);

    void detachView();
}
