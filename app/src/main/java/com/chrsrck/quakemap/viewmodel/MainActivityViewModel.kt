package com.chrsrck.quakemap.viewmodel

import android.arch.lifecycle.ViewModel
import com.chrsrck.quakemap.data.DataSourceUSGS

class MainActivityViewModel : ViewModel() {

    val activeItem : Int

    init {
        activeItem = 0
    }


}