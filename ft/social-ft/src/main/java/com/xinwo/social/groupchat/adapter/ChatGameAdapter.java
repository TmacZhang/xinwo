package com.xinwo.social.groupchat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinwo.social.R;
import com.xinwo.social.chat.entity.ChatGameEntity;

import java.util.List;

public class ChatGameAdapter extends BaseQuickAdapter<ChatGameEntity, BaseViewHolder> {

    public ChatGameAdapter(@Nullable List<ChatGameEntity> data) {
        super(R.layout.item_chat_game, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ChatGameEntity item) {
        helper.setImageResource(R.id.iv, item.gameResID);
    }
}
