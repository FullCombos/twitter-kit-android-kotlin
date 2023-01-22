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

import com.twitter.sdk.android.core.internal.oauth.GuestAuthToken
import com.twitter.sdk.android.core.internal.oauth.OAuth2Service
import java.util.concurrent.CountDownLatch

class GuestSessionProvider(
    private val oAuth2Service: OAuth2Service,
    private val sessionManager: SessionManager<GuestSession>
) {

    @Synchronized
    fun getCurrentSession(): GuestSession? {
        val session = sessionManager.getActiveSession()
        if (isSessionValid(session)) {
            return session
        }
        refreshToken()
        return sessionManager.getActiveSession()
    }

    @Synchronized
    fun refreshCurrentSession(expiredSession: GuestSession?): GuestSession? {
        val session = sessionManager.getActiveSession()
        if (expiredSession != null && expiredSession == session) {
            refreshToken()
        }
        return sessionManager.getActiveSession()
    }

    private fun refreshToken() {
        Twitter.getLogger().d("GuestSessionProvider", "Refreshing expired guest session.")
        val latch = CountDownLatch(1)
        oAuth2Service.requestGuestAuthToken(object : Callback<GuestAuthToken>() {

            override fun success(result: Result<GuestAuthToken>) {
                sessionManager.setActiveSession(GuestSession(result.data))
                latch.countDown()
            }

            override fun failure(exception: TwitterException) {
                sessionManager.clearSession(GuestSession.LOGGED_OUT_USER_ID)
                latch.countDown()
            }
        })

        try {
            latch.await()
        } catch (e: InterruptedException) {
            sessionManager.clearSession(GuestSession.LOGGED_OUT_USER_ID)
        }
    }

    private fun isSessionValid(session: GuestSession?): Boolean {
        return session != null && !session.authToken.isExpired
    }
}