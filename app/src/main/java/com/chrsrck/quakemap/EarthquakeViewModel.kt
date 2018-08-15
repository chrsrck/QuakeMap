package com.chrsrck.quakemap

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log

class EarthquakeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    //var darkMode: LiveData<Boolean>
    var quakeHashMap: HashMap<Double, Earthquake> = HashMap()
    val quakeHashMapLiveData : MutableLiveData<HashMap<Double, Earthquake>> = MutableLiveData()

    val darkMode: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val modeText: MutableLiveData<String> = MutableLiveData<String>()

    var key : Double

    init {
        darkMode.value = false
        modeText.value = "Light mode"
        quakeHashMapLiveData.value = quakeHashMap
        key = 0.0
    }

    fun onDarkModeClick() {
        Log.d(this.javaClass.simpleName, "onDarkModeClicked")
        darkMode.value = (darkMode.value)?.not()
        modeText.value = if (darkMode.value!!) { "Dark mode" } else "Light mode"
        addEarthquake()
    }

    /*
    LiveData uses a version counter to see if the data changes.
    Adding an item to the list doesn't cause the observer to activate
    since the underlying data structure object is the same.
    setValue(T t) causes the version counter to update. To have
    the observer activate after putting in a new element into
    the HashMap, you need to increment the LiveData's version
    counter by reassigning the LiveData's HashMap to the existing
    HashMap
     */
    private fun addEarthquake() {
        Log.d(this.javaClass.simpleName, "addEarthquake called")
        key++
        quakeHashMap.put(key, Earthquake("Hello World", key, key))
        quakeHashMapLiveData.value = quakeHashMap
    }
}
