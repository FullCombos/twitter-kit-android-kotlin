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

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import com.twitter.sdk.android.core.models.ApiError
import com.twitter.sdk.android.core.models.ApiErrors
import com.twitter.sdk.android.core.models.SafeListAdapter
import com.twitter.sdk.android.core.models.SafeMapAdapter
import retrofit2.Response

/**
 * Represents a Twitter API error.
 */
class TwitterApiException private constructor(
    val response: Response<*>,
    private val apiError: ApiError?,
    val twitterRateLimit: TwitterRateLimit,
    val statusCode: Int
) : TwitterException(createExceptionMessage(statusCode)) {

    companion object {

        private const val DEFAULT_ERROR_CODE = 0

        private fun readApiRateLimit(response: Response<*>): TwitterRateLimit {
            return TwitterRateLimit(response.headers())
        }

        private fun readApiError(response: Response<*>): ApiError? {
            try {
                // The response buffer can only be read once, so we clone the underlying buffer so the
                // response can be consumed down stream if necessary.
                val body = response.errorBody()?.source()?.buffer?.clone()?.readUtf8()
                if (!body.isNullOrEmpty()) {
                    return parseApiError(body)
                }
            } catch (e: Exception) {
                Twitter.getLogger().e(TwitterCore.TAG, "Unexpected response", e)
            }
            return null
        }

        private fun parseApiError(body: String): ApiError? {
            val gson = GsonBuilder()
                .registerTypeAdapterFactory(SafeListAdapter())
                .registerTypeAdapterFactory(SafeMapAdapter())
                .create()
            try {
                val apiErrors = gson.fromJson(body, ApiErrors::class.java)
                if (apiErrors.errors.isNotEmpty()) {
                    return apiErrors.errors[0]
                }
            } catch (e: JsonSyntaxException) {
                Twitter.getLogger().e(TwitterCore.TAG, "Invalid json: $body", e)
            }
            return null
        }

        private fun createExceptionMessage(code: Int): String {
            return "HTTP request failed, Status: $code"
        }
    }

    constructor(response: Response<*>) : this(
        response,
        readApiError(response),
        readApiRateLimit(response),
        response.code()
    )

    /**
     * Error code returned by API request.
     *
     * @return API error code
     */
    fun getErrorCode(): Int {
        return apiError?.code ?: DEFAULT_ERROR_CODE
    }

    /**
     * Error message returned by API request. Error message may change, the codes will stay the same.
     *
     * @return API error message
     */
    fun getErrorMessage(): String {
        return apiError?.message.orEmpty()
    }
}