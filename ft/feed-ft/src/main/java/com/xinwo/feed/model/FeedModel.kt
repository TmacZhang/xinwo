package com.xinwo.feed.model

import com.atech.staggedrv.model.StaggedModel

class FeedModel(width: Int, height: Int, resourceId: Int) : StaggedModel {
    private val mWidth = width
    private val mHeight = height
    private val mResourceId = resourceId

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
        return mResourceId
    }

}
