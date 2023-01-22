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

/**
 * Interface to support custom logger.
 */
interface Logger {
    fun isLoggable(tag: String, level: Int): Boolean
    fun getLogLevel(): Int
    fun setLogLevel(logLevel: Int)
    fun d(tag: String, text: String, throwable: Throwable? = null)
    fun v(tag: String, text: String, throwable: Throwable? = null)
    fun i(tag: String, text: String, throwable: Throwable? = null)
    fun w(tag: String, text: String, throwable: Throwable? = null)
    fun e(tag: String, text: String, throwable: Throwable? = null)
    fun log(priority: Int, tag: String, msg: String, forceLog: Boolean = false)
}