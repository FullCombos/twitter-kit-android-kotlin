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
 * Collection of relevant Tweets matching a specified query.
 */
class Search(tweets: List<Tweet>, searchMetadata: SearchMetadata) {

    @JvmField
    @SerializedName("statuses")
    val tweets: List<Tweet>

    @SerializedName("search_metadata")
    val searchMetadata: SearchMetadata

    init {
        this.tweets = ModelUtils.getSafeList(tweets)
        this.searchMetadata = searchMetadata
    }
}