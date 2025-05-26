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

import com.twitter.sdk.android.core.GuestSessionProvider
import com.twitter.sdk.android.core.Session
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterAuthToken
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient

internal class OkHttpClientHelper private constructor() {

    companion object {

        fun getOkHttpClient(guestSessionProvider: GuestSessionProvider): OkHttpClient {
            return addGuestAuth(OkHttpClient.Builder(), guestSessionProvider).build()
        }

        fun getOkHttpClient(
            session: Session<TwitterAuthToken>,
            authConfig: TwitterAuthConfig
        ): OkHttpClient {
            return addSessionAuth(OkHttpClient.Builder(), session, authConfig).build()
        }

        fun getCustomOkHttpClient(
            httpClient: OkHttpClient,
            guestSessionProvider: GuestSessionProvider
        ): OkHttpClient {
            return addGuestAuth(httpClient.newBuilder(), guestSessionProvider)
                .build()
        }

        fun getCustomOkHttpClient(
            httpClient: OkHttpClient,
            session: Session<TwitterAuthToken>,
            authConfig: TwitterAuthConfig
        ): OkHttpClient {
            return addSessionAuth(httpClient.newBuilder(), session, authConfig)
                .build()
        }

        private fun addGuestAuth(
            builder: OkHttpClient.Builder,
            guestSessionProvider: GuestSessionProvider
        ): OkHttpClient.Builder {
            return builder
                //.certificatePinner(certificatePinner)
                .authenticator(GuestAuthenticator(guestSessionProvider))
                .addInterceptor(GuestAuthInterceptor(guestSessionProvider))
                .addNetworkInterceptor(GuestAuthNetworkInterceptor())
        }

        private fun addSessionAuth(
            builder: OkHttpClient.Builder,
            session: Session<TwitterAuthToken>,
            authConfig: TwitterAuthConfig
        ): OkHttpClient.Builder {
            return builder
                //.certificatePinner(certificatePinner)
                .addInterceptor(OAuth1aInterceptor(session, authConfig))
        }

        //VERISIGN_CLASS1
        //VERISIGN_CLASS1_G3
        //VERISIGN_CLASS2_G2
        //VERISIGN_CLASS2_G3
        //VERISIGN_CLASS3_G2
        //VERISIGN_CLASS3_G3
        //VERISIGN_CLASS3_G4
        //VERISIGN_CLASS3_G5
        //VERISIGN_CLASS4_G3
        //VERISIGN_UNIVERSAL
        //GEOTRUST_GLOBAL
        //GEOTRUST_GLOBAL2
        //GEOTRUST_PRIMARY
        //GEOTRUST_PRIMARY_G2
        //GEOTRUST_PRIMARY_G3
        //GEOTRUST_UNIVERAL
        //GEOTRUST_UNIVERSAL2
        //DIGICERT_GLOBAL_ROOT
        //DIGICERT_EV_ROOT
        //DIGICERT_ASSUREDID_ROOT
        //TWITTER1
        val certificatePinner
            get() = CertificatePinner.Builder()
                .add("*.twitter.com", "sha1/I0PRSKJViZuUfUYaeX7ATP7RcLc=") //VERISIGN_CLASS1
                .add("*.twitter.com", "sha1/VRmyeKyygdftp6vBg5nDu2kEJLU=") //VERISIGN_CLASS1_G3
                .add("*.twitter.com", "sha1/Eje6RRfurSkm/cHN/r7t8t7ZFFw=") //VERISIGN_CLASS2_G2
                .add("*.twitter.com", "sha1/Wr7Fddyu87COJxlD/H8lDD32YeM=") //VERISIGN_CLASS2_G3
                .add("*.twitter.com", "sha1/GiG0lStik84Ys2XsnA6TTLOB5tQ=") //VERISIGN_CLASS3_G2
                .add("*.twitter.com", "sha1/IvGeLsbqzPxdI0b0wuj2xVTdXgc=") //VERISIGN_CLASS3_G3
                .add("*.twitter.com", "sha1/7WYxNdMb1OymFMQp4xkGn5TBJlA=") //VERISIGN_CLASS3_G4
                .add("*.twitter.com", "sha1/sYEIGhmkwJQf+uiVKMEkyZs0rMc=") //VERISIGN_CLASS3_G5
                .add("*.twitter.com", "sha1/PANDaGiVHPNpKri0Jtq6j+ki5b0=") //VERISIGN_CLASS4_G3
                .add("*.twitter.com", "sha1/u8I+KQuzKHcdrT6iTb30I70GsD0=") //VERISIGN_UNIVERSAL
                .add("*.twitter.com", "sha1/wHqYaI2J+6sFZAwRfap9ZbjKzE4=") //GEOTRUST_GLOBAL
                .add("*.twitter.com", "sha1/cTg28gIxU0crbrplRqkQFVggBQk=") //GEOTRUST_GLOBAL2
                .add("*.twitter.com", "sha1/sBmJ5+/7Sq/LFI9YRjl2IkFQ4bo=") //GEOTRUST_PRIMARY
                .add("*.twitter.com", "sha1/vb6nG6txV/nkddlU0rcngBqCJoI=") //GEOTRUST_PRIMARY_G2
                .add("*.twitter.com", "sha1/nKmNAK90Dd2BgNITRaWLjy6UONY=") //GEOTRUST_PRIMARY_G3
                .add("*.twitter.com", "sha1/h+hbY1PGI6MSjLD/u/VR/lmADiI=") //GEOTRUST_UNIVERAL
                .add("*.twitter.com", "sha1/Xk9ThoXdT57KX9wNRW99UbHcm3s=") //GEOTRUST_UNIVERSAL2
                .add("*.twitter.com", "sha1/1S4TwavjSdrotJWU73w4Q2BkZr0=") //DIGICERT_GLOBAL_ROOT
                .add("*.twitter.com", "sha1/gzF+YoVCU9bXeDGQ7JGQVumRueM=") //DIGICERT_EV_ROOT
                .add("*.twitter.com", "sha1/aDMOYTWFIVkpg6PI0tLhQG56s8E=") //DIGICERT_ASSUREDID_ROOT
                .add("*.twitter.com", "sha1/Vv7zwhR9TtOIN/29MFI4cgHld40=") //TWITTER1
                .build()
    }
}