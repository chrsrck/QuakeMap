package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeViewModel : ViewModel() {
    // TODO: Implement the ViewModel

    val heatMode: MutableLiveData<Boolean> = MutableLiveData()

    init {
        heatMode.value = false
    }

    fun heatMapToggled() {
        Log.d(this.javaClass.simpleName, "onDarkModeClicked")
        heatMode.value = (heatMode.value)?.not()
    }
}
