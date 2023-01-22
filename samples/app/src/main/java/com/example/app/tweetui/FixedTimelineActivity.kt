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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.example.app.R
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.FixedTweetTimeline
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter
import com.twitter.sdk.android.tweetui.TweetUtils

class FixedTimelineActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.fixed_timeline)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return FixedTimelineFragment.newInstance()
    }

    /**
     * Fragment showing a Timeline with a fixed list of Tweets.
     */
    private class FixedTimelineFragment : ListFragment() {

        private val tweetIds = mutableListOf<Long>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            tweetIds.addAll(
                listOf(
                    574000939800993792L, 503435417459249153L, 510908133917487104L,
                    473514864153870337L, 477788140900347904L, 20L, 484816434313195520L,
                    466041861774114819L, 448250020773380096L
                )
            )

            TweetUtils.loadTweets(tweetIds, object : Callback<List<Tweet>>() {

                override fun success(result: Result<List<Tweet>>) {
                    val fixedTimeline: FixedTweetTimeline = FixedTweetTimeline.Builder()
                        .setTweets(result.data).build()
                    val adapter = TweetTimelineListAdapter(
                        activity,
                        fixedTimeline
                    )
                    listAdapter = adapter
                }

                override fun failure(exception: TwitterException) {
                    val activity = activity
                    if (activity != null && !activity.isFinishing) {
                        Toast.makeText(
                            activity, R.string.multi_tweet_view_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.tweetui_timeline, container, false)
        }

        companion object {

            fun newInstance(): FixedTimelineFragment {
                return FixedTimelineFragment()
            }
        }
    }
}