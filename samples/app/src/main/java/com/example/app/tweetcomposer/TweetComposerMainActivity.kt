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
package com.example.app.tweetcomposer

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.example.app.BaseActivity
import com.example.app.databinding.TweetcomposerActivityMainBinding
import com.twitter.sdk.android.core.TwitterCore
import com.twitter.sdk.android.tweetcomposer.ComposerActivity
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import java.net.MalformedURLException
import java.net.URL

class TweetComposerMainActivity : BaseActivity() {

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                launchComposer(it.data?.data)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = TweetcomposerActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(R.string.kit_tweetcomposer)

        binding.tweetComposer.setOnClickListener {
            try {
                TweetComposer.Builder(this@TweetComposerMainActivity)
                    .text("Tweet from TwitterKit!")
                    .url(URL("http://www.twitter.com"))
                    .show()
            } catch (e: MalformedURLException) {
                Log.e(TAG, "error creating tweet intent", e)
            }
        }
        binding.organicComposer.setOnClickListener { launchPicker() }
    }

    private fun launchPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = IMAGE_TYPES
        activityResultLauncher.launch(Intent.createChooser(intent, "Pick an Image"))
    }

    private fun launchComposer(uri: Uri?) {
        val session = TwitterCore.getInstance().sessionManager.activeSession
        val intent: Intent = ComposerActivity.Builder(this)
            .session(session)
            .image(uri)
            .text("Tweet from TwitterKit!")
            .hashtags("#twitter")
            .createIntent()
        startActivity(intent)
    }

    companion object {
        private const val TAG = "TweetComposer"
        private const val IMAGE_TYPES = "image/*"
    }
}