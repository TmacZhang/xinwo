package com.xinwo.produce.music.ui.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xinwo.produce.R;
import com.xinwo.produce.music.bean.MusicCategoryBean;

import java.util.List;


/**
 * Created by 25623 on 2018/10/11.
 */

public class MusicCategoryAdapter extends BaseQuickAdapter<MusicCategoryBean, BaseViewHolder> {
    private boolean isShort = true;

    public MusicCategoryAdapter() {
        this(null);
    }

    public MusicCategoryAdapter(@Nullable List<MusicCategoryBean> data) {
        super(R.layout.item_music_category, data);
    }



    @Override
    protected void convert(BaseViewHolder helper, MusicCategoryBean item) {
        helper.setImageResource(R.id.ivCategory, item.categoryIconId);
        helper.setText(R.id.tvCategory, item.category);
    }

    public void setShort(boolean isShort){
        isShort = isShort;
    }

    public boolean isShort(){
        return isShort;
    }
}
