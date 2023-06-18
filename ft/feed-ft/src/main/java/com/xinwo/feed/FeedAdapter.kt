package com.xinwo.feed

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xjh.xinwo.mvp.model.BaseBean

class FeedAdapter(layoutResId: Int, data: List<BaseBean?>?) :
    BaseQuickAdapter<BaseBean?, BaseViewHolder>(layoutResId, data) {

    override fun convert(helper: BaseViewHolder, item: BaseBean?) {
        helper.setImageResource(R.id.feed_item_iv, R.drawable.girl_photo_01_small)
        helper.setText(R.id.feed_item_tv, "去看周杰伦的演唱会不？")
    }
}