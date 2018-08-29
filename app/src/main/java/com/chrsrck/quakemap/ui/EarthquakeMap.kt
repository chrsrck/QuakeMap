package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

class EarthquakeMap(googleMap: GoogleMap, dataSource: DataSourceUSGS) {

    private val googleMap : GoogleMap
    private val dataSource : DataSourceUSGS

    private var overlay : TileOverlay? = null
    private var markerList : List<Marker>? = null

    init {
        this.googleMap = googleMap
        this.dataSource = dataSource
        googleMap.uiSettings.isMapToolbarEnabled = false
    }

    val heatObserver : Observer<Boolean> = Observer { heatMode ->
        toggleMarkers(heatMode)
    }

    val quakeObserver : Observer<HashMap<String, Earthquake>> = Observer{ quakeHashMap ->
        removeMarkers()
        makeMarkers()
    }

    private fun makeMarkers() {
//        googleMap.clear() // TODO refactor viewmodel from data source
        markerList = dataSource.hashMap.value?.values?.map { earthquake: Earthquake ->
            addEarthquake(earthquake)
        }
    }

    private fun addEarthquake(earthquake : Earthquake) : Marker {
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

        return marker
    }

    private fun makeHeatMap() {
        val list : ArrayList<LatLng> =
                dataSource.hashMap.value?.values?.map { earthquake ->
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

    private fun removeMarkers() {
        markerList?.forEach { marker -> marker.remove() }
    }


    private fun toggleMarkers(isHeatMode : Boolean?) {
        if (isHeatMode!!) {
            toggleMarkerVisibility(isVisible = false)
            makeHeatMap()
        }
        else if (isHeatMode?.not()){
            overlay?.remove()
            toggleMarkerVisibility(isVisible = true)
        }
    }

    private fun toggleMarkerVisibility(isVisible: Boolean) {
        markerList?.forEach { marker -> marker.isVisible = isVisible }
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