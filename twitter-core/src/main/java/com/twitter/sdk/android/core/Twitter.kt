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
import com.twitter.sdk.android.core.internal.ActivityLifecycleManager
import com.twitter.sdk.android.core.internal.CommonUtils
import java.io.File

/**
 * The [Twitter] class stores common configuration and state for TwitterKit SDK.
 */
class Twitter private constructor(config: TwitterConfig) {

    private val context = config.context
    private val twitterAuthConfig: TwitterAuthConfig
    private val lifecycleManager = ActivityLifecycleManager(context)
    private val logger = config.logger ?: DEFAULT_LOGGER
    private val debug = config.debug
    private val imageLoader = config.imageLoader

    init {
        twitterAuthConfig = if (config.twitterAuthConfig == null) {
            val key = CommonUtils.getStringResourceValue(context, CONSUMER_KEY, "")
            val secret =
                CommonUtils.getStringResourceValue(context, CONSUMER_SECRET, "")
            TwitterAuthConfig(key, secret)
        } else {
            config.twitterAuthConfig
        }
    }

    /**
     * @param component the component name
     * @return A [TwitterContext] for specified component.
     */
    fun getContext(component: String): Context {
        return TwitterContext(context, component, ".TwitterKit" + File.separator + component)
    }

    /**
     * @return the global [TwitterAuthConfig].
     */
    fun getTwitterAuthConfig(): TwitterAuthConfig {
        return twitterAuthConfig
    }

    /**
     * @return the global [ActivityLifecycleManager].
     */
    fun getActivityLifecycleManager(): ActivityLifecycleManager {
        return lifecycleManager
    }

    fun getImageLoader(): TwitterImageLoader? {
        return imageLoader
    }

    companion object {

        internal const val TAG = "Twitter"

        private const val CONSUMER_KEY = "com.twitter.sdk.android.CONSUMER_KEY"
        private const val CONSUMER_SECRET = "com.twitter.sdk.android.CONSUMER_SECRET"
        private const val NOT_INITIALIZED_MESSAGE =
            "Must initialize Twitter before using getInstance()"
        private val DEFAULT_LOGGER = DefaultLogger()

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: Twitter? = null

        /**
         * Entry point to initialize the TwitterKit SDK.
         *
         *
         * Only the Application context is retained.
         * See http://developer.android.com/resources/articles/avoiding-memory-leaks.html
         *
         *
         * Should be called from `OnCreate()` method of custom `Application` class.
         * <pre>
         * public class SampleApplication extends Application {
         * &#64;Override
         * public void onCreate() {
         * Twitter.initialize(this);
         * }
         * }
        </pre> *
         *
         * @param context Android context used for initialization
         */
        fun initialize(context: Context) {
            val config = TwitterConfig.Builder(context)
                .build()
            initialize(config)
        }

        /**
         * Entry point to initialize the TwitterKit SDK.
         *
         *
         * Only the Application context is retained.
         * See http://developer.android.com/resources/articles/avoiding-memory-leaks.html
         *
         *
         * Should be called from `OnCreate()` method of custom `Application` class.
         * <pre>
         * public class SampleApplication extends Application {
         * &#64;Override
         * public void onCreate() {
         * final TwitterConfig config = new TwitterConfig.Builder(this).build();
         * Twitter.initialize(config);
         * }
         * }
        </pre> *
         *
         * @param config [TwitterConfig] user for initialization
         */
        fun initialize(config: TwitterConfig) {
            createTwitter(config)
        }

        @Synchronized
        fun createTwitter(config: TwitterConfig): Twitter {
            if (instance == null) {
                instance = Twitter(config)
            }
            return instance!!
        }

        private fun checkInitialized() {
            checkNotNull(instance) { NOT_INITIALIZED_MESSAGE }
        }

        /**
         * @return Single instance of the [Twitter].
         */
        @JvmStatic
        fun getInstance(): Twitter {
            checkInitialized()
            return instance!!
        }

        /**
         * @return the global value for debug mode.
         */
        fun isDebug(): Boolean {
            return getInstance().debug
        }

        /**
         * @return the global [Logger].
         */
        @JvmStatic
        fun getLogger(): Logger {
            return getInstance().logger
        }
    }
}