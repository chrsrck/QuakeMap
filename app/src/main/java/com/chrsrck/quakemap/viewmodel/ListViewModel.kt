package com.chrsrck.quakemap.viewmodel

import androidx.lifecycle.ViewModel
import com.chrsrck.quakemap.model.Earthquake
/*
For EarthquakeListFragment
 */
class ListViewModel() : ViewModel() {

    private var quakeList :  ArrayList<Earthquake>

    init {
        quakeList = ArrayList()
    }

    fun updateQuakeList(map : HashMap<String, Earthquake>?) {
        if (map != null) {
            quakeList.clear()
            map.forEach({entry ->
                quakeList.add(entry.value)
            })
        }
    }

    fun isEmptyQuakeList() : Boolean {
        return quakeList.isEmpty()
    }

    fun getQuakeList() : ArrayList<Earthquake> {
        return quakeList
    }
}