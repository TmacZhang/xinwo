package com.cavalry.androidlib.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import in.srain.cube.views.ptr.LibPtrFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * 可以指定任意View具有Refresh和Loadmore功能的Fragment
 *
 * <p>1.{@link #setRLMode(PtrFrameLayout.Mode)}设置具有refresh还是loadmore功能, 默认二者皆有, 在OnViewCreated之前使用有效</p>
 * <p>2.{@link #getRLView()}指定某个View具备refresh/loadmore功能</p>
 * <p>3.{@link #onRefreshStart(PtrFrameLayout)}开始refresh</p>
 * <p>4.{@link #onLoadMoreStart(PtrFrameLayout)}开始loadmore</p>
 * <p>5.{@link #isRefreshingOrLoadingMore()}是否正在refresh/loadmore</p>
 * <p>6.{@link #refreshOrLoadMoreComplete()}完成refresh/loadmore</p>
 *
 * @author Cavalry Lin
 * @since 1.0.0
 */

public abstract class LibRLBaseFragment extends LibBaseFragment {

    private static final String TAG = "LibRLBaseFragment";

    private PtrFrameLayout mPtrFrameLayout;
    private PtrFrameLayout.Mode mRLMode = PtrFrameLayout.Mode.BOTH;

    /**
     * 设置是否可以下拉刷新和上拉加载,默认两者皆可.  在OnViewCreated之前调用生效.
     * @param rlMode
     */
    protected void setRLMode(PtrFrameLayout.Mode rlMode){
        mRLMode = rlMode;
    }

    private PtrFrameLayout.Mode getRLMode(){
        return mRLMode;
    }


    @Override
    protected View afterInitView(View view) {
        if(getRLMode() != PtrFrameLayout.Mode.NONE){
            addRefreshLoadmore(getRLView());
        }
        return view;
    }

    /**
     * 把上下拉刷新控件添加到View中
     * 返回整个上拉下拉控件
     * @param rlView    刷新和加载的View
     */
    protected PtrFrameLayout addRefreshLoadmore(View rlView){
        if (null == rlView) {
            throw new RuntimeException("rlView为空");
        }
        mPtrFrameLayout = initPtrFrameLayout();
        ViewGroup parent = (ViewGroup) rlView.getParent();
        if(parent!=null){
            int childCount = parent.getChildCount();
            for(int i=0;i<childCount;i++){
                if(rlView==parent.getChildAt(i)){
                    parent.removeView(rlView);

//                    if(rlView.getLayoutParams()!=null)
//                        mPtrFrameLayout.setLayoutParams(rlView.getLayoutParams());

                    mPtrFrameLayout.setContentView(rlView);
                    parent.addView(mPtrFrameLayout,i);
                    break;
                }
            }
        }else{
            Log.e(TAG,"addRefreshLoadmore-->rlView的parent为空");
        }
        return mPtrFrameLayout;
    }

    /**
     * 初始化刷新控件
     * @return
     */
    private LibPtrFrameLayout initPtrFrameLayout(){
        LibPtrFrameLayout ptrFrameLayout =  new LibPtrFrameLayout(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ptrFrameLayout.setLayoutParams(layoutParams);
        ptrFrameLayout.setDurationToClose(200);
        ptrFrameLayout.setDurationToCloseHeader(600);
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);
        ptrFrameLayout.setPullToRefresh(false);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setMode(getRLMode());
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {

            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {
                onLoadMoreStart(frame);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                onRefreshStart(frame);
            }
        });

        return ptrFrameLayout;
    }

    /**
     * 获取刷新的控件
     * @return
     */
    protected PtrFrameLayout getPtrFramLayout(){
        return mPtrFrameLayout;
    }

    protected void autoRefresh(){
        if(mPtrFrameLayout != null){
            mPtrFrameLayout.autoRefresh();
        }
    }

    protected boolean isRefreshing(){
        if(mPtrFrameLayout!=null){
            return mPtrFrameLayout.isRefreshingEnhance();
        }
        return false;
    }

    protected boolean isLoadingMore(){
        if(mPtrFrameLayout!=null){
            return mPtrFrameLayout.isLoadingMoreEnhance();
        }
        return false;
    }

    protected boolean isRefreshingOrLoadingMore(){
        if(mPtrFrameLayout!=null){
            return mPtrFrameLayout.isRefreshingOrLoadingMore();
        }
        return false;
    }

    protected void refreshOrLoadMoreComplete(){
        if(mPtrFrameLayout!=null){
            mPtrFrameLayout.refreshComplete();
        }
    }

    /**
     * 指定具有refresh和loadmore功能的View
     * @return
     */
    protected abstract View getRLView();

    protected abstract void onRefreshStart(PtrFrameLayout frame);
    protected abstract void onLoadMoreStart(PtrFrameLayout frame);
}
