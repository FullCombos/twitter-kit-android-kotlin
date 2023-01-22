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

class XmlTweetActivity : TweetUiActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle(R.string.xml_tweets_activity)
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return XmlTweetFragment.newInstance()
    }

    /**
     * UI fragment showing XML Tweet views for automated ui testing
     */
    private class XmlTweetFragment : Fragment() {

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.tweetui_fragment_xml_tweet, container, false)
        }

        companion object {

            fun newInstance(): XmlTweetFragment {
                return XmlTweetFragment()
            }
        }
    }
}