package com.chrsrck.quakemap.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v4.app.Fragment
import android.widget.Toast
import com.chrsrck.quakemap.MainActivity
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake

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
            dataSource.fetchJSON()
        }
        else {
            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
        }
    }


    fun isOnline(): Boolean {
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    fun observeEarthquakes(lifecycleOwner: LifecycleOwner, observer: Observer<HashMap<String, Earthquake>>) {
        dataSource.hashMap.observe(lifecycleOwner, observer)
    }
    

}