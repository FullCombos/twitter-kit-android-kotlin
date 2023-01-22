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

class UserBuilder {

    private var contributorsEnabled = false
    private var createdAt: String? = null
    private var defaultProfile = false
    private var defaultProfileImage = false
    private var description: String? = null
    private var email: String? = null
    private var entities: UserEntities? = null
    private var favouritesCount = 0
    private var followRequestSent = false
    private var followersCount = 0
    private var friendsCount = 0
    private var geoEnabled = false
    private var id: Long = User.INVALID_ID
    private var idStr: String? = null
    private var isTranslator = false
    private var lang: String? = null
    private var listedCount = 0
    private var location: String? = null
    private var name: String? = null
    private var profileBackgroundColor: String? = null
    private var profileBackgroundImageUrl: String? = null
    private var profileBackgroundImageUrlHttps: String? = null
    private var profileBackgroundTile = false
    private var profileBannerUrl: String? = null
    private var profileImageUrl: String? = null
    private var profileImageUrlHttps: String? = null
    private var profileLinkColor: String? = null
    private var profileSidebarBorderColor: String? = null
    private var profileSidebarFillColor: String? = null
    private var profileTextColor: String? = null
    private var profileUseBackgroundImage = false
    private var protectedUser = false
    private var screenName: String? = null
    private var showAllInlineMedia = false
    private var status: Tweet? = null
    private var statusesCount = 0
    private var timeZone: String? = null
    private var url: String? = null
    private var utcOffset = 0
    private var verified = false
    private var withheldInCountries: List<String>? = null
    private var withheldScope: String? = null

    fun setContributorsEnabled(contributorsEnabled: Boolean): UserBuilder {
        this.contributorsEnabled = contributorsEnabled
        return this
    }

    fun setCreatedAt(createdAt: String?): UserBuilder {
        this.createdAt = createdAt
        return this
    }

    fun setDefaultProfile(defaultProfile: Boolean): UserBuilder {
        this.defaultProfile = defaultProfile
        return this
    }

    fun setDefaultProfileImage(defaultProfileImage: Boolean): UserBuilder {
        this.defaultProfileImage = defaultProfileImage
        return this
    }

    fun setDescription(description: String?): UserBuilder {
        this.description = description
        return this
    }

    fun setEmail(email: String?): UserBuilder {
        this.email = email
        return this
    }

    fun setEntities(entities: UserEntities?): UserBuilder {
        this.entities = entities
        return this
    }

    fun setFavouritesCount(favouritesCount: Int): UserBuilder {
        this.favouritesCount = favouritesCount
        return this
    }

    fun setFollowRequestSent(followRequestSent: Boolean): UserBuilder {
        this.followRequestSent = followRequestSent
        return this
    }

    fun setFollowersCount(followersCount: Int): UserBuilder {
        this.followersCount = followersCount
        return this
    }

    fun setFriendsCount(friendsCount: Int): UserBuilder {
        this.friendsCount = friendsCount
        return this
    }

    fun setGeoEnabled(geoEnabled: Boolean): UserBuilder {
        this.geoEnabled = geoEnabled
        return this
    }

    fun setId(id: Long): UserBuilder {
        this.id = id
        return this
    }

    fun setIdStr(idStr: String?): UserBuilder {
        this.idStr = idStr
        return this
    }

    fun setIsTranslator(isTranslator: Boolean): UserBuilder {
        this.isTranslator = isTranslator
        return this
    }

    fun setLang(lang: String?): UserBuilder {
        this.lang = lang
        return this
    }

    fun setListedCount(listedCount: Int): UserBuilder {
        this.listedCount = listedCount
        return this
    }

    fun setLocation(location: String?): UserBuilder {
        this.location = location
        return this
    }

    fun setName(name: String?): UserBuilder {
        this.name = name
        return this
    }

    fun setProfileBackgroundColor(profileBackgroundColor: String?): UserBuilder {
        this.profileBackgroundColor = profileBackgroundColor
        return this
    }

    fun setProfileBackgroundImageUrl(profileBackgroundImageUrl: String?): UserBuilder {
        this.profileBackgroundImageUrl = profileBackgroundImageUrl
        return this
    }

    fun setProfileBackgroundImageUrlHttps(profileBackgroundImageUrlHttps: String?): UserBuilder {
        this.profileBackgroundImageUrlHttps = profileBackgroundImageUrlHttps
        return this
    }

    fun setProfileBackgroundTile(profileBackgroundTile: Boolean): UserBuilder {
        this.profileBackgroundTile = profileBackgroundTile
        return this
    }

    fun setProfileBannerUrl(profileBannerUrl: String?): UserBuilder {
        this.profileBannerUrl = profileBannerUrl
        return this
    }

    fun setProfileImageUrl(profileImageUrl: String?): UserBuilder {
        this.profileImageUrl = profileImageUrl
        return this
    }

    fun setProfileImageUrlHttps(profileImageUrlHttps: String?): UserBuilder {
        this.profileImageUrlHttps = profileImageUrlHttps
        return this
    }

    fun setProfileLinkColor(profileLinkColor: String?): UserBuilder {
        this.profileLinkColor = profileLinkColor
        return this
    }

    fun setProfileSidebarBorderColor(profileSidebarBorderColor: String?): UserBuilder {
        this.profileSidebarBorderColor = profileSidebarBorderColor
        return this
    }

    fun setProfileSidebarFillColor(profileSidebarFillColor: String?): UserBuilder {
        this.profileSidebarFillColor = profileSidebarFillColor
        return this
    }

    fun setProfileTextColor(profileTextColor: String?): UserBuilder {
        this.profileTextColor = profileTextColor
        return this
    }

    fun setProfileUseBackgroundImage(profileUseBackgroundImage: Boolean): UserBuilder {
        this.profileUseBackgroundImage = profileUseBackgroundImage
        return this
    }

    fun setProtectedUser(protectedUser: Boolean): UserBuilder {
        this.protectedUser = protectedUser
        return this
    }

    fun setScreenName(screenName: String?): UserBuilder {
        this.screenName = screenName
        return this
    }

    fun setShowAllInlineMedia(showAllInlineMedia: Boolean): UserBuilder {
        this.showAllInlineMedia = showAllInlineMedia
        return this
    }

    fun setStatus(status: Tweet?): UserBuilder {
        this.status = status
        return this
    }

    fun setStatusesCount(statusesCount: Int): UserBuilder {
        this.statusesCount = statusesCount
        return this
    }

    fun setTimeZone(timeZone: String?): UserBuilder {
        this.timeZone = timeZone
        return this
    }

    fun setUrl(url: String?): UserBuilder {
        this.url = url
        return this
    }

    fun setUtcOffset(utcOffset: Int): UserBuilder {
        this.utcOffset = utcOffset
        return this
    }

    fun setVerified(verified: Boolean): UserBuilder {
        this.verified = verified
        return this
    }

    fun setWithheldInCountries(withheldInCountries: List<String>?): UserBuilder {
        this.withheldInCountries = withheldInCountries
        return this
    }

    fun setWithheldScope(withheldScope: String?): UserBuilder {
        this.withheldScope = withheldScope
        return this
    }

    fun build(): User {
        return User(
            contributorsEnabled,
            createdAt.orEmpty(),
            defaultProfile,
            defaultProfileImage,
            description,
            email,
            entities,
            favouritesCount,
            followRequestSent,
            followersCount,
            friendsCount,
            geoEnabled,
            id,
            idStr.orEmpty(),
            isTranslator,
            lang.orEmpty(),
            listedCount,
            location,
            name.orEmpty(),
            profileBackgroundColor.orEmpty(),
            profileBackgroundImageUrl.orEmpty(),
            profileBackgroundImageUrlHttps.orEmpty(),
            profileBackgroundTile,
            profileBannerUrl.orEmpty(),
            profileImageUrl.orEmpty(),
            profileImageUrlHttps.orEmpty(),
            profileLinkColor.orEmpty(),
            profileSidebarBorderColor.orEmpty(),
            profileSidebarFillColor.orEmpty(),
            profileTextColor.orEmpty(),
            profileUseBackgroundImage,
            protectedUser,
            screenName.orEmpty(),
            showAllInlineMedia,
            status,
            statusesCount,
            timeZone,
            url,
            utcOffset,
            verified,
            withheldInCountries.orEmpty(),
            withheldScope.orEmpty()
        )
    }
}