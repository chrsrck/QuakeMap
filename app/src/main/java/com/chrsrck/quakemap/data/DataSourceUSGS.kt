package com.chrsrck.quakemap.data

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.support.v4.app.Fragment
import android.util.Log
import com.chrsrck.quakemap.model.Earthquake
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class DataSourceUSGS {

    private val client : OkHttpClient
    private val TAG : String = this.javaClass.simpleName
    val MAG_SIGNIFICANT_MONTH_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson"
//    val ALL_PAST_HOUR = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"
    /*
    LiveData uses a version counter to see if the data changes.
    Adding an item to the list doesn't cause the observer to activate
    since the underlying data structure object is the same.
    setValue(T t) causes the version counter to update. To have
    the observer activate after putting in a new element into
    the HashMap, you need to increment the LiveData's version
    counter by reassigning the LiveData's HashMap to the existing
    HashMap
     */
    private val hashMap : MutableLiveData<HashMap<String, Earthquake>> = MutableLiveData()
    private val parser : jsonParserUSGS

    init {
        client = OkHttpClient()
        parser = jsonParserUSGS()
        fetchJSON()
    }

    fun fetchJSON() = launch(UI) {
        hashMap.value = async(CommonPool) {
            val request: Request = Request.Builder().url(MAG_SIGNIFICANT_MONTH_URL).build()
            val response: Response = client.newCall(request).execute()
            val json : JSONObject = if (response.isSuccessful) {
                    JSONObject(response.body()?.string())
                } else {
                    JSONObject("")
                }
            return@async parser.parseQuakes(json)
        }.await()
        Log.d(TAG,"Finished the coroutine")
    }

    fun observeEarthquakes(frag : Fragment, observer: Observer<HashMap<String, Earthquake>>) {
        hashMap.observe(frag, observer)
    }

    fun provideEarthquakeList() : ArrayList<Earthquake> {
        var list =  hashMap?.value?.values?.map {
            earthquake -> earthquake
        } as ArrayList<Earthquake>
//        val list = ArrayList<Earthquake>()
//        list.add(Earthquake("test", 1.0, "Nowhere",
//                0, "earthquake", 0.0, 0.0))
        return list
    }
}