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

import com.google.gson.*
import java.lang.reflect.Type

internal class BindingValuesAdapter : JsonSerializer<BindingValues>,
    JsonDeserializer<BindingValues> {

    companion object {
        private const val STRING_TYPE = "STRING"
        private const val IMAGE_TYPE = "IMAGE"
        private const val USER_TYPE = "USER"
        private const val BOOLEAN_TYPE = "BOOLEAN"
        private const val TYPE_MEMBER = "type"
        private const val TYPE_VALUE_MEMBER = "string_value"
        private const val IMAGE_VALUE_MEMBER = "image_value"
        private const val USER_VALUE_MEMBER = "user_value"
        private const val BOOLEAN_MEMBER = "boolean_value"
    }

    override fun serialize(
        src: BindingValues,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement? {
        return null
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): BindingValues {
        if (!json.isJsonObject) {
            return BindingValues()
        }
        val obj = json.asJsonObject
        val bindingHash = obj.entrySet().associate {
            val memberObj = it.value.asJsonObject
            val value = getValue(memberObj, context)
            it.key to value
        }
        return BindingValues(bindingHash)
    }

    private fun getValue(obj: JsonObject, context: JsonDeserializationContext): Any? {
        val typeObj = obj[TYPE_MEMBER]
        return if (typeObj == null || !typeObj.isJsonPrimitive) {
            null
        } else when (typeObj.asString) {
            STRING_TYPE -> context.deserialize(
                obj[TYPE_VALUE_MEMBER],
                String::class.java
            )
            IMAGE_TYPE -> context.deserialize(
                obj[IMAGE_VALUE_MEMBER],
                ImageValue::class.java
            )
            USER_TYPE -> context.deserialize(
                obj[USER_VALUE_MEMBER],
                UserValue::class.java
            )
            BOOLEAN_TYPE -> context.deserialize<Any>(
                obj[BOOLEAN_MEMBER],
                Boolean::class.java
            )
            else -> null
        }
    }
}