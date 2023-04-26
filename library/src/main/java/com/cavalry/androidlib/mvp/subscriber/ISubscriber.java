package com.cavalry.androidlib.mvp.subscriber;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface ISubscriber<T> {
    void onLoadStart();
    void onLoadError(Throwable e);
    void onLoadSuccess(T t);
    void onLoadComplete();
}
