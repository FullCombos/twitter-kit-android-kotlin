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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.mopub.nativeads.*

class TwitterStaticNativeAdRenderer(private val styleResId: Int = DEFAULT_STYLE) :
    MoPubAdRenderer<StaticNativeAd> {

    override fun createAdView(context: Context, parent: ViewGroup?): View {
        return TwitterStaticNativeAd(context, null, styleResId)
    }

    override fun renderAdView(view: View, staticNativeAd: StaticNativeAd) {
        update(view as TwitterStaticNativeAd, staticNativeAd)
    }

    override fun supports(nativeAd: BaseNativeAd): Boolean {
        return nativeAd is StaticNativeAd
    }

    private fun update(
        staticNativeView: TwitterStaticNativeAd,
        staticNativeAd: StaticNativeAd
    ) {
        NativeRendererHelper.addTextView(
            staticNativeView.getAdTitleView(),
            staticNativeAd.title
        )
        NativeRendererHelper.addTextView(staticNativeView.getAdTextView(), staticNativeAd.text)
        NativeRendererHelper.addTextView(
            staticNativeView.getCtaView(),
            staticNativeAd.callToAction
        )
        NativeImageHelper.loadImageView(
            staticNativeAd.mainImageUrl,
            staticNativeView.getMainImageView()
        )
        NativeImageHelper.loadImageView(
            staticNativeAd.iconImageUrl,
            staticNativeView.getAdIconView()
        )
        NativeRendererHelper.addPrivacyInformationIcon(
            staticNativeView.getPrivacyInfoView(),
            staticNativeAd.privacyInformationIconImageUrl,
            staticNativeAd.privacyInformationIconClickThroughUrl
        )
    }

    companion object {
        private val DEFAULT_STYLE = R.style.twitter_ad_LightStyle
    }
}