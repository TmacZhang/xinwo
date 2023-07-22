package com.xinwo.feed.model

import com.atech.staggedrv.model.StaggedModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FeedModel(private val width: Int, private val height: Int, private val resourceId: Int) : StaggedModel, Serializable {

    @SerializedName("width")
    private var mWidth: Int = width

    @SerializedName("height")
    private var mHeight: Int = height

    @SerializedName("fileName")
    private var mUrl : String = ""

    override fun getWidth(): Int {
        return mWidth
    }

    override fun getHeight(): Int {
        return mHeight
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getThumb(): String {
        return ""
    }

    override fun localResorce(): Int {
        return resourceId
    }

}
