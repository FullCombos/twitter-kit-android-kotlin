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
import android.content.SharedPreferences

internal class PreferenceStoreImpl(context: Context, name: String) : PreferenceStore {

    private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    /**
     * @return [android.content.SharedPreferences] name spaced to Kit
     */
    override fun get(): SharedPreferences {
        return sharedPreferences
    }

    /**
     * @return [android.content.SharedPreferences.Editor] name spaced to Kit
     */
    override fun edit(): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    /**
     * Apply thread safe saves based on Android API level
     * @param editor
     * @return boolean success
     */
    override fun save(editor: SharedPreferences.Editor): Boolean {
        editor.apply()
        return true
    }
}