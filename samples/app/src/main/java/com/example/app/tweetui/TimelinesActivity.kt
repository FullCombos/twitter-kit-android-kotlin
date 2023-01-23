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

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.app.R
import com.example.app.databinding.ActivityPagerBinding

/**
 * TimelinesActivity pages between different timeline Fragments.
 */
class TimelinesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityPagerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = TimelinePagerAdapter(supportFragmentManager, resources)
        binding.pager.adapter = pagerAdapter
        binding.tabLayout.setupWithViewPager(binding.pager)
    }

    private class TimelinePagerAdapter(fm: FragmentManager, private val resources: Resources) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                PAGE_SEARCH -> SearchTimelineFragment.newInstance()
                PAGE_USER -> UserTimelineFragment.newInstance()
                PAGE_USER_RECYCLER_VIEW -> UserTimelineRecyclerViewFragment.newInstance()
                PAGE_COLLECTION -> CollectionTimelineFragment.newInstance()
                PAGE_LIST -> ListTimelineFragment.newInstance()
                else -> throw IllegalStateException("Unexpected Fragment page item requested.")
            }
        }

        override fun getCount(): Int {
            return PAGE_TITLE_RES_IDS.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                PAGE_SEARCH -> resources.getString(PAGE_TITLE_RES_IDS[PAGE_SEARCH])
                PAGE_USER -> resources.getString(PAGE_TITLE_RES_IDS[PAGE_USER])
                PAGE_USER_RECYCLER_VIEW -> resources.getString(
                    PAGE_TITLE_RES_IDS[PAGE_USER_RECYCLER_VIEW]
                )
                PAGE_COLLECTION -> resources.getString(PAGE_TITLE_RES_IDS[PAGE_COLLECTION])
                PAGE_LIST -> resources.getString(PAGE_TITLE_RES_IDS[PAGE_LIST])
                else -> throw IllegalStateException("Unexpected Fragment page title requested.")
            }
        }

        companion object {
            // titles for timeline fragments, in order
            private val PAGE_TITLE_RES_IDS = intArrayOf(
                R.string.search_timeline_title,
                R.string.user_timeline_title,
                R.string.user_recycler_view_timeline_title,
                R.string.collection_timeline_title,
                R.string.list_timeline_title
            )
        }
    }

    companion object {
        private const val PAGE_SEARCH = 0
        private const val PAGE_USER = 1
        private const val PAGE_USER_RECYCLER_VIEW = 2
        private const val PAGE_COLLECTION = 3
        private const val PAGE_LIST = 4
    }
}