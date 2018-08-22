package com.chrsrck.quakemap.ui

import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class EarthquakeMap(googleMap: GoogleMap) {

    val googleMap : GoogleMap

    init {
        this.googleMap = googleMap
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

    fun refreshQuakes(hashMap : HashMap<String, Earthquake>?) {
        hashMap?.values?.map { earthquake: Earthquake ->
            addEarthquake(earthquake)
        }
    }

    fun addEarthquake(earthquake : Earthquake) {
        val options = MarkerOptions()
        when(earthquake.magnitude) {
            in 0..2 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            in 2..5 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            in 5..8 -> options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
            else -> {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
        }

        val marker = googleMap.addMarker(options
                .title(earthquake.title)
                .position(LatLng(earthquake.latitude, earthquake.longitude)))
        marker.tag = earthquake // associates earthquake obj with that specific marker

    }
}