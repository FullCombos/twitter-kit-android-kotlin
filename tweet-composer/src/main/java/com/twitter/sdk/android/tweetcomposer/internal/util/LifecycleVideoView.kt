package com.twitter.sdk.android.tweetcomposer.internal.util

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner

internal class LifecycleVideoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : VideoView(context, attrs, defStyleAttr), DefaultLifecycleObserver {

    private var mediaControllerEnable = true

    private var lifecycle: Lifecycle? = null
        get() = field ?: viewTreeLifecycle

    private val viewTreeLifecycle by lazy {
        findViewTreeLifecycleOwner()?.lifecycle
    }

    private val mediaController by lazy {
        MediaController(context)
    }

    init {
        setOnPreparedListener {
            it.isLooping = true
            it.setOnSeekCompleteListener {
                start()
            }
        }
    }

    fun setLifecycle(lifecycle: Lifecycle?) {
        this.lifecycle = lifecycle
    }

    fun setVideoURI(uri: Uri?, mediaControllerEnable: Boolean) {
        this.mediaControllerEnable = mediaControllerEnable
        setVideoURI(uri)
    }

    override fun setVideoURI(uri: Uri?) {
        lifecycle?.let {
            it.removeObserver(this)
            it.addObserver(this)
        }

        if (mediaControllerEnable) {
            setMediaController(mediaController)
        }

        super.setVideoURI(uri)
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)

        if (isVisible) {
            tag = currentPosition
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)

        if (isVisible) {
            tag ?: return
            seekTo(tag as Int)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        lifecycle?.removeObserver(this)
    }
}