package com.xinwo.social.chat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinwo.social.R;
import com.xjh.xinwo.mvp.model.BaseBean;

import java.util.List;

public class StoryAdapter extends BaseQuickAdapter<BaseBean, BaseViewHolder> {


    public StoryAdapter(int layoutResId, @Nullable List<BaseBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseBean item) {
        helper.setImageResource(R.id.nivBg, R.drawable.girl_photo_01_medium);
    }
}
