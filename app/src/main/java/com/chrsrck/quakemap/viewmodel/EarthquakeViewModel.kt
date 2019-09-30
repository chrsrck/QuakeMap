package com.chrsrck.quakemap.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeViewModel(application: Application) : AndroidViewModel(application) {

    val heatMode : MutableLiveData<Boolean> = MutableLiveData()
    var eqHashMap : HashMap<String, Earthquake>?

    val styleLiveData = MutableLiveData<MapStyleOptions>()

    init {
        heatMode.value = false
        eqHashMap = null

        val mode = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val styleId = when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.dark_mode_style
            Configuration.UI_MODE_NIGHT_NO -> R.raw.light_mode_style
            else ->                         { R.raw.standard_style }
        }

        viewModelScope.async {
            styleLiveData.postValue(loadStyle(styleId, application))
        }

    }

    fun heatMapToggled() {
        heatMode.value = (heatMode.value)?.not()
    }

    private suspend fun loadStyle(styleId : Int, context: Context?) = withContext(Dispatchers.IO) {
        return@withContext MapStyleOptions.loadRawResourceStyle(context, styleId)
    }
}
