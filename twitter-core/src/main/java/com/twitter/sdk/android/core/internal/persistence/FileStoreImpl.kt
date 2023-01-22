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
package com.twitter.sdk.android.core.internal.persistence

import android.content.Context
import android.os.Environment
import com.twitter.sdk.android.core.Twitter
import java.io.File

internal class FileStoreImpl(private val context: Context) : FileStore {

    /**
     *
     * @return Directory to store internal cache files.
     */
    override fun getCacheDir(): File? {
        return prepare(context.cacheDir)
    }

    /**
     * Requires [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
     *
     * @return Directory to store External Cache files.
     */
    override fun getExternalCacheDir(): File? {
        return if (isExternalStorageAvailable()) {
            prepare(context.externalCacheDir)
        } else {
            prepare(null)
        }
    }

    /**
     *
     * @return Directory to store internal files.
     */
    override fun getFilesDir(): File? {
        return prepare(context.filesDir)
    }

    /**
     * Requires [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
     *
     * @return Directory to store External files.
     */
    override fun getExternalFilesDir(): File? {
        return if (isExternalStorageAvailable()) {
            prepare(context.getExternalFilesDir(null))
        } else {
            prepare(null)
        }
    }

    private fun prepare(file: File?): File? {
        if (file != null) {
            if (file.exists() || file.mkdirs()) {
                return file
            } else {
                Twitter.getLogger().w(Twitter.TAG, "Couldn't create file")
            }
        } else {
            Twitter.getLogger().d(Twitter.TAG, "Null File")
        }
        return null
    }

    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        if (Environment.MEDIA_MOUNTED != state) {
            Twitter.getLogger().w(
                Twitter.TAG,
                """
                    External Storage is not mounted and/or writable
                    Have you declared android.permission.WRITE_EXTERNAL_STORAGE in the manifest?
                """.trimIndent()
            )
            return false
        }
        return true
    }
}