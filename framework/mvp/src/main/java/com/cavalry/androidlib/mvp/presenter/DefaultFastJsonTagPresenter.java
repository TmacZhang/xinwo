package com.cavalry.androidlib.mvp.presenter;

import android.content.Context;

import com.xinwo.network.DefualtServiceGenerator;
import com.xinwo.network.GetService;
import com.xinwo.network.PostBodyService;
import com.xinwo.network.PostFormService;
import com.cavalry.androidlib.mvp.view.IView;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */

public class DefaultFastJsonTagPresenter extends LibFastJsonTagPresenter {

    public DefaultFastJsonTagPresenter(Context context, IView view) {
        super(context, view);
    }

    protected GetService createGetService() {
        return DefualtServiceGenerator.getInstance().createService(GetService.class);
    }

    protected PostFormService createPostFormService(){
        return DefualtServiceGenerator.getInstance().createService(PostFormService.class);
    }

    @Override
    protected PostBodyService createPostBodyService() {
        return DefualtServiceGenerator.getInstance().createService(PostBodyService.class);
    }
}
