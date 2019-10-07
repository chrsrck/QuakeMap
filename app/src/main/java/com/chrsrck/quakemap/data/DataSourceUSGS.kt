package com.chrsrck.quakemap.data

import androidx.lifecycle.MutableLiveData
import com.chrsrck.quakemap.model.Earthquake
//import kotlinx.coroutines.experimental.CommonPool
//import kotlinx.coroutines.experimental.CoroutineExceptionHandler
//import kotlinx.coroutines.experimental.launch
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
//    val exceptionHandler : CoroutineExceptionHandler
//            = CoroutineExceptionHandler({_, e -> })

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
    private val parser : jsonParserUSGS

    init {
        activeFeed = MAG_SIGNIFICANT_MONTH_URL
        client = OkHttpClient()
        parser = jsonParserUSGS()
    }

    fun setFeed(key : String?) {
        activeFeed = when(key) {
            "all" -> MAG_ALL_HOUR_URL
            "2.5+" -> MAG_2_HALF_DAY_URL
            "4.5+" -> MAG_4_HALF_WEEK_URL
            else -> {
                MAG_SIGNIFICANT_MONTH_URL
            }
        }
    }

    fun fetchJSON() : HashMap<String, Earthquake> {
            val request: Request = Request.Builder().url(activeFeed).build()
            val response: Response = client.newCall(request).execute()
            val json: JSONObject = if (response.isSuccessful) {
                JSONObject(response.body()?.string())
            } else {
                JSONObject("{}")
            }
            return parser.parseQuakes(json)
    }
}