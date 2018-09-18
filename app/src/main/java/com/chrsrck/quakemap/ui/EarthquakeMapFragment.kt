package com.chrsrck.quakemap.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.res.Configuration
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeMapFragmentBinding
import com.chrsrck.quakemap.viewmodel.EarthquakeViewModel
import com.chrsrck.quakemap.viewmodel.MainActivityViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import android.graphics.drawable.Drawable
import android.databinding.BindingAdapter
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.widget.ImageView
import com.chrsrck.quakemap.MainActivity
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions


// binding adapter must be static method
// allows fab toggle
@BindingAdapter("app:srcCompat")
fun bindSrcCompat(imageView: ImageView, drawable: Drawable) {
    // Your setter code goes here, like setDrawable or similar
    imageView.setImageDrawable(drawable)
}

class EarthquakeMapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = EarthquakeMapFragment()
    }

    private lateinit var viewModel: EarthquakeViewModel
    private lateinit var activityViewModel : MainActivityViewModel
    private var mapView : MapView? = null
    private var quakeMap : EarthquakeMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.earthquake_map_fragment, container, false)
        // Creating the binding and inflating the layout
        // don't use DataBindingUtil since the layout binding is known in advance
        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        activityViewModel = ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)
//        viewModel.heatMode.value = (activity as MainActivity).sharedPreferences.getBoolean("heatMode", false)

        val binding: EarthquakeMapFragmentBinding =
                EarthquakeMapFragmentBinding.inflate(inflater)

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


        val preferences = (activity as MainActivity).sharedPreferences
        val latitude  = preferences.getFloat("latitude", 0.0f).toDouble()
        val longitude = preferences.getFloat("longitude", 0.0f).toDouble()
        val zoom = preferences.getFloat("zoom", 0f)
        val tilt = preferences.getFloat("tilt", 0f)
        val bearing = preferences.getFloat("bearing", 0f)
//        viewModel.heatMode.value = preferences.getBoolean("heatMode", false)

        val pos = CameraPosition(LatLng(latitude, longitude), zoom, tilt, bearing)
        quakeMap = EarthquakeMap(googleMap!!, activityViewModel.dataSource, resources,
                pos)

        activityViewModel.dataSource.hashMap.observe(frag, quakeMap?.quakeObserver!!)
        viewModel.heatMode.observe(frag, quakeMap?.heatObserver!!)
    }

    // Must call lifecycle methods on map view to prevent memory leaks
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        val preferences = (activity as MainActivity).sharedPreferences
        val camPos = quakeMap?.googleMap?.cameraPosition
        if (camPos != null) {
            preferences.edit().putFloat("latitude",
                    camPos?.target.latitude.toFloat()).apply()
            preferences.edit().putFloat("longitude",
                    camPos.target.longitude.toFloat()).apply()
            preferences.edit().putFloat("bearing", camPos.bearing).apply()
            preferences.edit().putFloat("zoom", camPos.zoom).apply()
            preferences.edit().putFloat("tilt", camPos.tilt).apply()
        }
        preferences.edit().putBoolean("heatMode", viewModel?.heatMode?.value!!).apply()

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
