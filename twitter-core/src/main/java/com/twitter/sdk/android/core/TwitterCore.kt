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

import android.annotation.SuppressLint
import android.content.*
import com.twitter.sdk.android.core.internal.SessionMonitor
import com.twitter.sdk.android.core.internal.TwitterApi
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service
import com.twitter.sdk.android.core.internal.persistence.PreferenceStoreImpl
import java.util.concurrent.ConcurrentHashMap

/**
 * The TwitterCore Kit provides Login with Twitter and the Twitter API.
 */
class TwitterCore internal constructor(
    val authConfig: TwitterAuthConfig,
    private val apiClients: ConcurrentHashMap<Session<*>, TwitterApiClient> = ConcurrentHashMap(),
    @Volatile private var guestClient: TwitterApiClient? = null
) {

    private var twitterSessionManager: SessionManager<TwitterSession>
    private var guestSessionManager: SessionManager<GuestSession>
    private var sessionMonitor: SessionMonitor<TwitterSession>
    private val context = Twitter.getInstance().getContext(getIdentifier())

    @Volatile
    private var guestSessionProvider: GuestSessionProvider? = null

    // Testing only
    init {
        twitterSessionManager = PersistedSessionManager(
            PreferenceStoreImpl(context, SESSION_PREF_FILE_NAME),
            TwitterSession.Serializer(),
            PREF_KEY_ACTIVE_TWITTER_SESSION,
            PREF_KEY_TWITTER_SESSION
        )
        guestSessionManager = PersistedSessionManager(
            PreferenceStoreImpl(context, SESSION_PREF_FILE_NAME),
            GuestSession.Serializer(),
            PREF_KEY_ACTIVE_GUEST_SESSION,
            PREF_KEY_GUEST_SESSION
        )
        sessionMonitor = SessionMonitor(
            twitterSessionManager,
            Twitter.getInstance().getExecutorService(),
            TwitterSessionVerifier()
        )
    }

    fun getVersion(): String {
        return BuildConfig.VERSION_NAME + "." + BuildConfig.BUILD_NUMBER
    }

    private fun doInBackground() {
        // Trigger restoration of session
        twitterSessionManager.getActiveSession()
        guestSessionManager.getActiveSession()
        getGuestSessionProvider()
        // Monitor activity lifecycle after sessions have been restored. Otherwise we would not
        // have any sessions to monitor anyways.
        sessionMonitor.monitorActivityLifecycle(
            Twitter.getInstance().getActivityLifecycleManager()
        )
    }

    private fun getIdentifier(): String {
        return BuildConfig.GROUP + ":" + BuildConfig.ARTIFACT_ID
    }

    /**********************************************************************************************
     * BEGIN PUBLIC API METHODS
     */
    
    /**
     * @return the [com.twitter.sdk.android.core.SessionManager] for user sessions.
     */
    fun getSessionManager(): SessionManager<TwitterSession> {
        return twitterSessionManager
    }

    fun getGuestSessionProvider(): GuestSessionProvider {
        if (guestSessionProvider == null) {
            createGuestSessionProvider()
        }
        return guestSessionProvider!!
    }

    @Synchronized
    private fun createGuestSessionProvider() {
        if (guestSessionProvider == null) {
            val service = OAuth2Service(this, TwitterApi())
            guestSessionProvider = GuestSessionProvider(service, guestSessionManager)
        }
    }

    /**
     * Creates [com.twitter.sdk.android.core.TwitterApiClient] from default
     * [com.twitter.sdk.android.core.Session].
     *
     * Caches internally for efficient access.
     */
    fun getApiClient(): TwitterApiClient {
        val session = twitterSessionManager.getActiveSession() ?: return getGuestApiClient()
        return getApiClient(session)
    }

    /**
     * Creates [com.twitter.sdk.android.core.TwitterApiClient] from authenticated
     * [com.twitter.sdk.android.core.Session] provided.
     *
     * Caches internally for efficient access.
     * @param session the session
     */
    fun getApiClient(session: TwitterSession): TwitterApiClient {
        if (!apiClients.containsKey(session)) {
            apiClients.putIfAbsent(session, TwitterApiClient(session))
        }
        return apiClients[session]!!
    }

    /**
     * Add custom [com.twitter.sdk.android.core.TwitterApiClient] for guest auth access.
     *
     * Only adds guest auth client if it's not already defined. Caches internally for efficient
     * access and storing it in TwitterCore's singleton.
     *
     * @param customTwitterApiClient the custom twitter api client
     */
    fun addGuestApiClient(customTwitterApiClient: TwitterApiClient) {
        if (guestClient == null) {
            createGuestClient(customTwitterApiClient)
        }
    }

    /**
     * Add custom [com.twitter.sdk.android.core.TwitterApiClient] for authenticated
     * [com.twitter.sdk.android.core.Session] access.
     *
     * Only adds session auth client if it's not already defined. Caches internally for efficient
     * access and storing it in TwitterCore's singleton.
     *
     * @param session the session
     * @param customTwitterApiClient the custom twitter api client
     */
    fun addApiClient(session: TwitterSession, customTwitterApiClient: TwitterApiClient) {
        if (!apiClients.containsKey(session)) {
            apiClients.putIfAbsent(session, customTwitterApiClient)
        }
    }

    /**
     * Creates [com.twitter.sdk.android.core.TwitterApiClient] using guest authentication.
     *
     * Caches internally for efficient access.
     */
    fun getGuestApiClient(): TwitterApiClient {
        if (guestClient == null) {
            createGuestClient()
        }
        return guestClient!!
    }

    @Synchronized
    private fun createGuestClient() {
        if (guestClient == null) {
            guestClient = TwitterApiClient()
        }
    }

    @Synchronized
    private fun createGuestClient(twitterApiClient: TwitterApiClient) {
        if (guestClient == null) {
            guestClient = twitterApiClient
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: TwitterCore? = null

        internal const val TAG = "Twitter"
        private const val PREF_KEY_ACTIVE_TWITTER_SESSION = "active_twittersession"
        private const val PREF_KEY_TWITTER_SESSION = "twittersession"
        private const val PREF_KEY_ACTIVE_GUEST_SESSION = "active_guestsession"
        private const val PREF_KEY_GUEST_SESSION = "guestsession"
        private const val SESSION_PREF_FILE_NAME = "session_store"

        @JvmStatic
        fun getInstance(): TwitterCore {
            if (instance == null) {
                synchronized(TwitterCore::class.java) {
                    if (instance == null) {
                        instance = TwitterCore(Twitter.getInstance().getTwitterAuthConfig())
                        Twitter.getInstance().getExecutorService()
                            .execute { instance!!.doInBackground() }
                    }
                }
            }
            return instance!!
        }
    }
}