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

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.text.format.DateUtils
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.models.Media
import com.twitter.sdk.android.core.models.TweetContent
import com.twitter.sdk.android.core.models.TweetResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.internal.toLongOrDefault
import retrofit2.awaitResponse
import java.io.File
import java.io.RandomAccessFile

class TweetUploadService
// testing purposes
internal constructor(private var dependencyProvider: DependencyProvider) : Service(),
    CoroutineScope {

    companion object {
        private const val UPLOAD_SUCCESS = "com.twitter.sdk.android.tweetcomposer.UPLOAD_SUCCESS"
        private const val UPLOAD_FAILURE = "com.twitter.sdk.android.tweetcomposer.UPLOAD_FAILURE"

        const val TWEET_COMPOSE_CANCEL =
            "com.twitter.sdk.android.tweetcomposer.TWEET_COMPOSE_CANCEL"

        const val EXTRA_TWEET_ID = "EXTRA_TWEET_ID"
        const val EXTRA_USER_TOKEN = "EXTRA_USER_TOKEN"
        const val EXTRA_TWEET_TEXT = "EXTRA_TWEET_TEXT"
        const val EXTRA_IMAGE_URI = "EXTRA_IMAGE_URI"
        const val EXTRA_VIDEO_URI = "EXTRA_VIDEO_URI"
        const val EXTRA_RETRY_INTENT = "EXTRA_RETRY_INTENT"

        const val TAG = "TweetUploadService"
    }

    override val coroutineContext = Dispatchers.IO + SupervisorJob()

    private var intent: Intent? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val token = intent?.getParcelableExtra<TwitterAuthToken>(EXTRA_USER_TOKEN)
            ?: return super.onStartCommand(intent, flags, startId)

        this.intent = intent
        val twitterSession = TwitterSession(
            token,
            TwitterSession.UNKNOWN_USER_ID,
            TwitterSession.UNKNOWN_USER_NAME
        )
        val tweetText = intent.getStringExtra(EXTRA_TWEET_TEXT)
        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)
        val videoUri = intent.getParcelableExtra<Uri>(EXTRA_VIDEO_URI)

        uploadTweet(twitterSession, tweetText, imageUri, videoUri)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun uploadTweet(
        session: TwitterSession,
        text: String?,
        imageUri: Uri?,
        videoUri: Uri?
    ) {
        val client = dependencyProvider.getTwitterApiClient(session)

        if (videoUri != null) {
            uploadTweetWithVideo(client, videoUri, text)
        } else if (imageUri != null) {
            uploadMediaWithImage(client, imageUri, object : Callback<Media>() {

                override fun success(result: Result<Media>) {
                    uploadTweetWithMediaId(client, text, result.data.mediaIdString)
                }

                override fun failure(exception: TwitterException) {
                    fail(exception)
                }
            })
        } else {
            uploadTweetWithMediaId(client, text, null)
        }
    }

    private fun uploadTweetWithMediaId(client: TwitterApiClient, text: String?, mediaId: String?) {
        val tweet = TweetContent(text, mediaId)
        client.getStatusesService()
            .updateV2(tweet)
            .enqueue(object : Callback<TweetResponse>() {

                override fun success(result: Result<TweetResponse>) {
                    sendSuccessBroadcast(result.data.id.toLongOrDefault(-1L))
                    stopSelf()
                }

                override fun failure(exception: TwitterException) {
                    fail(exception)
                }
            })
    }

    private fun uploadMediaWithImage(
        client: TwitterApiClient,
        imageUri: Uri,
        callback: Callback<Media>
    ) {
        val path = FileUtils.getPath(this@TweetUploadService, imageUri)
        if (path == null) {
            fail(TwitterException("Uri file path resolved to null"))
            return
        }
        val file = File(path)
        val mimeType = FileUtils.getMimeType(file)
        val media = file.asRequestBody(mimeType?.toMediaTypeOrNull())
        client.getMediaService().upload(media, null, null).enqueue(callback)
    }

    // https://developer.twitter.com/en/docs/twitter-api/v1/media/upload-media/uploading-media/chunked-media-upload
    // example in python https://github.com/twitterdev/large-video-upload-python/blob/master/async-upload.py
    private fun uploadTweetWithVideo(
        client: TwitterApiClient,
        videoUri: Uri,
        text: String?
    ) {
        launch(Dispatchers.IO) {
            try {
                val file = RandomAccessFile(videoUri.toString(), "r")
                uploadTweetWithVideoInit(client, file, videoUri, text)
            } catch (e: Exception) {
                fail(e)
            }
        }
    }

    private suspend fun uploadTweetWithVideoInit(
        client: TwitterApiClient,
        file: RandomAccessFile,
        videoUri: Uri,
        text: String?
    ) {
        val mimeType = FileUtils.getMimeType(File(videoUri.toString())).orEmpty()

        withContext(Dispatchers.IO) {
            val result =
                client.getMediaService().init(mediaType = mimeType, totalBytes = file.length())
                    .awaitResponse()

            uploadTweetWithVideoAppend(
                client,
                file,
                text,
                result.body()?.mediaId,
                mimeType
            )
        }
    }

    private suspend fun uploadTweetWithVideoAppend(
        client: TwitterApiClient,
        file: RandomAccessFile,
        text: String?,
        mediaId: Long?,
        mimeType: String
    ) {
        if (mediaId == null) {
            fail(IllegalArgumentException("media id can not be null"))
            return
        }

        withContext(Dispatchers.IO) {

            val mediaService = client.getMediaService()
            val fileLength = file.length()
            val maxSize = 5 * 1024 * 1024
            var data = ByteArray(minOf(maxSize, fileLength.toInt()))
            var segmentIndex = 0
            var isComplete = true

            file.use {
                while (file.read(data) != -1) {
                    val result = mediaService.append(
                        mediaId = mediaId.toString().toRequestBody(),
                        media = data.toRequestBody(mimeType.toMediaTypeOrNull()),
                        segmentIndex = segmentIndex.toString().toRequestBody()
                    ).awaitResponse()

                    if (!result.isSuccessful) {
                        isComplete = false
                        break
                    }

                    val nextSize = minOf(maxSize, (fileLength - file.filePointer).toInt())
                    if (nextSize > 0 && nextSize != data.size) {
                        data = ByteArray(nextSize)
                    }
                    segmentIndex++
                }
            }

            if (isComplete) {
                uploadTweetWithVideoFinalize(client, text, mediaId)
            } else {
                fail(IllegalStateException("upload video failed"))
            }
        }
    }

    private suspend fun uploadTweetWithVideoFinalize(
        client: TwitterApiClient,
        text: String?,
        mediaId: Long
    ) {
        withContext(Dispatchers.IO) {
            val result = client.getMediaService().finalize(mediaId = mediaId).awaitResponse()
            uploadTweetWithVideoStatus(client, text, result.body())
        }
    }

    private suspend fun uploadTweetWithVideoStatus(
        client: TwitterApiClient,
        text: String?,
        videoMedia: Media?
    ) {
        if (videoMedia == null) {
            fail(IllegalStateException("request finalize api failed"))
            return
        }

        val processingInfo = videoMedia.processingInfo
        if (processingInfo == null || processingInfo.state == "succeeded") {
            uploadTweetWithMediaId(client, text, videoMedia.mediaIdString)
            return
        }

        if (processingInfo.state == "failed") {
            fail(IllegalStateException("upload video failed"))
            return
        }

        val error = processingInfo.error
        if (error != null) {
            fail(IllegalStateException(error.message.orEmpty()))
            return
        }

        withContext(Dispatchers.IO) {
            val checkAfterSecs = processingInfo.checkAfterSecs ?: 0
            delay(checkAfterSecs * DateUtils.SECOND_IN_MILLIS)

            val result =
                client.getMediaService().checkStatus(mediaId = videoMedia.mediaId).awaitResponse()
            if (result.isSuccessful) {
                uploadTweetWithVideoStatus(client, text, result.body())
            } else {
                fail(IllegalStateException("request check status api failed"))
            }
        }
    }

    private fun fail(e: Exception?) {
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

    override fun onDestroy() {
        cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    /*
     * Mockable class that provides ComposerController dependencies.
     */
    class DependencyProvider {

        fun getTwitterApiClient(session: TwitterSession): TwitterApiClient {
            return TwitterCore.getInstance().getApiClient(session)
        }
    }
}