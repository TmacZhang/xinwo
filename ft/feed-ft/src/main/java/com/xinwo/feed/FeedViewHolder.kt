package com.xinwo.feed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var mTextView: TextView? = itemView.findViewById<TextView>(R.id.feed_item_tv)
    var mImageView: ImageView? = itemView.findViewById<ImageView>(R.id.feed_item_iv)
}