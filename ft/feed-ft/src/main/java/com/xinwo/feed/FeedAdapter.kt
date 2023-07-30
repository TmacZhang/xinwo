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
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.video.VideoListener
import com.xinwo.feed.find.model.FeedModel
import com.xinwo.feed.player.VideoPlayerManager
import java.security.MessageDigest

class FeedAdapter(c: Context?) : StaggedAdapter<FeedModel>(c) {
    private val mVideoPlayerManager = VideoPlayerManager(c!!)
    private val mViewholders: MutableList<FeedViewHolder> = mutableListOf()
    override fun getItemCount(): Int {
        return datas.size
    }

    override fun addViewHolder(viewGroup: ViewGroup?, i: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(c)
            .inflate(R.layout.feed_item, viewGroup, false)
        return FeedViewHolder(v)
    }

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
            bindVideoItem(i, viewHolder, feedViewHolder)
        }

        val layoutParams: ViewGroup.LayoutParams? =
            feedViewHolder.mRelativeLayout?.getLayoutParams()
        layoutParams?.height = datas.get(i).height
        feedViewHolder.mRelativeLayout?.setLayoutParams(layoutParams)
    }

    private fun bindVideoItem(
        i: Int,
        viewHolder: RecyclerView.ViewHolder?,
        feedViewHolder: FeedViewHolder
    ) {
        mViewholders.add(i, viewHolder as FeedViewHolder)
        setVideoFrame(feedViewHolder, i)
        feedViewHolder.mVideoPlayer?.player = mVideoPlayerManager.initializePlayer(i)
        feedViewHolder.mImageView?.setOnClickListener {
            feedViewHolder.mVideoPlayer?.resizeMode = RESIZE_MODE_ZOOM
            feedViewHolder.mVideoPlayer?.setControllerVisibilityListener { visibility ->
                if (visibility == View.VISIBLE) {
                    feedViewHolder.mVideoPlayer?.hideController()
                }
            }

            feedViewHolder.mVideoPlayer?.player?.videoComponent?.addVideoListener(object :
                VideoListener {
                override fun onRenderedFirstFrame() {
                    feedViewHolder.mImageView?.visibility.let {
                        if (it == View.VISIBLE) {
                            feedViewHolder.mImageView?.visibility = View.GONE
                        }
                    }
                }
            })
            feedViewHolder.mVideoPlayer?.player?.let {
                it.addListener(object : Player.EventListener {
                    override fun onPlayerStateChanged(
                        playWhenReady: Boolean,
                        playbackState: Int
                    ) {
                        if (playbackState == Player.STATE_ENDED) {
                            mVideoPlayerManager.resetVideoState(
                                playbackState,
                                datas.get(i).getUrl(),
                                it as SimpleExoPlayer,
                                i
                            )
                        }
                    }
                })
            }
            mVideoPlayerManager.playVideo(datas[i].getUrl(), i)
            feedViewHolder.mVideoPlayer?.hideController()
            feedViewHolder.mVideoPlayer?.visibility = View.VISIBLE

            mViewholders.forEach {
                if (mViewholders.indexOf(it) != i) {
                    mViewholders[mViewholders.indexOf(it)].mImageView?.visibility =
                        View.VISIBLE
                    mViewholders[mViewholders.indexOf(it)].mVideoPlayer?.visibility =
                        View.GONE
                }
            }
        }

        feedViewHolder.mVideoPlayer?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mVideoPlayerManager.playVideo(datas[i].getUrl(), i)
            }
        })
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