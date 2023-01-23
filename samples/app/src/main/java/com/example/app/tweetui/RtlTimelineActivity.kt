package com.example.app.tweetui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.ListFragment
import com.example.app.R
import com.twitter.sdk.android.tweetui.TweetTimelineListAdapter
import com.twitter.sdk.android.tweetui.UserTimeline
import java.util.*

class RtlTimelineActivity : TweetUiActivity() {

    private val deviceLocale = Locale.getDefault()
    private var customContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setTitle(R.string.rtl_timeline)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            findViewById<View>(android.R.id.content).layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }

    override val layout = R.layout.activity_frame

    override fun createFragment(): Fragment {
        return RtlTimelineFragment.newInstance()
    }

    override fun onResume() {
        super.onResume()
        setLocale(Locale("ar"))
    }

    override fun onPause() {
        super.onPause()
        setLocale(deviceLocale)
    }

    private fun setLocale(locale: Locale) {
        Locale.setDefault(locale)

        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
            customContext = createConfigurationContext(config)
        } else {
            config.locale = locale
            resources.updateConfiguration(config, resources.displayMetrics)
            customContext = null
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(customContext ?: newBase)
    }

    /**
     * Fragment showing a Timeline with a list of Rtl Tweets.
     */
    private class RtlTimelineFragment : ListFragment() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val userTimeline = UserTimeline.Builder()
                .screenName("DubaiAirportsAr")
                .build()
            val adapter: TweetTimelineListAdapter = TweetTimelineListAdapter.Builder(activity)
                .setTimeline(userTimeline)
                .build()

            listAdapter = adapter
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            return inflater.inflate(R.layout.tweetui_timeline, container, false)
        }

        companion object {

            fun newInstance(): RtlTimelineFragment {
                return RtlTimelineFragment()
            }
        }
    }
}