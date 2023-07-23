package com.xinwo.social.profile.fragment

import com.atech.staggedrv.model.StaggedModel

data class ProfileItemModel(private val width: Int, private val height: Int, val resourceId: Int) : StaggedModel {
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
