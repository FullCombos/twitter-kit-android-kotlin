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

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.twitter.sdk.android.core.internal.persistence.SerializationStrategy

/**
 * Represents a Twitter session that is associated with a [com.twitter.sdk.android.core.TwitterAuthToken].
 */
/**
 * @param authToken Auth token
 * @param userId    User ID
 * @param userName  User Name
 *
 * @throws java.lang.IllegalArgumentException if token argument is null
 */
data class TwitterSession
    (
    override val authToken: TwitterAuthToken,
    val userId: Long,

    @SerializedName("user_name")
    val userName: String

) : Session<TwitterAuthToken>(authToken, userId) {

    internal class Serializer : SerializationStrategy<TwitterSession> {

        private val gson: Gson = Gson()

        override fun serialize(`object`: TwitterSession): String {
            try {
                return gson.toJson(`object`)
            } catch (e: Exception) {
                Twitter.getLogger().d(TwitterCore.TAG, e.message.orEmpty())
            }
            return ""
        }

        override fun deserialize(serializedObject: String?): TwitterSession? {
            if (!serializedObject.isNullOrEmpty()) {
                try {
                    return gson.fromJson(serializedObject, TwitterSession::class.java)
                } catch (e: Exception) {
                    Twitter.getLogger().d(TwitterCore.TAG, e.message.orEmpty())
                }
            }
            return null
        }
    }

    companion object {
        const val UNKNOWN_USER_ID = -1L
        const val UNKNOWN_USER_NAME = ""
    }
}