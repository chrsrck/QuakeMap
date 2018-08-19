package com.chrsrck.quakemap.data

import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class DataSourceUSGS {

    private val client : OkHttpClient

    init {
        client = OkHttpClient()
    }

    
}