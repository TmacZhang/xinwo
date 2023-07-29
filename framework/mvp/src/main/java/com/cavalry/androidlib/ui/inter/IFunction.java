package com.cavalry.androidlib.ui.inter;

public interface IFunction {
    /**
     * 调用顺序:{@link #initData()},{@link #initPresenter()},{@link #initView()},{@link #loadData()}
     */
    void initData();

    /**
     * 调用顺序:{@link #initData()},{@link #initPresenter()},{@link #initView()},{@link #loadData()}
     */
    void initPresenter();

    /**
     * 调用顺序:{@link #initData()},{@link #initPresenter()},{@link #initView()},{@link #loadData()}
     */
    void initView();

    /**
     * 调用顺序:{@link #initData()},{@link #initPresenter()},{@link #initView()},{@link #loadData()}
     */
    void loadData();

}
