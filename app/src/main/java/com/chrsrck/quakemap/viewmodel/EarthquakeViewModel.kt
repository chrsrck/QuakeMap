package com.chrsrck.quakemap.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeViewModel : ViewModel() {

    val heatMode: MutableLiveData<Boolean> = MutableLiveData()

    init {
        heatMode.value = false
    }

    fun heatMapToggled() {
        heatMode.value = (heatMode.value)?.not()
    }
}
