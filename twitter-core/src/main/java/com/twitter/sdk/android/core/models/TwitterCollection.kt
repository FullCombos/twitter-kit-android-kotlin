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
 * TwitterCollection is a new type of timeline you control: you create the collection, give it a
 * name, and select which Tweets to add, either by hand or programmatically using the REST API.
 */
data class TwitterCollection(

    @SerializedName("objects")
    val contents: Content?,

    @SerializedName("response")
    val metadata: Metadata?
) {
    /**
     * Contents represent the grouped, decomposed collection objects (tweets, users).
     */
    class Content(tweetMap: Map<Long, Tweet>?, userMap: Map<Long, User>?) {

        /**
         * Represents the mapping from string Tweet ids to user-trimmed Tweets.
         */
        @JvmField
        @SerializedName("tweets")
        val tweetMap: Map<Long, Tweet>?

        /**
         * Represents the mapping from string user ids to Users who authored Tweets or Timelines.
         */
        @JvmField
        @SerializedName("users")
        val userMap: Map<Long, User>?

        init {
            this.tweetMap = ModelUtils.getSafeMap(tweetMap)
            this.userMap = ModelUtils.getSafeMap(userMap)
        }
    }

    /**
     * Metadata lists references to decomposed objects and contextual information (such as cursors)
     * needed to navigate the boundaries of the collection in subsequent requests.
     */
    data class Metadata(

        /**
         * The collection object identifier (e.g. "custom-393773270547177472")
         */
        @SerializedName("timeline_id")
        val timelineId: String,

        @SerializedName("position")
        val position: Position?,

        /**
         * The ordered set of Collection items.
         */
        @SerializedName("timeline")
        val timelineItems: List<TimelineItem>?

    ) {
        /**
         * Position information for navigation.
         */
        data class Position(
            /**
             * The inclusive maximum position value of the results (positions will be less than or
             * equal to this value).
             */
            @SerializedName("max_position")
            val maxPosition: Long,

            /**
             * The exclusive minimum position value of the results (positions will be greater than
             * this value).
             */
            @SerializedName("min_position")
            val minPosition: Long
        )
    }

    /**
     * Represents an item in a Timeline with a object references.
     */
    data class TimelineItem(
        /**
         * Represents a reference to a Tweet.
         */
        @SerializedName("tweet") val tweetItem: TweetItem
    ) {
        data class TweetItem(
            /**
             * A Tweet id.
             */
            @SerializedName("id") val id: Long
        )
    }
}