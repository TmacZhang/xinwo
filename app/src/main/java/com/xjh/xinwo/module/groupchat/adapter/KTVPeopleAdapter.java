package com.xjh.xinwo.module.groupchat.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.xinwo.R;
import com.xjh.xinwo.enity.KTVPeopleEntity;

import java.util.List;

public class KTVPeopleAdapter extends BaseQuickAdapter<KTVPeopleEntity, BaseViewHolder> {


    public KTVPeopleAdapter(@Nullable List<KTVPeopleEntity> data) {
        super(R.layout.item_ktv_people, data);
    }



    @Override
    protected void convert(@NonNull BaseViewHolder helper, KTVPeopleEntity item) {
        helper.setImageResource(R.id.ivHeader, item.headerResID);
        helper.setImageResource(R.id.ivMicroPhone, item.microPhoneResID);
        if(helper.getAdapterPosition() == 1 || helper.getAdapterPosition() == 4 || helper.getAdapterPosition() == 5){
            helper.setVisible(R.id.ivMicroPhone, true);
        }else{
            helper.setVisible(R.id.ivMicroPhone, false);
        }
    }
}
