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

internal class SearchMetadata(
    maxId: Int,
    sinceId: Int,
    refreshUrl: String,
    nextResults: String,
    count: Int,
    completedIn: Double,
    sinceIdStr: String,
    query: String,
    maxIdStr: String
) {

    @SerializedName("max_id")
    val maxId: Long

    @SerializedName("since_id")
    val sinceId: Long

    @SerializedName("refresh_url")
    val refreshUrl: String

    @SerializedName("next_results")
    val nextResults: String

    @SerializedName("count")
    val count: Long

    @SerializedName("completed_in")
    val completedIn: Double

    @SerializedName("since_id_str")
    val sinceIdStr: String

    @SerializedName("query")
    val query: String

    @SerializedName("max_id_str")
    val maxIdStr: String

    init {
        this.maxId = maxId.toLong()
        this.sinceId = sinceId.toLong()
        this.refreshUrl = refreshUrl
        this.nextResults = nextResults
        this.count = count.toLong()
        this.completedIn = completedIn
        this.sinceIdStr = sinceIdStr
        this.query = query
        this.maxIdStr = maxIdStr
    }
}