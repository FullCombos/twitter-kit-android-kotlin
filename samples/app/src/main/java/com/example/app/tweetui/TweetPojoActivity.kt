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
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.example.app.R
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.twitter.sdk.android.core.models.SafeListAdapter
import com.twitter.sdk.android.core.models.SafeMapAdapter
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.FixedTweetTimeline
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter
import java.io.InputStreamReader

/**
 * Example code showing how to load Tweets from JSON.
 */
class TweetPojoActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.tweet_pojo)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return TweetPojoFragment.newInstance()
    }

    private class TweetPojoFragment : ListFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Note: Load should normally be called from background thread.
            val tweets = loadTweets()
            val fixedTimeline: FixedTweetTimeline = FixedTweetTimeline.Builder()
                .setTweets(tweets).build()
            val adapter = TweetTimelineListAdapter(
                activity,
                fixedTimeline
            )

            listAdapter = adapter
        }

        private fun loadTweets(): List<Tweet> {

            val gson = GsonBuilder()
                .registerTypeAdapterFactory(SafeListAdapter())
                .registerTypeAdapterFactory(SafeMapAdapter())
                .create()
            return InputStreamReader(resources.openRawResource(R.raw.tweets)).use {
                gson.fromJson(
                    it,
                    object : TypeToken<ArrayList<Tweet?>?>() {}.type
                )
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.tweetui_timeline, container, false)
        }

        companion object {

            fun newInstance(): TweetPojoFragment {
                return TweetPojoFragment()
            }
        }
    }
}