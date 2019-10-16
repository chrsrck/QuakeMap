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
import com.chrsrck.quakemap.utilities.MapCameraPreferenceManager
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

    var camPos : CameraPosition
    private val sp : MapCameraPreferenceManager
    private var magStr : String

    init {
        sp = MapCameraPreferenceManager.getInstance(application)
        heatMode.value = sp.getIsHeatMode()
        camPos = sp.getCameraPosition()
        magStr = application.resources.getString(R.string.magnitude_snippet_title)
    }

    fun setupMapStyle(context: Context?) {
        if (context == null)
            return

        val mode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val styleId = when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> R.raw.dark_mode_style
            Configuration.UI_MODE_NIGHT_NO -> R.raw.light_mode_style
            else ->                         { R.raw.standard_style }
        }

        viewModelScope.async {
            styleLiveData.postValue(loadStyle(styleId, context))
        }
    }

    fun heatMapToggled() {
        heatMode.value = (heatMode.value)?.not()
    }

    private suspend fun loadStyle(styleId : Int, context: Context?) = withContext(Dispatchers.IO) {
        return@withContext MapStyleOptions.loadRawResourceStyle(context, styleId)
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
                        .title(magStr + ", " + eq.magnitude.toString())
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
        sp.saveCameraPosition(camPos)
        sp.saveHeatMode(isHeatMode())
        super.onCleared()
    }
}
