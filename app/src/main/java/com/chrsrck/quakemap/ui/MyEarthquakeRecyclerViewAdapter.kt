package com.chrsrck.quakemap.ui


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.databinding.EarthquakeListViewholderBinding
import com.chrsrck.quakemap.model.Earthquake
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.earthquake_list_viewholder.view.*
import java.text.SimpleDateFormat
import java.util.*

class MyEarthquakeRecyclerViewAdapter(
        private val mValues: List<Earthquake>)
    : androidx.recyclerview.widget.RecyclerView.Adapter<MyEarthquakeRecyclerViewAdapter.ViewHolder>() {

    private val dateFormater : SimpleDateFormat
    private lateinit var binding: EarthquakeListViewholderBinding

    init {
        dateFormater = SimpleDateFormat("MMM-dd-yyyy h:mm:ss a z", Locale.US)
        dateFormater.timeZone = TimeZone.getDefault()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = DataBindingUtil.inflate(inflater, R.layout.earthquake_list_viewholder, parent, false)
        return ViewHolder(binding.root, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        binding.eq = item
        binding.executePendingBindings()

        with(holder.mView) {
            holder.bindView(item)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        val mapHolder = holder
        if (mapHolder.googleMap != null) {
            mapHolder.googleMap?.clear()
            mapHolder.googleMap?.mapType = GoogleMap.MAP_TYPE_NONE
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View, val context: Context) : androidx.recyclerview.widget.RecyclerView.ViewHolder(mView), OnMapReadyCallback {

        val layout : View
        val mapView : MapView
        var googleMap : GoogleMap?

        val timeText : TextView
        val onMapClickListener : GoogleMap.OnMapClickListener = GoogleMap.OnMapClickListener {  }


        init {
            layout = mView
            mapView = layout.findViewById(R.id.map_card)
            timeText = mView.time_text

            googleMap = null
            if (mapView != null) {
                mapView.onCreate(null) // forces the actual map to appear in a card
                mapView.getMapAsync(this) // gets the map bit map image
            }
        }

        fun bindView(item : Earthquake) {
            layout.tag = this
            mapView.tag = item
            timeText.text = dateFormater.format(Date(item.time))
            configureMap(item)
        }

        override fun onMapReady(p0: GoogleMap?) {
            MapsInitializer.initialize(context)
            googleMap = p0
            val quake = mapView.tag as Earthquake
            if (googleMap != null) {
                configureMap(quake)
            }
        }

        private fun configureMap(quake : Earthquake) {
            val pos = LatLng(quake.latitude, quake.longitude)
            val opt = MarkerOptions().position(pos)
            if (googleMap?.mapType != GoogleMap.MAP_TYPE_TERRAIN) {
                googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            }
            googleMap?.addMarker(opt)
//            googleMap?.moveCamera(CameraUpdateFactory.newLatLng(pos))
            googleMap?.uiSettings?.isMapToolbarEnabled = false
            googleMap?.setOnMapClickListener(onMapClickListener)
        }
    }
}
