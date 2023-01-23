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
package com.twitter.sdk.android.mopub.internal

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView

class RoundedImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private lateinit var roundedCornerRadii: FloatArray

    init {
        setCornerRadii(0, 0, 0, 0)
    }

    /**
     * Set radius for each corner and override default behavior of no rounded corners.
     *
     * @param topLeftRadius     top left radius of view
     * @param topRightRadius    top right radius of view
     * @param bottomLeftRadius  bottom left radius of view
     * @param bottomRightRadius bottom right radius of view
     */
    fun setCornerRadii(
        topLeftRadius: Int,
        topRightRadius: Int,
        bottomLeftRadius: Int,
        bottomRightRadius: Int
    ) {
        check(topLeftRadius >= 0 && topRightRadius >= 0 && bottomRightRadius >= 0 && bottomLeftRadius >= 0) { "Radius must not be negative" }
        roundedCornerRadii = floatArrayOf(
            topLeftRadius.toFloat(), topLeftRadius.toFloat(),
            topRightRadius.toFloat(), topRightRadius.toFloat(),
            bottomLeftRadius.toFloat(), bottomLeftRadius.toFloat(),
            bottomRightRadius.toFloat(), bottomRightRadius.toFloat()
        )
    }

    override fun setImageBitmap(bitmap: Bitmap?) {
        if (bitmap != null) {
            setImageDrawable(BitmapDrawable(resources, transform(bitmap)))
        } else {
            setImageDrawable(null)
        }
    }

    private fun transform(source: Bitmap): Bitmap {
        val rect = RectF(0f, 0f, source.width.toFloat(), source.height.toFloat())
        val result = Bitmap.createBitmap(
            source.width, source.height,
            source.config
        )
        val bitmapShader = BitmapShader(
            source, Shader.TileMode.CLAMP,
            Shader.TileMode.CLAMP
        )
        val paint = Paint()
        paint.isAntiAlias = true
        paint.shader = bitmapShader
        val path = Path()
        path.addRoundRect(rect, roundedCornerRadii, Path.Direction.CCW)
        val canvas = Canvas(result)
        canvas.drawPath(path, paint)
        return result
    }
}