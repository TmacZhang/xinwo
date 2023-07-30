package com.xinwo.feed

import android.content.Context
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.xinwo.base.BaseApplication.mContext

class FeedCache() {
    companion object {
        private val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(1024 * 1024 * 1024)
        private val databaseProvider = ExoDatabaseProvider(mContext)
        var simpleCache: SimpleCache? = SimpleCache(mContext?.cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
    }
}
