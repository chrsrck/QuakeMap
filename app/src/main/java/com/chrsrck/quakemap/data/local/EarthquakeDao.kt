package com.chrsrck.quakemap.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chrsrck.quakemap.model.Earthquake

@Dao
interface EarthquakeDao {
    @Query("SELECT * from earthquakes")
    fun getEarthquakes() : LiveData<List<Earthquake>>

    @Query("SELECT * from earthquakes WHERE time > :timestamp")
    fun getEarthquakes(timestamp : Long) : LiveData<List<Earthquake>>

//    @Query("SELECT * from earthquakes WHERE id = :id")
//    fun getEarthquake(id : String)

    // Conflict strategy is insert if more update
    // to date earthquake information is available.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eq : Earthquake)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(earthquakes : List<Earthquake>)

    @Query("DELETE FROM earthquakes")
    fun deleteAllEarthquakes()
}