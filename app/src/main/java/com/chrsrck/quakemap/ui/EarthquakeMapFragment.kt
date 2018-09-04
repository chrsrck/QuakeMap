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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //val view = inflater.inflate(R.layout.earthquake_map_fragment, container, false)
        // Creating the binding and inflating the layout
        // don't use DataBindingUtil since the layout binding is known in advance
        viewModel = ViewModelProviders.of(this).get(EarthquakeViewModel::class.java)
        activityViewModel = ViewModelProviders.of(activity!!).get(MainActivityViewModel::class.java)

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
        googleMap ?: return
        with(googleMap) {
//            moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(SYDNEY, ZOOM_LEVEL))
//            addMarker(com.google.android.gms.maps.model.MarkerOptions().position(SYDNEY))
//            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity as Context, R.raw.dark_mode_style))

            val quakeMap = EarthquakeMap(googleMap, activityViewModel.dataSource, resources)

            activityViewModel.dataSource.hashMap.observe(frag, quakeMap.quakeObserver)
            viewModel.heatMode.observe(frag, quakeMap.heatObserver)
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
