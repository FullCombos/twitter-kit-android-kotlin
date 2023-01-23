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
package com.example.app.tweetui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.ListFragment
import com.example.app.BuildConfig
import com.example.app.R
import com.example.app.twittercore.TwitterCoreMainActivity
import com.mopub.nativeads.MoPubAdAdapter
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.mopub.TwitterMoPubAdAdapter
import com.twitter.sdk.android.mopub.TwitterStaticNativeAdRenderer
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter
import com.twitter.sdk.android.tweetui.UserTimeline

/**
 * UserTimelineFragment demonstrates a TimelineListAdapter with a UserTimeline.
 */
class UserTimelineFragment : ListFragment() {

    private var moPubAdAdapter: MoPubAdAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // launch the app login activity when a guest user tries to favorite a Tweet
        val actionCallback: Callback<Tweet> = object : Callback<Tweet>() {

            // Intentionally blank
            override fun success(result: Result<Tweet>) = Unit

            override fun failure(exception: TwitterException) {
                if (exception is TwitterAuthException) {
                    startActivity(activity?.let { TwitterCoreMainActivity.newIntent(it) })
                }
            }
        }
        val userTimeline = UserTimeline.Builder()
            .screenName("twitterdev")
            .build()
        val adapter = TweetTimelineListAdapter.Builder(activity)
            .setTimeline(userTimeline)
            .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
            .setOnActionCallback(actionCallback)
            .build()

        moPubAdAdapter = TwitterMoPubAdAdapter(activity as FragmentActivity, adapter).apply {
            val adRenderer = TwitterStaticNativeAdRenderer()
            registerAdRenderer(adRenderer)
            loadAds(BuildConfig.MOPUB_AD_UNIT_ID)
            listAdapter = this
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.tweetui_timeline, container, false)
    }

    override fun onDestroy() {
        // You must call this or the ad adapter may cause a memory leak
        moPubAdAdapter?.destroy()
        super.onDestroy()
    }

    companion object {

        fun newInstance(): UserTimelineFragment {
            return UserTimelineFragment()
        }
    }
}