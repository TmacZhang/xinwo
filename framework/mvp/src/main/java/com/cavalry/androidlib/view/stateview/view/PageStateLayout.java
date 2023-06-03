package com.cavalry.androidlib.view.stateview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.cavalry.androidlib.R;
import com.xinwo.log.LibDimenUtils;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class PageStateLayout extends FrameLayout {

    private Context mContext;

    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;
    private MaterialProgress mLoading;

    public PageStateLayout(Context context) {
        super(context);
        init();
        mContext = context;
    }

    public PageStateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        mContext = context;
    }

    private void init() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }


    public void show(PageState state, View.OnClickListener listener) {
        if (null == mLoadingView) {
            mLoadingView = initLoadingView();
            mLoadingView.setOnClickListener(listener);
            addView(mLoadingView);
        } else {
            if (mLoading instanceof MaterialProgress) {
                mLoading.reset();
            }
        }

        if (null == mErrorView) {
            mErrorView = initErrorView();
            mErrorView.setOnClickListener(listener);
            addView(mErrorView);
        }

        if (null == mEmptyView) {
            mEmptyView = initEmptyView();
            mEmptyView.setOnClickListener(listener);
            addView(mEmptyView);
        }

        mLoadingView.setVisibility(state == PageState.LOADING || state == PageState.REQEUSTING ? VISIBLE : GONE);
        mErrorView.setVisibility(state == PageState.ERROR ? VISIBLE : GONE);
        mEmptyView.setVisibility(state == PageState.EMPTY ? VISIBLE : GONE);

    }


    private View initLoadingView() {
        View loadingView = LayoutInflater.from(mContext).inflate(R.layout.loading, null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LibDimenUtils.dip2px(80),
                LibDimenUtils.dip2px(80)
        );
        params.gravity = Gravity.CENTER;
        loadingView.setLayoutParams(params);

        mLoading = (MaterialProgress) loadingView.findViewById(R.id.loadingProgress);

        return loadingView;
    }


    private View initErrorView() {
        View errorView = LayoutInflater.from(mContext).inflate(R.layout.error, null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        errorView.setLayoutParams(params);
        return errorView;
    }


    private View initEmptyView() {
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.empty, null, false);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER;
        emptyView.setLayoutParams(params);

        return emptyView;
    }


    public enum PageState {
        LOADING, EMPTY, ERROR, REQEUSTING
    }
}
