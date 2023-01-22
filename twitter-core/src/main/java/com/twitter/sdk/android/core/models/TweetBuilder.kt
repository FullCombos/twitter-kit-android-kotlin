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

class TweetBuilder {

    private var coordinates: Coordinates? = null
    private var createdAt: String? = null
    private var currentUserRetweet: Any? = null
    private var entities: TweetEntities? = null
    private var extendedEntities: TweetEntities? = null
    private var favoriteCount: Int? = null
    private var favorited: Boolean? = false
    private var filterLevel: String? = null
    private var id: Long = Tweet.INVALID_ID
    private var idStr: String? = null
    private var inReplyToScreenName: String? = null
    private var inReplyToStatusId: Long? = 0
    private var inReplyToStatusIdStr: String? = null
    private var inReplyToUserId: Long? = 0
    private var inReplyToUserIdStr: String? = null
    private var lang: String? = null
    private var place: Place? = null
    private var possiblySensitive: Boolean? = false
    private var scopes: Any? = null
    private var quotedStatusId: Long = 0
    private var quotedStatusIdStr: String? = null
    private var quotedStatus: Tweet? = null
    private var retweetCount = 0
    private var retweeted = false
    private var retweetedStatus: Tweet? = null
    private var source: String? = null
    private var text: String? = null
    private var displayTextRange: List<Int>? = null
    private var truncated = false
    private var user: User? = null
    private var withheldCopyright = false
    private var withheldInCountries: List<String>? = null
    private var withheldScope: String? = null
    private var card: Card? = null

    fun setCoordinates(coordinates: Coordinates?): TweetBuilder {
        this.coordinates = coordinates
        return this
    }

    fun setCreatedAt(createdAt: String?): TweetBuilder {
        this.createdAt = createdAt
        return this
    }

    fun setCurrentUserRetweet(currentUserRetweet: Any?): TweetBuilder {
        this.currentUserRetweet = currentUserRetweet
        return this
    }

    fun setEntities(entities: TweetEntities?): TweetBuilder {
        this.entities = entities
        return this
    }

    fun setExtendedEntities(extendedEntities: TweetEntities?): TweetBuilder {
        this.extendedEntities = extendedEntities
        return this
    }

    fun setFavoriteCount(favoriteCount: Int): TweetBuilder {
        this.favoriteCount = favoriteCount
        return this
    }

    fun setFavorited(favorited: Boolean): TweetBuilder {
        this.favorited = favorited
        return this
    }

    fun setFilterLevel(filterLevel: String?): TweetBuilder {
        this.filterLevel = filterLevel
        return this
    }

    fun setId(id: Long): TweetBuilder {
        this.id = id
        return this
    }

    fun setIdStr(idStr: String?): TweetBuilder {
        this.idStr = idStr
        return this
    }

    fun setInReplyToScreenName(inReplyToScreenName: String?): TweetBuilder {
        this.inReplyToScreenName = inReplyToScreenName
        return this
    }

    fun setInReplyToStatusId(inReplyToStatusId: Long): TweetBuilder {
        this.inReplyToStatusId = inReplyToStatusId
        return this
    }

    fun setInReplyToStatusIdStr(inReplyToStatusIdStr: String?): TweetBuilder {
        this.inReplyToStatusIdStr = inReplyToStatusIdStr
        return this
    }

    fun setInReplyToUserId(inReplyToUserId: Long): TweetBuilder {
        this.inReplyToUserId = inReplyToUserId
        return this
    }

    fun setInReplyToUserIdStr(inReplyToUserIdStr: String?): TweetBuilder {
        this.inReplyToUserIdStr = inReplyToUserIdStr
        return this
    }

    fun setLang(lang: String?): TweetBuilder {
        this.lang = lang
        return this
    }

    fun setPlace(place: Place?): TweetBuilder {
        this.place = place
        return this
    }

    fun setPossiblySensitive(possiblySensitive: Boolean): TweetBuilder {
        this.possiblySensitive = possiblySensitive
        return this
    }

    fun setScopes(scopes: Any?): TweetBuilder {
        this.scopes = scopes
        return this
    }

    fun setQuotedStatusId(quotedStatusId: Long): TweetBuilder {
        this.quotedStatusId = quotedStatusId
        return this
    }

    fun setQuotedStatusIdStr(quotedStatusIdStr: String?): TweetBuilder {
        this.quotedStatusIdStr = quotedStatusIdStr
        return this
    }

    fun setQuotedStatus(quotedStatus: Tweet?): TweetBuilder {
        this.quotedStatus = quotedStatus
        return this
    }

    fun setRetweetCount(retweetCount: Int): TweetBuilder {
        this.retweetCount = retweetCount
        return this
    }

    fun setRetweeted(retweeted: Boolean): TweetBuilder {
        this.retweeted = retweeted
        return this
    }

    fun setRetweetedStatus(retweetedStatus: Tweet?): TweetBuilder {
        this.retweetedStatus = retweetedStatus
        return this
    }

    fun setSource(source: String?): TweetBuilder {
        this.source = source
        return this
    }

    fun setText(text: String?): TweetBuilder {
        this.text = text
        return this
    }

    fun setDisplayTextRange(displayTextRange: List<Int>?): TweetBuilder {
        this.displayTextRange = displayTextRange
        return this
    }

    fun setTruncated(truncated: Boolean): TweetBuilder {
        this.truncated = truncated
        return this
    }

    fun setUser(user: User?): TweetBuilder {
        this.user = user
        return this
    }

    fun setWithheldCopyright(withheldCopyright: Boolean): TweetBuilder {
        this.withheldCopyright = withheldCopyright
        return this
    }

    fun setWithheldInCountries(withheldInCountries: List<String>?): TweetBuilder {
        this.withheldInCountries = withheldInCountries
        return this
    }

    fun setWithheldScope(withheldScope: String?): TweetBuilder {
        this.withheldScope = withheldScope
        return this
    }

    fun setCard(card: Card?): TweetBuilder {
        this.card = card
        return this
    }

    fun copy(tweet: Tweet): TweetBuilder {
        coordinates = tweet.coordinates
        createdAt = tweet.createdAt
        currentUserRetweet = tweet.currentUserRetweet
        entities = tweet.entities
        extendedEntities = tweet.extendedEntities
        favoriteCount = tweet.favoriteCount
        favorited = tweet.favorited
        filterLevel = tweet.filterLevel
        id = tweet.id
        idStr = tweet.idStr
        inReplyToScreenName = tweet.inReplyToScreenName
        inReplyToStatusId = tweet.inReplyToStatusId
        inReplyToStatusIdStr = tweet.inReplyToStatusIdStr
        inReplyToUserId = tweet.inReplyToUserId
        inReplyToUserIdStr = tweet.inReplyToStatusIdStr
        lang = tweet.lang
        place = tweet.place
        possiblySensitive = tweet.possiblySensitive
        scopes = tweet.scopes
        quotedStatusId = tweet.quotedStatusId
        quotedStatusIdStr = tweet.quotedStatusIdStr
        quotedStatus = tweet.quotedStatus
        retweetCount = tweet.retweetCount
        retweeted = tweet.retweeted
        retweetedStatus = tweet.retweetedStatus
        source = tweet.source
        text = tweet.text
        displayTextRange = tweet.displayTextRange
        truncated = tweet.truncated
        user = tweet.user
        withheldCopyright = tweet.withheldCopyright
        withheldInCountries = tweet.withheldInCountries
        withheldScope = tweet.withheldScope
        card = tweet.card
        return this
    }

    fun build(): Tweet {
        return Tweet(
            coordinates,
            createdAt.orEmpty(),
            currentUserRetweet,
            entities,
            extendedEntities,
            favoriteCount,
            favorited,
            filterLevel.orEmpty(),
            id,
            idStr.orEmpty(),
            inReplyToScreenName.orEmpty(),
            inReplyToStatusId,
            inReplyToStatusIdStr.orEmpty(),
            inReplyToUserId,
            inReplyToUserIdStr.orEmpty(),
            lang.orEmpty(),
            place,
            possiblySensitive,
            scopes,
            quotedStatusId,
            quotedStatusIdStr.orEmpty(),
            quotedStatus,
            retweetCount,
            retweeted,
            retweetedStatus,
            source.orEmpty(),
            text.orEmpty(),
            displayTextRange.orEmpty(),
            truncated,
            user,
            withheldCopyright,
            withheldInCountries.orEmpty(),
            withheldScope.orEmpty(),
            card
        )
    }
}