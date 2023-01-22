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
package com.twitter.sdk.android.core.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Contains information about video.
 */
class VideoInfo(aspectRatio: List<Int>, durationMillis: Long, variants: List<Variant>) :
    Serializable {
    
    /**
     * The aspect ratio of the video, as a simplified fraction of width and height in a 2-element
     * list. Typical values are [4, 3] or [16, 9].
     */
    @SerializedName("aspect_ratio")
    val aspectRatio: List<Int>

    /**
     * The length of the video, in milliseconds.
     */
    @JvmField
    @SerializedName("duration_millis")
    val durationMillis: Long

    /**
     * Different encodings/streams of the video.
     */
    @JvmField
    @SerializedName("variants")
    val variants: List<Variant>

    init {
        this.aspectRatio = ModelUtils.getSafeList(aspectRatio)
        this.durationMillis = durationMillis
        this.variants = ModelUtils.getSafeList(variants)
    }

    class Variant(

        @SerializedName("bitrate")
        val bitrate: Long,

        @SerializedName("content_type")
        val contentType: String,

        @SerializedName("url")
        val url: String

    ) : Serializable
}