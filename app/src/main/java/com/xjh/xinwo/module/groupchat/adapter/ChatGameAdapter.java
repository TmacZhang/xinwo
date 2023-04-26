package com.xjh.xinwo.module.groupchat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.xinwo.R;
import com.xjh.xinwo.enity.ChatGameEntity;
import com.xjh.xinwo.enity.KTVPeopleEntity;

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
