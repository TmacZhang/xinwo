package com.xinwo.feed.hot

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.xinwo.feed.FeedCache
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll

private const val TAG = "PreCachingService"

class PreCachingService(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {
    private val mContext = appContext
    private var cacheDataSourceFactory: CacheDataSourceFactory? = null



    override suspend fun doWork(): Result = coroutineScope {
        cacheDataSourceFactory = CacheDataSourceFactory(
            FeedCache.simpleCache,
            DefaultHttpDataSourceFactory(
                Util.getUserAgent(
                    applicationContext,
                    "exo"
                )
            )
        )

        val dataList = inputData.getStringArray(Constants.KEY_STORIES_LIST_DATA)

        val jobs = dataList?.map { data ->
            async {
                val dataUri = Uri.parse(data)
                val dataSpec = DataSpec(dataUri, 0, 500 * 1024, null)

                val dataSource: DataSource =
                    DefaultDataSourceFactory(
                        applicationContext,
                        Util.getUserAgent(
                            applicationContext,
                            "exo"
                        )
                    ).createDataSource()

                preloadVideo(dataSpec,
                    FeedCache.simpleCache,
                    dataSource,
                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, newBytesCached: Long ->
                        val downloadPercentage = (bytesCached * 100.0
                                / requestLength)
                        Log.d(TAG, "downloadPercentage: $downloadPercentage")
                    }
                )
            }
        }
        jobs?.joinAll()
        Result.success()
    }

    private fun preloadVideo(
        dataSpec: DataSpec?,
        cache: Cache?,
        upstream: DataSource?,
        progressListener: CacheUtil.ProgressListener?
    ) {
        Log.d(TAG, "preloadVideo")
        try {
            CacheUtil.cache(
                dataSpec,
                cache,
                CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
                upstream,
                progressListener,
                null
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}