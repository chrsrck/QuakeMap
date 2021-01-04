package com.chrsrck.quakemap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chrsrck.quakemap.model.Earthquake

@Database(entities = [Earthquake::class], version = 1)
abstract class EarthquakeRoomDatabase : RoomDatabase() {

    abstract fun eqDao() : EarthquakeDao

    companion object {
        @Volatile
        private var INSTANCE : EarthquakeRoomDatabase? = null

        fun getDatabase(context : Context) : EarthquakeRoomDatabase {
            return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                            context.applicationContext,
                            EarthquakeRoomDatabase::class.java,
                            "earthquakes")
                            .build()
                    INSTANCE = instance
                    instance
            }
        }
    }
}