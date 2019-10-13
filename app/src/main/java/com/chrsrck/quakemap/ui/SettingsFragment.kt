package com.chrsrck.quakemap.ui

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.viewmodel.NetworkViewModel


class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var networkViewModel : NetworkViewModel

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val prefListner : OnSharedPreferenceChangeListener
            = OnSharedPreferenceChangeListener() { prefs, key -> prefChanged(prefs, key) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_settings)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        networkViewModel = ViewModelProviders.of((activity as MainActivity)).get(NetworkViewModel::class.java)
        return view
    }


    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(activity)
                .registerOnSharedPreferenceChangeListener(prefListner)
    }
    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(activity)
                .unregisterOnSharedPreferenceChangeListener(prefListner)

        super.onPause()
    }

    private fun prefChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when(key) {
            resources.getString(R.string.is_dark_key) -> {
                val isDark = sharedPreferences?.getBoolean(key, false)

                if (isDark != null && isDark)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                sharedPreferences?.edit()?.putBoolean("key_theme_recreate", true)?.apply()
                activity?.setTheme(R.style.AppTheme)
                val act = (activity as MainActivity)
                val intent = Intent(act, act::class.java)
                intent.putExtra("fromSettings", true)
                startActivity(intent)
                activity?.finish()

            }
            resources.getString(R.string.pref_key_feed) -> {
                val feedKey = sharedPreferences?.getString(key, resources.getString(R.string.key_sig_eq_feed))
                networkViewModel.fetchEarthquakeData(feedKey)
            }


        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        addPreferencesFromResource(R.xml.preferences_settings)
    }

}
