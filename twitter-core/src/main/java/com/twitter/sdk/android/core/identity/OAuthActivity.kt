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
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import com.twitter.sdk.android.core.R
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService
import androidx.core.view.isVisible

/**
 * Activity for performing OAuth flow when Single Sign On is not available. This activity should not
 * be called directly.
 */
// This activity assumes it will handle configuration changes itself and MUST have the
// following attribute defined in the AndroidManifest.xml
// file: android:configChanges="orientation|screenSize"
class OAuthActivity : Activity(), OAuthController.Listener {

    companion object {
        const val EXTRA_AUTH_CONFIG = "auth_config"
        private const val STATE_PROGRESS = "progress"
    }

    private lateinit var oAuthController: OAuthController

    private val twitterSpinner: ProgressBar by lazy { findViewById(R.id.twitter_spinner) }
    private val twitterWebView: WebView by lazy { findViewById(R.id.twitter_web_view) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.twitter_activity_oauth)

        val showProgress = savedInstanceState?.getBoolean(STATE_PROGRESS, false) ?: true
        twitterSpinner.visibility = if (showProgress) View.VISIBLE else View.GONE
        oAuthController = OAuthController(
            twitterSpinner,
            twitterWebView,
            intent.getParcelableExtra(EXTRA_AUTH_CONFIG),
            OAuth1aService(TwitterCore.getInstance(), TwitterApi()),
            this
        )
        oAuthController.startAuth()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (twitterWebView.isVisible) {
            outState.putBoolean(STATE_PROGRESS, true)
        }
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        oAuthController.handleAuthError(
            RESULT_CANCELED,
            TwitterAuthException("Authorization failed, request was canceled.")
        )
    }

    override fun onComplete(resultCode: Int, data: Intent?) {
        setResult(resultCode, data)
        finish()
    }
}