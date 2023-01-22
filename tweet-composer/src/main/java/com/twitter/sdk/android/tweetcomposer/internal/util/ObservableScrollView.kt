/*
 *  Copyright (C) 2022 Garena Online Pvt Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.twitter.sdk.android.tweetcomposer.internal.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView

internal class ObservableScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    var scrollViewListener: ScrollViewListener? = null

    override fun onScrollChanged(currentX: Int, currentY: Int, oldX: Int, oldY: Int) {
        super.onScrollChanged(currentX, currentY, oldX, oldY)
        scrollViewListener?.onScrollChanged(currentY)
    }

    interface ScrollViewListener {
        fun onScrollChanged(scrollY: Int)
    }
}