package com.cavalry.androidlib.view.stateview.helper;


import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cavalry.androidlib.R;
import com.cavalry.androidlib.toolbox.utils.LibUtils;
import com.cavalry.androidlib.view.stateview.view.PageStateLayout;

/**
 * @author Cavalry Lin
 * @since 1.0.0
 */
public class VaryViewHelperController {

    private IVaryViewHelper mHelper;
    private PageStateLayout mPageStateLayout;

    /**
     * @param originalView 原view(将会被stateView替换)
     */
    public VaryViewHelperController(View originalView) {
        this(new VaryViewHelper(originalView));
    }

    public VaryViewHelperController(IVaryViewHelper helper) {
        this.mHelper = helper;
    }

    /**
     * 显示指定drawable和msg的布局
     * @param drawable 要显示的drawable,为空则不显示
     * @param msg   要显示的文字信息,为空则不显示
     * @param listener 显示后的布局的点击事件
     */
    public void showState(Drawable drawable, String msg, View.OnClickListener listener){
        View stateView = mHelper.inflate(R.layout.function_layout);
        ImageView ivFunction = (ImageView) stateView.findViewById(R.id.iv_function);
        TextView tvFunction = (TextView) stateView.findViewById(R.id.tv_function);

        if(drawable==null){
            ivFunction.setVisibility(View.GONE);
        }else{
            ivFunction.setVisibility(View.VISIBLE);
            ivFunction.setImageDrawable(drawable);
        }

        if(TextUtils.isEmpty(msg)){
            tvFunction.setVisibility(View.GONE);
        }else {
            tvFunction.setVisibility(View.VISIBLE);
            tvFunction.setText(msg);
        }

        showState(stateView,listener);
    }

    /**
     * 显示指定的布局
     * @param stateView 用stateView替换originalView
     * @param listener
     */
    public void showState(View stateView, View.OnClickListener listener){
        LibUtils.checkNotNull(stateView,"stateView不能为空");
        stateView.setOnClickListener(listener);
        mHelper.showLayout(stateView);
    }

    /**
     * 显示默认的布局
     * @param state
     * @param listener
     */
    public void showState(PageStateLayout.PageState state,View.OnClickListener listener){
        if(mPageStateLayout==null){
            mPageStateLayout = new PageStateLayout(mHelper.getContext());
        }

        mPageStateLayout.show(state,listener);
        mHelper.showLayout(mPageStateLayout);
    }

    /**
     * 还原布局
      */
    public void restore() {
        mHelper.restoreView();
    }
}
