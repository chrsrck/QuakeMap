package com.chrsrck.quakemap

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.chrsrck.quakemap.dummy.DummyContent
import com.chrsrck.quakemap.ui.EarthquakeListFragment

class MainActivity : AppCompatActivity(), EarthquakeListFragment.OnListFragmentInteractionListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment
        findViewById<BottomNavigationView>(R.id.bottom_nav_menu)?.let { bottomNavView ->
            NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
        }
    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        Toast.makeText(this, "you clicked an item", Toast.LENGTH_SHORT).show()
    }

}
