package com.xinwo.feed.player

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.xinwo.feed.FeedCache

class VideoPlayerManager(context: Context) {
    val mContext = context
    private val players: MutableList<SimpleExoPlayer> = mutableListOf()
    private var currentPlayingIndex: Int? = null
    var cacheDataSourceFactory: CacheDataSourceFactory? = null

    fun initializePlayer(index: Int): SimpleExoPlayer {
        val player = ExoPlayerFactory.newSimpleInstance(mContext)
        players.add(index, player)
        cacheDataSourceFactory = CacheDataSourceFactory(
            FeedCache.simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(mContext, "exo")
            )
        )
        return player
    }

    fun playVideo(url: String, playerIndex: Int) {
        if (playerIndex < 0 || playerIndex >= players.size) {
            return
        }

        pauseOtherVideo(playerIndex)

        val player = players[playerIndex]
        if (player.playWhenReady) {
            player.playWhenReady = false
            return
        }

        if (currentPlayingIndex == playerIndex) {
            player.playWhenReady = true
            return
        }

        val mediaSource = buildMediaSource(url)
        player.prepare(mediaSource)
        player.repeatMode = Player.REPEAT_MODE_OFF
        player.playWhenReady = true
        currentPlayingIndex = playerIndex
    }

    fun resetVideoState(
        playbackState: Int,
        url: String,
        player: SimpleExoPlayer,
        playerIndex: Int
    ) {
        if (playbackState == Player.STATE_ENDED) {
            val mediaSource = buildMediaSource(url)
            player.prepare(mediaSource)
            player.repeatMode = Player.REPEAT_MODE_OFF
            player.playWhenReady = false
            currentPlayingIndex = playerIndex
        }
    }

    fun releaseAllPlayers() {
        for (player in players) {
            player.release()
        }
        players.clear()
        currentPlayingIndex = null
    }

    private fun pauseOtherVideo(playerIndex: Int) {
        players.forEach {
            if (players.indexOf(it) != playerIndex) {
                it.playWhenReady = false
            }
        }
    }

    private fun buildMediaSource(url: String): MediaSource {
        return ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(Uri.parse(url))
    }


}
