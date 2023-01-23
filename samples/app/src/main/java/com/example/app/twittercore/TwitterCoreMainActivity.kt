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
package com.example.app.twittercore

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.example.app.BaseActivity
import com.example.app.R
import com.example.app.databinding.TwittercoreActivityMainBinding
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.identity.TwitterAuthClient

class TwitterCoreMainActivity : BaseActivity() {

    private lateinit var binding: TwittercoreActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = TwittercoreActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setTitle(R.string.kit_twittercore)

        // Set up the login button by setting callback to invoke when authorization request
        // completes
        binding.loginButton.setCallback(object : Callback<TwitterSession>() {

            override fun success(result: Result<TwitterSession>) {
                requestEmailAddress(applicationContext, result.data)
            }

            override fun failure(exception: TwitterException) {
                // Upon error, show a toast message indicating that authorization request failed.
                Toast.makeText(this@TwitterCoreMainActivity, exception.message, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result to the saveSession button.
        binding.loginButton.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        /**
         * Constructs an intent for starting an instance of this activity.
         * @param packageContext A context from the same package as this activity.
         * @return Intent for starting an instance of this activity.
         */
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, TwitterCoreMainActivity::class.java)
        }

        private fun requestEmailAddress(context: Context, session: TwitterSession) {
            TwitterAuthClient().requestEmail(session, object : Callback<String>() {

                override fun success(result: Result<String>) {
                    Toast.makeText(context, result.data, Toast.LENGTH_SHORT).show()
                }

                override fun failure(exception: TwitterException) {
                    Toast.makeText(context, exception.message, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}