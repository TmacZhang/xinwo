package com.xinwo.feed

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.staggedrv.StaggedAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.VideoDecoder.FRAME_OPTION
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.xinwo.feed.model.FeedModel
import com.xinwo.feed.player.VideoPlayerManager
import java.security.MessageDigest

class FeedAdapter(c: Context?) : StaggedAdapter<FeedModel>(c) {
    private val mVideoPlayerManager = VideoPlayerManager(c!!)
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun addViewHolder(viewGroup: ViewGroup?, i: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(c)
            .inflate(R.layout.feed_item, viewGroup, false)
        return FeedViewHolder(v)
    }

    @SuppressLint("CheckResult")
    override fun bindView(viewHolder: RecyclerView.ViewHolder?, i: Int) {
        val feedViewHolder = viewHolder as FeedViewHolder
        Log.i("jin", datas.get(i).getUrl())
        // 在加载图片之前设定好图片的宽高，防止出现item错乱及闪烁
        if (datas.get(i).bucketName.equals("img")) {
            feedViewHolder.mImageView?.let {
                Glide.with(c)
                    .load(datas.get(i).getUrl())
                    .into(it)
            }
            feedViewHolder.mTextView?.setText("去看周杰伦的演唱会不？")
        } else {
            setVideoFrame(feedViewHolder, i)
            feedViewHolder.mVideoPlayer?.player = mVideoPlayerManager.initializePlayer(i)
            feedViewHolder.mImageView?.setOnClickListener { v ->
                feedViewHolder.mImageView?.visibility = View.GONE
                feedViewHolder.mVideoPlayer?.visibility = View.VISIBLE

                feedViewHolder.mVideoPlayer?.resizeMode = RESIZE_MODE_ZOOM
                feedViewHolder.mVideoPlayer?.setControllerVisibilityListener { visibility ->
                    if (visibility == View.VISIBLE) {
                        feedViewHolder.mVideoPlayer?.hideController()
                    }
                }
                mVideoPlayerManager.playVideo(datas.get(i).getUrl(), i)
                feedViewHolder.mVideoPlayer?.hideController()
            }

            feedViewHolder.mVideoPlayer?.setOnClickListener(object : View.OnClickListener {
                override fun onClick(view: View?) {
                    mVideoPlayerManager.playVideo(datas.get(i).getUrl(), i)
                }
            })
        }

        val layoutParams: ViewGroup.LayoutParams? =
            feedViewHolder.mRelativeLayout?.getLayoutParams()
        layoutParams?.height = datas.get(i).height
        feedViewHolder.mRelativeLayout?.setLayoutParams(layoutParams)
    }

    @SuppressLint("CheckResult")
    private fun setVideoFrame(feedViewHolder: FeedViewHolder, i: Int) {
        feedViewHolder.mTextView?.setText("视频")
        val requestOptions = RequestOptions.frameOf(100)
        requestOptions.set(FRAME_OPTION, MediaMetadataRetriever.OPTION_CLOSEST)
        requestOptions.transform(object : BitmapTransformation() {
            override fun updateDiskCacheKey(messageDigest: MessageDigest) {

            }

            override fun transform(
                pool: BitmapPool,
                toTransform: Bitmap,
                outWidth: Int,
                outHeight: Int
            ): Bitmap {
                return toTransform
            }
        })
        feedViewHolder.mImageView?.let {
            Glide.with(c).load(datas.get(i).getUrl()).apply(requestOptions)
                .into(it)
        }
    }
}