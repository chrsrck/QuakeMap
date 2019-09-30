package com.chrsrck.quakemap.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.lifecycle.*
import com.chrsrck.quakemap.data.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkViewModel(application: Application) : AndroidViewModel(application) {


    private val dataSource : DataSourceUSGS
    private val connMgr : ConnectivityManager
    val eqLiveData = MutableLiveData<HashMap<String, Earthquake>>()

    init {
        dataSource = DataSourceUSGS()
        connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        eqLiveData.postValue(dataSource.fetchJSON())
    }

    private fun isOnline(): Boolean {
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }
}