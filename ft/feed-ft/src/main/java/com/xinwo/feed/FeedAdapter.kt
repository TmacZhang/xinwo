package com.xinwo.feed

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
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
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.xinwo.feed.model.FeedModel
import java.security.MessageDigest


class FeedAdapter(c: Context?) : StaggedAdapter<FeedModel>(c) {
    val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 1024)
    val databaseProvider: DatabaseProvider = ExoDatabaseProvider(c)

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

            if (simpleCache == null) {
                Log.i("jin", "simplaecache 111")
                simpleCache =
                    SimpleCache(c?.cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
            }
            feedViewHolder.mImageView?.setOnClickListener { v ->

                val simplePlayer = ExoPlayerFactory.newSimpleInstance(c)
                val cacheDataSourceFactory = CacheDataSourceFactory(
                    simpleCache,
                    DefaultHttpDataSourceFactory(
                        Util.getUserAgent(c, "exo")
                    )
                )

                feedViewHolder.mImageView?.visibility = View.GONE
                feedViewHolder.mVideoPlayer?.visibility = View.VISIBLE
                feedViewHolder.mVideoPlayer?.player = simplePlayer
                feedViewHolder.mVideoPlayer?.resizeMode = RESIZE_MODE_ZOOM
                val uri = Uri.parse(datas.get(i).getUrl())
                val mediaSource =
                    ProgressiveMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri)
                simplePlayer?.prepare(mediaSource, true, true)
                simplePlayer?.repeatMode = Player.REPEAT_MODE_ONE
                simplePlayer?.playWhenReady = true

                simplePlayer?.addListener(object : Player.EventListener {
                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        Log.i("jin", playbackState.toString() + " simplePlayer?.playWhenReady = "+ simplePlayer.playWhenReady)
                    }

                    override fun onPlayerError(error: com.google.android.exoplayer2.ExoPlaybackException?) {
                        super.onPlayerError(error)
                    }
                })
            }
        }

        val layoutParams: ViewGroup.LayoutParams? =
            feedViewHolder.mRelativeLayout?.getLayoutParams()
        layoutParams?.height = datas.get(i).height
        feedViewHolder.mRelativeLayout?.setLayoutParams(layoutParams)
    }

    companion object {
        var simpleCache: SimpleCache? = null
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