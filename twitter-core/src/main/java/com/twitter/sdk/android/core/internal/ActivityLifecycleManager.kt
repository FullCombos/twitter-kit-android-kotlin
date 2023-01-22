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
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.content.Context
import android.os.Bundle

/**
 * This is a convenience class that wraps the ActivityLifecycleCallbacks registration. It provides
 * an abstract Callbacks class that reduces required boilerplate code in your callbacks as well as
 * OS Version checks that make it compatible with Android versions less than Ice Cream Sandwich.
 */
class ActivityLifecycleManager(context: Context) {

    private val callbacksWrapper: ActivityLifecycleCallbacksWrapper

    /**
     * Override the methods corresponding to the activity.
     */
    interface Callbacks {
        fun onActivityCreated(activity: Activity, bundle: Bundle?) = Unit
        fun onActivityStarted(activity: Activity) = Unit
        fun onActivityResumed(activity: Activity) = Unit
        fun onActivityPaused(activity: Activity) = Unit
        fun onActivityStopped(activity: Activity) = Unit
        fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) = Unit
        fun onActivityDestroyed(activity: Activity) = Unit
    }

    init {
        val application = context.applicationContext as Application
        callbacksWrapper = ActivityLifecycleCallbacksWrapper(application)
    }

    /**
     * @param callbacks The callbacks
     * @return true if the version of the application context supports registering lifecycle
     * callbacks
     */
    fun registerCallbacks(callbacks: Callbacks): Boolean {
        return callbacksWrapper.registerLifecycleCallbacks(callbacks)
    }

    /**
     * Unregisters all previously registered callbacks on the application context.
     */
    fun resetCallbacks() {
        callbacksWrapper.clearCallbacks()
    }

    private class ActivityLifecycleCallbacksWrapper constructor(private val application: Application) {

        private val registeredCallbacks = mutableSetOf<ActivityLifecycleCallbacks>()

        fun clearCallbacks() {
            for (callback in registeredCallbacks) {
                application.unregisterActivityLifecycleCallbacks(callback)
            }
        }

        fun registerLifecycleCallbacks(callbacks: Callbacks): Boolean {
            val callbackWrapper = object : ActivityLifecycleCallbacks {

                override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
                    callbacks.onActivityCreated(activity, bundle)
                }

                override fun onActivityStarted(activity: Activity) {
                    callbacks.onActivityStarted(activity)
                }

                override fun onActivityResumed(activity: Activity) {
                    callbacks.onActivityResumed(activity)
                }

                override fun onActivityPaused(activity: Activity) {
                    callbacks.onActivityPaused(activity)
                }

                override fun onActivityStopped(activity: Activity) {
                    callbacks.onActivityStopped(activity)
                }

                override fun onActivitySaveInstanceState(
                    activity: Activity,
                    bundle: Bundle
                ) {
                    callbacks.onActivitySaveInstanceState(activity, bundle)
                }

                override fun onActivityDestroyed(activity: Activity) {
                    callbacks.onActivityDestroyed(activity)
                }
            }
            application.registerActivityLifecycleCallbacks(callbackWrapper)
            registeredCallbacks.add(callbackWrapper)
            return true
        }
    }
}