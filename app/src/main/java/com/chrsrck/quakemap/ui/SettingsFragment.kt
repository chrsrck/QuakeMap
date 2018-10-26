package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.viewmodel.SettingsViewModel


class SettingsFragment : PreferenceFragmentCompat() {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private val prefListner : OnSharedPreferenceChangeListener
            = OnSharedPreferenceChangeListener() { prefs, key -> prefChanged(prefs, key) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences_settings)
    }


    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(activity)
                .registerOnSharedPreferenceChangeListener(prefListner)
    }
    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(activity)
                .unregisterOnSharedPreferenceChangeListener(prefListner)
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
                activity?.recreate()
            }
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
//        addPreferencesFromResource(R.xml.preferences_settings)
    }

}
