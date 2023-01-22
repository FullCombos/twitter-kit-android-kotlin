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
package com.twitter.sdk.android.core.internal.oauth

internal class OAuthConstants private constructor() {

    companion object {

        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_GUEST_TOKEN = "x-guest-token"

        // OAuth1.0a parameter constants.
        const val PARAM_CALLBACK = "oauth_callback"
        const val PARAM_CONSUMER_KEY = "oauth_consumer_key"
        const val PARAM_NONCE = "oauth_nonce"
        const val PARAM_SIGNATURE_METHOD = "oauth_signature_method"
        const val PARAM_TIMESTAMP = "oauth_timestamp"
        const val PARAM_TOKEN = "oauth_token"
        const val PARAM_TOKEN_SECRET = "oauth_token_secret"
        const val PARAM_VERSION = "oauth_version"
        const val PARAM_SIGNATURE = "oauth_signature"
        const val PARAM_VERIFIER = "oauth_verifier"

        // OAuth2
        const val AUTHORIZATION_BASIC = "Basic"
        const val AUTHORIZATION_BEARER = "Bearer"
        const val PARAM_GRANT_TYPE = "grant_type"
        const val GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials"
    }
}