package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.chrsrck.quakemap.viewmodel.EarthquakeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlinx.coroutines.experimental.async
import java.io.InputStream
import java.util.*
import kotlin.collections.HashMap
import com.google.maps.android.data.geojson.GeoJsonLineStringStyle
import org.json.JSONException
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch


class EarthquakeMap(googleMap: GoogleMap,
                    resources: Resources, cameraPosition: CameraPosition,
                    vmEQ : EarthquakeViewModel,
                    data : HashMap<String, Earthquake>?,
                    context : Context?) {

    val googleMap : GoogleMap
    private val resources : Resources
    val vm : EarthquakeViewModel

    private var overlay : TileOverlay? = null
    private var markerList : List<Marker>? = null

    private var eqHashMap : HashMap<String, Earthquake>?

    init {
        vm = vmEQ

        this.googleMap = googleMap
        this.resources = resources
        eqHashMap = data

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        setMapStyle()
        loadPlateBoundaries(context)
    }

    val heatObserver : Observer<Boolean> = Observer { heatMode ->
        toggleMarkers(heatMode)
    }

    val quakeObserver : Observer<HashMap<String, Earthquake>> = Observer{ quakeHashMap ->

        eqHashMap = quakeHashMap // capture reference to new data
//        googleMap.clear()
        if (overlay == null)
            makeHeatMap()
        else {
            overlay?.remove()
            makeHeatMap()
        }

        if (markerList == null)
            makeMarkers()
        else {
            removeMarkers()
            makeMarkers()
        }

//        toggleMarkers(vm.heatMode?.value)
    }

    private fun makeMarkers() {
        markerList = eqHashMap?.values?.map { earthquake: Earthquake ->
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

        if (vm?.heatMode?.value!!) {
            marker.isVisible = false
        }
        else {
            marker.isVisible = true
        }

        return marker
    }

    private fun makeHeatMap() {
        val list : ArrayList<LatLng>? =
                eqHashMap?.values?.map { earthquake ->
                    LatLng(earthquake.latitude, earthquake.longitude)
                } as ArrayList<LatLng>?

        if (list != null) {
            val heatmapTileProvider = HeatmapTileProvider.Builder()
                    .opacity(0.5)
                    .radius(50)
                    .data(list)
                    .build()

            heatmapTileProvider.setData(list)
            val options: TileOverlayOptions = TileOverlayOptions().tileProvider(heatmapTileProvider)
            overlay = googleMap.addTileOverlay(options)

            if (vm.heatMode.value!!) {
                overlay?.isVisible = true
            }
            else {
                overlay?.isVisible = false
            }
        }
    }

    private fun removeMarkers() {
        markerList?.forEach { marker -> marker.remove() }
    }


    private fun toggleMarkers(isHeatMode : Boolean?) {
//        Log.d(TAG, "Toggled Markers")
        if (isHeatMode!!) {
            toggleMarkerVisibility(isVisible = false)
            overlay?.isVisible = true
        }
        else if (isHeatMode?.not()){
//            overlay?.remove()
            overlay?.isVisible = false
            toggleMarkerVisibility(isVisible = true)
        }
    }

    private fun toggleMarkerVisibility(isVisible: Boolean) {
        markerList?.forEach { marker -> marker.isVisible = isVisible }
    }

    private fun loadPlateBoundaries(context: Context?) {
//        val layer : KmlLayer = KmlLayer(googleMap, resources.openRawResource(R.raw.gov_plate_boundaries), context)
//        layer.addLayerToMap()

        launch (UI) {
            val plates_layer: GeoJsonLayer? = async (CommonPool) {
                try {
                    val layer = GeoJsonLayer(googleMap, R.raw.plates, context)
                    layer.defaultPolygonStyle.strokeWidth = 2f
                    layer.defaultPolygonStyle.strokeColor = Color.RED
                    return@async layer
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                return@async null
            }.await()

            plates_layer?.addLayerToMap()
        }
    }

    fun setMapStyle() {
        val mode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val styleId: Int
        when (mode) {
            Configuration.UI_MODE_NIGHT_YES -> styleId = R.raw.dark_mode_style
            Configuration.UI_MODE_NIGHT_NO -> styleId = R.raw.light_mode_style
            else -> {
                styleId = R.raw.standard_style
            }
        }

        launch (UI){
            val style = async (CommonPool){
                val stream : InputStream = resources.openRawResource(styleId)
                return@async Scanner(stream).useDelimiter("\\A").next()
            }.await()
            googleMap.setMapStyle(MapStyleOptions(style))
        }
//        val stream = resources.openRawResource(styleId)
//        val style = Scanner(stream).useDelimiter("\\A").next()
//        googleMap.setMapStyle(MapStyleOptions(style))
    }
}