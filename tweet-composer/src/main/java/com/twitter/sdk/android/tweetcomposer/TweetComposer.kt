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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.BuildConfig
import com.twitter.sdk.android.core.internal.network.UrlUtils
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import java.net.URL

/**
 * The TweetComposer Kit provides a lightweight mechanism for creating intents to interact with the installed Twitter app or a browser.
 */
class TweetComposer internal constructor() {

    // this is TwitterContext
    private val context: Context

    init {
        context = Twitter.getInstance().getContext(getIdentifier())
    }

    private fun getVersion(): String {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER
    }

    private fun getIdentifier(): String {
        return BuildConfig.GROUP + ":" + "tweet-composer"
    }

    /**
     * The TweetComposer Builder will use the installed Twitter instance and fall back to a browser
     */
    class Builder(context: Context) {

        private val context: Context
        private var text: String? = null
        private var url: URL? = null
        private var imageUri: Uri? = null

        /**
         * Initializes a new [com.twitter.sdk.android.tweetcomposer.TweetComposer.Builder]
         */
        init {
            this.context = context
        }

        /**
         * Sets Text for Tweet Intent, no length validation is performed
         */
        fun text(text: String?): Builder {
            this.text = text
            return this
        }

        /**
         * Sets URL for Tweet Intent, no length validation is performed
         */
        fun url(url: URL?): Builder {
            this.url = url
            return this
        }

        /**
         * Sets Image [android.net.Uri] for the Tweet. Only valid if the Twitter App is
         * installed.
         * The Uri should be a file Uri to a local file (e.g. <pre>`Uri.fromFile(someExternalStorageFile)`</pre>))
         */
        fun image(imageUri: Uri?): Builder {
            this.imageUri = imageUri
            return this
        }

        /**
         * Creates [android.content.Intent] based on data in [com.twitter.sdk.android.tweetcomposer.TweetComposer.Builder]
         * @return an Intent to the Twitter for Android or a web intent.
         */
        private fun createIntent(): Intent {
            var intent = createTwitterIntent()
            if (intent == null) {
                intent = createWebIntent()
            }
            return intent
        }

        private fun createTwitterIntent(): Intent? {
            val intent = Intent(Intent.ACTION_SEND)
            val builder = StringBuilder()

            if (!text.isNullOrEmpty()) {
                builder.append(text)
            }

            if (url != null) {
                if (builder.isNotEmpty()) {
                    builder.append(' ')
                }
                builder.append(url.toString())
            }

            intent.putExtra(Intent.EXTRA_TEXT, builder.toString())
            intent.type = MIME_TYPE_PLAIN_TEXT

            if (imageUri != null) {
                intent.putExtra(Intent.EXTRA_STREAM, imageUri)
                intent.type = MIME_TYPE_JPEG
            }

            val packManager = context.packageManager
            val resolvedInfoList = packManager.queryIntentActivities(
                intent,
                PackageManager.MATCH_DEFAULT_ONLY
            )
            for (resolveInfo in resolvedInfoList) {
                if (resolveInfo.activityInfo.packageName.startsWith(TWITTER_PACKAGE_NAME)) {
                    intent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name
                    )
                    return intent
                }
            }
            return null
        }

        private fun createWebIntent(): Intent {
            val url = url.toString()
            val tweetUrl =
                String.format(WEB_INTENT, UrlUtils.urlEncode(text), UrlUtils.urlEncode(url))
            return Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl))
        }

        /**
         * Starts the intent created in [com.twitter.sdk.android.tweetcomposer.TweetComposer.Builder.createIntent]
         */
        fun show() {
            val intent = createIntent()
            context.startActivity(intent)
        }
    }

    companion object {
        private const val MIME_TYPE_PLAIN_TEXT = "text/plain"
        private const val MIME_TYPE_JPEG = "image/jpeg"
        private const val TWITTER_PACKAGE_NAME = "com.twitter.android"
        private const val WEB_INTENT = "https://twitter.com/intent/tweet?text=%s&url=%s"
    }
}