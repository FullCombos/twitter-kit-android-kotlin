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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.app.R
import com.example.app.databinding.TweetuiFragmentTweetPreviewBinding
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.models.Tweet
import com.twitter.sdk.android.tweetui.CompactTweetView
import com.twitter.sdk.android.tweetui.TweetUtils
import com.twitter.sdk.android.tweetui.TweetView

class TweetPreviewActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.preview_tweet)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return TweetPreviewFragment.newInstance()
    }

    /**
     * Fragment showing a Tweet id input field, light/dark buttons, and a scrollable region which
     * renders light/dark previews of the requested Tweet for quick manual validation.
     */
    private class TweetPreviewFragment : Fragment() {

        private var _binding: TweetuiFragmentTweetPreviewBinding? = null
        private val binding get() = _binding!!

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            _binding = TweetuiFragmentTweetPreviewBinding.inflate(layoutInflater)

            val tweetRegion = binding.tweetRegion
            val selectorInput = binding.selectorInputTweetId
            binding.buttonShowLight.setOnClickListener {
                val tweetId = selectorInput.text.toString().toLong()
                tweetRegion.removeAllViews()
                loadTweet(tweetId, tweetRegion, R.style.tw__TweetLightStyle)
            }
            binding.buttonShowDark.setOnClickListener {
                val tweetId = selectorInput.text.toString().toLong()
                tweetRegion.removeAllViews()
                loadTweet(tweetId, tweetRegion, R.style.tw__TweetDarkStyle)
            }
            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }

        /**
         * loadTweet wraps TweetUtils.loadTweet with a callback that ensures a compact and default
         * view with the correct style and spacing are inserted.
         */
        private fun loadTweet(tweetId: Long, container: ViewGroup, style: Int) {
            val singleTweetCallback: Callback<Tweet> = object : Callback<Tweet>() {

                override fun success(result: Result<Tweet>) {
                    val context = activity ?: return
                    val tweet = result.data
                    val cv = CompactTweetView(context, tweet, style)
                    container.addView(cv)
                    val spacer = View(context)
                    val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                        0,
                        context.resources.getDimension(R.dimen.demo_tweet_space).toInt()
                    )
                    spacer.layoutParams = params
                    container.addView(spacer)
                    val tv = TweetView(context, tweet, style)
                    container.addView(tv)
                }

                override fun failure(exception: TwitterException) {
                    val activity = activity
                    if (activity != null && !activity.isFinishing) {
                        Toast.makeText(
                            activity, R.string.tweet_load_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    Log.e(TAG, "loadTweet failure", exception)
                }
            }
            TweetUtils.loadTweet(tweetId, singleTweetCallback)
        }

        companion object {

            fun newInstance(): TweetPreviewFragment {
                return TweetPreviewFragment()
            }
        }
    }

    companion object {
        private const val TAG = "TweetPreviewActivity"
    }
}