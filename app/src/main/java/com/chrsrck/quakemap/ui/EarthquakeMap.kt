package com.chrsrck.quakemap.ui

import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlin.collections.HashMap
import android.arch.lifecycle.Observer

class EarthquakeMap(googleMap: GoogleMap) {

    private val googleMap : GoogleMap
    private lateinit var overlay : TileOverlay
    private lateinit var eqHashMap: HashMap<String, Earthquake>

    val heatObserver : Observer<Boolean> = Observer { heatMode ->
        if (heatMode!!)
            makeHeatMap()
    }

    val quakeObserver : Observer<HashMap<String, Earthquake>> = Observer{ quakeHashMap ->
        eqHashMap = quakeHashMap!!
        refreshQuakes()
    }

    init {
        this.googleMap = googleMap
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

    fun refreshQuakes() {
        googleMap.clear() // TODO only remove map markers instead of everything
        eqHashMap?.values?.map { earthquake: Earthquake ->
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
                .title("Magnitude " + earthquake.magnitude.toString())
                .snippet(earthquake.place)
                .position(LatLng(earthquake.latitude, earthquake.longitude)))
        marker.tag = earthquake // associates earthquake obj with that specific marker

    }

    fun makeHeatMap() {
        val list : ArrayList<LatLng> =
                eqHashMap?.values?.map { earthquake ->
                    LatLng(earthquake.latitude, earthquake.longitude)
                } as ArrayList<LatLng>

        val heatmapTileProvider = HeatmapTileProvider.Builder()
                .opacity(0.5)
                .radius(50)
                .data(list)
                .build()

        heatmapTileProvider.setData(list)
        val options: TileOverlayOptions = TileOverlayOptions().tileProvider(heatmapTileProvider)
        overlay = googleMap.addTileOverlay(options)
    }

    fun removeHeatMap() {

    }

//    fun toggleDarkMode(isDark : Boolean) {
//        if (isDark) {
//            googleMap.setMapStyle(MapStyleOptions(R.raw.dark_mode_style.toString()))
//        }
//        else {
//            googleMap.setMapStyle(MapStyleOptions("[]")) // TODO "[]" is standard style
//        }
//    }
}