package com.example.app

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import com.squareup.picasso.Target
import com.twitter.sdk.android.core.LoadImageCallback
import com.twitter.sdk.android.core.TwitterImageLoader

class ImageLoader : TwitterImageLoader {

    private val picasso = Picasso.get()
    private var requestCreator: RequestCreator? = null

    override fun load(url: String?) = apply {
        requestCreator = picasso.load(url)
    }

    override fun fit() = apply {
        requestCreator?.fit()
    }

    override fun centerCrop() = apply {
        requestCreator?.centerCrop()
    }

    override fun placeholder(placeholder: Drawable?) = apply {
        placeholder ?: return@apply
        requestCreator?.placeholder(placeholder)
    }

    override fun placeholder(@DrawableRes placeholderRes: Int) = apply {
        requestCreator?.placeholder(placeholderRes)
    }

    override fun error(error: Drawable?) = apply {
        error ?: return@apply
        requestCreator?.error(error)
    }

    override fun error(@DrawableRes errorRes: Int) = apply {
        requestCreator?.error(errorRes)
    }

    override fun into(view: View, callback: LoadImageCallback?) {
        requestCreator?.run {
            this.into(object : Target {

                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    if (callback != null) {
                        callback.onSuccess(bitmap)
                    } else {
                        setImage(view, BitmapDrawable(view.context.resources, bitmap))
                    }
                }

                override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
                    if (callback != null) {
                        callback.onError(errorDrawable)
                    } else {
                        setImage(view, errorDrawable)
                    }
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable) {
                    if (callback != null) {
                        callback.onPrepare(placeHolderDrawable)
                    } else {
                        setImage(view, placeHolderDrawable)
                    }
                }
            })
        }
    }

    private fun setImage(view: View, drawable: Drawable) {
        if (view is ImageView) {
            view.setImageDrawable(drawable)
        } else {
            view.background = drawable
        }
    }
}