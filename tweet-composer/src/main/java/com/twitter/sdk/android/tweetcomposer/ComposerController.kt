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

import android.content.Intent
import android.net.Uri
import com.twitter.Validator
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.models.User
import com.twitter.sdk.android.tweetcomposer.ComposerActivity.Finisher

internal class ComposerController constructor(
    val composerView: ComposerView,
    val session: TwitterSession,
    val imageUri: Uri?,
    text: String?,
    hashtags: String?,
    val finisher: Finisher,
    private val dependencyProvider: DependencyProvider = DependencyProvider()
) {

    init {
        composerView.setCallbacks(ComposerCallbacksImpl())
        composerView.setTweetText(generateText(text, hashtags))
        setProfilePhoto()
        setImageView(imageUri)
    }

    private fun generateText(text: String?, hashtags: String?): String {
        val sb = StringBuilder()
        if (!text.isNullOrEmpty()) {
            sb.append(text)
        }
        if (!hashtags.isNullOrEmpty()) {
            if (sb.isNotEmpty()) {
                sb.append(" ")
            }
            sb.append(hashtags)
        }
        return sb.toString()
    }

    private fun setProfilePhoto() {
        dependencyProvider.getApiClient(session).accountService
            .verifyCredentials(false, true, false)
            .enqueue(object : Callback<User>() {

                override fun success(result: Result<User>) {
                    composerView.setProfilePhotoView(result.data)
                }

                override fun failure(exception: TwitterException) {
                    // show placeholder background color
                    composerView.setProfilePhotoView(null)
                }
            })
    }

    private fun setImageView(imageUri: Uri?) {
        if (imageUri != null) {
            composerView.setImageView(imageUri)
        }
    }

    interface ComposerCallbacks {
        fun onTextChanged(text: String?)
        fun onTweetPost(text: String?)
        fun onCloseClick()
    }

    internal inner class ComposerCallbacksImpl : ComposerCallbacks {

        override fun onTextChanged(text: String?) {
            val charCount = tweetTextLength(text)
            composerView.setCharCount(remainingCharCount(charCount))
            // character count overflow red color
            if (isTweetTextOverflow(charCount)) {
                composerView.setCharCountTextStyle(R.style.twitter_ComposerCharCountOverflow)
            } else {
                composerView.setCharCountTextStyle(R.style.twitter_ComposerCharCount)
            }
            // Tweet post button enable/disable
            composerView.postTweetEnabled(isPostEnabled(charCount))
        }

        override fun onTweetPost(text: String?) {
            val intent = Intent(composerView.context, TweetUploadService::class.java)
            intent.putExtra(TweetUploadService.EXTRA_USER_TOKEN, session.authToken)
            intent.putExtra(TweetUploadService.EXTRA_TWEET_TEXT, text)
            intent.putExtra(TweetUploadService.EXTRA_IMAGE_URI, imageUri)
            composerView.context.startService(intent)
            finisher.finish()
        }

        override fun onCloseClick() {
            onClose()
        }
    }

    fun onClose() {
        sendCancelBroadcast()
        finisher.finish()
    }

    private fun tweetTextLength(text: String?): Int {
        return if (text.isNullOrEmpty()) {
            0
        } else {
            dependencyProvider.tweetValidator.getTweetLength(text)
        }
    }

    private fun sendCancelBroadcast() {
        val intent = Intent(TweetUploadService.TWEET_COMPOSE_CANCEL)
        intent.setPackage(composerView.context.packageName)
        composerView.context.sendBroadcast(intent)
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    internal class DependencyProvider {
        val tweetValidator = Validator()

        fun getApiClient(session: TwitterSession): TwitterApiClient {
            return TwitterCore.getInstance().getApiClient(session)
        }
    }

    companion object {

        private fun remainingCharCount(charCount: Int): Int {
            return Validator.MAX_TWEET_LENGTH - charCount
        }

        /*
        * @return true if the Tweet text is a valid length, false otherwise.
        */
        private fun isPostEnabled(charCount: Int): Boolean {
            return charCount > 0 && charCount <= Validator.MAX_TWEET_LENGTH
        }

        /*
        * @return true if the Tweet text is too long, false otherwise.
        */
        private fun isTweetTextOverflow(charCount: Int): Boolean {
            return charCount > Validator.MAX_TWEET_LENGTH
        }
    }
}