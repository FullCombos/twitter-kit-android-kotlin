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
import com.google.gson.GsonBuilder
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken
import com.twitter.sdk.android.core.internal.persistence.SerializationStrategy

/**
 * @param authToken Auth token
 *
 * @throws java.lang.IllegalArgumentException if token argument is null
 */
class GuestSession(authToken: GuestAuthToken) :
    Session<GuestAuthToken>(authToken, LOGGED_OUT_USER_ID) {

    class Serializer : SerializationStrategy<GuestSession> {

        private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(GuestAuthToken::class.java, AuthTokenAdapter())
            .create()

        override fun serialize(`object`: GuestSession): String {
            try {
                return gson.toJson(`object`)
            } catch (e: Exception) {
                Twitter.getLogger().d(
                    TwitterCore.TAG,
                    "Failed to serialize session " + e.message
                )
            }
            return ""
        }

        override fun deserialize(serializedObject: String?): GuestSession? {
            if (!serializedObject.isNullOrEmpty()) {
                try {
                    return gson.fromJson(serializedObject, GuestSession::class.java)
                } catch (e: Exception) {
                    Twitter.getLogger().d(
                        TwitterCore.TAG,
                        "Failed to deserialize session " + e.message
                    )
                }
            }
            return null
        }
    }

    companion object {
        const val LOGGED_OUT_USER_ID = 0L
    }
}