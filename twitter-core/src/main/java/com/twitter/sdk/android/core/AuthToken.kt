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

import androidx.core.util.ObjectsCompat
import com.google.gson.annotations.SerializedName

/**
 * Base class for authentication tokens.
 */
abstract class AuthToken protected constructor(

    /**
     * Unit time or epoch time when the token was created (always in UTC). The
     * time may be 0 if the token is deserialized from data missing the field.
     */
    @SerializedName("created_at")
    open val createdAt: Long
) {
    /**
     * Determines whether a token is known to have expired.
     * @return true if the token is known to have expired, otherwise false to indicate the token
     * may or may not be considered expired by the server.
     */
    abstract val isExpired: Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AuthToken) return false
        return ObjectsCompat.equals(createdAt, other.createdAt)
    }

    override fun hashCode(): Int {
        return createdAt.hashCode()
    }
}