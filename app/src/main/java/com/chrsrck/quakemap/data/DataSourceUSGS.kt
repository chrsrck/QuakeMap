package com.chrsrck.quakemap.data

import okhttp3.OkHttpClient

class DataSourceUSGS {

    private val client : OkHttpClient

    init {
        client = OkHttpClient()
    }
}