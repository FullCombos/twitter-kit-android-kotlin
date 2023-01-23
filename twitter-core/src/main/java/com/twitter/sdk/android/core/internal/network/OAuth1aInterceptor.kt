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

import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants
import okhttp3.*
import java.io.IOException

/**
 * Signs requests with OAuth1a signature
 */
internal class OAuth1aInterceptor(
    private val session: Session<TwitterAuthToken>,
    private val authConfig: TwitterAuthConfig
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val hackRequest = request.newBuilder()
            .url(urlWorkaround(request.url))
            .build()
        val newRequest = hackRequest
            .newBuilder()
            .header(OAuthConstants.HEADER_AUTHORIZATION, getAuthorizationHeader(hackRequest))
            .build()
        return chain.proceed(newRequest)
    }

    private fun urlWorkaround(url: HttpUrl): HttpUrl {
        val builder = url.newBuilder().query(null)
        val size = url.querySize
        for (i in 0 until size) {
            builder.addEncodedQueryParameter(
                UrlUtils.percentEncode(url.queryParameterName(i)),
                UrlUtils.percentEncode(url.queryParameterValue(i))
            )
        }
        return builder.build()
    }

    @Throws(IOException::class)
    fun getAuthorizationHeader(request: Request): String {
        return OAuth1aHeaders().getAuthorizationHeader(
            authConfig,
            session.authToken, null, request.method, request.url.toString(),
            getPostParams(request)
        )
    }

    @Throws(IOException::class)
    fun getPostParams(request: Request): Map<String, String> {
        val params = mutableMapOf<String, String>()
        if ("POST" == request.method.uppercase()) {
            val output = request.body
            if (output is FormBody) {
                for (i in 0 until output.size) {
                    params[output.encodedName(i)] = output.value(i)
                }
            }
        }
        return params
    }
}