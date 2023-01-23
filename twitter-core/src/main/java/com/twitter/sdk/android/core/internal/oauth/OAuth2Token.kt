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
package com.twitter.sdk.android.core.internal.oauth

import android.os.Parcelable
import androidx.core.util.ObjectsCompat
import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.AuthToken
import kotlinx.parcelize.Parcelize

/**
 * OAuth2.0 token.
 */
@Parcelize
open class OAuth2Token(

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("access_token")
    val accessToken: String,

    open val _createdAt: Long = System.currentTimeMillis()

) : AuthToken(_createdAt), Parcelable {

    // Oauth 2.0 tokens do not have a common expiration policy. Returning false indicates
    // the token is not known to have expired. App auth tokens only expire when manually
    // invalidated, while guest auth tokens are known to have expired after 3 hours.
    override val isExpired: Boolean
        get() =
        // Oauth 2.0 tokens do not have a common expiration policy. Returning false indicates
        // the token is not known to have expired. App auth tokens only expire when manually
            // invalidated, while guest auth tokens are known to have expired after 3 hours.
            false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is OAuth2Token) return false
        return ObjectsCompat.equals(accessToken, other.accessToken) &&
                ObjectsCompat.equals(tokenType, other.tokenType)
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + ObjectsCompat.hash(tokenType, accessToken)
    }
}