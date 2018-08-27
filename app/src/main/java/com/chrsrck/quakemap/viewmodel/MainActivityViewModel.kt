package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.ViewModel
import com.chrsrck.quakemap.data.DataSourceUSGS

class MainActivityViewModel : ViewModel() {

    val activeItem : Int
    val dataSource : DataSourceUSGS

    init {
        activeItem = 0
        dataSource = DataSourceUSGS()
    }


}