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

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.databinding.TweetuiFragmentTweetBinding
import com.example.app.twittercore.TwitterCoreMainActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.CompactTweetView
import com.twitter.sdk.android.tweetui.TweetUtils
import com.twitter.sdk.android.tweetui.TweetView
import java.util.*

class TweetActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.tweets_activity)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return TweetsFragment.newInstance()
    }

    private class TweetsFragment : Fragment() {

        private var _binding: TweetuiFragmentTweetBinding? = null
        private val binding get() = _binding!!

        // launch the app login activity when a guest user tries to favorite a Tweet
        private val actionCallback: Callback<Tweet> = object : Callback<Tweet>() {
            override fun success(result: Result<Tweet>) {
                // Intentionally blank
            }

            override fun failure(exception: TwitterException) {
                if (exception is TwitterAuthException) {
                    startActivity(TwitterCoreMainActivity.newIntent(requireContext()))
                }
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = TweetuiFragmentTweetBinding.inflate(inflater)
            val tweetRegion = binding.tweetRegion

            // load single Tweets and construct TweetViews
            loadTweet(20L, tweetRegion, R.id.jack_regular_tweet)
            loadTweet(510908133917487104L, tweetRegion, R.id.bike_regular_tweet)

            // load multiple Tweets and construct CompactTweetViews
            val tweetIds = listOf(20L, 510908133917487104L)
            val viewIds = listOf(
                R.id.jack_compact_tweet,
                R.id.bike_compact_tweet
            )
            loadTweets(tweetIds, tweetRegion, viewIds)
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        /**
         * loadTweet wraps TweetUtils.loadTweet with a callback that ensures the view is given a
         * known id to simplify UI automation testing.
         */
        private fun loadTweet(tweetId: Long, container: ViewGroup, viewId: Int) {

            val singleTweetCallback: Callback<Tweet> = object : Callback<Tweet>() {

                override fun success(result: Result<Tweet>) {
                    val context = activity ?: return
                    val tweet = result.data
                    val tv = TweetView(
                        context, tweet,
                        R.style.tw__TweetLightWithActionsStyle
                    )
                    tv.setOnActionCallback(actionCallback)
                    tv.id = viewId
                    container.addView(tv)
                }

                override fun failure(exception: TwitterException) {
                    Log.e(TAG, "loadTweet failure", exception)
                }
            }
            TweetUtils.loadTweet(tweetId, singleTweetCallback)
        }

        /**
         * loadTweets wraps TweetUtils.loadTweets to use a callback that ensures each view is given
         * a known id to simplify UI automation testing.
         */
        private fun loadTweets(
            tweetIds: List<Long>, container: ViewGroup,
            viewIds: List<Int>
        ) {
            TweetUtils.loadTweets(tweetIds, object : Callback<List<Tweet>>() {

                override fun success(result: Result<List<Tweet>>) {
                    val context = activity ?: return
                    for (i in result.data.indices) {
                        val tv = CompactTweetView(
                            context, result.data[i],
                            R.style.tw__TweetDarkWithActionsStyle
                        )
                        tv.setOnActionCallback(actionCallback)
                        tv.id = viewIds[i]
                        container.addView(tv)
                    }
                }

                override fun failure(exception: TwitterException) {
                    Log.e(TAG, "loadTweets failure $tweetIds", exception)
                }
            })
        }

        companion object {

            fun newInstance(): TweetsFragment {
                return TweetsFragment()
            }
        }
    }

    companion object {
        private const val TAG = "TweetActivity"
    }
}