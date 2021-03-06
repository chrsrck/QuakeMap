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
import androidx.lifecycle.Observer
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeMapFragmentBinding
import com.chrsrck.quakemap.viewmodel.EarthquakeMapFragmentViewModel
import com.chrsrck.quakemap.viewmodel.NetworkViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.TileOverlay


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
    private var googleMap : GoogleMap? = null
    private var heatMapOverlay : TileOverlay? = null
    private var markers : MutableList<Marker>? = null

    private val heatObs = Observer<Boolean> { isHeatMode ->
        heatMapOverlay?.isVisible = isHeatMode
        markers?.forEach { it.isVisible = !isHeatMode }
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
        if (googleMap == null) return

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.camPos))
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.setOnCameraIdleListener {
            viewModel.camPos = googleMap.cameraPosition
        }

        networkViewModel.eqLiveData.observe(this, Observer {
            viewModel.eqHashMap = it
        })

        viewModel.styleLiveData.observe(this,  Observer {
            googleMap?.setMapStyle(it)
        })

        viewModel.markerOptionsLiveData.observe(this, Observer {

            if (markers != null) {
                markers?.forEach { it.remove()}
                markers?.clear()
            }

            markers = it.map { option ->
                val marker = googleMap.addMarker(option)
                marker.isVisible = !viewModel.isHeatMode()
                return@map marker
            }.toMutableList()
        })

        viewModel.overlayOptionsLiveData.observe(this, Observer {

            if (heatMapOverlay != null)
                heatMapOverlay?.remove()

            heatMapOverlay = googleMap.addTileOverlay(it)
            heatMapOverlay?.isVisible = viewModel.isHeatMode()
        })

        viewModel.heatMode.observe(this, heatObs)

        this.googleMap = googleMap
    }

    /*
    For restoration when system dark theme changed with
    app in background
     */
    override fun onStart() {
        viewModel.setUpMapStyle(this.context)
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    /*
    Calls shared preference code here since the navigation component
    does not have the onDestroy / viewmodel
    onCleared() lifecycle methods work properly. This is with or without
    the bottom nav extensions with multiple nav graphs.
    https://github.com/android/architecture-components-samples/blob/master/NavigationAdvancedSample/app/src/main/java/com/example/android/navigationadvancedsample/NavigationExtensions.kt
     */
    override fun onStop() {
        viewModel.saveMapSetUp()
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
