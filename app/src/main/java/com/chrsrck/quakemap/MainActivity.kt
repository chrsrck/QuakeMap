package com.chrsrck.quakemap

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import com.chrsrck.quakemap.databinding.ActivityMainBinding
import com.chrsrck.quakemap.model.Earthquake
import com.chrsrck.quakemap.ui.EarthquakeListFragment
import com.chrsrck.quakemap.viewmodel.MainActivityViewModel
import com.chrsrck.quakemap.viewmodel.NetworkViewModel

class MainActivity : AppCompatActivity(), EarthquakeListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel : MainActivityViewModel
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var networkViewModel: NetworkViewModel
    val dataObserver : Observer<HashMap<String, Earthquake>> = Observer { it ->
        Toast.makeText(this, "Updated Earthquake Data", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences_settings, false)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        restoreTheme()
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        val binding : ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        val bottomNav = binding.bottomNavMenu as BottomNavigationView

        networkViewModel = ViewModelProviders.of(this).get(NetworkViewModel::class.java)
        networkViewModel.observeEarthquakes(this, dataObserver)

        restoreNetworkData()

        val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment

        NavigationUI.setupWithNavController(bottomNav, navHostFragment.navController)
        bottomNav.setOnNavigationItemSelectedListener({ item ->
                    item.onNavDestinationSelected(navHostFragment.navController)
                })

        navHostFragment.navController.addOnNavigatedListener(
                NavController.OnNavigatedListener(
                        fun (controller: NavController, dest : NavDestination) {

                        }
                ))
    }

    override fun onListFragmentInteraction(item: Earthquake?) {
//        Toast.makeText(this, "you clicked an item", Toast.LENGTH_SHORT).show()
    }

    private fun restoreTheme() {
        val modeDark =
                sharedPreferences.getBoolean(resources.getString(R.string.is_dark_key), false)

        if(modeDark) {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun restoreNetworkData() {
        val feed =
                sharedPreferences.getString(resources.getString(R.string.pref_key_feed),
                        resources.getString(R.string.key_sig_eq_feed))
        networkViewModel.fetchEarthquakeData(feed)

    }

}
