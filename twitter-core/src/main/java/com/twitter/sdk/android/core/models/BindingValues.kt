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
package com.twitter.sdk.android.core.models

import java.util.*

/**
 * Map of key/value pairs representing card data.
 */
class BindingValues(bindingValues: Map<String, Any?> = emptyMap()) {

    private val bindingValues: Map<String, Any?> = Collections.unmodifiableMap(bindingValues)

    /**
     * Returns `true` if specified key exists.
     */
    fun containsKey(key: String): Boolean {
        return bindingValues.containsKey(key)
    }

    /**
     * Returns the value for the specified key. Returns `null` if key does not exist, or
     * object cannot be cast to return type.
     */
    fun <T> get(key: String): T? {
        return try {
            bindingValues[key] as T
        } catch (ex: ClassCastException) {
            null
        }
    }
}