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

import okhttp3.Headers

/**
 * Represents the rate limit data returned on the headers of a request
 *
 * @see [Rate Limiting](https://dev.twitter.com/rest/public/rate-limiting)
 */
class TwitterRateLimit(headers: Headers) {

    /**
     * Returns the rate limit ceiling for that given request
     */
    var requestLimit = 0
        private set

    /**
     * Returns the number of requests left for the 15 minute window
     */
    var remainingRequest = 0
        private set

    /**
     * Returns epoch time that rate limit reset will happen.
     */
    var resetSeconds: Long = 0
        private set

    init {
        for (i in 0 until headers.size) {
            if (LIMIT_KEY == headers.name(i)) {
                requestLimit = Integer.valueOf(headers.value(i))
            } else if (REMAINING_KEY == headers.name(i)) {
                remainingRequest = Integer.valueOf(headers.value(i))
            } else if (RESET_KEY == headers.name(i)) {
                resetSeconds = java.lang.Long.valueOf(headers.value(i))
            }
        }
    }

    companion object {
        private const val LIMIT_KEY = "x-rate-limit-limit"
        private const val REMAINING_KEY = "x-rate-limit-remaining"
        private const val RESET_KEY = "x-rate-limit-reset"
    }
}