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

import com.twitter.sdk.android.core.models.Card
import com.twitter.sdk.android.core.models.ImageValue
import com.twitter.sdk.android.core.models.UserValue

class VineCardUtils private constructor() {

    companion object {

        private const val PLAYER_CARD = "player"
        private const val VINE_CARD = "vine"
        private const val VINE_USER_ID: Long = 586671909

        @JvmStatic
        fun isVine(card: Card): Boolean {
            return PLAYER_CARD == card.name || VINE_CARD == card.name && isVineUser(card)
        }

        private fun isVineUser(card: Card): Boolean {
            try {
                val user = card.bindingValues.get<UserValue>("site")
                if (user?.idStr?.toLong() == VINE_USER_ID) {
                    return true
                }
            } catch (ex: NumberFormatException) {
                return false
            }
            return false
        }

        fun getPublisherId(card: Card): String? {
            return card.bindingValues.get<UserValue>("site")?.idStr
        }

        @JvmStatic
        fun getStreamUrl(card: Card): String? {
            return card.bindingValues.get("player_stream_url")
        }

        @JvmStatic
        fun getImageValue(card: Card): ImageValue? {
            return card.bindingValues.get("player_image")
        }
    }
}