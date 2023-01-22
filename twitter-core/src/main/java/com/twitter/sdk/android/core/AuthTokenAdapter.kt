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

import com.google.gson.*
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken
import com.twitter.sdk.android.core.internal.oauth.OAuth2Token
import java.lang.reflect.Type

/**
 * Provides custom serialization and deserialization for classes that hold any type of
 * [com.twitter.sdk.android.core.AuthToken].
 */
class AuthTokenAdapter : JsonSerializer<AuthToken>, JsonDeserializer<AuthToken> {

    companion object {

        private const val AUTH_TYPE = "auth_type"
        private const val AUTH_TOKEN = "auth_token"

        private val authTypeRegistry = mutableMapOf<String, Class<out AuthToken>>(
            "oauth1a" to TwitterAuthToken::class.java,
            "oauth2" to OAuth2Token::class.java,
            "guest" to GuestAuthToken::class.java
        )

        fun getAuthTypeString(authTokenClass: Class<AuthToken>): String {
            for ((key, value) in authTypeRegistry) {
                if (value == authTokenClass) {
                    return key
                }
            }
            return ""
        }
    }

    private val gson: Gson = Gson()

    override fun serialize(
        src: AuthToken,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty(AUTH_TYPE, getAuthTypeString(src.javaClass))
        jsonObject.add(AUTH_TOKEN, gson.toJsonTree(src))
        return jsonObject
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): AuthToken {
        val jsonObject = json.asJsonObject
        val jsonAuthType = jsonObject.getAsJsonPrimitive(AUTH_TYPE)
        val authType = jsonAuthType.asString
        val jsonAuthToken = jsonObject[AUTH_TOKEN]
        return gson.fromJson(jsonAuthToken, authTypeRegistry[authType])
    }
}