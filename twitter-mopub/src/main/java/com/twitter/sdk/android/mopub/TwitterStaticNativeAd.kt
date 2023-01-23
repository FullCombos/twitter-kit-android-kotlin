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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.*
import android.graphics.drawable.shapes.RectShape
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.core.content.ContextCompat
import com.twitter.sdk.android.mopub.databinding.TwitterNativeAdBinding

@SuppressLint("ResourceType")
internal class TwitterStaticNativeAd @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    styleResId: Int = DEFAULT_AD_STYLE
) : FrameLayout(context, attrs, styleResId) {

    private val binding: TwitterNativeAdBinding

    // style colors
    private var containerBackgroundColor = 0
    private var cardBackgroundColor = 0
    private var primaryTextColor = 0
    private var ctaBackgroundColor = 0
    private var cardBorderColor = 0

    init {
        binding = TwitterNativeAdBinding.inflate(LayoutInflater.from(context), this, true)
        initAttributes(styleResId)
        setStyleAttributes()
    }

    fun getAdTitleView(): TextView = binding.nativeAdTitle

    fun getAdTextView(): TextView = binding.nativeAdText

    fun getCtaView(): Button = binding.nativeAdCta

    fun getMainImageView(): ImageView = binding.nativeAdMainImage

    fun getAdIconView(): ImageView = binding.nativeAdIconImage

    fun getPrivacyInfoView(): ImageView = binding.nativeAdPrivacyInfoIconImage

    private fun initAttributes(styleResId: Int) {
        val a = context.theme.obtainStyledAttributes(
            styleResId,
            R.styleable.twitter_native_ad
        )
        try {
            readStyleAttributes(a)
        } finally {
            a.recycle()
        }
    }

    private fun readStyleAttributes(typedArray: TypedArray) {
        containerBackgroundColor = typedArray.getColor(
            R.styleable.twitter_native_ad_twitter_ad_container_bg_color,
            ContextCompat.getColor(context, R.color.twitter_ad_light_container_bg_color)
        )
        cardBackgroundColor = typedArray.getColor(
            R.styleable.twitter_native_ad_twitter_ad_card_bg_color,
            ContextCompat.getColor(context, R.color.twitter_ad_light_card_bg_color)
        )
        primaryTextColor = typedArray.getColor(
            R.styleable.twitter_native_ad_twitter_ad_text_primary_color,
            ContextCompat.getColor(context, R.color.twitter_ad_light_text_primary_color)
        )
        ctaBackgroundColor = typedArray.getColor(
            R.styleable.twitter_native_ad_twitter_ad_cta_button_color,
            ContextCompat.getColor(context, R.color.twitter_ad_cta_default)
        )
    }

    private fun setStyleAttributes() {
        binding.twitterAdMopubLayout.setBackgroundColor(containerBackgroundColor)
        binding.nativeAdTitle.setTextColor(primaryTextColor)
        binding.nativeAdText.setTextColor(primaryTextColor)

        val adViewRadius = resources.getDimension(R.dimen.twitter_ad_view_radius).toInt()
        binding.nativeAdMainImage.setCornerRadii(adViewRadius, adViewRadius, 0, 0)

        val privacyTextView = findViewById<TextView>(R.id.native_ad_privacy_text)
        privacyTextView.setTextColor(
            ColorUtils.calculateContrastingColor(containerBackgroundColor)
        )

        setCardStyling()
        setCallToActionStyling()
    }

    private fun setCardStyling() {
        val isLightBg = ColorUtils.isLightColor(containerBackgroundColor)
        cardBorderColor = if (isLightBg) {
            ContextCompat.getColor(context, R.color.twitter_ad_light_card_border_color)
        } else {
            ContextCompat.getColor(context, R.color.twitter_ad_dark_card_border_color)
        }

        val bgDrawable = ShapeDrawable(RectShape())
        bgDrawable.paint.color = cardBackgroundColor

        val borderDrawable = ShapeDrawable(RectShape())
        borderDrawable.paint.color = cardBorderColor

        val layers = arrayOfNulls<Drawable>(2)
        layers[0] = borderDrawable
        layers[1] = bgDrawable
        val layerDrawable = LayerDrawable(layers)
        layerDrawable.setLayerInset(0, 0, 0, 0, 0)
        layerDrawable.setLayerInset(1, 1, 0, 1, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.nativeAdCard.background = layerDrawable
        } else {
            @Suppress("DEPRECATION")
            binding.nativeAdCard.setBackgroundDrawable(layerDrawable)
        }
    }

    private fun setCallToActionStyling() {
        val calculatedCTATextColor = ColorUtils.calculateCtaTextColor(ctaBackgroundColor)
        binding.nativeAdCta.setTextColor(calculatedCTATextColor)

        // Setup StateListDrawable obj with two gradient drawables:
        // First is the selected item with lighter/darker bg color of original
        // Second is unselected item with the call to action background color
        // Also set the default ad view radius for bottomLeft and bottomRight corners
        val stateListDrawable = StateListDrawable()
        val adViewRadius = resources.getDimension(R.dimen.twitter_ad_view_radius).toInt()
        val ctaViewRadii = floatArrayOf(
            0f, 0f,
            0f, 0f,
            adViewRadius.toFloat(), adViewRadius.toFloat(),
            adViewRadius.toFloat(), adViewRadius.toFloat()
        )
        val selectedItem = GradientDrawable()
        selectedItem.cornerRadii = ctaViewRadii
        val ctaPressedBgColor = ColorUtils.calculateCtaOnTapColor(ctaBackgroundColor)
        selectedItem.setColor(ctaPressedBgColor)

        stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), selectedItem)
        val unselectedItem = GradientDrawable()
        unselectedItem.cornerRadii = ctaViewRadii
        unselectedItem.setColor(ctaBackgroundColor)
        stateListDrawable.addState(intArrayOf(), unselectedItem)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            binding.nativeAdCta.background = stateListDrawable
        } else {
            @Suppress("DEPRECATION")
            binding.nativeAdCta.setBackgroundDrawable(stateListDrawable)
        }
    }

    companion object {

        private val DEFAULT_AD_STYLE = R.style.twitter_ad_LightStyle
    }
}