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

import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.network.UrlUtils
import okio.ByteString.Companion.encodeUtf8
import retrofit2.Call
import retrofit2.http.*

/**
 * OAuth2.0 service. Provides methods for requesting guest auth tokens.
 */
class OAuth2Service(twitterCore: TwitterCore, api: TwitterApi) : OAuthService(twitterCore, api) {

    interface OAuth2Api {

        @POST("/1.1/guest/activate.json")
        fun getGuestToken(
            @Header(OAuthConstants.HEADER_AUTHORIZATION) auth: String
        ): Call<GuestTokenResponse>

        @Headers("Content-Type: application/x-www-form-urlencoded;charset=UTF-8")
        @FormUrlEncoded
        @POST("/oauth2/token")
        fun getAppAuthToken(
            @Header(OAuthConstants.HEADER_AUTHORIZATION) auth: String,
            @Field(OAuthConstants.PARAM_GRANT_TYPE) grantType: String
        ): Call<OAuth2Token>
    }

    private var oauth2Api: OAuth2Api = retrofit.create(OAuth2Api::class.java)

    /**
     * Requests a guest auth token.
     * @param callback The callback interface to invoke when when the request completes.
     */
    fun requestGuestAuthToken(callback: Callback<GuestAuthToken>) {
        val appAuthCallback = object : Callback<OAuth2Token>() {

            override fun success(result: Result<OAuth2Token>) {
                val appAuthToken = result.data
                // Got back an app auth token, now request a guest auth token.
                val guestTokenCallback = object : Callback<GuestTokenResponse>() {

                    override fun success(result: Result<GuestTokenResponse>) {
                        // Return a GuestAuthToken that includes the guestToken.
                        val guestAuthToken = GuestAuthToken(
                            appAuthToken.tokenType,
                            appAuthToken.accessToken,
                            result.data.guestToken
                        )
                        callback.success(Result(guestAuthToken, null))
                    }

                    override fun failure(exception: TwitterException) {
                        Twitter.getLogger().e(
                            TwitterCore.TAG,
                            "Your app may not allow guest auth. Please talk to us "
                                    + "regarding upgrading your consumer key.",
                            exception
                        )
                        callback.failure(exception)
                    }
                }
                requestGuestToken(guestTokenCallback, appAuthToken)
            }

            override fun failure(exception: TwitterException) {
                Twitter.getLogger().e(TwitterCore.TAG, "Failed to get app auth token", exception)
                callback.failure(exception)
            }
        }

        requestAppAuthToken(appAuthCallback)
    }

    /**
     * Requests an application-only auth token.
     *
     * @param callback The callback interface to invoke when when the request completes.
     */
    private fun requestAppAuthToken(callback: Callback<OAuth2Token>) {
        oauth2Api.getAppAuthToken(authHeader, OAuthConstants.GRANT_TYPE_CLIENT_CREDENTIALS)
            .enqueue(callback)
    }

    /**
     * Requests a guest token.
     *
     * @param callback The callback interface to invoke when when the request completes.
     * @param appAuthToken The application-only auth token.
     */
    fun requestGuestToken(
        callback: Callback<GuestTokenResponse>,
        appAuthToken: OAuth2Token
    ) {
        oauth2Api.getGuestToken(getAuthorizationHeader(appAuthToken)).enqueue(callback)
    }

    /**
     * Gets authorization header for inclusion in HTTP request headers.
     */
    private fun getAuthorizationHeader(token: OAuth2Token): String {
        return OAuthConstants.AUTHORIZATION_BEARER + " " + token.accessToken
    }

    private val authHeader: String
        get() {
            val authConfig = twitterCore.authConfig
            val string = (UrlUtils.percentEncode(authConfig.consumerKey)
                    + ":"
                    + UrlUtils.percentEncode(authConfig.consumerSecret)
                    ).encodeUtf8()
            return OAuthConstants.AUTHORIZATION_BASIC + " " + string.base64()
        }
}