package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable
import android.databinding.Observable

class SettingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val isDarkMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        isDarkMode.value = false
    }

}
