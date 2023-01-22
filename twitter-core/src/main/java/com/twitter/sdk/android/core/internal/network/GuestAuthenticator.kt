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

import com.twitter.sdk.android.core.GuestSession
import com.twitter.sdk.android.core.GuestSessionProvider
import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Refreshes guest auth session when server indicates session is expired.
 */
internal class GuestAuthenticator(private val guestSessionProvider: GuestSessionProvider) :
    Authenticator {

    companion object {
        private const val MAX_RETRIES = 2
    }

    @Throws(IOException::class)
    override fun authenticate(route: Route?, response: Response): Request? {
        return reauth(response)
    }

    private fun reauth(response: Response): Request? {
        if (canRetry(response)) {
            val session = guestSessionProvider
                .refreshCurrentSession(getExpiredSession(response))
            val token = session?.authToken
            if (token != null) {
                return resign(response.request, token)
            }
        }
        return null
    }

    private fun getExpiredSession(response: Response): GuestSession? {
        val headers = response.request.headers
        val auth = headers[OAuthConstants.HEADER_AUTHORIZATION]
        val guest = headers[OAuthConstants.HEADER_GUEST_TOKEN]
        if (auth != null && guest != null) {
            val token = GuestAuthToken("bearer", auth.replace("bearer ", ""), guest)
            return GuestSession(token)
        }
        return null
    }

    private fun resign(request: Request, token: GuestAuthToken): Request {
        val builder = request.newBuilder()
        GuestAuthInterceptor.addAuthHeaders(builder, token)
        return builder.build()
    }

    private fun canRetry(response: Response): Boolean {
        var resp = response
        var responseCount = 1
        while (resp.priorResponse?.also { resp = it } != null) {
            responseCount++
        }
        return responseCount < MAX_RETRIES
    }
}