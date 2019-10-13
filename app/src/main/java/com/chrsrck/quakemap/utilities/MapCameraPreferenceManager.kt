package com.chrsrck.quakemap.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

class MapCameraPreferenceManager private constructor(
        private var sp : SharedPreferences
) {
    companion object {
        private var instance : MapCameraPreferenceManager? = null
        private val latKey = "latitude"
        private val longKey = "longitude"
        private val zoomKey = "zoom"
        private val tiltKey = "tilt"
        private val bearingKey = "bearing"
        private val heatModeKey = "heatMode"
        fun getInstance(context: Context?) : MapCameraPreferenceManager {
            return instance?: MapCameraPreferenceManager(PreferenceManager.getDefaultSharedPreferences(context))
        }
    }

    fun getCameraPosition() : CameraPosition {
        if (sp == null)
            return CameraPosition(LatLng(0.0, 0.0), 0f, 0f, 0f)

        val latitude  = sp.getFloat(latKey, 0.0f).toDouble()
        val longitude = sp.getFloat(longKey, 0.0f).toDouble()
        val zoom = sp.getFloat(zoomKey, 0f)
        val tilt = sp.getFloat(tiltKey, 0f)
        val bearing = sp.getFloat(bearingKey, 0f)


        return CameraPosition(LatLng(latitude, longitude), zoom, tilt, bearing)
    }

    fun getIsHeatMode() : Boolean {
        return sp.getBoolean(heatModeKey, false)
    }

    fun saveCameraPosition(pos : CameraPosition) {
        if (pos == null)
            return

        if (pos != null) {
            sp.edit().putFloat(latKey, pos.target.latitude.toFloat()).apply()
            sp.edit().putFloat(longKey,
                    pos.target.longitude.toFloat()).apply()
            sp.edit().putFloat(bearingKey, pos.bearing).apply()
            sp.edit().putFloat(zoomKey, pos.zoom).apply()
            sp.edit().putFloat(tiltKey, pos.tilt).apply()
        }
    }

    fun saveHeatMode(isHeatMode: Boolean) {
        sp.edit().putBoolean(heatModeKey, isHeatMode)?.apply()
    }
}