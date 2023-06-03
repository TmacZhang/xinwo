package com.xjh.xinwo.module.chat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.xinwo.R;
import com.xjh.xinwo.mvp.model.BaseBean;

import java.util.List;

public class ChatAdapter extends BaseQuickAdapter<BaseBean, BaseViewHolder> {


    public ChatAdapter(int layoutResId, @Nullable List<BaseBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseBean item) {
        helper.setImageResource(R.id.ivFriendPhoto, R.drawable.girl_photo_01_small);
        helper.setText(R.id.tvFriendName, "我家兔九九");
        helper.setText(R.id.tvChatContent, "去看周杰伦的演唱会不？");
        helper.setText(R.id.tvTime, "15:30");
    }
}
