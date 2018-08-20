package com.chrsrck.quakemap.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.viewmodel.EarthquakeViewModel
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeMapFragmentBinding
import com.google.android.gms.maps.model.LatLng
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPointStyle
import org.json.JSONObject


class EarthquakeMapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = EarthquakeMapFragment()
    }

    private lateinit var viewModel: EarthquakeViewModel
    private var mapView : MapView? = null

    val SYDNEY = LatLng(-33.862, 151.21)
    val ZOOM_LEVEL = 4f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.earthquake_map_fragment, container, false)
        // Creating the binding and inflating the layout
        // don't use DataBindingUtil since the layout binding is known in advance
        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        val binding: EarthquakeMapFragmentBinding =
                EarthquakeMapFragmentBinding.inflate(inflater, container, false)
        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)
        val view = binding.root


        mapView = view.findViewById(R.id.mapView) as MapView
        mapView?.onCreate(savedInstanceState) // lifecycle method for memory leak prevention
        mapView?.getMapAsync(this)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val frag : EarthquakeMapFragment = this
        googleMap ?: return
        with(googleMap) {
//            moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(SYDNEY, ZOOM_LEVEL))
//            addMarker(com.google.android.gms.maps.model.MarkerOptions().position(SYDNEY))
            googleMap.uiSettings.isMapToolbarEnabled = false
            
            val sourceObserver = Observer<JSONObject> { data ->
                val layer = GeoJsonLayer(googleMap, data)
                layer.defaultPointStyle.isDraggable = false
                layer.features.forEach {
                    it.pointStyle = GeoJsonPointStyle()
                    it.pointStyle.title =
                            if (it.hasProperty("title")) {
                                it.getProperty("title")
                            }
                            else {
                                "Place not found"
                            }

                }
                layer.addLayerToMap()
            }


            val mapObserver = Observer<Boolean> { darkMode ->
                // Update the UI, in this case, a TextView.
                if (darkMode!!) {
                    googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.dark_mode_style))
                }
                else {
                    googleMap.setMapStyle(MapStyleOptions("[]")) // "[]" is standard style
                }
            }

            val quakeLiveDataObserver = Observer<HashMap<Double, Earthquake>> { quakeHashMap ->
                //googleMap.clear()
                quakeHashMap?.values?.map { earthquake: Earthquake ->
                    googleMap.addMarker(MarkerOptions()
                            .title(earthquake.place)
                            .position(LatLng(earthquake.latitude, earthquake.longitude)))
                }

                if (quakeHashMap!!.size > 0) {
                    val newEQ = quakeHashMap!!.get(viewModel.key)
                    moveCamera(CameraUpdateFactory
                            .newLatLngZoom(LatLng(newEQ!!
                                    .latitude, newEQ!!
                                    .longitude), ZOOM_LEVEL))
                }
            }
            viewModel.darkMode.observe(frag, mapObserver)
            viewModel.quakeHashMapLiveData.observe(frag, quakeLiveDataObserver)
            viewModel.dataSource.jsonObject.observe(frag, sourceObserver)
        }
    }

    // Must call lifecycle methods on map view to prevent memory leaks
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}
