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

import android.R
import android.app.Activity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.widget.AbsListView
import android.widget.ListView
import android.widget.Toast
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.app.BaseActivity
import com.example.app.databinding.TweetuiSwipeTimelineBinding
import com.example.app.databinding.TweetuiTimelineBinding
import com.example.app.twittercore.TwitterCoreMainActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.SearchTimeline
import com.twitter.sdk.android.tweetui.TimelineResult
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter
import java.lang.ref.WeakReference

/**
 * TimelineActivity shows a full screen timeline which is useful for screenshots.
 */
class TimelineActivity : BaseActivity() {

    private val activityRef: WeakReference<Activity> = WeakReference(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = TweetuiSwipeTimelineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setTitle(R.string.refresh_timeline_title)

        // launch the app login activity when a guest user tries to favorite a Tweet
        val actionCallback: Callback<Tweet> = object : Callback<Tweet>() {

            // Intentionally blank
            override fun success(result: Result<Tweet>) = Unit

            override fun failure(exception: TwitterException) {
                if (exception is TwitterAuthException) {
                    startActivity(TwitterCoreMainActivity.newIntent(this@TimelineActivity))
                }
            }
        }

        with(binding.list) {
            emptyView = binding.empty
            val timeline: SearchTimeline = SearchTimeline.Builder().query("#twitter").build()
            val adapter: TweetTimelineListAdapter = TweetTimelineListAdapter.Builder(this)
                .setTimeline(timeline)
                .setViewStyle(R.style.tw__TweetLightWithActionsStyle)
                .setOnActionCallback(actionCallback)
                .build()

            setAdapter(adapter)

            // set custom scroll listener to enable swipe refresh layout only when at list top
            setOnScrollListener(object : AbsListView.OnScrollListener {
                var enableRefresh = false

                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) = Unit

                override fun onScroll(
                    view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int,
                    totalItemCount: Int
                ) {
                    enableRefresh = if (binding.list.childCount > 0) {
                        // check that the first item is visible and that its top matches the parent
                        binding.list.firstVisiblePosition == 0 && binding.list.getChildAt(0).top >= 0
                    } else {
                        false
                    }
                    binding.swipeLayout.isEnabled = enableRefresh
                }
            })
        }

        with(binding.swipeLayout) {
            setColorSchemeResources(R.color.twitter_blue, R.color.twitter_dark)
            // specify action to take on swipe refresh
            setOnRefreshListener {
                isRefreshing = true
                adapter.refresh(object : Callback<TimelineResult<Tweet>>() {

                    override fun success(result: Result<TimelineResult<Tweet>>) {
                        isRefreshing = false
                    }

                    override fun failure(exception: TwitterException) {
                        isRefreshing = false
                        val activity: Activity? = activityRef.get()
                        if (activity != null && !activity.isFinishing) {
                            Toast.makeText(
                                activity, exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        }
    }
}