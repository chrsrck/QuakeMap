package com.chrsrck.quakemap.ui


import android.app.Application
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.chrsrck.quakemap.ui.EarthquakeListFragment.OnListFragmentInteractionListener
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.earthquake_map_fragment.view.*
import kotlinx.android.synthetic.main.fragment_earthquake.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyEarthquakeRecyclerViewAdapter(
        private val mValues: List<Earthquake>,
        private val mListener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<MyEarthquakeRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val dateFormater : SimpleDateFormat

    init {
        mOnClickListener = View.OnClickListener { v ->
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
        }
        dateFormater = SimpleDateFormat("MMM-dd-yyyy h:mm:ss a z")
        dateFormater.timeZone = TimeZone.getDefault()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_earthquake, parent, false)
        return ViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        with(holder.mView) {
            holder.bindView(item)
            setOnClickListener(mOnClickListener)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        val mapHolder = holder
        if (mapHolder != null && mapHolder.googleMap != null) {
            mapHolder?.googleMap?.clear()
            mapHolder?.googleMap?.mapType = GoogleMap.MAP_TYPE_NONE
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View, val context: Context) : RecyclerView.ViewHolder(mView), OnMapReadyCallback {

        val layout : View
        val mapView : MapView
        var googleMap : GoogleMap?

        val titleText : TextView
        val magText : TextView
        val placeText : TextView
        val timeText : TextView
        val latText : TextView
        val longText : TextView
        val onMapClickListener : GoogleMap.OnMapClickListener = GoogleMap.OnMapClickListener {  }


        init {
            layout = mView
            mapView = layout.findViewById(R.id.map_card)
            titleText = mView.findViewById(R.id.title_text)
            magText = mView.mag_text
            placeText = mView.place_text
            timeText = mView.time_text
            latText = mView.lat_text
            longText = mView.long_text

            googleMap = null
            if (mapView != null) {
                mapView.onCreate(null) // forces the actual map to appear in a card
                mapView.getMapAsync(this) // gets the map bit map image
            }
        }

        fun bindView(item : Earthquake) {
            layout.tag = this
            mapView.tag = item
            titleText.text = item.id
            magText.text = item.magnitude.toString()
            placeText.text = item.place
            timeText.text = dateFormater.format(Date(item.time))
            latText.text = item.latitude.toString()
            longText.text = item.longitude.toString()

            configureMap(item)
        }

        override fun onMapReady(p0: GoogleMap?) {
            MapsInitializer.initialize(context)
            googleMap = p0
            val quake = mapView.tag as Earthquake
            if (quake != null && googleMap != null) {
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
