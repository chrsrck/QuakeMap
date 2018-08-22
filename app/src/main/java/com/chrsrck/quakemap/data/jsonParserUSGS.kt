package com.chrsrck.quakemap.data

import com.chrsrck.quakemap.model.Earthquake
import org.json.JSONObject
import kotlin.collections.HashMap


/*
Based on https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php
 */
class jsonParserUSGS() {

    // specified in top level json
    private val features_key = "features"
    private val ID_key = "id"
    private val properties_key = "properties"
    private val geometry_key = "geometry"
    private val coordinates_key = "coordinates"

    /* Specified in Features geojson
    */
    private val mag_key = "mag"
    private val place_key = "place"
    private val time_key = "time"
    private val type_key = "type"
    private val title_key = "title"

    /* Specified in the geometry of geojson
    */
    private val LAT_key = "latitude"
    val LONG_key = "longitude"



    fun parseQuakes(root : JSONObject) : HashMap<String, Earthquake> {
        val hashMap : HashMap<String, Earthquake> = HashMap()
        val earthquakes = root.getJSONArray(features_key)
        val numQuakes = earthquakes.length()

        // sanitize security check to prevent overflow
        if (numQuakes > 1000) {
            return hashMap
        }

        for (i in 0..numQuakes - 1) {

            val item = earthquakes.getJSONObject(i)
            val id = item.getString(ID_key)

            val properties = item.getJSONObject(properties_key)
            val coordirnates = item.getJSONObject(geometry_key).getJSONArray(coordinates_key)

            val mag = properties.getDouble(mag_key)
            val place = properties.getString(place_key)
            val time = properties.getLong(time_key)
            val type = properties.getString(type_key)

            val quake = Earthquake(id, mag, place, time, type,
                    coordirnates.getDouble(0), coordirnates.getDouble(1))
            hashMap.put(quake.id, quake)
        }
        return hashMap
    }
}