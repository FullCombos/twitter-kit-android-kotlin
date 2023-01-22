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

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.Tweet
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class TweetUploadService  // testing purposes
internal constructor(private var dependencyProvider: DependencyProvider) :
    IntentService("TweetUploadService") {

    companion object {
        private const val UPLOAD_SUCCESS = "com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS"
        private const val UPLOAD_FAILURE = "com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE"

        const val TWEET_COMPOSE_CANCEL =
            "com.twitter.sdk.android.tweetcomposer.TWEET_COMPOSE_CANCEL"

        const val EXTRA_TWEET_ID = "EXTRA_TWEET_ID"
        const val EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN"
        const val EXTRA_TWEET_TEXT = "EXTRA_TWEET_TEXT"
        const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        const val EXTRA_RETRY_INTENT = "EXTRA_RETRY_INTENT"

        const val TAG = "TweetUploadService"
    }

    private var intent: Intent? = null

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val token = intent?.getParcelableExtra<TwitterAuthToken>(EXTRA_USER_TOKEN)
        this.intent = intent
        val twitterSession = TwitterSession(
            token,
            TwitterSession.UNKNOWN_USER_ID,
            TwitterSession.UNKNOWN_USER_NAME
        )
        val tweetText = intent?.getStringExtra(EXTRA_TWEET_TEXT)
        val imageUri = intent?.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)
        uploadTweet(twitterSession, tweetText, imageUri)
    }

    private fun uploadTweet(session: TwitterSession?, text: String?, imageUri: Uri?) {
        if (imageUri != null) {
            uploadMedia(session, imageUri, object : Callback<Media>() {

                override fun success(result: Result<Media>) {
                    uploadTweetWithMedia(session, text, result.data.mediaIdString)
                }

                override fun failure(exception: TwitterException) {
                    fail(exception)
                }
            })
        } else {
            uploadTweetWithMedia(session, text, null)
        }
    }

    private fun uploadTweetWithMedia(session: TwitterSession?, text: String?, mediaId: String?) {
        val client = dependencyProvider.getTwitterApiClient(session)
        client.statusesService.update(text, null, null, null, null, null, null, true, mediaId)
            .enqueue(object : Callback<Tweet>() {

                override fun success(result: Result<Tweet>) {
                    sendSuccessBroadcast(result.data.id)
                    stopSelf()
                }

                override fun failure(exception: TwitterException) {
                    fail(exception)
                }
            })
    }

    private fun uploadMedia(session: TwitterSession?, imageUri: Uri, callback: Callback<Media>) {
        val client = dependencyProvider.getTwitterApiClient(session)
        val path = FileUtils.getPath(this@TweetUploadService, imageUri)
        if (path == null) {
            fail(TwitterException("Uri file path resolved to null"))
            return
        }
        val file = File(path)
        val mimeType = FileUtils.getMimeType(file)
        val media = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        client.mediaService.upload(media, null, null).enqueue(callback)
    }

    private fun fail(e: TwitterException?) {
        sendFailureBroadcast(intent)
        Twitter.getLogger().e(TAG, "Post Tweet failed", e)
        stopSelf()
    }

    private fun sendSuccessBroadcast(tweetId: Long) {
        val intent = Intent(UPLOAD_SUCCESS)
        intent.putExtra(EXTRA_TWEET_ID, tweetId)
        intent.setPackage(applicationContext.packageName)
        sendBroadcast(intent)
    }

    private fun sendFailureBroadcast(original: Intent?) {
        val intent = Intent(UPLOAD_FAILURE)
        intent.putExtra(EXTRA_RETRY_INTENT, original)
        intent.setPackage(applicationContext.packageName)
        sendBroadcast(intent)
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    class DependencyProvider {

        fun getTwitterApiClient(session: TwitterSession?): TwitterApiClient {
            return TwitterCore.getInstance().getApiClient(session)
        }
    }
}