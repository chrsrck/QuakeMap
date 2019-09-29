package com.chrsrck.quakemap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.viewModelScope
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkViewModel(application: Application) : AndroidViewModel(application) {

    private val dataSource : DataSourceUSGS
    val connMgr : ConnectivityManager
    val context : Context

    init {
        dataSource = DataSourceUSGS()
        context = application.applicationContext
        connMgr =
                application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getEarthquakeData() : HashMap<String, Earthquake>? {
        return dataSource.hashMap.value
    }

    fun fetchEarthquakeData(feedKey: String?) {
        dataSource.setFeed(feedKey)
        if (isOnline()) {
            viewModelScope.async {
                getData()
            }
        }
    }

    private suspend fun getData() = withContext(Dispatchers.Default) {
        dataSource.fetchJSON()
    }

    private fun isOnline(): Boolean {
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun observeEarthquakes(lifecycleOwner: LifecycleOwner, observer: Observer<HashMap<String, Earthquake>>) {
        dataSource.hashMap.observe(lifecycleOwner, observer)
    }
    

}