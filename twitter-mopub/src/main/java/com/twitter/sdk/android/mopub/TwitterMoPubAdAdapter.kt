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
package com.twitter.sdk.android.mopub

import android.app.Activity
import android.widget.Adapter
import com.mopub.nativeads.MoPubAdAdapter
import com.mopub.nativeads.MoPubNativeAdPositioning.MoPubClientPositioning
import com.mopub.nativeads.RequestParameters

class TwitterMoPubAdAdapter : MoPubAdAdapter {

    constructor(
        activity: Activity,
        originalAdapter: Adapter,
    ) : super(activity, originalAdapter)

    constructor(
        activity: Activity,
        originalAdapter: Adapter,
        adPositioning: MoPubClientPositioning
    ) : super(activity, originalAdapter, adPositioning)

    companion object {
        private const val TWITTER_KIT_KEYWORD = "src:twitterkit"
    }

    override fun loadAds(adUnitId: String) {
        loadAds(adUnitId, null)
    }

    override fun loadAds(
        adUnitId: String,
        requestParams: RequestParameters?
    ) {
        val builder = RequestParameters.Builder()
        if (requestParams != null) {
            val keywords =
                if (requestParams.keywords.isNullOrEmpty()) TWITTER_KIT_KEYWORD else requestParams.keywords + "," + TWITTER_KIT_KEYWORD
            builder.keywords(keywords)
            builder.location(requestParams.location)
        } else {
            builder.keywords(TWITTER_KIT_KEYWORD)
        }
        super.loadAds(adUnitId, builder.build())
    }
}