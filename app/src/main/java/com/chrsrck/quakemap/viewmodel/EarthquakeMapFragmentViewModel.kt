package com.chrsrck.quakemap.viewmodel

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeMapFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val heatMode : MutableLiveData<Boolean> = MutableLiveData()
    var eqHashMap : HashMap<String, Earthquake>?

    val styleLiveData = MutableLiveData<MapStyleOptions>()
    val camPos : CameraPosition

    private val latKey = "latitude"
    private val longKey = "longitude"
    private val zoomKey = "zoom"
    private val tiltKey = "tilt"
    private val bearingKey = "bearing"
    private val heatModeKey = "heatMode"

    init {
        val sp = PreferenceManager.getDefaultSharedPreferences(application)

        val mode = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val styleId = when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.dark_mode_style
            Configuration.UI_MODE_NIGHT_NO -> R.raw.light_mode_style
            else ->                         { R.raw.standard_style }
        }

        viewModelScope.async {
            styleLiveData.postValue(loadStyle(styleId, application))
        }

        heatMode.value = sp.getBoolean(heatModeKey, false)
        val latitude  = sp.getFloat(latKey, 0.0f).toDouble()
        val longitude = sp.getFloat(longKey, 0.0f).toDouble()
        val zoom = sp.getFloat(zoomKey, 0f)
        val tilt = sp.getFloat(tiltKey, 0f)
        val bearing = sp.getFloat(bearingKey, 0f)
        camPos = CameraPosition(LatLng(latitude, longitude), zoom, tilt, bearing)
        eqHashMap = null
    }

    fun heatMapToggled() {
        heatMode.value = (heatMode.value)?.not()
    }

    private suspend fun loadStyle(styleId : Int, context: Context?) = withContext(Dispatchers.IO) {
        return@withContext MapStyleOptions.loadRawResourceStyle(context, styleId)
    }

    fun saveCameraPosToPreferences(pos : CameraPosition) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication())
        if (pos != null) {
            preferences.edit().putFloat(latKey, pos.target.latitude.toFloat()).apply()
            preferences.edit().putFloat(longKey,
                    pos.target.longitude.toFloat()).apply()
            preferences.edit().putFloat(bearingKey, pos.bearing).apply()
            preferences.edit().putFloat(zoomKey, pos.zoom).apply()
            preferences.edit().putFloat(tiltKey, pos.tilt).apply()
        }

        val saveHeat = heatMode.value ?: false
        preferences.edit().putBoolean(heatModeKey, saveHeat).apply()
    }

    override fun onCleared() {
        super.onCleared()
    }
}
