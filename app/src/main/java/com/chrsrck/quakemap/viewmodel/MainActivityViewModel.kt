package com.chrsrck.quakemap.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chrsrck.quakemap.data.EarthquakeRepository
import com.chrsrck.quakemap.data.local.EarthquakeRoomDatabase
import kotlinx.coroutines.launch

/*
TODO: For binding future ui features on home screen
 */
class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = EarthquakeRepository(EarthquakeRoomDatabase.getDatabase(application))
    val earthquakes = repo.earthquakes

    init {
        refreshEqData(application)
    }

    private fun refreshEqData(application: Application) {
        viewModelScope.launch {
            if (isOnline(application))
                repo.refresh()
        }
    }

    private fun isOnline(application: Application): Boolean {
        val connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}