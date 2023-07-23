package com.xinwo.feed.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.ArrayList

data class FeedListModel(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("list") val listModel: ArrayList<FeedModel>
) : Serializable
