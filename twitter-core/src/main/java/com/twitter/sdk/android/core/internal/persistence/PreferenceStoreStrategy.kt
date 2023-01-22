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

import android.annotation.SuppressLint

internal class PreferenceStoreStrategy<T>(
    private val store: PreferenceStore,
    private val serializer: SerializationStrategy<T>,
    private val key: String
) : PersistenceStrategy<T> {

    @SuppressLint("CommitPrefEdits")
    override fun save(`object`: T) {
        store.save(store.edit().putString(key, serializer.serialize(`object`)))
    }

    override fun restore(): T? {
        val store = store.get()
        return serializer.deserialize(store.getString(key, null))
    }

    override fun clear() {
        //TODO create a remove on the PreferenceStore
        store.edit().remove(key).apply()
    }
}