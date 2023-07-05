package com.xinwo.feed

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.staggedrv.StaggedAdapter
import com.xinwo.feed.model.FeedModel

class FeedAdapter(c: Context?) : StaggedAdapter<FeedModel>(c) {

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun addViewHolder(viewGroup: ViewGroup?, i: Int): RecyclerView.ViewHolder {

        //绑定自定义的viewholder
        val v: View = LayoutInflater.from(c)
            .inflate(R.layout.item_feed, viewGroup, false)
        return FeedViewHolder(v)
    }

    override fun bindView(viewHolder: RecyclerView.ViewHolder?, i: Int) {
          val feedViewHolder = viewHolder as  FeedViewHolder
        feedViewHolder.mTextView?.setText("去看周杰伦的演唱会不？")

        // 在加载图片之前设定好图片的宽高，防止出现item错乱及闪烁
        feedViewHolder.mImageView?.setImageResource(datas[i].localResorce())
        val layoutParams: ViewGroup.LayoutParams? = feedViewHolder.mImageView?.getLayoutParams()
        layoutParams?.height = datas.get(i).height
        feedViewHolder.mImageView?.setLayoutParams(layoutParams)
    }
}