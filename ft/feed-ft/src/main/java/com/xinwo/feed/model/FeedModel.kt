package com.xinwo.feed.model

import com.atech.staggedrv.model.StaggedModel

data class FeedModel(private val width: Int, private val height: Int, val resourceId: Int) : StaggedModel {
    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
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
