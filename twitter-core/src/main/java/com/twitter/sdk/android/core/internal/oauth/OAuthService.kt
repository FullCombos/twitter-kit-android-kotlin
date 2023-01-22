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

import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.network.OkHttpClientHelper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Base class for OAuth service.
 */
abstract class OAuthService(
    protected val twitterCore: TwitterCore,
    protected val api: TwitterApi
) {
    protected val retrofit: Retrofit

    init {
        val userAgent = TwitterApi.buildUserAgent(CLIENT_NAME, twitterCore.getVersion())
        val client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .build()
                chain.proceed(request)
            })
            .certificatePinner(OkHttpClientHelper.certificatePinner)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(api.baseHostUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    companion object {
        private const val CLIENT_NAME = "TwitterAndroidSDK"
    }
}