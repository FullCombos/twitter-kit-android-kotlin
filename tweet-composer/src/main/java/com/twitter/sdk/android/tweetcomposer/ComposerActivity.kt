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
package com.twitter.sdk.android.tweetcomposer

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.twitter.Regex
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession

class ComposerActivity : Activity() {

    companion object {
        private const val EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN"
        private const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        private const val EXTRA_THEME = "EXTRA_THEME"
        private const val EXTRA_TEXT = "EXTRA_TEXT"
        private const val EXTRA_HASHTAGS = "EXTRA_HASHTAGS"
    }

    private var composerController: ComposerController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = intent.getParcelableExtra<TwitterAuthToken>(EXTRA_USER_TOKEN)
        val session = TwitterSession(
            token, TwitterSession.UNKNOWN_USER_ID, TwitterSession.UNKNOWN_USER_NAME
        )
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)
        val text = intent.getStringExtra(EXTRA_TEXT)
        val hashtags = intent.getStringExtra(EXTRA_HASHTAGS)
        val themeResId = intent.getIntExtra(EXTRA_THEME, R.style.ComposerLight)
        setTheme(themeResId)
        setContentView(R.layout.twitter_activity_composer)

        val composerView = findViewById<ComposerView>(R.id.twitter_composer_view)
        composerController = ComposerController(
            composerView, session, imageUri, text, hashtags, FinisherImpl()
        )
    }

    internal interface Finisher {

        fun finish()
    }

    // FinisherImpl allows sub-components to finish the host Activity.
    internal inner class FinisherImpl : Finisher {

        override fun finish() {
            this@ComposerActivity.finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        composerController?.onClose()
    }

    class Builder(context: Context) {

        private val context: Context
        private var token: TwitterAuthToken? = null
        private var themeResId = R.style.ComposerLight
        private var imageUri: Uri? = null
        private var text: String? = null
        private var hashtags: String? = null

        init {
            this.context = context
        }

        fun session(session: TwitterSession): Builder {
            val token = session.authToken
                ?: throw IllegalArgumentException("TwitterSession token must not be null")
            // session passed via the parcelable auth token
            this.token = token
            return this
        }

        fun image(imageUri: Uri?): Builder {
            this.imageUri = imageUri
            return this
        }

        fun text(text: String?): Builder {
            this.text = text
            return this
        }

        fun hashtags(vararg hashtags: String): Builder {
            val sb = StringBuilder()
            for (hashtag in hashtags) {
                if (Regex.VALID_HASHTAG.matcher(hashtag).find()) {
                    if (sb.isNotEmpty()) {
                        sb.append(" ")
                    }
                    sb.append(hashtag)
                }
            }
            this.hashtags = if (sb.isEmpty()) null else sb.toString()
            return this
        }

        fun darkTheme(): Builder {
            themeResId = R.style.ComposerDark
            return this
        }

        fun createIntent(): Intent {
            checkNotNull(token) { "Must set a TwitterSession" }
            return Intent(context, ComposerActivity::class.java).apply {
                putExtra(EXTRA_USER_TOKEN, token)
                putExtra(EXTRA_IMAGE_URI, imageUri)
                putExtra(EXTRA_THEME, themeResId)
                putExtra(EXTRA_TEXT, text)
                putExtra(EXTRA_HASHTAGS, hashtags)
            }
        }
    }
}