package com.xinwo.produce.gestureheart.activity.adapter;


import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinwo.produce.R;
import com.xinwo.produce.gestureheart.activity.bean.FilterTimeBean;

import java.util.List;


/**
 * Created by 25623 on 2018/8/29.
 */

public class FilterAdapter extends BaseQuickAdapter<FilterTimeBean, BaseViewHolder> {

    private OnItemTouchListener mOnItemTouchListener;

    public FilterAdapter(@LayoutRes int layoutResId, @Nullable List<FilterTimeBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, FilterTimeBean item) {
        if(mOnItemTouchListener != null){
            helper.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mOnItemTouchListener.onItemTouch(v, helper.getAdapterPosition(), event);
                    return true;
                }
            });
        }
        helper.setText(R.id.tvFilterName, item.name);
        helper.setImageResource(R.id.ivFilterIcon, item.resId);
    }

    public void setOnItemTouchListener(OnItemTouchListener listener){
        mOnItemTouchListener = listener;
    }

    public interface OnItemTouchListener{
        void onItemTouch(View v, int position, MotionEvent event);
    }
}
