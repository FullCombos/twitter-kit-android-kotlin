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
package com.twitter.sdk.android.core.internal.network

import com.twitter.sdk.android.core.GuestSessionProvider
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants
import okhttp3.*
import java.io.IOException

/**
 * Signs requests with OAuth2 signature.
 */
internal class GuestAuthInterceptor(private val guestSessionProvider: GuestSessionProvider) :
    Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val session = guestSessionProvider.getCurrentSession()
        val token = session?.authToken
        if (token != null) {
            val builder = request.newBuilder()
            addAuthHeaders(builder, token)
            return chain.proceed(builder.build())
        }
        return chain.proceed(request)
    }

    companion object {

        fun addAuthHeaders(builder: Request.Builder, token: GuestAuthToken) {
            val authHeader = token.tokenType + " " + token.accessToken
            builder.header(OAuthConstants.HEADER_AUTHORIZATION, authHeader)
            builder.header(OAuthConstants.HEADER_GUEST_TOKEN, token.guestToken)
        }
    }
}