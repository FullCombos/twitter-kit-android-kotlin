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
package com.twitter.sdk.android.core.internal.network

import java.io.UnsupportedEncodingException
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

internal class UrlUtils private constructor() {

    companion object {

        const val UTF8 = "UTF8"

        fun getQueryParams(uri: URI, decode: Boolean): TreeMap<String, String> {
            return getQueryParams(uri.rawQuery, decode)
        }

        fun getQueryParams(paramsString: String?, decode: Boolean): TreeMap<String, String> {
            val params = TreeMap<String, String>()
            if (paramsString == null) {
                return params
            }
            for (nameValuePairString in paramsString.split("&".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()) {
                val nameValuePair =
                    nameValuePairString.split("=".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                if (nameValuePair.size == 2) {
                    if (decode) {
                        params[urlDecode(nameValuePair[0])] = urlDecode(nameValuePair[1])
                    } else {
                        params[nameValuePair[0]] = nameValuePair[1]
                    }
                } else if (nameValuePair[0].isNotEmpty()) {
                    if (decode) {
                        params[urlDecode(nameValuePair[0])] = ""
                    } else {
                        params[nameValuePair[0]] = ""
                    }
                }
            }
            return params
        }

        fun urlEncode(s: String?): String {
            return if (s == null) {
                ""
            } else try {
                URLEncoder.encode(s, UTF8)
            } catch (unlikely: UnsupportedEncodingException) {
                throw RuntimeException(unlikely.message, unlikely)
            }
        }

        fun urlDecode(s: String?): String {
            return if (s == null) {
                ""
            } else try {
                URLDecoder.decode(s, UTF8)
            } catch (unlikely: UnsupportedEncodingException) {
                throw RuntimeException(unlikely.message, unlikely)
            }
        }

        /**
         * Percent encodes by doing the following:
         * 1) url encode string using UTF8
         * 2) apply additional encoding to string, replacing:
         * "*" => "%2A"
         * "+" => "%20"
         * "%7E" => "~"
         *
         * @param s the string to encode
         * @return the encoded string
         */
        fun percentEncode(s: String?): String {
            if (s == null) {
                return ""
            }
            val sb = StringBuilder()
            val encoded = urlEncode(s)
            val encodedLength = encoded.length
            var i = 0
            while (i < encodedLength) {
                val c = encoded[i]
                if (c == '*') {
                    sb.append("%2A")
                } else if (c == '+') {
                    sb.append("%20")
                } else if (c == '%' && i + 2 < encodedLength && encoded[i + 1] == '7' && encoded[i + 2] == 'E') {
                    sb.append('~')
                    i += 2
                } else {
                    sb.append(c)
                }
                i++
            }
            return sb.toString()
        }
    }
}