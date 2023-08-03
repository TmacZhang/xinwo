package com.xinwo.feed.util

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.xinwo.feed.hot.Constants
import com.xinwo.feed.hot.PreCachingService
import com.xinwo.feed.hot.StoriesDataModel

object PreCacheUtil {
    fun startPreCaching(context: Context, dataList: ArrayList<StoriesDataModel>) {
        val urlList = arrayOfNulls<String>(dataList.size)
        dataList.mapIndexed { index, storiesDataModel ->
            urlList[index] = storiesDataModel.storyUrl
        }
        val inputData =
            Data.Builder().putStringArray(Constants.KEY_STORIES_LIST_DATA, urlList).build()
        val preCachingWork = OneTimeWorkRequestBuilder<PreCachingService>().setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueue(preCachingWork)
    }
}