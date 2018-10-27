package com.chrsrck.quakemap.model


class Earthquake(
        val id: String,
        val magnitude: Double,
        val place: String,
        val time: Long,
        val type: String, //possible to be not than eq ex. nuke bomb
        val longitude: Double,
        val latitude: Double)  {
}