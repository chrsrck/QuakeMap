package com.chrsrck.quakemap.ui

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeMapFragmentBinding
import com.chrsrck.quakemap.viewmodel.EarthquakeMapFragmentViewModel
import com.chrsrck.quakemap.viewmodel.NetworkViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng


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

    private lateinit var viewModel: EarthquakeMapFragmentViewModel
    private lateinit var networkViewModel : NetworkViewModel
    private var mapView : MapView? = null
//    private var quakeMap : EarthquakeMap? = null

    private val heatObs = Observer<Boolean> {
        
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Creating the binding and inflating the layout
        // don't use DataBindingUtil since the layout binding is known in advance
        viewModel = ViewModelProviders.of(this).get(EarthquakeMapFragmentViewModel::class.java)
        val binding: EarthquakeMapFragmentBinding =
                EarthquakeMapFragmentBinding.inflate(inflater)

        binding.viewmodel = viewModel
        binding.setLifecycleOwner(this)
        val view = binding.root

        networkViewModel =
                ViewModelProviders.of((activity as MainActivity)).get(NetworkViewModel::class.java)

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView?.onCreate(savedInstanceState) // lifecycle method for memory leak prevention
        mapView?.getMapAsync(this)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        networkViewModel.eqLiveData.observe(this, Observer {
            viewModel.eqHashMap = it
        })

        viewModel.styleLiveData.observe(this,  Observer {
            googleMap?.setMapStyle(it)
        })

        viewModel.heatMode.observe(this, heatObs)
    }

    // Must call lifecycle methods on map view to prevent memory leaks
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
//        val preferences = (activity as MainActivity).sharedPreferences
//        val camPos = quakeMap?.googleMap?.cameraPosition
//        if (camPos != null) {
//            preferences.edit().putFloat("latitude",
//                    camPos.target.latitude.toFloat()).apply()
//            preferences.edit().putFloat("longitude",
//                    camPos.target.longitude.toFloat()).apply()
//            preferences.edit().putFloat("bearing", camPos.bearing).apply()
//            preferences.edit().putFloat("zoom", camPos.zoom).apply()
//            preferences.edit().putFloat("tilt", camPos.tilt).apply()
//        }
//        preferences.edit().putBoolean("heatMode", viewModel.heatMode.value as Boolean).apply()

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
