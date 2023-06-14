package com.xinwo.social.groupchat.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinwo.social.R;
import com.xinwo.social.chat.entity.TotalGroupChatEntity;

import java.util.List;

public class TotalGroupChatAdapter extends BaseQuickAdapter<TotalGroupChatEntity, BaseViewHolder> {

    public TotalGroupChatAdapter(@Nullable List<TotalGroupChatEntity> data) {
        super(R.layout.item_total_group_chat, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, TotalGroupChatEntity item) {
        helper.setBackgroundColor(R.id.container, Color.parseColor(item.color));
    }
}
