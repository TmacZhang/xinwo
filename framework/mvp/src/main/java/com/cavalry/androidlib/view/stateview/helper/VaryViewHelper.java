/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cavalry.androidlib.view.stateview.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.xinwo.log.LibLog;

import in.srain.cube.views.ptr.LibPtrFrameLayout;


/**
 * 增加对LibPtrFrameLayout的支持
 */
public class VaryViewHelper implements IVaryViewHelper {
    private final String TAG = "VaryViewHelper";

    private View originalView;//原view(即将被替换的view)
    private ViewGroup parentView;//originalView的parent
    private int viewIndex;//originalView在parentView中的位置
    private ViewGroup.LayoutParams params;//originalView的LayoutParams
    private View currentView;//当前正在显示的view

    public VaryViewHelper(View originalView) {
        super();
        //如果父View是LibPtrFrameLayout,将LibPrtFrameLayout作为OriginalView
        ViewParent parent = originalView.getParent();
        LibLog.e(TAG, "parent = " + parent);
        if (parent != null && parent instanceof LibPtrFrameLayout) {
            originalView = (View) parent;
        }
        this.originalView = originalView;
    }

    private void init() {
        params = originalView.getLayoutParams();
//		params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (originalView.getParent() != null) {
            parentView = (ViewGroup) originalView.getParent();
        } else {
            parentView = (ViewGroup) originalView.getRootView().findViewById(android.R.id.content);
        }
        int count = parentView.getChildCount();
        for (int index = 0; index < count; index++) {
            if (originalView == parentView.getChildAt(index)) {
                viewIndex = index;
                break;
            }
        }
        currentView = originalView;
    }

    /**
     * 用新的view替换正在显示的view
     *
     * @param view
     */
    @Override
    public void showLayout(View view) {
        //如果originalView是LibPtrFrameLayout， 那么替换之前需立即结束LibPtrFrameLayout的refresh/loadmore状态
        if (originalView instanceof LibPtrFrameLayout) {
            if (((LibPtrFrameLayout) originalView).isRefreshingOrLoadingMore()) {
                ((LibPtrFrameLayout) originalView).refreshCompleteAtOnce();
            }
        }

        if (parentView == null) {
            init();
        }

        // 当前显示的view与新的view不同才执行替换动作
        if (currentView != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }

            parentView.removeViewAt(viewIndex);
            parentView.addView(view, viewIndex, params);
            currentView = view;
        }
    }

    /**
     * 用新的view替换正在显示的view
     *
     * @param layoutId
     */
    public void showLayout(int layoutId) {
        showLayout(inflate(layoutId));
    }

    /**
     * 还原为originalView
     */
    @Override
    public void restoreView() {
        //LibPtrFrameLayout被替换，会导致刷新状态无法结束，这里再次结束
//		if(originalView instanceof LibPtrFrameLayout){
////			if(((LibPtrFrameLayout)originalView).isRefreshingOrLoadingMore()){
//				((LibPtrFrameLayout)originalView).refreshComplete();
////			}
//		}
        showLayout(originalView);
    }


    @Override
    public View inflate(int layoutId) {
        return LayoutInflater.from(getContext()).inflate(layoutId, null);
    }

    @Override
    public Context getContext() {
        return originalView.getContext();
    }

    public View getOriginalView() {
        return originalView;
    }

    /**
     * 获取originalView的parent
     *
     * @return
     */
    public View getParentView() {
        if (parentView == null) {
            init();
        }
        return parentView;
    }

    /**
     * 获取当前正在显示的view
     *
     * @return
     */
    @Override
    public View getCurrentLayout() {
        return currentView;
    }

}
