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
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYMediaPlayerListener
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.xinwo.feed.model.FeedModel
import java.security.MessageDigest


class FeedAdapter(c: Context?) : StaggedAdapter<FeedModel>(c) {

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun addViewHolder(viewGroup: ViewGroup?, i: Int): RecyclerView.ViewHolder {

        //绑定自定义的viewholder
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

            feedViewHolder.mImageView?.setOnClickListener { v ->
                PlayerFactory.setPlayManager(IjkPlayerManager::class.java)
                feedViewHolder.mImageView?.visibility = View.GONE
                feedViewHolder.mVideoPlayer?.visibility = View.VISIBLE
                feedViewHolder.mVideoPlayer?.setUp(datas.get(i).getUrl(), true, "")
                feedViewHolder.mVideoPlayer?.startPlayLogic()
                feedViewHolder.mVideoPlayer?.getBackButton()?.setVisibility(View.GONE);
                feedViewHolder.mVideoPlayer?.getFullscreenButton()?.setOnClickListener {
                    feedViewHolder.mVideoPlayer?.startWindowFullscreen(c, false, true)
                }
                GSYVideoManager.instance().setLastListener(object : GSYMediaPlayerListener {
                    override fun onPrepared() {

                    }

                    override fun onAutoCompletion() {
                        feedViewHolder.mImageView?.visibility = View.VISIBLE
                        feedViewHolder.mVideoPlayer?.visibility = View.GONE
                    }

                    override fun onCompletion() {
                        feedViewHolder.mImageView?.visibility = View.VISIBLE
                        feedViewHolder.mVideoPlayer?.visibility = View.GONE
                    }

                    override fun onBufferingUpdate(percent: Int) {

                    }

                    override fun onSeekComplete() {

                    }

                    override fun onError(what: Int, extra: Int) {

                    }

                    override fun onInfo(what: Int, extra: Int) {

                    }

                    override fun onVideoSizeChanged() {

                    }

                    override fun onBackFullscreen() {

                    }

                    override fun onVideoPause() {

                    }

                    override fun onVideoResume() {

                    }

                    override fun onVideoResume(seek: Boolean) {

                    }
                })
            }
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