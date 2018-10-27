package com.chrsrck.quakemap.ui


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.chrsrck.quakemap.R
import com.chrsrck.quakemap.model.Earthquake
import com.chrsrck.quakemap.ui.EarthquakeListFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_earthquake.view.*

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

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as Earthquake
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_earthquake, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.mIdView.text = item.id
        holder.magTextView.text = "Magnitude: " + item.magnitude
        holder.placeTextView.text = "Place: " + item.place
        holder.latTextView.text = "Latitude: " + item.latitude
        holder.longTextView.text = "Longitude: " + item.longitude
        holder.timeTextView.text = "Time: " + item.time
        holder.typeTextView.text = "Event type: " + item.type

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.id_eq_text
        val magTextView: TextView = mView.magText
        val placeTextView = mView.placeText
        val latTextView = mView.latText
        val longTextView = mView.longText
        val timeTextView = mView.timeText
        val typeTextView = mView.typeText
    }
}
