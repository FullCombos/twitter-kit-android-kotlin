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

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

class SafeMapAdapter : TypeAdapterFactory {

    override fun <T> create(gson: Gson, tokenType: TypeToken<T>): TypeAdapter<T> {
        val delegate = gson.getDelegateAdapter(this, tokenType)

        return object : TypeAdapter<T>() {
            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: T) {
                delegate.write(out, value)
            }

            @Suppress("UNCHECKED_CAST")
            @Throws(IOException::class)
            override fun read(arg0: JsonReader): T {
                val t = delegate.read(arg0)
                if (MutableMap::class.java.isAssignableFrom(tokenType.rawType)) {
                    if (t == null) {
                        return Collections.EMPTY_MAP as T
                    }
                    val map = t as Map<*, *>
                    return Collections.unmodifiableMap(map) as T
                }
                return t
            }
        }
    }
}