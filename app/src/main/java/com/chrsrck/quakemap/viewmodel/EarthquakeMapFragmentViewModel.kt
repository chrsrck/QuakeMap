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
import com.google.android.gms.maps.model.*
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

// Shared view model between MapFragment and List Fragment
// See google documentation for canonical shared view models that present the same data
// https://developer.android.com/topic/libraries/architecture/viewmodel#lifecycle
class EarthquakeMapFragmentViewModel(application: Application) : AndroidViewModel(application) {

    val heatMode : MutableLiveData<Boolean> = MutableLiveData()
    var eqHashMap : HashMap<String, Earthquake>? = null
        set(value) {
            configureMap(value)
        }


    val styleLiveData = MutableLiveData<MapStyleOptions>()
    val markerOptionsLiveData = MutableLiveData<List<MarkerOptions>>()
    val overlayOptionsLiveData = MutableLiveData<TileOverlayOptions>()


    val camPos : CameraPosition

    private val latKey = "latitude"
    private val longKey = "longitude"
    private val zoomKey = "zoom"
    private val tiltKey = "tilt"
    private val bearingKey = "bearing"
    private val heatModeKey = "heatMode"

    private var magStr : String

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
        magStr = application.resources.getString(R.string.magnitude_snippet_title)
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


    private fun configureMap(hashMap : HashMap<String, Earthquake>?) {
        if (hashMap == null)
            return

        viewModelScope.async(Dispatchers.Default) {
            val l = hashMap.map {
                val eq = it.value
                val options = MarkerOptions()
                when (eq.magnitude) {
                    in 0.0..2.0 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    in 2.0..5.0 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    in 5.0..8.0 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    else -> {
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    }
                }
                options
                        .title(magStr + eq.magnitude.toString())
                        .snippet(eq.place)
                        .position(LatLng(eq.latitude, eq.longitude))
                return@map options
            }
            markerOptionsLiveData.postValue(l)
        }

        viewModelScope.async(Dispatchers.Default) {
            val tp = HeatmapTileProvider.Builder()
                    .opacity(0.5)
                    .radius(50)
                    .data(hashMap.values.map { LatLng(it.latitude, it.longitude) })
                    .build()

            overlayOptionsLiveData.postValue(TileOverlayOptions().tileProvider(tp))
        }
    }

    fun isHeatMode() : Boolean {
        return heatMode.value ?: true
    }

    override fun onCleared() {
        super.onCleared()
    }
}
