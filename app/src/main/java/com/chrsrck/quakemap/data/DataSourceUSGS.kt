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

    private var activeFeed : String
    private val MAG_SIGNIFICANT_MONTH_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson"
    val MAG_ALL_HOUR_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson"
    val MAG_2_HALF_DAY_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson"
    val MAG_4_HALF_WEEK_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_week.geojson"

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
    val hashMap : MutableLiveData<HashMap<String, Earthquake>> = MutableLiveData()
    private val parser : jsonParserUSGS

    init {
        activeFeed = MAG_SIGNIFICANT_MONTH_URL
        client = OkHttpClient()
        parser = jsonParserUSGS()
    }

    fun setFeed(key : String?) {
        when(key) {
            "all" -> activeFeed = MAG_ALL_HOUR_URL
            "2.5+" -> activeFeed = MAG_2_HALF_DAY_URL
            "4.5+" -> activeFeed = MAG_4_HALF_WEEK_URL
            else -> { activeFeed = MAG_SIGNIFICANT_MONTH_URL }
        }
    }

    fun fetchJSON() =
            launch(UI) { async(CommonPool) {
                val request: Request = Request.Builder().url(activeFeed).build()
                val response: Response = client.newCall(request).execute()
                val json : JSONObject = if (response.isSuccessful) {
                        JSONObject(response.body()?.string())
                    } else {
                        JSONObject("")
                    }
                val parseMap = parser.parseQuakes(json)
                hashMap.postValue(parseMap) }
            }
}