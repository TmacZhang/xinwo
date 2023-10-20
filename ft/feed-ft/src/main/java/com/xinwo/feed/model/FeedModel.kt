package com.xinwo.feed.model

import com.atech.staggedrv.model.StaggedModel
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.Random

data class FeedModel(
    @SerializedName("bucketName") val bucketName: String,
    @SerializedName("fileName") val fileName: String
) : StaggedModel, Serializable {

    override fun getWidth(): Int {
        return 300 + Random().nextInt(300)
    }

    override fun getHeight(): Int {
        return 600 + Random().nextInt(400)
    }

    override fun getTitle(): String {
        return ""
    }

    override fun getThumb(): String {
        return ""
    }

    fun getUrl(): String {
        bucketName.apply {
            if (this.isNotEmpty()) {
                fileName.apply {
                    if (this.isNotEmpty()) {
                        return "http://121.37.162.226:19000/$bucketName/$fileName"
                    }
                }
            }
        }

        return ""
    }
}
