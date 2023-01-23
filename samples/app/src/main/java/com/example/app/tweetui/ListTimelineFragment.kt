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
import androidx.fragment.app.ListFragment
import com.example.app.R
import com.example.app.twittercore.TwitterCoreMainActivity
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.*
import java.io.InputStream
import java.io.InputStreamReader

/**
 * ListTimelineFragment demonstrates a TimelineListAdapter with a TwitterListTimeline.
 */
class ListTimelineFragment : ListFragment() {

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
        val timeline: TwitterListTimeline = TwitterListTimeline.Builder()
            .slugWithOwnerScreenName("twitter-bots", "dghubble")
            .build()
        val adapter: TweetTimelineListAdapter = TweetTimelineListAdapter.Builder(activity)
            .setTimelineFilter(basicTimelineFilter)
            .setTimeline(timeline)
            .setViewStyle(com.twitter.sdk.android.tweetui.R.style.twitter_TweetLightWithActionsStyle)
            .setOnActionCallback(actionCallback)
            .build()
        listAdapter = adapter
    }

    private val basicTimelineFilter: TimelineFilter
        get() {
            val inputStream: InputStream =
                requireContext().resources.openRawResource(R.raw.filter_values)
            val reader = JsonReader(InputStreamReader(inputStream))
            val filterValues = Gson().fromJson<FilterValues>(reader, FilterValues::class.java)
            return BasicTimelineFilter(filterValues)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.tweetui_timeline, container, false)
    }

    companion object {

        fun newInstance(): ListTimelineFragment {
            return ListTimelineFragment()
        }
    }
}