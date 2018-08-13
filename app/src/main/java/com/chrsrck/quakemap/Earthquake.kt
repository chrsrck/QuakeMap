package com.chrsrck.quakemap

class Earthquake {

    val magnitude: Double
    val description: String
    val latitude: Double
    val longitude: Double

    constructor(description: String, magnitude: Double, longitude: Double, latitude: Double) {
        this.description = description
        this.magnitude = magnitude
        this.longitude = longitude
        this.latitude = latitude
    }
}