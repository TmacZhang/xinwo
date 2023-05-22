package com.cavalry.androidlib.mvp.view;

import java.util.Map;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public interface IView {
    Map<String, String> getParams(int tag);
    void onSuccess(Object bean, int tag);
    void onError(Throwable e, int tag);
}
