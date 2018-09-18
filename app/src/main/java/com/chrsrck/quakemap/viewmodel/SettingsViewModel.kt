package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable
import android.databinding.Observable
import android.support.v7.app.AppCompatDelegate

class SettingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val isDarkMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val modeObserver : Observer<Boolean>

    // AppCompatDelegate.getDefaultNodeMode is the system setting
    // delegate.setLocalNightMode actually resets the theme.
    init {
        isDarkMode.value =
                (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        modeObserver = Observer {
            setIsDarkMode(it!!)
        }
        isDarkMode.observeForever(modeObserver)
    }

    fun setIsDarkMode(value : Boolean) {
        if (value != isDarkMode.value) {
            isDarkMode.value = value
            if (isDarkMode?.value!!) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onCleared() {
        isDarkMode.removeObserver(modeObserver)
        super.onCleared()
    }
}
