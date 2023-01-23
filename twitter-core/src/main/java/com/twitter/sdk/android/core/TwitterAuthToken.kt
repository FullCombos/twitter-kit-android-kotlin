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
package com.twitter.sdk.android.core

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Represents an authorization token and its secret.
 */
@Parcelize
data class TwitterAuthToken(

    @SerializedName("token")
    val token: String,

    @SerializedName("secret")
    val secret: String,

    val _createdAt: Long = System.currentTimeMillis()

) : AuthToken(_createdAt), Parcelable {

    override val isExpired: Boolean
        // Twitter does not expire OAuth1a tokens
        get() = false

    override fun toString(): String {
        val sb = StringBuilder()
            .append("token=").append(token)
            .append(",secret=").append(secret)
        return sb.toString()
    }
}