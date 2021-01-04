package com.chrsrck.quakemap.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.chrsrck.quakemap.data.local.EarthquakeDao
import com.chrsrck.quakemap.data.local.EarthquakeRoomDatabase
import com.chrsrck.quakemap.data.network.DataSourceUSGS
import com.chrsrck.quakemap.model.Earthquake
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EarthquakeRepository(private val database : EarthquakeRoomDatabase) {


    val earthquakes = database.eqDao().getEarthquakes()

    suspend fun refresh() = withContext(Dispatchers.IO) {
//        val quakes = DataSourceUSGS.source.getEarthquakesSig()
        val quakes = DataSourceUSGS.fetchJSON()
        database.eqDao().insert(quakes.values.toList())
    }



//    fun getEarthquakes() : LiveData<List<Earthquake>> {
//        eqDao.getEarthquakes()
//    }
//
//    private suspend fun insert(eq : Earthquake) {
//        eqDao.insert(eq)
//    }
//
//    private suspend fun insert(earthquakes : List<Earthquake>) {
//        eqDao.insert(earthquakes)
//    }
}