package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.databinding.Bindable
import android.databinding.Observable

class SettingsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val isNightMode : MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    init {
        isNightMode.value = false
    }

    fun toggleNightMode() {
        isNightMode.value = isNightMode.value?.not()
    }



}
