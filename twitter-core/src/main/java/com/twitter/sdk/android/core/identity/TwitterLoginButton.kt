/*
 * Copyright (C) 2015 Twitter, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.twitter.sdk.android.core.identity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.internal.CommonUtils
import java.lang.ref.WeakReference

/**
 * Log in button for logging into Twitter. When the button is clicked, an authorization request
 * is started and the user is presented with a screen requesting access to the user's Twitter
 * account. If successful, a [com.twitter.sdk.android.core.TwitterSession] is provided
 * in the [com.twitter.sdk.android.core.Callback.success]
 */
class TwitterLoginButton internal constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int,
    authClient: TwitterAuthClient?
) : Button(context, attrs, defStyle) {

    companion object {
        private const val TAG: String = TwitterCore.TAG
        private const val ERROR_MSG_NO_ACTIVITY = ("TwitterLoginButton requires an activity."
                + " Override getActivity to provide the activity for this button.")
    }

    private val activityRef: WeakReference<Activity>

    @Volatile
    private var authClient: TwitterAuthClient?
    private var onClickListener: OnClickListener? = null
    private var callback: Callback<TwitterSession>? = null

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = android.R.attr.buttonStyle
    ) : this(context, attrs, defStyle, null)

    init {
        activityRef = WeakReference(activity)
        this.authClient = authClient
        setupButton()
        checkTwitterCoreAndEnable()
    }

    private fun setupButton() {
        super.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(context, R.drawable.twitter_ic_logo_default), null, null, null
        )
        super.setCompoundDrawablePadding(
            resources.getDimensionPixelSize(R.dimen.twitter_login_btn_drawable_padding)
        )
        super.setText(R.string.twitter_login_btn_txt)
        super.setTextColor(ContextCompat.getColor(context, R.color.twitter_solid_white))
        super.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.twitter_login_btn_text_size).toFloat()
        )
        super.setTypeface(Typeface.DEFAULT_BOLD)
        super.setPadding(
            resources.getDimensionPixelSize(R.dimen.twitter_login_btn_left_padding), 0,
            resources.getDimensionPixelSize(R.dimen.twitter_login_btn_right_padding), 0
        )
        super.setBackgroundResource(R.drawable.twitter_login_btn)
        super.setOnClickListener(LoginClickListener())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.setAllCaps(false)
        }
    }

    /**
     * Sets the [com.twitter.sdk.android.core.Callback] to invoke when login completes.
     *
     * @param callback The callback interface to invoke when login completes.
     * @throws java.lang.IllegalArgumentException if callback is null.
     */
    fun setCallback(callback: Callback<TwitterSession>) {
        this.callback = callback
    }

    /**
     * @return the current [com.twitter.sdk.android.core.Callback]
     */
    fun getCallback(): Callback<TwitterSession>? {
        return callback
    }

    /**
     * Call this method when [android.app.Activity.onActivityResult]
     * is called to complete the authorization flow.
     *
     * @param requestCode the request code used for SSO
     * @param resultCode the result code returned by the SSO activity
     * @param data the result data returned by the SSO activity
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == twitterAuthClient.requestCode) {
            twitterAuthClient.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Gets the activity. Override this method if this button was created with a non-Activity
     * context.
     */
    private val activity: Activity?
        get() = if (context is ContextThemeWrapper &&
            (context as ContextThemeWrapper).baseContext is Activity
        ) {
            (context as ContextThemeWrapper).baseContext as Activity
        } else if (context is Activity) {
            context as Activity
        } else if (isInEditMode) {
            null
        } else {
            throw IllegalStateException(ERROR_MSG_NO_ACTIVITY)
        }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        this.onClickListener = onClickListener
    }

    private inner class LoginClickListener : OnClickListener {

        override fun onClick(view: View) {
            val callback = this@TwitterLoginButton.callback
            checkCallback(callback)
            val activity = activityRef.get()
            checkActivity(activity)
            twitterAuthClient.authorize(activity!!, callback!!)
            onClickListener?.onClick(view)
        }

        private fun checkCallback(callback: Callback<*>?) {
            if (callback == null) {
                CommonUtils.logOrThrowIllegalStateException(
                    TwitterCore.TAG,
                    "Callback must not be null, did you call setCallback?"
                )
            }
        }

        private fun checkActivity(activity: Activity?) {
            if (activity == null || activity.isFinishing) {
                CommonUtils.logOrThrowIllegalStateException(
                    TwitterCore.TAG,
                    ERROR_MSG_NO_ACTIVITY
                )
            }
        }
    }

    private val twitterAuthClient: TwitterAuthClient
        get() {
            if (authClient == null) {
                synchronized(TwitterLoginButton::class.java) {
                    if (authClient == null) {
                        authClient = TwitterAuthClient()
                    }
                }
            }
            return authClient!!
        }

    private fun checkTwitterCoreAndEnable() {
        //Default (Enabled) in edit mode
        if (isInEditMode) return
        try {
            TwitterCore.getInstance()
        } catch (ex: IllegalStateException) {
            //Disable if TwitterCore hasn't started
            Twitter.getLogger().e(TAG, ex.message)
            isEnabled = false
        }
    }
}