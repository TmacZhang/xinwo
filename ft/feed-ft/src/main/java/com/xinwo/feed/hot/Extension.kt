package com.xinwo.feed.hot

import android.util.Log
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

fun AppCompatTextView.setTextOrHide(value: String? = null) {
    if (!value.isNullOrBlank()) {
        text = value
        isVisible = true
    } else {
        isVisible = false
    }
}

fun <T>Class<in T>.logError(message: String) {
    Log.e(this::class.java.name, message)
}

fun Any.logError(message: String) {
    Log.e(this::class.java.name, message)
}

fun Long.formatNumberAsReadableFormat(): String {
    return Utility.formatNumberAsNumberFormat(this)
}


fun ShapeableImageView.loadCenterCropImageFromUrl(imageUrl: String?) {
    Glide.with(this)
        .load(imageUrl)
        .centerCrop()
        .into(this)
}