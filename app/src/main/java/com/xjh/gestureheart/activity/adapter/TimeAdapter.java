package com.xjh.gestureheart.activity.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.gestureheart.activity.bean.FilterTimeBean;
import com.xjh.xinwo.R;

import java.util.List;

/**
 * Created by 25623 on 2018/8/29.
 */

public class TimeAdapter extends BaseQuickAdapter<FilterTimeBean, BaseViewHolder> {

    public TimeAdapter(@LayoutRes int layoutResId, @Nullable List<FilterTimeBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, FilterTimeBean item) {
        helper.setText(R.id.tvFilterName, item.name);
        helper.setImageResource(R.id.ivFilterIcon, item.resId);
    }
}
