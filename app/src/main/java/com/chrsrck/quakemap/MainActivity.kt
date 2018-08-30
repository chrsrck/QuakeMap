package com.chrsrck.quakemap

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
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

class MainActivity : AppCompatActivity(), EarthquakeListFragment.OnListFragmentInteractionListener {

    private lateinit var viewModel : MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES); //enables night mode

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)
        val binding : ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        val bottomNav = binding.bottomNavMenu as BottomNavigationView

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
        Toast.makeText(this, "you clicked an item", Toast.LENGTH_SHORT).show()
    }

}
