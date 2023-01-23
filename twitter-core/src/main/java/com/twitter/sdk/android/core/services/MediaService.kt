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
package com.twitter.sdk.android.core.services

import com.twitter.sdk.android.core.models.*
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.http.*

interface MediaService {

    /**
     * Uploads media (images) to Twitter for use in a Tweet or Twitter-hosted Card. You may
     * upload the raw binary file or its base64 encoded contents. The media and media_data
     * parameters are mutually exclusive. Media uploads for images are limited to 5MB in file
     * size.
     * Supported MIME-types are PNG, JPEG, BMP, WEBP, GIF, and Animated Gif
     * @param media the raw binary file content to upload. Cannot be used with the mediaData
     * parameter.
     * @param mediaData the base64-encoded file content to upload. Cannot be used with the media
     * parameter
     */
    @Multipart
    @POST("https://upload.twitter.com/1.1/media/upload.json")
    fun upload(
        @Part("media") media: RequestBody?,
        @Part("media_data") mediaData: RequestBody?,
        @Part("additional_owners") additionalOwners: RequestBody?
    ): Call<Media>

    // https://developer.twitter.com/en/docs/twitter-api/v1/media/upload-media/api-reference/post-media-upload-init
    @FormUrlEncoded
    @POST("https://upload.twitter.com/1.1/media/upload.json")
    fun init(
        @Field("command") command: String = "INIT",
        @Field("media_type") mediaType: String,
        @Field("total_bytes") totalBytes: Long,
        @Field("media_category") mediaCategory: String? = "tweet_video",
        @Field("additional_owners") additionalOwners: RequestBody? = null
    ): Call<Media>

    // https://developer.twitter.com/en/docs/twitter-api/v1/media/upload-media/api-reference/post-media-upload-append
    @Multipart
    @POST("https://upload.twitter.com/1.1/media/upload.json")
    fun append(
        @Part("command") command: RequestBody = "APPEND".toRequestBody(),
        @Part("media_id") mediaId: RequestBody,
        @Part("media") media: RequestBody? = null,
        @Part("media_data") mediaData: RequestBody? = null,
        @Part("segment_index") segmentIndex: RequestBody
    ): Call<Unit>

    // https://developer.twitter.com/en/docs/twitter-api/v1/media/upload-media/api-reference/post-media-upload-finalize
    @FormUrlEncoded
    @POST("https://upload.twitter.com/1.1/media/upload.json")
    fun finalize(
        @Field("command") command: String = "FINALIZE",
        @Field("media_id") mediaId: Long
    ): Call<Media>

    // https://developer.twitter.com/en/docs/twitter-api/v1/media/upload-media/api-reference/get-media-upload-status
    @GET("https://upload.twitter.com/1.1/media/upload.json")
    fun checkStatus(
        @Query("command") command: String = "STATUS",
        @Query("media_id") mediaId: Long
    ): Call<Media>
}