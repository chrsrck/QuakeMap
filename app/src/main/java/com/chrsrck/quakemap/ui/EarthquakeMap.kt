package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import android.content.res.Configuration
import android.content.res.Resources
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.io.InputStream
import java.util.*

class EarthquakeMap(googleMap: GoogleMap, dataSource: DataSourceUSGS,
                    resources: Resources, cameraPosition: LatLng) {

    val googleMap : GoogleMap
    private val dataSource : DataSourceUSGS
    private val resources : Resources
    val cameraPosition : LatLng

    private var overlay : TileOverlay? = null
    private var markerList : List<Marker>? = null

    init {
        this.googleMap = googleMap
        this.dataSource = dataSource
        this.resources = resources
        this.cameraPosition = cameraPosition
//        googleMap.setMapStyle(MapStyleOptions(R.raw.dark_mode_style.toString()))
        googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(cameraPosition, 2f))
        googleMap.uiSettings.isMapToolbarEnabled = false
        setMapStyle()
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

    fun setMapStyle() {
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        // TODO "[]" is standard style
        when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> {
                val stream : InputStream = resources.openRawResource(R.raw.dark_mode_style)
                val style = Scanner(stream).useDelimiter("\\A").next()
                googleMap.setMapStyle(MapStyleOptions(style))
            }
            Configuration.UI_MODE_NIGHT_NO ->
                googleMap.setMapStyle(MapStyleOptions("[]"))
            Configuration.UI_MODE_NIGHT_UNDEFINED ->
                googleMap.setMapStyle(MapStyleOptions("[]"))
        }
    }
}