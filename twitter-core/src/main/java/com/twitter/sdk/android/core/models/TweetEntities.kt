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

/**
 * Provides metadata and additional contextual information about content posted in a tweet.
 */
class TweetEntities(
    urls: List<UrlEntity>?,
    userMentions: List<MentionEntity>?,
    media: List<MediaEntity>?,
    hashtags: List<HashtagEntity>?,
    symbols: List<SymbolEntity>?
) {

    /**
     * Represents URLs included in the text of a Tweet or within textual fields of a user object.
     */
    @JvmField
    @SerializedName("urls")
    val urls: List<UrlEntity>?

    /**
     * Represents other Twitter users mentioned in the text of the Tweet.
     */
    @JvmField
    @SerializedName("user_mentions")
    val userMentions: List<MentionEntity>?

    /**
     * Represents media elements uploaded with the Tweet.
     */
    @JvmField
    @SerializedName("media")
    val media: List<MediaEntity>?

    /**
     * Represents hashtags which have been parsed out of the Tweet text.
     */
    @JvmField
    @SerializedName("hashtags")
    val hashtags: List<HashtagEntity>?

    /**
     * Represents symbols which have been parsed out of the Tweet text.
     */
    @JvmField
    @SerializedName("symbols")
    val symbols: List<SymbolEntity>?

    init {
        this.urls = ModelUtils.getSafeList(urls)
        this.userMentions = ModelUtils.getSafeList(userMentions)
        this.media = ModelUtils.getSafeList(media)
        this.hashtags = ModelUtils.getSafeList(hashtags)
        this.symbols = ModelUtils.getSafeList(symbols)
    }

    companion object {
        val EMPTY = TweetEntities(null, null, null, null, null)
    }
}