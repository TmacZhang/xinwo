package com.xinwo.application

import android.app.Application

class XinApplicationUtil {
    var mApplication: Application? = null
        get() = field
        set(value) {
            field = value
        }


    companion object {
        val instance by lazy(LazyThreadSafetyMode.NONE) {
            XinApplicationUtil()
        }
    }
}