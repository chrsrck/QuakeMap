package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel;
import android.util.Log
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import org.json.JSONObject

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    //var darkMode: LiveData<Boolean>
//    val dataSource : DataSourceUSGS

    val heatMode: MutableLiveData<Boolean> = MutableLiveData()
//    val modeText: MutableLiveData<String> = MutableLiveData<String>()
//    val jsonData : MutableLiveData<JSONObject> = MutableLiveData()

    init {
        heatMode.value = false
//        dataSource = DataSourceUSGS()
    }

    fun heatMapToggled() {
        Log.d(this.javaClass.simpleName, "onDarkModeClicked")
        heatMode.value = (heatMode.value)?.not()
    }
}
