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

import android.graphics.Color
import kotlin.math.roundToInt

internal class ColorUtils private constructor() {

    companion object {

        private const val RGB_TOTAL_COLORS = 256
        private const val DEFAULT_LIGHTNESS_THRESHOLD = .6f
        private const val ON_TAP_LIGHTNESS_THRESHOLD = .3f
        private const val CTA_ON_TAP_DARKNESS_FACTOR = 0.1f
        private const val CTA_ON_TAP_LIGHTNESS_FACTOR = 0.2f
        private const val CTA_TEXT_LIGHTNESS_FACTOR = .6f

        private val OPAQUE_ALPHA = (255 * 1.0f).roundToInt()
        private val TRANSPARENT_ALPHA = (255 * 0.9f).roundToInt()
        private val COLOR_FULLY_WHITE = (255 * 1.0f).roundToInt()
        private val COLOR_PARTIALLY_BLACK = (255 * 0.4f).roundToInt()

        fun calculateCtaTextColor(ctaBackgroundColor: Int): Int {
            return if (isLightColor(ctaBackgroundColor)) {
                calculateDarkerColor(
                    ctaBackgroundColor,
                    CTA_TEXT_LIGHTNESS_FACTOR
                )
            } else {
                Color.WHITE
            }
        }

        fun calculateCtaOnTapColor(ctaBackgroundColor: Int): Int {
            return if (isLightColor(
                    ctaBackgroundColor,
                    ON_TAP_LIGHTNESS_THRESHOLD
                )
            ) {
                calculateDarkerColor(
                    ctaBackgroundColor,
                    CTA_ON_TAP_DARKNESS_FACTOR
                )
            } else {
                calculateLighterColor(
                    ctaBackgroundColor,
                    CTA_ON_TAP_LIGHTNESS_FACTOR
                )
            }
        }

        fun isLightColor(color: Int): Boolean {
            return isLightColor(color, DEFAULT_LIGHTNESS_THRESHOLD)
        }

        /**
         * This method calculates a darker color provided a factor of reduction in lightness.
         *
         * @param color The original color value
         * @param factor Factor of lightness reduction, range can be between 0 - 1.0
         * @return  The calculated darker color
         */
        private fun calculateDarkerColor(color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val lightnessLevel = (RGB_TOTAL_COLORS * factor).roundToInt()
            return Color.argb(
                a,
                (r - lightnessLevel).coerceAtLeast(0),
                (g - lightnessLevel).coerceAtLeast(0),
                (b - lightnessLevel).coerceAtLeast(0)
            )
        }

        /**
         * This method calculates a lighter color provided a factor of increase in lightness.
         *
         * @param color A color value
         * @param factor Factor of increase in lightness, range can be between 0 - 1.0
         * @return  The calculated darker color
         */
        private fun calculateLighterColor(color: Int, factor: Float): Int {
            val a = Color.alpha(color)
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val lightnessLevel = (RGB_TOTAL_COLORS * factor).roundToInt()
            return Color.argb(
                a,
                (r + lightnessLevel).coerceAtMost(255),
                (g + lightnessLevel).coerceAtMost(255),
                (b + lightnessLevel).coerceAtMost(255)
            )
        }

        /**
         * This method calculates the suitable contrasting color that is viewable.
         *
         * @param color A color value.
         * @return  The calculated contrasting color that is viewable.
         */
        fun calculateContrastingColor(color: Int): Int {
            val isLightColor = isLightColor(color)
            val alpha = if (isLightColor) OPAQUE_ALPHA else TRANSPARENT_ALPHA
            val rgbColor = if (isLightColor) COLOR_PARTIALLY_BLACK else COLOR_FULLY_WHITE
            return Color.argb(alpha, rgbColor, rgbColor, rgbColor)
        }

        /**
         * This method uses HSL to determine in a human eyesight terms if a color is light or not.
         * See: http://en.wikipedia.org/wiki/HSL_and_HSV. The threshold values are from ITU Rec. 709
         * http://en.wikipedia.org/wiki/Rec._709#Luma_coefficients
         *
         *
         * @param  color A color value
         * @param  factor A factor of lightness measured between 0-1.0
         * @return Whether or not the color is considered light
         */
        private fun isLightColor(color: Int, factor: Float): Boolean {
            val r = Color.red(color)
            val g = Color.green(color)
            val b = Color.blue(color)
            val threshold = 0.21 * r + 0.72 * g + 0.07 * b
            return threshold > RGB_TOTAL_COLORS * factor
        }
    }
}