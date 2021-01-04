package com.chrsrck.quakemap.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName = "earthquakes")
data class Earthquake(
        @PrimaryKey val id: String,
        val magnitude: Double,
        val place: String,
        @ColumnInfo val time: Long,
        val type: String, //possible to be not than eq ex. nuke bomb
        val longitude: Double,
        val latitude: Double)