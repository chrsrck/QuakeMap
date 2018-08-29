package com.chrsrck.quakemap.model


class Earthquake(
        val id: String,
        val magnitude: Double,
        val place: String,
        val time: Long,
        val type: String,
        val longitude: Double,
        val latitude: Double)  {
}