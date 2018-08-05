package com.chrsrck.quakemap

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.chrsrck.quakemap.dummy.DummyContent
import com.google.android.gms.maps.*
import kotlinx.android.synthetic.main.activity_main.*

// google maps libraries
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback, EarthquakeListFragment.OnListFragmentInteractionListener {

    //private lateinit var mMap: GoogleMap

    val SYDNEY = LatLng(-33.862, 151.21)
    val ZOOM_LEVEL = 13f


//    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        when (item.itemId) {
//            R.id.navigation_home -> {
//
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_dashboard -> {
//                //setUpListFragment()
//                return@OnNavigationItemSelectedListener true
//            }
//            R.id.navigation_notifications -> {
//                return@OnNavigationItemSelectedListener true
//            }
//        }
//        false
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bottom_nav_menu.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        findViewById<BottomNavigationView>(R.id.bottom_nav_menu)?.let { bottomNavView ->
            NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
        }
        //setupMapFragment()
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

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
