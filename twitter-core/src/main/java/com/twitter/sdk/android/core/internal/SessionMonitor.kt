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
package com.twitter.sdk.android.core.internal

import android.app.Activity
import android.text.format.DateUtils
import com.twitter.sdk.android.core.Session
import com.twitter.sdk.android.core.SessionManager
import java.util.*
import java.util.concurrent.ExecutorService

/**
 * A session monitor for validating sessions.
 * @param <T>
</T> */
internal class SessionMonitor<T : Session<*>> private constructor(
    private val sessionManager: SessionManager<T>,
    private val time: SystemCurrentTimeProvider,
    private val executorService: ExecutorService,
    private val monitorState: MonitorState,
    private val sessionVerifier: SessionVerifier<T>
) {
    /**
     * @param sessionManager A user auth based session manager
     * @param executorService used to
     */
    constructor(
        sessionManager: SessionManager<T>,
        executorService: ExecutorService,
        sessionVerifier: SessionVerifier<T>
    ) : this(
        sessionManager,
        SystemCurrentTimeProvider(),
        executorService,
        MonitorState(),
        sessionVerifier
    )

    /**
     * This is how we hook into the activity lifecycle to detect if the user is using the app.
     * @param activityLifecycleManager
     */
    fun monitorActivityLifecycle(activityLifecycleManager: ActivityLifecycleManager) {
        activityLifecycleManager.registerCallbacks(object : ActivityLifecycleManager.Callbacks {

            override fun onActivityStarted(activity: Activity) {
                triggerVerificationIfNecessary()
            }
        })
    }

    /**
     * triggerVerificationIfNecessary checks if there are any sessions to verify and if enough time
     * has passed in order to run another verification. If it determines it can verify, it submits a
     * runnable that does the verification in a background thread.
     */
    fun triggerVerificationIfNecessary() {
        val session = sessionManager.getActiveSession()
        val currentTime = time.getCurrentTimeMillis()
        val startVerification = session != null &&
                monitorState.beginVerification(currentTime)
        if (startVerification) {
            executorService.submit { verifyAll() }
        }
    }

    private fun verifyAll() {
        for (session in sessionManager.getSessionMap().values) {
            sessionVerifier.verifySession(session)
        }
        monitorState.endVerification(time.getCurrentTimeMillis())
    }

    /**
     * Encapsulates time based state that rate limits our calls to the verification api.
     * Ensure we don't end up with racy parallel calls with beginVerification.
     */
    private class MonitorState {

        companion object {
            private const val TIME_THRESHOLD_IN_MILLIS = 6 * DateUtils.HOUR_IN_MILLIS
        }

        private var verifying = false
        private var lastVerification: Long = 0
        private val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        @Synchronized
        fun beginVerification(currentTime: Long): Boolean {
            val isPastThreshold = currentTime - lastVerification > TIME_THRESHOLD_IN_MILLIS
            val dayHasChanged = !isOnSameDate(currentTime, lastVerification)
            return if (!verifying && (isPastThreshold || dayHasChanged)) {
                verifying = true
                true
            } else {
                false
            }
        }

        @Synchronized
        fun endVerification(currentTime: Long) {
            verifying = false
            lastVerification = currentTime
        }

        private fun isOnSameDate(timeA: Long, timeB: Long): Boolean {
            utcCalendar.timeInMillis = timeA
            val dayA = utcCalendar[Calendar.DAY_OF_YEAR]
            val yearA = utcCalendar[Calendar.YEAR]
            utcCalendar.timeInMillis = timeB
            val dayB = utcCalendar[Calendar.DAY_OF_YEAR]
            val yearB = utcCalendar[Calendar.YEAR]
            return dayA == dayB && yearA == yearB
        }
    }
}