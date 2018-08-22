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
    val dataSource : DataSourceUSGS

    val darkMode: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val modeText: MutableLiveData<String> = MutableLiveData<String>()
//    val jsonData : MutableLiveData<JSONObject> = MutableLiveData()

    var key : Double

    init {
        darkMode.value = false
        modeText.value = "Light mode"
        key = 0.0
        dataSource = DataSourceUSGS()
    }

    fun onDarkModeClick() {
        Log.d(this.javaClass.simpleName, "onDarkModeClicked")
        darkMode.value = (darkMode.value)?.not()
        modeText.value = if (darkMode.value!!) { "Dark mode" } else "Light mode"
    }
}
