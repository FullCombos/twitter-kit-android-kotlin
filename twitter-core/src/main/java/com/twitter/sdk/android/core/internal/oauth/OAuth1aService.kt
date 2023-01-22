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

import android.net.Uri
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.network.UrlUtils
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/**
 * OAuth1.0a service. Provides methods for requesting request tokens, access tokens, and signing
 * requests.
 */
internal class OAuth1aService(twitterCore: TwitterCore, api: TwitterApi) :
    OAuthService(twitterCore, api) {

    interface OAuthApi {
        @POST("/oauth/request_token")
        fun getTempToken(@Header(OAuthConstants.HEADER_AUTHORIZATION) auth: String): Call<ResponseBody>

        @POST("/oauth/access_token")
        fun getAccessToken(
            @Header(OAuthConstants.HEADER_AUTHORIZATION) auth: String,
            @Query(OAuthConstants.PARAM_VERIFIER) verifier: String
        ): Call<ResponseBody>
    }

    companion object {
        private const val RESOURCE_OAUTH = "oauth"
        private const val CALLBACK_URL = "twittersdk://callback"
        private const val PARAM_SCREEN_NAME = "screen_name"
        private const val PARAM_USER_ID = "user_id"

        /**
         * @return  [OAuthResponse] parsed from the
         * response, may be `null` if the response does not contain an auth token and secret.
         */
        fun parseAuthResponse(response: String): OAuthResponse? {
            val params = UrlUtils.getQueryParams(response, false)
            val token = params[OAuthConstants.PARAM_TOKEN]
            val secret = params[OAuthConstants.PARAM_TOKEN_SECRET]
            val userName = params[PARAM_SCREEN_NAME]
            val userId = if (params.containsKey(PARAM_USER_ID)) {
                params[PARAM_USER_ID]!!.toLong()
            } else {
                0L
            }
            return if (token == null || secret == null) {
                null
            } else {
                OAuthResponse(TwitterAuthToken(token, secret), userName.orEmpty(), userId)
            }
        }
    }

    private var oauthApi: OAuthApi = retrofit.create(OAuthApi::class.java)

    /**
     * Requests a temp token to start the Twitter sign-in flow.
     *
     * @param callback The callback interface to invoke when the request completes.
     */
    fun requestTempToken(callback: Callback<OAuthResponse>) {
        val config = twitterCore.authConfig
        val url = tempTokenUrl
        oauthApi.getTempToken(
            OAuth1aHeaders().getAuthorizationHeader(
                config, null, buildCallbackUrl(config), "POST", url, null
            )
        ).enqueue(getCallbackWrapper(callback))
    }

    private val tempTokenUrl: String = api.baseHostUrl + "/oauth/request_token"

    /**
     * Builds a callback url that is used to receive a request containing the oauth_token and
     * oauth_verifier parameters.
     *
     * @param authConfig The auth config
     * @return the callback url
     */
    fun buildCallbackUrl(authConfig: TwitterAuthConfig?): String {
        return Uri.parse(CALLBACK_URL).buildUpon()
            .appendQueryParameter("version", twitterCore.getVersion())
            .appendQueryParameter("app", authConfig?.consumerKey)
            .build()
            .toString()
    }

    /**
     * Requests a Twitter access token to act on behalf of a user account.
     *
     * @param callback The callback interface to invoke when when the request completes.
     */
    fun requestAccessToken(
        callback: Callback<OAuthResponse>,
        requestToken: TwitterAuthToken?,
        verifier: String
    ) {
        val url = accessTokenUrl
        val authHeader = OAuth1aHeaders().getAuthorizationHeader(
            twitterCore.authConfig, requestToken, null, "POST", url, null
        )
        oauthApi.getAccessToken(authHeader, verifier).enqueue(getCallbackWrapper(callback))
    }

    private val accessTokenUrl: String = api.baseHostUrl + "/oauth/access_token"

    /**
     * @param requestToken The request token.
     * @return authorization url that can be used to get a verifier code to get access token.
     */
    fun getAuthorizeUrl(requestToken: TwitterAuthToken?): String {
        // https://api.twitter.com/oauth/authorize?oauth_token=%s
        return api.buildUponBaseHostUrl(RESOURCE_OAUTH, "authorize")
            .appendQueryParameter(OAuthConstants.PARAM_TOKEN, requestToken?.token)
            .build()
            .toString()
    }

    private fun getCallbackWrapper(callback: Callback<OAuthResponse>): Callback<ResponseBody> {
        return object : Callback<ResponseBody>() {

            override fun success(result: Result<ResponseBody>) {
                //Try to get response body
                try {
                    val sb = StringBuilder()
                    BufferedReader(
                        InputStreamReader(result.data.byteStream())
                    ).use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            sb.append(line)
                        }
                    }

                    val responseAsStr = sb.toString()
                    val authResponse = parseAuthResponse(responseAsStr)
                    if (authResponse == null) {
                        callback.failure(
                            TwitterAuthException("Failed to parse auth response: $responseAsStr")
                        )
                    } else {
                        callback.success(Result(authResponse, null))
                    }
                } catch (e: IOException) {
                    callback.failure(TwitterAuthException(e.message.orEmpty(), e))
                }
            }

            override fun failure(exception: TwitterException) {
                callback.failure(exception)
            }
        }
    }
}