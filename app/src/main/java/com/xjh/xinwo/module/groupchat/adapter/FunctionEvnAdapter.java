package com.xjh.xinwo.module.groupchat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.xinwo.R;
import com.xjh.xinwo.enity.FunctionEnvEntity;

import java.util.List;

public class FunctionEvnAdapter extends BaseQuickAdapter<FunctionEnvEntity, BaseViewHolder> {
    public FunctionEvnAdapter(int layoutResId, @Nullable List<FunctionEnvEntity> data) {
        super(layoutResId, data);
    }

    public FunctionEvnAdapter(@Nullable List<FunctionEnvEntity> data) {
        super(data);
    }

    public FunctionEvnAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, FunctionEnvEntity item) {
        helper.setImageResource(R.id.iv, item.picResID);
        helper.setText(R.id.tv, item.name);
    }
}
