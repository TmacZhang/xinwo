package com.xjh.gestureheart.music.ui.adapter;


import androidx.annotation.Nullable;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.xjh.gestureheart.music.bean.MusicBean;
import com.xjh.xinwo.R;

import java.util.List;

/**
 * Created by 25623 on 2018/10/11.
 */

public class MusicAdapter extends BaseQuickAdapter<MusicBean, BaseViewHolder> {

    private int itemClickPosition = -1;

    public MusicAdapter() {
        this(null);
    }

    public MusicAdapter(@Nullable List<MusicBean> data) {
        super(R.layout.item_music, data);
    }



    @Override
    protected void convert(BaseViewHolder helper, MusicBean item) {
        helper.setText(R.id.tvName,item.name);
        helper.setText(R.id.tvAuthor, item.author);
        helper.setText(R.id.tvDuration, item.duration);
        if(helper.getAdapterPosition() == itemClickPosition){
            helper.setGone(R.id.tvCapture, true);
        }else{
            helper.setGone(R.id.tvCapture, false);
        }
        if(item.isFavorite){
            helper.setImageResource(R.id.ivFavorite, R.mipmap.music_favorite);
        }else{
            helper.setImageResource(R.id.ivFavorite, R.mipmap.music_favrite_not);
        }
    }

    public void setItemClickPosition(int itemClickPosition) {
        this.itemClickPosition = itemClickPosition;
        notifyDataSetChanged();
    }
}
