package com.xinwo.feed.base;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.View;

import androidx.appcompat.app.ActionBar;

import com.cavalry.androidlib.toolbox.exception.LibException;
import com.cavalry.androidlib.ui.activity.LibRLBaseActivity;
import com.xjh.xinwo.mvp.model.BaseBean;
import com.xjh.xinwo.mvp.presenter.TagPresenter;

import java.util.Map;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * <p>1.具有refresh/loadmore功能, 默认为关闭状态, 子类通过{@link #setRLMode(PtrFrameLayout.Mode)}设置</p>
 * <p>2.带有页码mPageNo. 在{@link #onRefreshStart(PtrFrameLayout)}时自动mPageNo=0;
 *      在{@link #onError(Throwable, int)}中, 若isLoadingMore(), 自动 --mPageNo</p>
 * <p>3.在{@link #success(Object, int)}之后自动{@link #refreshOrLoadMoreComplete()}</p>
 * <p>4.在{@link #error(Throwable, int)}之前自动{@link #refreshOrLoadMoreComplete()}</p>
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */
public abstract class BaseActivity extends LibRLBaseActivity {
    protected TagPresenter mPresenter;
    protected final int PAGE_SIZE = 20;
    protected int mPageNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRLMode(PtrFrameLayout.Mode.NONE);//设置位没有refresh/loadmore功能, 这样就不会初始化RL控件. 子类可重新设置RL功能
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null)
            mPresenter.detachView();
    }


    @Override
    public void onSuccess(Object bean, int tag) {
        BaseBean baseBean = (BaseBean) bean;
        if(baseBean.message.code == 200){
            success(bean,tag);

            afterSuccess(bean, tag);
        }else{
            onError(new LibException(baseBean.message.code, baseBean.message.msg), tag);
        }

    }

    private void afterSuccess(Object bean, int tag) {
        if(isRefreshingOrLoadingMore()){
            refreshOrLoadMoreComplete();
        }
    }


    @Override
    public void onError(Throwable e, int tag) {
        beforeError(e,tag);
        error(e,tag);
    }

    private void beforeError(Throwable e, int tag) {
//        if(BuildConfig.DEBUG){
//            e.printStackTrace();
//        }
        if (isLoadingMore() && mPageNo>0){
            --mPageNo;
        }
        if(isRefreshingOrLoadingMore()){
            refreshOrLoadMoreComplete();
        }
    }

    protected abstract void success(Object bean, int tag);

    protected abstract void error(Throwable e, int tag);

    //RL的几个方法覆写为非抽象
    /**
     * {@inheritDoc}
     * @return
     */
    protected View getRLView(){return null;}

    /**
     * 已将mPageNo置为0
     * @return
     */
    protected void onRefreshStart(PtrFrameLayout frame){
        mPageNo = 0;
    }

    /**
     * {@inheritDoc}
     * @return
     */
    protected void onLoadMoreStart(PtrFrameLayout frame){}

    protected Map<String, String> getHeaders(String jwt_code){
        ArrayMap<String, String> headers = new ArrayMap<>();
        headers.put("Content-Type", "application/json");
        if(jwt_code != null){
            headers.put("Authorization", jwt_code);
        }

        return headers;
    }

    public void hideStatusBar(){
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Delayed removal of status and navigation bar

        // Note that some of these constants are new as of API 16 (Jelly Bean)
        // and API 19 (KitKat). It is safe to use them, as they are inlined
        // at compile-time and do nothing on earlier devices.
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public void showStatusBar(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }
}