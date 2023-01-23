package com.twitter.sdk.android.core

import android.graphics.Bitmap
import android.graphics.drawable.Drawable

interface LoadImageCallback {

    fun onPrepare(placeholder: Drawable?)

    fun onSuccess(bitmap: Bitmap)

    fun onError(error: Drawable?)
}