package com.xinwo.feed.api

import com.xinwo.feed.find.model.FeedListModel
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface IGetHostService {
    @GET("/minio/fileList")
    fun getData(
        @Query("pageNum") param1: Int,
        @Query("pageSize") param2: Int,
        @Query("bucketName") param3: String
    ): Observable<FeedListModel>
}