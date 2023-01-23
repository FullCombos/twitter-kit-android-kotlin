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
package com.example.app

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterCore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "Setting up StrictMode policy checking.")
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        Twitter.initialize(this)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        val customClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor).build()
        val activeSession = TwitterCore.getInstance().getSessionManager().getActiveSession()

        val customApiClient: TwitterApiClient
        if (activeSession != null) {
            customApiClient = TwitterApiClient(activeSession, customClient)
            TwitterCore.getInstance().addApiClient(activeSession, customApiClient)
        } else {
            customApiClient = TwitterApiClient(customClient)
            TwitterCore.getInstance().addGuestApiClient(customApiClient)
        }
    }

    companion object {
        private val TAG = SampleApplication::class.java.simpleName
    }
}