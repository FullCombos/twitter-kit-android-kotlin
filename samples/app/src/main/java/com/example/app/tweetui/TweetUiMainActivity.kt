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

import android.content.Intent
import android.os.Bundle
import com.example.app.BaseActivity
import com.example.app.R
import com.example.app.databinding.TweetuiActivityMainBinding

class TweetUiMainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = TweetuiActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(R.string.kit_tweetui)

        binding.buttonXmlTweetActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    XmlTweetActivity::class.java
                )
            )
        }
        binding.buttonTweetActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    TweetActivity::class.java
                )
            )
        }
        binding.buttonUniqueTweetActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    UniqueTweetActivity::class.java
                )
            )
        }
        binding.buttonFixedTimelineActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    FixedTimelineActivity::class.java
                )
            )
        }
        binding.buttonRefreshTimelineActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    TimelineActivity::class.java
                )
            )
        }
        binding.buttonTimelinesActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    TimelinesActivity::class.java
                )
            )
        }
        binding.buttonTweetPreviewActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    TweetPreviewActivity::class.java
                )
            )
        }
        binding.buttonTweetPojoActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    TweetPojoActivity::class.java
                )
            )
        }
        binding.buttonRtlTimelineActivity.setOnClickListener {
            startActivity(
                Intent(
                    this@TweetUiMainActivity,
                    RtlTimelineActivity::class.java
                )
            )
        }
    }
}