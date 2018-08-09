package com.chrsrck.quakemap

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel;
import android.util.Log

class EarthquakeViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    //var darkMode: LiveData<Boolean>
    val darkMode: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    val modeText: MutableLiveData<String> = MutableLiveData<String>()

    init {
        darkMode.value = false
        modeText.value = "Light mode"
    }

    fun onDarkModeClick() {
        darkMode.value = (darkMode.value)?.not()
        Log.d(this.javaClass.simpleName, "onDarkModeClicked")
        modeText.value = if (darkMode.value!!) { "Dark mode" } else "Light mode"
    }

}
