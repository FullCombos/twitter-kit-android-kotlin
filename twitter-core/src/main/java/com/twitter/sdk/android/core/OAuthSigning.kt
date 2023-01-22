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

import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders

/**
 * Provides convenience methods for generating OAuth headers for Twitter
 */
internal class OAuthSigning private constructor(
    private val authConfig: TwitterAuthConfig,
    private val authToken: TwitterAuthToken,
    private val oAuth1aHeaders: OAuth1aHeaders
) {
    companion object {
        private const val VERIFY_CREDENTIALS_URL: String = TwitterApi.BASE_HOST_URL +
                "/1.1/account/verify_credentials.json"
    }

    /**
     * Constructs OAuthSigning with TwitterAuthConfig and TwitterAuthToken
     *
     * @param authConfig The auth config.
     * @param authToken  The auth token to use to sign the request.
     */
    constructor(authConfig: TwitterAuthConfig, authToken: TwitterAuthToken) : this(
        authConfig,
        authToken,
        OAuth1aHeaders()
    )

    /**
     * Gets authorization header for inclusion in HTTP request headers.
     *
     * @param method The HTTP method.
     * @param url The url.
     * @param postParams The post parameters.
     */
    fun getAuthorizationHeader(
        method: String,
        url: String,
        postParams: Map<String, String>?
    ): String {
        return oAuth1aHeaders.getAuthorizationHeader(
            authConfig, authToken, null, method, url, postParams
        )
    }

    /**
     * Returns OAuth Echo header using given parameters.
     *
     * OAuth Echo allows you to securely delegate an API request to a third party. For example,
     * you may wish to verify a users credentials from your backend (i.e. the third party). This
     * method provides the OAuth parameters required to make an authenticated request from your
     * backend.
     *
     * @param method     The HTTP method (GET, POST, PUT, DELETE, etc).
     * @param url        The url delegation should be sent to (e.g. https://api.twitter.com/1.1/account/verify_credentials.json).
     * @param postParams The post parameters.
     * @return A map of OAuth Echo headers
     * @see [OAuth Echo](https://dev.twitter.com/oauth/echo)
     */
    fun getOAuthEchoHeaders(
        method: String,
        url: String,
        postParams: Map<String, String>?
    ): Map<String, String> {
        return oAuth1aHeaders.getOAuthEchoHeaders(
            authConfig, authToken, null, method, url, postParams
        )
    }

    /**
     * Returns OAuth Echo header for [verify_credentials](https://dev.twitter.com/rest/reference/get/account/verify_credentials) endpoint.
     *
     * @return A map of OAuth Echo headers
     * @see .getOAuthEchoHeaders
     */
    fun getOAuthEchoHeadersForVerifyCredentials(): Map<String, String> {
        return oAuth1aHeaders.getOAuthEchoHeaders(
            authConfig, authToken, null, "GET", VERIFY_CREDENTIALS_URL, null
        )
    }
}