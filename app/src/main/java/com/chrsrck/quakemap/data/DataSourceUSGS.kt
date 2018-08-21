package com.chrsrck.quakemap.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject

class DataSourceUSGS {

    private val client : OkHttpClient
    private val TAG : String = this.javaClass.simpleName
    val MAG_SIGNIFICANT_MONTH_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson"
    val jsonObject : MutableLiveData<JSONObject> = MutableLiveData()

    init {
        client = OkHttpClient()
        jsonObject.value = JSONObject()
        fetchJSON()
    }

    fun fetchJSON() = launch(UI) {
        jsonObject.value = async(CommonPool) {
            val request: Request = Request.Builder().url(MAG_SIGNIFICANT_MONTH_URL).build()
            val response: Response = client.newCall(request).execute()
            val json : JSONObject = if (response.isSuccessful) {
                    JSONObject(response.body()?.string())
                } else {
                    JSONObject("")
                }
            return@async json
        }.await()
    }

//        Log.d(TAG,"Finished the coroutine")}
}