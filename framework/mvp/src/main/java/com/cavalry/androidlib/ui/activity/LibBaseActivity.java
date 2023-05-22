package com.cavalry.androidlib.ui.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.cavalry.androidlib.mvp.view.IView;
import com.cavalry.androidlib.ui.inter.IFunction;


/**
 * @author Cavalry Lin
 * @since 1.0.0
 * 创建{@link com.cavalry.androidlib.view.stateview.helper.VaryViewHelperController}
 * 请在{@link #initVaryViewHelperController}方法中创建
 * 否则会影响上下拉刷新界面中的VaryViewHelperController的使用
 */
public abstract class LibBaseActivity extends AppCompatActivity implements IView,IFunction {
    protected View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initPresenter();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        mContentView = LayoutInflater.from(getBaseContext()).inflate(layoutResID, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(layoutParams);
        setContentView(mContentView);
    }

    @Override
    public void setContentView(View view) {
        setContentView(view,view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        afterInitView(view);
        initVaryViewHelperController();
        loadData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 在initView之后执行的方法, 可以对view进行一些操作. 默认不做任何修改.
     * @param view
     * @return
     */
    protected View afterInitView(View view){
        return view;
    }

    protected void initVaryViewHelperController() {}
}
