package com.xinwo.feed

import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ui.PlayerView

class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mTextView: TextView? = itemView.findViewById(R.id.feed_item_tv)
    var mImageView: ImageView? = itemView.findViewById(R.id.feed_item_iv)
    var mVideoPlayer : PlayerView? = itemView.findViewById(R.id.feed_item_surfaceview)
    var mRelativeLayout : RelativeLayout? = itemView.findViewById(R.id.feed_item_relativeLayout)
}