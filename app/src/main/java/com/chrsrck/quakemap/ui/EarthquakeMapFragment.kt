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
import androidx.preference.PreferenceManager
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeMapFragmentBinding
import com.chrsrck.quakemap.viewmodel.EarthquakeMapFragmentViewModel
import com.chrsrck.quakemap.viewmodel.NetworkViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var googleMap : GoogleMap
    private var heatMapOverlay : TileOverlay? = null
    private var markers : List<Marker>? = null

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

        networkViewModel.eqLiveData.observe(this, Observer {
            viewModel.eqHashMap = it
        })

        viewModel.styleLiveData.observe(this,  Observer {
            googleMap?.setMapStyle(it)
        })

        viewModel.markerOptionsLiveData.observe(this, Observer {
            markers = it.map { option ->
                val marker = googleMap.addMarker(option)
                marker.isVisible = !viewModel.isHeatMode()
                return@map marker
            }
        })

        viewModel.overlayOptionsLiveData.observe(this, Observer {
            heatMapOverlay = googleMap.addTileOverlay(it)
            heatMapOverlay?.isVisible = viewModel.isHeatMode()
            heatMapOverlay?.fadeIn = true
        })

        viewModel.heatMode.observe(this, heatObs)


        this.googleMap = googleMap
    }

    // Must call lifecycle methods on map view to prevent memory leaks
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        viewModel.saveCameraPosToPreferences(googleMap.cameraPosition)
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
