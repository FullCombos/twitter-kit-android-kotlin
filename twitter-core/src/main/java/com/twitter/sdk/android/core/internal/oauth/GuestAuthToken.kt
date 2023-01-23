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

import android.text.format.DateUtils
import androidx.core.util.ObjectsCompat
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class GuestAuthToken(

    val _tokenType: String,

    val _accessToken: String,

    @SerializedName("guest_token")
    val guestToken: String,

    override val _createdAt: Long = System.currentTimeMillis()

) : OAuth2Token(_tokenType, _accessToken, _createdAt) {

    companion object {
        /*
     * macaw-login oauth2/token does not return an expires_in field as recommended in RFC 6749,
     * https://tools.ietf.org/html/rfc6749#section-4.2.2. If token expiration policies change,
     * update this constant to help prevent requests with tokens known to be expired.
     * https://cgit.twitter.biz/birdcage/tree/passbird/server/src/main/scala/com/twitter/passbird/profile/PassbirdServerProfile.scala#n186
     */
        private const val EXPIRES_IN_MS = DateUtils.HOUR_IN_MILLIS * 3
    }

    // Passbird maintains guest tokens for at least 1 hour, but no more than 3 hours. Tokens
    // older than 3 hours are known to have expired and should not be reused.
    override val isExpired: Boolean
        get() = System.currentTimeMillis() >= createdAt + EXPIRES_IN_MS

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (other !is GuestAuthToken) return false
        return ObjectsCompat.equals(guestToken, other.guestToken)
    }

    override fun hashCode(): Int {
        return 31 * super.hashCode() + guestToken.hashCode()
    }
}