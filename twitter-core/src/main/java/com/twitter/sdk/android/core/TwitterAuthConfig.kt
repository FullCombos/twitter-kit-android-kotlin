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

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Authorization configuration details.
 */
@Parcelize
class TwitterAuthConfig(

    /**
     * @return the consumer key
     */
    val consumerKey: String,
    /**
     * @return the consumer secret
     */
    val consumerSecret: String,
    /**
     * @return The request code to use for Single Sign On. This code will
     * be returned in [android.app.Activity.onActivityResult]
     * when the activity exits.
     */
    val requestCode: Int = DEFAULT_AUTH_REQUEST_CODE,
    /**
     * @return the callback url
     */
    val callbackUrl: String

) : Parcelable {

    companion object {
        /**
         * The default request code to use for Single Sign On. This code will
         * be returned in [android.app.Activity.onActivityResult]
         */
        const val DEFAULT_AUTH_REQUEST_CODE = 140
    }
}