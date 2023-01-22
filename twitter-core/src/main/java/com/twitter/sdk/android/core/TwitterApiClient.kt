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
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.network.OkHttpClientHelper
import com.twitter.sdk.android.core.models.BindingValues
import com.twitter.sdk.android.core.models.BindingValuesAdapter
import com.twitter.sdk.android.core.models.SafeListAdapter
import com.twitter.sdk.android.core.models.SafeMapAdapter
import com.twitter.sdk.android.core.services.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.ConcurrentHashMap

/**
 * A class to allow authenticated access to Twitter API endpoints.
 * Can be extended to provided additional endpoints by extending and providing Retrofit API
 * interfaces to [com.twitter.sdk.android.core.TwitterApiClient.getService]
 */
class TwitterApiClient internal constructor(client: OkHttpClient, twitterApi: TwitterApi) {

    private val services: ConcurrentHashMap<Class<*>, Any>
    private val retrofit: Retrofit

    /**
     * Constructs Guest Session based TwitterApiClient.
     */
    constructor() : this(
        OkHttpClientHelper.getOkHttpClient(TwitterCore.getInstance().getGuestSessionProvider()),
        TwitterApi()
    )

    /**
     * Constructs Guest Session based TwitterApiClient, with custom http client.
     *
     * The custom http client can be constructed with [okhttp3.Interceptor], and other
     * optional params provided in [okhttp3.OkHttpClient].
     */
    constructor(client: OkHttpClient) : this(
        OkHttpClientHelper.getCustomOkHttpClient(
            client,
            TwitterCore.getInstance().getGuestSessionProvider()
        ),
        TwitterApi()
    )

    /**
     * Constructs User Session based TwitterApiClient.
     */
    constructor(session: TwitterSession) : this(
        OkHttpClientHelper.getOkHttpClient(
            session,
            TwitterCore.getInstance().authConfig
        ),
        TwitterApi()
    )

    /**
     * Constructs User Session based TwitterApiClient, with custom http client.
     *
     * The custom http client can be constructed with [okhttp3.Interceptor], and other
     * optional params provided in [okhttp3.OkHttpClient].
     */
    constructor(session: TwitterSession, client: OkHttpClient) : this(
        OkHttpClientHelper.getCustomOkHttpClient(
            client,
            session,
            TwitterCore.getInstance().authConfig
        ),
        TwitterApi()
    )

    init {
        services = buildConcurrentMap()
        retrofit = buildRetrofit(client, twitterApi)
    }

    private fun buildRetrofit(httpClient: OkHttpClient, twitterApi: TwitterApi): Retrofit {
        return Retrofit.Builder()
            .client(httpClient)
            .baseUrl(twitterApi.baseHostUrl)
            .addConverterFactory(GsonConverterFactory.create(buildGson()))
            .build()
    }

    private fun buildGson(): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(SafeListAdapter())
            .registerTypeAdapterFactory(SafeMapAdapter())
            .registerTypeAdapter(BindingValues::class.java, BindingValuesAdapter())
            .create()
    }

    private fun buildConcurrentMap(): ConcurrentHashMap<Class<*>, Any> {
        return ConcurrentHashMap<Class<*>, Any>()
    }

    /**
     * @return [com.twitter.sdk.android.core.services.AccountService] to access TwitterApi
     */
    fun getAccountService(): AccountService {
        return getService(AccountService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.FavoriteService] to access TwitterApi
     */
    fun getFavoriteService(): FavoriteService {
        return getService(FavoriteService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.StatusesService] to access TwitterApi
     */
    fun getStatusesService(): StatusesService {
        return getService(StatusesService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.SearchService] to access TwitterApi
     */
    fun getSearchService(): SearchService {
        return getService(SearchService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.ListService] to access TwitterApi
     */
    fun getListService(): ListService {
        return getService(ListService::class.java)
    }

    /**
     * Use CollectionTimeline directly, CollectionService is expected to change.
     * @return [CollectionService] to access TwitterApi
     */
    fun getCollectionService(): CollectionService {
        return getService(CollectionService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.ConfigurationService] to access TwitterApi
     */
    fun getConfigurationService(): ConfigurationService {
        return getService(ConfigurationService::class.java)
    }

    /**
     * @return [com.twitter.sdk.android.core.services.MediaService] to access Twitter API
     * upload endpoints.
     */
    fun getMediaService(): MediaService {
        return getService(MediaService::class.java)
    }

    /**
     * Converts Retrofit style interface into instance for API access
     *
     * @param cls Retrofit style interface
     * @return instance of cls
     */
    protected fun <T : Any> getService(cls: Class<T>): T {
        if (!services.contains(cls)) {
            services.putIfAbsent(cls, retrofit.create(cls))
        }
        return services[cls] as T
    }
}