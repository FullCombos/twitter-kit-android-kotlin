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

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import java.io.File

/**
 * Wraps Context to provide sub directories for Kits
 */
internal class TwitterContext(
    base: Context,
    private val componentName: String,
    private val componentPath: String
) : ContextWrapper(base) {

    override fun getDatabasePath(name: String): File {
        val dir = File(
            super.getDatabasePath(name).parentFile,
            componentPath
        )
        dir.mkdirs()
        return File(dir, name)
    }

    override fun openOrCreateDatabase(
        name: String,
        mode: Int,
        factory: CursorFactory
    ): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(
            getDatabasePath(name), factory
        )
    }

    override fun openOrCreateDatabase(
        name: String,
        mode: Int,
        factory: CursorFactory,
        errorHandler: DatabaseErrorHandler?
    ): SQLiteDatabase {
        return SQLiteDatabase.openOrCreateDatabase(
            getDatabasePath(name).path, factory, errorHandler
        )
    }

    override fun getFilesDir(): File {
        return File(super.getFilesDir(), componentPath)
    }

    override fun getExternalFilesDir(type: String?): File? {
        return File(super.getExternalFilesDir(type), componentPath)
    }

    override fun getCacheDir(): File {
        return File(super.getCacheDir(), componentPath)
    }

    override fun getExternalCacheDir(): File? {
        return File(super.getExternalCacheDir(), componentPath)
    }

    override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
        return super.getSharedPreferences("$componentName:$name", mode)
    }
}