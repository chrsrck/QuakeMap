package com.chrsrck.quakemap.data.network

import com.chrsrck.quakemap.model.Earthquake
//import kotlinx.coroutines.experimental.CommonPool
//import kotlinx.coroutines.experimental.CoroutineExceptionHandler
//import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.converter.moshi.MoshiConverterFactory


interface ServiceUSGS {
//    @GET("all_hour.geojson")
//    suspend fun getEarthquakesHour(): List<Earthquake>
//
//    @GET("2.5_day.geojson")
//    suspend fun getEarthquakesHour(): List<Earthquake>
//
//    @GET("4.5_week.geojson")
//    suspend fun getEarthquakesHour(): List<Earthquake>

    @GET("significant_month.geojson")
    suspend fun getEarthquakesSig(): List<Earthquake>
}


// https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/
object DataSourceUSGS {


        val activeFeed = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson"
        val client = OkHttpClient()
        val parser = JsonParserUSGS()

    private val TAG : String = this.javaClass.simpleName


    private val retrofit = Retrofit.Builder()
            .baseUrl("https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

    val source = retrofit.create(ServiceUSGS::class.java)

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