package com.cavalry.androidlib.ui.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.cavalry.androidlib.mvp.view.IView;
import com.cavalry.androidlib.ui.inter.IFunction;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 *
 * 创建{@link com.cavalry.androidlib.view.stateview.helper.VaryViewHelperController}
 * 请在{@link #initVaryViewHelperController}方法中创建
 * 否则会影响上下拉刷新界面中的VaryViewHelperController的使用
 */

public abstract class LibBaseFragment extends Fragment implements IFunction,IView{


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initPresenter();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        afterInitView(view);
        initVaryViewHelperController();
        loadData();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 在initView之后执行的方法, 可以对view进行一些操作. 默认不做任何修改.
     * @param rootView
     * @return
     */
    protected View afterInitView(View rootView){
        return rootView;
    }

    protected void initVaryViewHelperController(){}

}
