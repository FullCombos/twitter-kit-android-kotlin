package com.twitter.sdk.android.core

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import androidx.annotation.DrawableRes

interface TwitterImageLoader {

    fun load(uri: Uri?) = load(uri?.toString())

    fun load(url: String?): TwitterImageLoader

    fun placeholder(placeholder: Drawable?): TwitterImageLoader

    fun placeholder(@DrawableRes placeholderRes: Int): TwitterImageLoader

    fun fit() = this

    fun centerCrop() = this

    fun error(error: Drawable?): TwitterImageLoader

    fun error(@DrawableRes errorRes: Int): TwitterImageLoader

    fun into(view: View, callback: LoadImageCallback? = null)
}