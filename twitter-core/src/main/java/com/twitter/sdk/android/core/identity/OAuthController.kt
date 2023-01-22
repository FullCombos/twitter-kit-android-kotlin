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
package com.twitter.sdk.android.core.identity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import com.twitter.sdk.android.core.*
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse

internal class OAuthController(
    private val spinner: ProgressBar,
    private val webView: WebView,
    private val authConfig: TwitterAuthConfig?,
    private val oAuth1aService: OAuth1aService,
    val listener: Listener
) : OAuthWebViewClient.Listener {

    interface Listener {
        fun onComplete(resultCode: Int, data: Intent?)
    }

    private var requestToken: TwitterAuthToken? = null

    fun startAuth() {
        // Step 1. Obtain a request token to start the sign in flow.
        Twitter.getLogger().d(TwitterCore.TAG, "Obtaining request token to start the sign in flow")
        oAuth1aService.requestTempToken(newRequestTempTokenCallback())
    }

    /**
     * Package private for testing.
     */
    private fun newRequestTempTokenCallback(): Callback<OAuthResponse> {
        return object : Callback<OAuthResponse>() {

            override fun success(result: Result<OAuthResponse>) {
                requestToken = result.data.authToken
                val authorizeUrl = oAuth1aService.getAuthorizeUrl(requestToken)
                // Step 2. Redirect user to web view to complete authorization flow.
                Twitter.getLogger().d(
                    TwitterCore.TAG,
                    "Redirecting user to web view to complete authorization flow"
                )
                setUpWebView(
                    webView,
                    OAuthWebViewClient(
                        oAuth1aService.buildCallbackUrl(authConfig), this@OAuthController
                    ),
                    authorizeUrl,
                    OAuthWebChromeClient()
                )
            }

            override fun failure(exception: TwitterException) {
                Twitter.getLogger().e(
                    TwitterCore.TAG,
                    "Failed to get request token", exception
                )
                // Create new exception that can be safely serialized since Retrofit errors may
                // throw a NotSerializableException.
                handleAuthError(
                    AuthHandler.RESULT_CODE_ERROR,
                    TwitterAuthException("Failed to get request token")
                )
            }
        }
    }

    fun handleAuthError(resultCode: Int, error: TwitterAuthException?) {
        val data = Intent()
        data.putExtra(AuthHandler.EXTRA_AUTH_ERROR, error)
        listener.onComplete(resultCode, data)
    }

    /**
     * Package private for testing.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView(
        webView: WebView,
        webViewClient: WebViewClient,
        url: String,
        webChromeClient: WebChromeClient
    ) {
        val webSettings = webView.settings
        webSettings.allowFileAccess = false
        webSettings.javaScriptEnabled = true
        webSettings.saveFormData = false
        webView.isVerticalScrollBarEnabled = false
        webView.isHorizontalScrollBarEnabled = false
        webView.webViewClient = webViewClient
        webView.loadUrl(url)
        webView.visibility = View.INVISIBLE
        webView.webChromeClient = webChromeClient
    }

    private fun handleWebViewSuccess(bundle: Bundle?) {
        Twitter.getLogger().d(TwitterCore.TAG, "OAuth web view completed successfully")
        if (bundle != null) {
            val verifier = bundle.getString(OAuthConstants.PARAM_VERIFIER)
            if (verifier != null) {
                // Step 3. Convert the request token to an access token.
                Twitter.getLogger().d(
                    TwitterCore.TAG,
                    "Converting the request token to an access token."
                )
                oAuth1aService.requestAccessToken(
                    newRequestAccessTokenCallback(),
                    requestToken,
                    verifier
                )
                return
            }
        }

        // If we get here, we failed to complete authorization.
        Twitter.getLogger().e(
            TwitterCore.TAG,
            "Failed to get authorization, bundle incomplete $bundle", null
        )
        handleAuthError(
            AuthHandler.RESULT_CODE_ERROR,
            TwitterAuthException("Failed to get authorization, bundle incomplete")
        )
    }

    /**
     * Package private for testing.
     */
    private fun newRequestAccessTokenCallback(): Callback<OAuthResponse> {
        return object : Callback<OAuthResponse>() {

            override fun success(result: Result<OAuthResponse>) {
                val data = Intent()
                val response = result.data
                data.putExtra(AuthHandler.EXTRA_SCREEN_NAME, response.userName)
                data.putExtra(AuthHandler.EXTRA_USER_ID, response.userId)
                data.putExtra(AuthHandler.EXTRA_TOKEN, response.authToken.token)
                data.putExtra(
                    AuthHandler.EXTRA_TOKEN_SECRET,
                    response.authToken.secret
                )
                listener.onComplete(Activity.RESULT_OK, data)
            }

            override fun failure(exception: TwitterException) {
                Twitter.getLogger().e(TwitterCore.TAG, "Failed to get access token", exception)
                // Create new exception that can be safely serialized since Retrofit errors may
                // throw a NotSerializableException.
                handleAuthError(
                    AuthHandler.RESULT_CODE_ERROR,
                    TwitterAuthException("Failed to get access token")
                )
            }
        }
    }

    private fun handleWebViewError(error: WebViewException) {
        Twitter.getLogger().e(TwitterCore.TAG, "OAuth web view completed with an error", error)
        handleAuthError(
            AuthHandler.RESULT_CODE_ERROR,
            TwitterAuthException("OAuth web view completed with an error")
        )
    }

    private fun dismissWebView() {
        webView.stopLoading()
        dismissSpinner()
    }

    private fun dismissSpinner() {
        spinner.visibility = View.GONE
    }

    override fun onPageFinished(webView: WebView, url: String) {
        dismissSpinner()
        webView.visibility = View.VISIBLE
    }

    override fun onSuccess(bundle: Bundle?) {
        handleWebViewSuccess(bundle)
        dismissWebView()
    }

    override fun onError(exception: WebViewException) {
        handleWebViewError(exception)
        dismissWebView()
    }
}