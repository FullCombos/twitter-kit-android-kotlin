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
package com.twitter.sdk.android.core.internal

import com.twitter.sdk.android.core.models.User

class UserUtils private constructor() {

    companion object {

        @JvmStatic
        fun getProfileImageUrlHttps(user: User?, size: AvatarSize): String? {
            return if (user?.profileImageUrlHttps != null) {
                user.profileImageUrlHttps.replace(AvatarSize.NORMAL.getSuffix(), size.getSuffix())
            } else {
                null
            }
        }

        /**
         * @return the given screenName, prepended with an "@"
         */
        @JvmStatic
        fun formatScreenName(screenName: CharSequence): CharSequence {
            if (screenName.isEmpty()) {
                return ""
            }
            return if (screenName[0] == '@') {
                screenName
            } else {
                "@$screenName"
            }
        }
    }

    // see https://dev.twitter.com/overview/general/user-profile-images-and-banners
    // see also: https://confluence.twitter.biz/display/PLATFORM/Image+Types+and+Sizes
    enum class AvatarSize(private val suffix: String) {

        NORMAL("_normal"),
        BIGGER("_bigger"),
        MINI("_mini"),
        ORIGINAL("_original"),
        REASONABLY_SMALL("_reasonably_small");

        fun getSuffix(): String {
            return suffix
        }
    }
}