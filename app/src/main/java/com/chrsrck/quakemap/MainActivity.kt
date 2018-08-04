package com.chrsrck.quakemap

import android.app.Fragment
import android.app.ListFragment
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_main.*

// google maps libraries
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //private lateinit var mMap: GoogleMap

    val SYDNEY = LatLng(-33.862, 151.21)
    val ZOOM_LEVEL = 13f

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                //setUpListFragment()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setupMapFragment()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //mMap ?: googleMap
        // Add a marker in Sydney and move the camera
        googleMap ?: return
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(SYDNEY, ZOOM_LEVEL))
            addMarker(MarkerOptions().position(SYDNEY))
        }
    }

    private fun setupMapFragment() {
        if (isOnline()) {
            val mapFragment : SupportMapFragment? = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.container, mapFragment).commit()
            mapFragment?.getMapAsync(this)
        }
        else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_LONG).show()
        }
    }

    private fun isOnline(): Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }
}
