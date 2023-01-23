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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app.databinding.TweetuiTimelineRecyclerviewBinding
import com.example.app.twittercore.TwitterCoreMainActivity
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterAuthException
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter
import com.twitter.sdk.android.tweetui.UserTimeline

class UserTimelineRecyclerViewFragment : Fragment() {

    private var _binding: TweetuiTimelineRecyclerviewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TweetuiTimelineRecyclerviewBinding.inflate(inflater)
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        val userTimeline = UserTimeline.Builder()
            .screenName("nasa")
            .build()

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
        val adapter = TweetTimelineRecyclerViewAdapter.Builder(context)
            .setTimeline(userTimeline)
            .setViewStyle(com.twitter.sdk.android.tweetui.R.style.twitter_TweetLightWithActionsStyle)
            .setOnActionCallback(actionCallback)
            .build()
        binding.recyclerView.setAdapter(adapter)
        return binding.root
    }

    companion object {

        fun newInstance(): UserTimelineRecyclerViewFragment {
            return UserTimelineRecyclerViewFragment()
        }
    }
}