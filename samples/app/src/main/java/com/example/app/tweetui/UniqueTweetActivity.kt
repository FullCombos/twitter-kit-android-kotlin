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
import com.example.app.R
import com.example.app.databinding.TweetuiFragmentUniqueTweetBinding
import com.twitter.sdk.android.core.models.TweetBuilder
import com.twitter.sdk.android.core.models.User
import com.twitter.sdk.android.core.models.UserBuilder
import com.twitter.sdk.android.tweetui.TweetView

class UniqueTweetActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.unqiue_tweets)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return UniqueTweetFragment.newInstance()
    }

    /**
     * Fragment showing unique Tweet view cases.
     */
    private class UniqueTweetFragment : Fragment() {

        private var _binding: TweetuiFragmentUniqueTweetBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = TweetuiFragmentUniqueTweetBinding.inflate(inflater)
            val tweetRegion = binding.tweetRegion

            // Tweet object already present, construct a TweetView
            val knownTweet = TweetBuilder()
                .setId(3L)
                .setUser(
                    UserBuilder()
                        .setId(User.INVALID_ID)
                        .setName("name")
                        .setScreenName("namename")
                        .setVerified(false)
                        .build()
                )
                .setText("Preloaded text of a Tweet that couldn't be loaded.")
                .setCreatedAt("Wed Jun 06 20:07:10 +0000 2012")
                .build()
            val knownTweetView = TweetView(activity, knownTweet)

            tweetRegion.addView(knownTweetView)
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        companion object {

            fun newInstance(): UniqueTweetFragment {
                return UniqueTweetFragment()
            }
        }
    }
}