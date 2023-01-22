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

import android.content.*
import com.twitter.sdk.android.core.Twitter
import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

internal class CommonUtils private constructor() {

    companion object {

        private const val TRACE_ENABLED_RESOURCE_NAME = "com.twitter.sdk.android.TRACE_ENABLED"
        private const val TRACE_ENABLED_DEFAULT = false

        private var clsTrace: Boolean? = null

        @Throws(IOException::class)
        fun streamToString(`is`: InputStream): String {
            // Previous code was running into this: http://code.google.com/p/android/issues/detail?id=14562
            // on Android 2.3.3. The below code, cribbed from: http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
            // does not exhibit that problem.
            val s = Scanner(`is`).useDelimiter("\\A")
            return if (s.hasNext()) s.next() else ""
        }

        /**
         * Copies all available data from the [InputStream] into the [OutputStream], using the
         * provided `buffer`. Neither stream is closed during this call.
         */
        @Throws(IOException::class)
        fun copyStream(`is`: InputStream, os: OutputStream, buffer: ByteArray) {
            var count: Int
            while (`is`.read(buffer).also { count = it } != -1) {
                os.write(buffer, 0, count)
            }
        }

        /**
         * Closes a [Closeable], ignoring any [IOException]s raised in the process.
         * Does nothing if the [Closeable] is `null`.
         *
         * @param c [Closeable] to close
         */
        fun closeOrLog(c: Closeable?, message: String) {
            if (c != null) {
                try {
                    c.close()
                } catch (e: IOException) {
                    Twitter.getLogger().e(Twitter.TAG, message, e)
                }
            }
        }

        /**
         * Uses the given context's application icon to retrieve the package name for the resources for the context
         * This package name only differs from context.getPackageName() when using aapt parameter --rename-manifest-package
         * @param context Context to get resource package name from
         * @return String representing the package name of the resources for the given context
         */
        fun getResourcePackageName(context: Context): String {
            // There should always be an icon
            // http://developer.android.com/guide/topics/manifest/application-element.html#icon
            // safety check anyway to prevent exceptions
            val iconId = context.applicationContext.applicationInfo.icon
            return if (iconId > 0) {
                context.resources.getResourcePackageName(iconId)
            } else {
                context.packageName
            }
        }

        fun getResourcesIdentifier(context: Context, key: String, resourceType: String): Int {
            val resources = context.resources
            return resources.getIdentifier(key, resourceType, getResourcePackageName(context))
        }

        /**
         *
         *
         * Gets a value for a boolean resource by its name. If a key is not present, the provided default value
         * will be returned.
         *
         *
         *
         * Tries to look up a boolean value two ways:
         *
         *  1. As a `bool` resource. A discovered value is returned as-is.
         *  1. As a `string` resource. A discovered value is turned into a boolean with
         * [Boolean.parseBoolean] before being returned.
         *
         *
         *
         * @param context [Context] to use when accessing resources
         * @param key [String] name of the boolean value to look up
         * @param defaultValue value to be returned if the specified resource could be not be found.
         * @return [String] value of the specified property, or an empty string if it could not be found.
         */
        fun getBooleanResourceValue(
            context: Context?,
            key: String,
            defaultValue: Boolean
        ): Boolean {
            if (context != null) {
                val resources = context.resources
                if (resources != null) {
                    var id = getResourcesIdentifier(context, key, "bool")
                    if (id > 0) {
                        return resources.getBoolean(id)
                    }
                    id = getResourcesIdentifier(context, key, "string")
                    if (id > 0) {
                        return context.getString(id).toBoolean()
                    }
                }
            }
            return defaultValue
        }

        /**
         *
         *
         * Gets a value for a string resource by its name. If a key is not present, the provided default value
         * will be returned.
         *
         *
         * @param context [Context] to use when accessing resources
         * @param key [String] name of the boolean value to look up
         * @param defaultValue value to be returned if the specified resource could be not be found.
         * @return [String] value of the specified property, or an empty string if it could not be found.
         */
        fun getStringResourceValue(context: Context?, key: String, defaultValue: String): String {
            if (context != null) {
                val resources = context.resources
                if (resources != null) {
                    val id = getResourcesIdentifier(context, key, "string")
                    if (id > 0) {
                        return resources.getString(id)
                    }
                }
            }
            return defaultValue
        }

        /**
         */
        fun isClsTrace(context: Context): Boolean {
            // Since the cached value is a Boolean object, it can be null. If it's null, load the value
            // and cache it.
            if (clsTrace == null) {
                clsTrace = getBooleanResourceValue(
                    context, TRACE_ENABLED_RESOURCE_NAME, TRACE_ENABLED_DEFAULT
                )
            }
            return clsTrace!!
        }

        /**
         * Used internally to log only when the com.twitter.sdk.android.TRACE_ENABLED resource value
         * is set to true.  When it is, this API passes processing to the log API.
         */
        fun logControlled(context: Context, msg: String) {
            if (isClsTrace(context)) {
                Twitter.getLogger().d(Twitter.TAG, msg)
            }
        }

        /**
         * Used internally to log errors only when the com.twitter.sdk.android.TRACE_ENABLED resource
         * value is set to true.  When it is, this API passes processing to the logError API.
         */
        fun logControlledError(context: Context, msg: String, tr: Throwable) {
            if (isClsTrace(context)) {
                Twitter.getLogger().e(Twitter.TAG, msg)
            }
        }

        /**
         * Used internally to log only when the com.twitter.sdk.android.TRACE_ENABLED resource value
         * is set to true.  When it is, this API passes processing to the log API.
         */
        fun logControlled(context: Context, level: Int, tag: String, msg: String) {
            if (isClsTrace(context)) {
                Twitter.getLogger().log(level, Twitter.TAG, msg)
            }
        }

        /**
         * If [Twitter.isDebug], throws an IllegalStateException,
         * else logs a warning.
         *
         * @param logTag the log tag to use for logging
         * @param errorMsg the error message
         */
        fun logOrThrowIllegalStateException(logTag: String, errorMsg: String) {
            check(!Twitter.isDebug()) { errorMsg }
            Twitter.getLogger().w(logTag, errorMsg)
        }
    }
}